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

/**
 * This class provides an abstract to be implemented message for a server connection.
 */
class ServerConnectionMessage {

    /**
     * The server connection to the server that created us.
     */
    protected ServerConnection serverConnection;

    ServerConnectionMessage(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    /**
     * Returns the ID of the connection
     * Uses the port from the ip
     * (ip=192.168.0.1,port=34527)34527.19216801
     * @return The string containing the ID of the connection.
     */
    synchronized String getID() {
        return "";
    }

    /**
     * Send a message to the client
     * @param message The message.
     */
    synchronized void sendMessage(Object message) {
    }

    /**
     * This should close the connection and remove it from the ServerSocket..
     */
    synchronized void close() {
    }

    /**
     * Get the time this message was received.
     * @return Time received.
     */
    synchronized Long getTimeReceived() {
        return 0L;
    }
}
