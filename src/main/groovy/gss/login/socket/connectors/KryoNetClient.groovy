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

import gss.login.socket.SocketClient
import com.esotericsoftware.kryonet.Connection
import gss.login.socket.ServerSocket
import org.apache.commons.lang.StringUtils

/**
 * This class provides a server connector to KryoNet library.
 */
class KryoNetClient extends SocketClient {
    /**
     * Do we make the ID of the client simple?
     */
    private Boolean simpleID = false;

    /**
     * The connection used and gained by KryoNet.
     */
    private Connection connection;

    /**
     * When the last message (this goes along with it) was received.
     */
    private Long receivedTime;

    /**
     * The creation of this class initation method.
     * @param serverSocket The KryoNet server that the client connected to.
     * @param connection The connection from the client.
     */
    KryoNetClient(KryoNetServer serverSocket, Connection connection) {
        super(serverSocket);
        this.connection = connection;
        receivedTime = System.currentTimeMillis();
    }
    /**
     * The creation of this class initation method.
     * @param serverSocket The KryoNet server that the client connected to.
     * @param connection The connection from the client.
     */
    KryoNetClient(KryoNetServer serverSocket, Connection connection, Boolean simpleID) {
        super(serverSocket);
        this.connection = connection;
        receivedTime = System.currentTimeMillis();
        this.simpleID = simpleID;
    }

    /**
     * Gets the IP of the client.
     * @return The IP of the client.
     */
    synchronized String getIP() {
        return connection.getRemoteAddressTCP().getAddress().getHostAddress();
    }

    /**
     * Gets the port of the client.
     * @return The port of the client.
     */
    synchronized int getPort() {
        return connection.getRemoteAddressTCP().getPort();
    }

    /**
     * Closes the connection to the client.
     */
    synchronized void close() {
        connection.close();
    }

    /**
     * Gets the connection to the user.
     * @return The connection to the user.
     */
    synchronized Connection getConnection() {
        return connection;
    }

    /**
     * Gets the unique ID of the connection
     * Uses the port and the ip
     * (ip=192.168.0.1,port=34527)
     * Simple ID = 19216801.34527
     * Non simple ID =
     * 19216801.34527 -> 72543.10861291 -> kryo.72543.10861291 -> kry o.7 254 3.1 086 129 1 -> 1kry3.1o.7254086129
     * @return The string containing the ID of the connection.
     */
    synchronized String getID() {
        String ip = getIP().replace(".", "");
        String ipPort = getPort() + "." + ip;
        if (simpleID)
            return ipPort;
        else {
            ipPort = StringUtils.reverse(ipPort);
            ipPort = "kryo." + ipPort;
            int length = ipPort.length() / 3;
            if ((ipPort.length() / 3) - length > 0)
                length++;
            String[] parts = new String[length];
            int done = 0;
            for (int i = 0; i < ipPort.length(); i++) {
                if (i % 3 == 0)
                    parts[done] = ipPort.charAt(i).toString();
                else
                    parts[done] += ipPort.charAt(i);
                if (i % 3 == 2)
                    done++;
            }
            String out = parts[parts.size() - 1] + parts[0] + parts[(int) (parts.length / 2)];
            parts.each {
                out += it;
            }
            return out;
        }
    }

    /**
     * Gets the game server socket connector.
     * @return The game server socket.
     */
    synchronized ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * Sends a message to the client using a serialized class.
     * @param message The message to send.
     */
    synchronized void sendMessage(Object message) {
        connection.sendTCP(message);
    }

    /**
     * Gets the time the last message was received (creation of this class).
     * @return The time we received a message and created this class object.
     */
    synchronized Long getTimeReceived() {
        return receivedTime;
    }
}
