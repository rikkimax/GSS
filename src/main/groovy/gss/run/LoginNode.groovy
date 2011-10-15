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
import gss.login.socket.ServerSocket
import gss.login.Servers

/**
 * This is a booter class, it will start every thing up for the login node.
 */
class LoginNode extends Booter {

    /**
     * What type of node is this?
     */
    public final String type = "login";

    /**
     * All the servers available from configuration file.
     */
    private Servers servers;

    /**
     * Lets store a copy of this booter instance...
     *
     */
    private static LoginNode instance;

    /**
     * Start up initiation method but as a singleton..
     * @param args Arguments given to application
     */
    static void main(String... args) {
        if (instance == null) {
            instance = new LoginNode();
            instance.boot(args);
        }
    }

    /**
     * An overidden method to provide extra start up procedures.
     */
    @Override
    void startup() {
        super;
        if (keepGoingStartUp)
            startUpServers();
    }

    /**
     * Starts up server connectors from configuration.
     */
    void startUpServers() {
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
            if (serverClassReflected != null) {
                Object serverClassObject = Eval.x(this, "return new " + serverClass + "(x);");
                if (serverClassObject instanceof ServerSocket) {
                    for (HashMap<Object> serverValue: values) {
                        ServerSocket serverConnector = serverClassObject;
                        serverConnector.setValues(serverValue);
                        servers.addSocket(serverConnector);
                        println("Started ---" + serverConnector);
                    }
                }
            }
        }
        servers.start();
    }

    /**
     * Gets the servers connectors.
     * @return The servers connectors.
     */
    Servers getServers() {
        return servers;
    }

    /**
     * Lets get the instance of this booter.
     * @return The instance of this booter.
     */
    LoginNode getInstance() {
        return instance;
    }
}