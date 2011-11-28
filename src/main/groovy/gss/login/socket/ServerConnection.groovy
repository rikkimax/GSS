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

package gss.login.socket

import gss.run.LoginNode

/**
 * An extended (to be implemented) class to offer an interface for connections to servers.
 */
abstract class ServerConnection {
    /**
     * Who created us.
     */
    protected LoginNode loginNode;

    /**
     * The server host / ip we are connecting to.
     */
    protected String server;

    /**
     * The server port we are connecting to.
     */
    protected int port;

    /**
     * We need to store who created us.
     * @param booter The booter who created us.
     */
    ServerConnection(LoginNode loginNode) {
        this.loginNode = loginNode;
    }

    /**
     * Sets the values of the socket.
     * @param values The values to set
     */
    synchronized void setValues(HashMap<Object, Object> values) {}
    /**
     * Start the connection
     */
    synchronized void start() {}
    /**
     * Stop the connection
     */
    synchronized void stop() {}

    /**
     * Get the server host / ip.
     * @return The server host / ip we are connecting to.
     */
    String getServerHost() {
        return server;
    }

    /**
     * The server port.
     * @return The server port we are connecting to.
     */
    int getServerPort() {
        return port;
    }

    /**
     * Is the connection connected to the server?
     * @return Is the connection connected to the server?
     */
    Boolean isConnected() {
        return false;
    }

    /**
     * Send a message to the server.
     * @param message The message to send.
     */
    void sendMessage(Object message) {
    }
}
