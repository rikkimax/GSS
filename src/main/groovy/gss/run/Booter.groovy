/*
 * Copyright © 2011, GSS team
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 * Neither the name of the GSS development organisation nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GSS team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package gss.run

import joptsimple.OptionParser
import joptsimple.OptionSet
import gss.config.Config
import java.util.logging.Level
import java.util.logging.Logger
import gss.config.Server
import org.hibernate.SessionFactory
import javax.persistence.EntityManager
import org.hibernate.Session
import org.apache.commons.vfs.FileObject
import org.yaml.snakeyaml.Yaml
import gss.eventing.Event
import gss.eventing.UnknownEvent
import org.apache.commons.vfs.VFS
import gss.eventing.EventManagerHandler
import org.apache.commons.vfs.FileType
import gss.queueing.TestQueue
import org.hibernate.cfg.AnnotationConfiguration
import gss.config.ScriptedBootLoader

/**
 * The point of this class is to provide a generic booter to be extended.
 */
abstract class Booter {

    /**
     * Do we keep the application start up process going?
     */
    protected Boolean keepGoingStartUp = true;

    /**
     * The configuration class
     */
    protected Config config;

    /**
     * Hibernate configuration for the session.
     */
    protected SessionFactory sessionFactory;

    /**
     * Event manager essentially associates a key(s) to event class(es).
     */
    protected EventManagerHandler eventManager;

    /**
     * The directory to use as the base.
     */
    protected File workingDir;

    /**
     * Loads in some code once... which can be used by anything.
     */
    protected ScriptedBootLoader scriptedBootLoader;

    /**
     * Lets do nothing in this constructor...
     */
    Booter() {
    }

    /**
     * Start up initiation method
     * @param args Arguments given to application
     */
    void boot(String... args) {
        Logger.getLogger("gss.Booter").setLevel(Level.ALL);
        OptionParser optionParser = new OptionParser();
        optionsAdd(optionParser);
        OptionSet optionSet = optionParser.parse(args);
        if (optionSet.has("help")) {
            optionParser.printHelpOn(System.out);
            keepGoingStartUp = false;
        } else {
            config = new Config();
            parseArguments(optionParser, optionSet);
        }
        if (keepGoingStartUp)
        //do something like actually setup some variables...
        //only because we are still wanted!
            startup();
    }

    /**
     * This method adds options to use.
     * @param op The OptionParser.
     */
    void optionsAdd(OptionParser op) {
        op.acceptsAll(["help", "?"], "Help information");
        op.acceptsAll(["configDir", "c"], "Configuration directory").withRequiredArg().ofType(File.class).defaultsTo(new File("config"));
        op.acceptsAll(["workingDir", "w"], "The working directory for scripts (eventing)").withRequiredArg().ofType(File.class).defaultsTo(new File("game"));
    }

    /**
     * Parses the arguments given to the application.
     * @param optionParser The parser class.
     * @param optionSet The options given.
     */
    void parseArguments(OptionParser optionParser, OptionSet optionSet) {
        File configDir = ((File) optionSet.valueOf("configDir"));
        workingDir = (File) optionSet.valueOf("workingDir");
        workingDir.mkdirs();
        configDir.mkdirs();
        if (configDir.exists()) {
            config.setDirectory(configDir);
            scriptedBootLoader = new ScriptedBootLoader(this);
            Logger.getLogger(Booter.class.getName()).info("Configuration directory being used " + configDir.getAbsolutePath());
        } else {
            Logger.getLogger(Booter.class.getName()).severe("Cannot continue configuration directory does not exist and cannot!\n" + configDir.getAbsolutePath());
            keepGoingStartUp = false;
        }
        if (keepGoingStartUp)
            config.load();
    }

    /**
     * Calls other methods upon start up.
     */
    void startup() {
        if (keepGoingStartUp)
            startUpEventManager();
        if (keepGoingStartUp)
            startUpDataBase();
    }

    /**
     * Set up Hibernate for database access.
     */
    void startUpDataBase() {
        //find database server details from config
        Server database;
        HashMap configH = new HashMap();
        config.getServers().each {
            if (it.getType().toLowerCase().equals("database")) {
                database = it;
            }
        }
        //set database server from config
        database.getOther().keySet().each {
            configH.put(it, database.getOther().get(it).toString().replace("{configDir}", config.getDirectory().getURL().toString()).replace("{workingDir}", workingDir.getAbsolutePath()));
        }
        if (configH.get("hibernate.connection.url") != null) {
            //database url exists, otherwise we can't work...
            Logger.getLogger(Booter.class.getName()).info("Using database connection: " + configH.get("hibernate.connection.url"));
            configH.keySet().each {
                config.getAnnotationConfiguration().setProperty(it, configH.get(it));
            }
            sessionFactory = config.getAnnotationConfiguration().buildSessionFactory();
        } else
            keepGoingStartUp = false;
    }

    /**
     * Set up the event manager from configuration.
     */
    void startUpEventManager() {
        eventManager = new EventManagerHandler(this);
        FileObject eventsFO = config.getDirectory()?.resolveFile("events.yml");
        if (eventsFO?.exists()) {
            //Our type has a configuration directory and events file
            Yaml yaml = new Yaml();
            List<HashMap<String, List<String>>> content = yaml.load(eventsFO.content.inputStream);
            content.each {triggersEvents ->
                triggersEvents.each {eventTrigger, events ->
                    events.each {event ->
                        Object eventTriggerEvaled = Eval.me("return ${eventTrigger};");
                        Object eventEvaled = Eval.me("return new ${event}();");
                        if (eventTriggerEvaled != null && eventEvaled != null) {
                            if (eventEvaled instanceof Event)
                                eventManager.addEvent(eventTriggerEvaled, eventEvaled);
                        }
                    }
                }
            }
        }
        // Convert any unused serialized classes to triggers or events, depending if it extends Event.
        config.getSerializedClasses().each {
            Object eventEvaled = Eval.me("return new ${it}();");
            Boolean toTrigger = true;
            if (eventEvaled != null)
                if (eventEvaled instanceof Event)
                    if (eventManager.containsEvent(eventEvaled) || eventManager.containsTrigger(eventEvaled))
                        toTrigger = false;
                    else {
                    }
                else if (eventManager.containsTrigger(it))
                    toTrigger = false;
            if (toTrigger)
                eventManager.addEvent(it, new UnknownEvent());
        }
        FileObject eventsDir = workingDirFileObject.resolveFile("events");
        if (!eventsDir.exists())
            eventsDir.createFolder();
        eventManager.addDirectoryMonitoring(eventsDir);
    }

    /**
     * Get the configuration class.
     * @return The config class instance.
     */
    synchronized Config getConfig() {
        return config;
    }

    /**
     * Get the entity manager for Hibernate.
     * @return The entity manager.
     */
    synchronized EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Get a new session to be used for Hibernate.
     * @return A new session to be used for Hibernate.
     */
    synchronized Session getSession() {
        //give us a new session that is opened so we can work with the database
        return sessionFactory.openSession();
    }

    /**
     * Gets the event manager.
     * @return The event manager.
     */
    synchronized EventManagerHandler getEventManager() {
        return eventManager;
    }

    /**
     * Get the type of the server.
     * @return The type of the server.
     */
    synchronized String getType() {
        return "booter";
    }

    /**
     * Gets the current working directory.
     * @return The current working directory.
     */
    synchronized File getWorkingDir() {
        return workingDir;
    }

    /**
     * Gets the current working directory.
     * @return The current working directory.
     */
    synchronized FileObject getWorkingDirFileObject() {
        return VFS.getManager().resolveFile(workingDir.getAbsolutePath());
    }

    /**
     * Do keep the server going?
     * @return Keep the server going value.
     */
    synchronized Boolean getKeepGoing() {
        return keepGoingStartUp;
    }
}
