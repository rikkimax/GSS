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
import java.util.logging.Level
import java.util.logging.Logger
import javax.persistence.EntityManager
import joptsimple.OptionParser
import joptsimple.OptionSet
import org.hibernate.SessionFactory
import org.hibernate.Session
import gss.socket.ServerSocket

class Booter {
    private static Boolean run = true;
    private static Config config;
    private static SessionFactory sessionFactory;
    private static Servers servers;

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
        if (run)
            startUpServers();

    }

    static void startUpDataBase() {
        //find database server details from config
        Server database;
        HashMap configH = new HashMap();
        for (Server server: config.getServers()) {
            if (server.getType().toLowerCase().equals("database")) {
                database = server;
            }
        }
        //set database server from config
        for (String key: database.getOther().keySet()) {
            if (key.startsWith("hibernate")) {
                configH.put(key, database.getOther().get(key).toString().replace("{configDir}", config.getDirectory().getURL().toString()));
            }
        }
        if (configH.get("hibernate.connection.url") == null) {
            run = false;
        } else {
            //database url exists, otherwise we can't work...
            Logger.getLogger(Booter.getClass().getName()).info("Using database connection: " + configH.get("openjpa.ConnectionURL"));
            for (String key: configH.keySet()) {
                config.getAnnotationConfiguration().setProperty(key, configH.get(key));
            }
            sessionFactory = config.getAnnotationConfiguration().buildSessionFactory();
        }
    }

    static void startUpServers() {
        //find current server details from config
        servers = new Servers();
        Server current;
        HashMap configH = new HashMap();
        for (Server server: config.getServers()) {
            if (server.getType().toLowerCase().equals("current")) {
                current = server;
            }
        }
        HashMap<ArrayList<HashMap<Object, Object>>> serversConfig = current.other?.get("servers");
        for (String serverClass: serversConfig.keySet()) {
            ArrayList<HashMap<Object, Object>> values = serversConfig.get(serverClass);
            Class serverClassReflected = Eval.me("return " + serverClass + ".class");
            println(serverClassReflected);
            if (serverClassReflected != null) {
                if (serverClassReflected.newInstance() instanceof ServerSocket) {
                    for (HashMap<Object> serverValue: values) {
                        ServerSocket serverConnector = serverClassReflected.newInstance();
                        serverConnector.setValues(values);
                        servers.addSocket(serverConnector);
                    }
                }
            }
        }
        servers.start();
    }

    static Config getConfig() {
        return config;
    }

    static EntityManager getEntityManager() {
        return entityManager;
    }

    static Session getSession() {
        //give us a new session that is opened so we can work with the database
        return sessionFactory.openSession();
    }

    static Servers getServers() {
        return servers;
    }
}