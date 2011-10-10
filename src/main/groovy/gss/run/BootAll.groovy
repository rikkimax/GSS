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
        if (optionSet.has("help")) {
            optionParser.printHelpOn(System.out);
            println("");
            println("A list of nodes to load");
            println("------------------------");
            println("\tValid types: Login,KeepGoing");
            keepGoingStartUp = false;
        } else {
            parseArguments(optionParser, optionSet);
        }
    }

    /**
     * This method adds options to use.
     * @param op The OptionParser.
     */
    static void optionsAdd(OptionParser op) {
        op.acceptsAll(["help", "?"], "Help information");
        op.acceptsAll(["configDir", "c"], "Configuration directory").withRequiredArg().ofType(File.class).defaultsTo(new File("config"));
        op.acceptsAll(["uniqueConfigDir", "ucd"], "Does each server have its own unique config directory?").withOptionalArg().ofType(Boolean.class).defaultsTo(false);
    }

    /**
     * Parses the arguments given to the application.
     * @param optionParser The parser class.
     * @param optionSet The options given.
     */
    static void parseArguments(OptionParser optionParser, OptionSet optionSet) {
        File configDir = optionSet.valueOf("configDir");
        Boolean uniqueConfigDir = optionSet.valueOf("uniqueConfigDir");
        optionSet.nonOptionArguments().each {
            String type = it.toLowerCase();
            File tempUniqueConfigDir = new File(configDir.getAbsolutePath().replace("\\", "/") + type + "/");
            switch (type) {
                case "keepgoing":
                    Thread.start {
                        while (true)
                            sleep 1000;
                    }
                    break;
                case "login":
                    Logger.getLogger("gss.BootAll").info("Creating login type node");
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
    }
}
