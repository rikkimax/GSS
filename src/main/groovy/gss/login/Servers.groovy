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

package gss.login

import gss.login.socket.ServerSocket;

/**
 * The point of this class is to store the server connectors.
 */
class Servers {

    /**
     * The list of server connectors.
     */
    private ArrayList<ServerSocket> sockets = new ArrayList<ServerSocket>();

    /**
     * Add a new server connector.
     * @param serverSocket The server connector to add.
     */
    void addSocket(ServerSocket serverSocket) {
        sockets.add(serverSocket);
    }

    /**
     * Remove a server connector.
     * @param serverSocket The server connector to remove.
     */
    void removeSocket(ServerSocket serverSocket) {
        sockets.remove(serverSocket);
        serverSocket.stop();
    }

    /**
     * Get all the servers connectors.
     * @return All the server connectors.
     */
    ArrayList<ServerSocket> getSockets() {
        return sockets;
    }

    /**
     * Removes all server connectors.
     */
    void clear() {
        sockets.each {
            removeSocket(it);
        }
    }

    /**
     * Start all server connectors.
     */
    synchronized void start() {
        sockets.each {
            it.start();
        }
    }

    /**
     * Stops all server connectors.
     */
    synchronized void stop() {
        sockets.each {
            it.stop();
        }
    }
}
