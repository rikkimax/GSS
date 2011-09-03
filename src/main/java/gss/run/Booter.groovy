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

import gss.config.Config
import gss.config.Server
import gss.run.entities.Test
import java.util.logging.Level
import java.util.logging.Logger
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence
import joptsimple.OptionParser
import joptsimple.OptionSet

class Booter {
    private static Boolean run = true;
    private static Config config;
    private static EntityManager entityManager;

    static void main(String... args) {
        Logger.getLogger("gss.run").setLevel(Level.ALL);
        OptionParser optionParser = new OptionParser();
        optionParser.acceptsAll(["help", "?"], "Help information");
        optionParser.acceptsAll(["configDir", "c"], "Configuration directory").withRequiredArg().ofType(File.class).defaultsTo(new File("config"));
        OptionSet optionSet = optionParser.parse(args);
        if (optionSet.has("help")) {
            optionParser.printHelpOn(System.out);
            run = false;
        } else {
            //we are not required as we printed help (END OF APP)
            config = new Config();
            parseArguments(optionParser, optionSet);
        }
        if (run) {
            //do something like actually setup some variables...
            //only because we are still wanted!
            startup();
        }
    }

    static void parseArguments(OptionParser optionParser, OptionSet optionSet) {
        File configDir = ((File) optionSet.valueOf("configDir"));
        configDir.mkdirs();
        if (configDir.exists()) {
            config.setDirectory(configDir);
            Logger.getLogger(Booter.getClass().getName()).info("Configuration directory being used " + configDir.getAbsolutePath());
        } else {
            Logger.getLogger(Booter.getClass().getName()).severe("Cannot continue configuration directory does not exist and cannot!\n" + configDir.getAbsolutePath());
            run = false;
        }
        //load configuration into config class!
        config.load();
    }

    static void startup() {
        if (run)
            startUpDataBase();

    }

    static void startUpDataBase() {
        Server database;
        HashMap configH = new HashMap();
        for (Server server: config.getServers()) {
            if (server.getType().toLowerCase().equals("database")) {
                database = server;
            }
        }
        for (String key: database.getOther().keySet()) {
            if (key.startsWith("openjpa")) {
                configH.put(key, database.getOther().get(key).toString().replace("{configDir}", config.getDirectory().getURL().toString()));
            }
        }
        if (configH.get("openjpa.ConnectionURL") != null) {
            Logger.getLogger(Booter.getClass().getName()).info("Using database connection: " + configH.get("openjpa.ConnectionURL"));
        }
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("database", configH);
        entityManager = emf.createEntityManager();
        if (!entityManager.open) {
            run = true;
        }
        entityManager.getTransaction().begin();
        Test t = new Test();
        entityManager.persist(t);

    }

    Config getConfig() {
        return config;
    }

    EntityManager getEntityManager() {
        return entityManager;
    }
}
