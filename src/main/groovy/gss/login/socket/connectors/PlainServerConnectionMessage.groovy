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

package gss.login.socket.connectors

import gss.login.socket.ServerConnectionMessage

/**
 * The point of this class is to wrap a message received from a server.
 */
class PlainServerConnectionMessage extends ServerConnectionMessage {
    /**
     * The message received.
     */
    private Object message;

    /**
     *  The time this message was received.
     */
    private Long received;

    PlainServerConnectionMessage(PlainServerConnection serverConnection, Object message) {
        super(serverConnection);
        received = System.currentTimeMillis();
    }

    /**
     * The id of this connection from the server connection.
     * @return The id of this connection.
     */
    @Override
    synchronized String getID() {
        return ((PlainServerConnection) serverConnection).getId();
    }

    /**
     * Send a message to the server.
     * @param message The message to send.
     */
    @Override
    synchronized void sendMessage(Object message) {
        ((PlainServerConnection) serverConnection).sendMessage(message);
    }

    /**
     * Close this connection and server connection.
     */
    @Override
    synchronized void close() {
        serverConnection.stop();
    }

    /**
     * Get the time this message was received.
     * @return The time this message was received.
     */
    @Override
    synchronized Long getTimeReceived() {
        return received;
    }

    /**
     * Get the message that was received.
     * @return The message that was received.
     */
    synchronized Object getMessage() {
        return message;
    }

    /**
     * Get the server that received this message.
     * @return The server that received this message.
     */
    synchronized PlainServerConnection getServer() {
        return (PlainServerConnection)serverConnection;
    }
}
