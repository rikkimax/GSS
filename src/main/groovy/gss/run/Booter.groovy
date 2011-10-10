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

/**
 * The point of this class is to provide a generic booter to be extended.
 */
class Booter {

    /**
     * Do we keep the application start up process going?
     */
    private static Boolean keepGoingStartUp = true;

    /**
     * The configuration class
     */
    private static Config config;

    /**
     * Hibernate configuration for the session.
     */
    private static SessionFactory sessionFactory;

    /**
     * Start up initiation method
     * @param args Arguments given to application
     */
    static void main(String... args) {
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
    static void optionsAdd(OptionParser op) {
        op.acceptsAll(["help", "?"], "Help information");
        op.acceptsAll(["configDir", "c"], "Configuration directory").withRequiredArg().ofType(File.class).defaultsTo(new File("config"));
    }

    /**
     * Parses the arguments given to the application.
     * @param optionParser The parser class.
     * @param optionSet The options given.
     */
    static void parseArguments(OptionParser optionParser, OptionSet optionSet) {
        File configDir = ((File) optionSet.valueOf("configDir"));
        configDir.mkdirs();
        if (configDir.exists()) {
            config.setDirectory(configDir);
            Logger.getLogger(Booter.class.getName()).info("Configuration directory being used " + configDir.getAbsolutePath());
        } else {
            Logger.getLogger(Booter.class.getName()).severe("Cannot continue configuration directory does not exist and cannot!\n" + configDir.getAbsolutePath());
            keepGoingStartUp = false;
        }
        config.load();
    }

    /**
     * Calls other methods upon start up.
     */
    static void startup() {
        if (keepGoingStartUp)
            startUpDataBase();
    }

    /**
     * Set up Hibernate for database access.
     */
    static void startUpDataBase() {
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
            if (it.startsWith("hibernate"))
                configH.put(it, database.getOther().get(it).toString().replace("{configDir}", config.getDirectory().getURL().toString()));
        }
        if (configH.get("hibernate.connection.url") == null)
            keepGoingStartUp = false;
        else {
            //database url exists, otherwise we can't work...
            Logger.getLogger(Booter.class.getName()).info("Using database connection: " + configH.get("openjpa.ConnectionURL"));
            configH.keySet().each {
                config.getAnnotationConfiguration().setProperty(it, configH.get(it));
            }
            sessionFactory = config.getAnnotationConfiguration().buildSessionFactory();
        }
    }

    /**
     * Get the configuration class.
     * @return The config class instance.
     */
    static Config getConfig() {
        return config;
    }

    /**
     * Get the entity manager for Hibernate.
     * @return The entity manager.
     */
    static EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Get a new session to be used for Hibernate.
     * @return A new session to be used for Hibernate.
     */
    static Session getSession() {
        //give us a new session that is opened so we can work with the database
        return sessionFactory.openSession();
    }
}
