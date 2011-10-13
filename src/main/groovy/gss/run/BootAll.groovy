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
import java.util.logging.Level
import java.util.logging.Logger
import org.hsqldb.Server

/**
 * Boots all or some of the other node types.
 */
class BootAll {

    /**
     * Do we keep the application start up process going?
     */
    private static Boolean keepGoingStartUp = true;

    /**
     * Start up initiation method
     * @param args Arguments given to application
     */
    static void main(String... args) {
        Logger.getLogger("gss.BootAll").setLevel(Level.ALL);
        OptionParser optionParser = new OptionParser();
        optionsAdd(optionParser);
        OptionSet optionSet = optionParser.parse(args);
        if (optionSet.has("help") || (!optionSet.hasOptions() && optionSet.nonOptionArguments().size() < 1)) {
            optionParser.printHelpOn(System.out);
            println("");
            println("A list of nodes to load");
            println("------------------------");
            println("\tValid types: Login,KeepGoing");
            println("It is recommended to use one and only one of KeepGoing node.");
            println("\tKeepGoing is used to keep the servers alive if there is no threaded tasks running.");
            keepGoingStartUp = false;
        } else if (optionSet.hasOptions() || optionSet.nonOptionArguments().size() > 0) {
            parseArguments(optionParser, optionSet);
        }
    }

    /**
     * This method adds options to use.
     * @param op The OptionParser.
     */
    private static void optionsAdd(OptionParser op) {
        op.acceptsAll(["help", "?"], "Help information");
        op.acceptsAll(["configDir", "c"], "Configuration directory").withRequiredArg().ofType(File.class).defaultsTo(new File("config"));
        op.acceptsAll(["uniqueConfigDir", "ucd"], "Does each server have its own unique config directory?").withOptionalArg().ofType(Boolean.class).defaultsTo(true);
        op.acceptsAll(["hsqldb", "db"], "Creates a HSQLDB on a speicifc port. Preferred to be used with uniqueConfigDir option as well.").withRequiredArg().ofType(Integer.class).defaultsTo(9001);
    }

    /**
     * Parses the arguments given to the application.
     * @param optionParser The parser class.
     * @param optionSet The options given.
     */
    private static void parseArguments(OptionParser optionParser, OptionSet optionSet) {
        File configDir = optionSet.valueOf("configDir");
        Boolean uniqueConfigDir = optionSet.valueOf("uniqueConfigDir");
        parseArgumentsDataBase(optionParser, optionSet, configDir, uniqueConfigDir);
        if (!uniqueConfigDir) {
            List<String> copy = new ArrayList<String>();
            Collections.copy(optionSet.nonOptionArguments(), copy);
            copy.remove("keepgoing");
            if (copy.size() > 1 || (copy.size() == 1 && optionSet.has("hsqldb")))
                Logger.getLogger("gss.BootAll").severe("There is more then 1 server node operational configuration will be corrupt for doing this.");
        }
        parseArgumentsNodes(optionParser, optionSet, configDir, uniqueConfigDir);
    }

    /**
     * Parses the arguments given to the application for a database server.
     * @param optionParser The parser class.
     * @param optionSet The options given.
     * @param configDir Configuration directory.
     * @param uniqueConfigDir Do we use a unique directory folder structure?
     */
    private static void parseArgumentsDataBase(OptionParser optionParser, OptionSet optionSet, File configDir, Boolean uniqueConfigDir) {
        if (optionSet.has("hsqldb")) {
            if (!uniqueConfigDir)
                Logger.getLogger("gss.BootAll").warning("Database server should be ran either by itself or with uniqueConfigDir on.");
            Thread.start {
                File tempUniqueConfigDir = new File(configDir.getAbsolutePath().replace("\\", "/") + "hsqldb/");
                Logger.getLogger("gss.BootAll").info("Creating database server");
                if (uniqueConfigDir)
                    Server.main(["-database", tempUniqueConfigDir, "-port " + optionSet.valueOf("hsqldb")]);
                else
                    Server.main(["-database", configDir, "-port " + optionSet.valueOf("hsqldb")]);
            }
        }
    }

    /**
     * Parses the arguments given to the application for the nodes.
     * @param optionParser The parser class.
     * @param optionSet The options given.
     * @param configDir Configuration directory.
     * @param uniqueConfigDir Do we use a unique directory folder structure?
     */
    private static void parseArgumentsNodes(OptionParser optionParser, OptionSet optionSet, File configDir, Boolean uniqueConfigDir) {
        Map<String, Integer> countUsed = new HashMap<String, Integer>();
        countUsed.put("keepgoing", 0);
        countUsed.put("login", 0);
        optionSet.nonOptionArguments().each {
            String type = it.toLowerCase();
            String extension = "";
            if (countUsed.get(type) > 0)
                extension = "_" + countUsed.get(type) + "";
            File tempUniqueConfigDir = new File(configDir.getAbsolutePath().replace("\\", "/") + type + extension + "/");
            switch (type) {
                case "keepgoing":
                    countUsed.put(type, countUsed.get(type) + 1);
                    if (countUsed.get(type) > 1)
                        Logger.getLogger("gss.BootAll").warning("We should have only one and max one KeepGoing node...");
                    Thread.start {
                        while (true)
                            sleep 1000;
                    }
                    break;
                case "login":
                    Logger.getLogger("gss.BootAll").info("Creating login type node");
                    countUsed.put(type, countUsed.get(type) + 1);
                    Thread.start {
                        if (uniqueConfigDir)
                            LoginNode.main("--configDir=${tempUniqueConfigDir}");
                        else
                            LoginNode.main("--configDir=${configDir}");
                    }
                    break;
                default:
                    println("[${type}] is not a node type!");
                    break;
            }
        };
        statisticsNodes(countUsed);
    }

    /**
     * Outputs some basic statistics from what was loaded to do with the nodes.
     * @param countUsed A list of loaded nodes.
     */
    private static void statisticsNodes(Map<String, Integer> countUsed) {
        Integer totalNodes = 0;
        println("");
        println("Loaded statistics");
        println("-----------------");
        countUsed.keySet().each {
            println(it + " has " + countUsed.get(it) + " implementations");
            totalNodes += countUsed.get(it);
        }
        println("");
        println("Total loaded " + totalNodes);
    }
}
