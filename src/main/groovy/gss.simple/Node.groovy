/*
 * Copyright © 2012, GSS team
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

package gss.simple

import gss.run.Booter
import java.util.logging.Level
import java.util.logging.Logger

/**
 * This class is to act as a Booter but do a lot more to ease development of the end developer greatly.
 */
class Node extends Booter {
    /**
     * The instance for singleton running.
     */
    synchronized static Node instance;

    /**
     * Start up initiation method but as a singleton..
     * @param args Arguments given to application
     */
    static void main(String... args) {
        if (instance == null) {
            instance = new Node();
            instance.boot(args);
        }
    }
    /**
     * Start up initiation method
     * @param args Arguments given to application
     */
    void boot(String... args) {
        Logger.getLogger("gss.Booter").setLevel(Level.SEVERE);
        config = new Config();
        if (args.length == 0)
            config.setConfigFile("config.yml");
        else if (args.length == 1) {
            if (new File(args[0]).exists())
                config.setConfigFile(args[0]);
            else
                config.setConfigFile("config.yml");
        }
        config.setDirectory(new File("."));
        config.load();
        scriptedBootLoader = new ModelsLoader(this);
        startup();
    }

    /**
     * Get the type of the server
     * @return The type of the server
     */
    @Override
    synchronized String getType() {
        return "Node";
    }
}
