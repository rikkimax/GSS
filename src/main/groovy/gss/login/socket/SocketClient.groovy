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

class SocketClient {
    /**
     * The server connector for this client.
     */
    private ServerSocket serverSocket;

    /**
     * Initiate and create this class object instance.
     * @param serverSocket The server connector class instance.
     */
    SocketClient(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    /**
     * Returns the ID of the connection
     * Uses the port from the ip
     * (ip=192.168.0.1,port=34527)34527.19216801
     * @return The string containing the ID of the connection.
     */
    String getID() {}

    /**
     * Get the IP of the client used.
     * @return The IP the client used.
     */
    String getIP() {}

    /**
     * Get the port the client used.
     * @return The port the client used to connect.
     */
    int getPort() {}

    /**
     * Send a message to the client
     * @param message The message.
     */
    void sendMessage(Object message) {}

    /**
     * This should close the connection and remove it from the ServerSocket..
     */
    void close() {}

    /**
     * Get the server socket (server connector).
     * @return The server connector.
     */
    ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * Get the time this message was received.
     * @return Time received.
     */
    Long getTimeReceived() {};
}