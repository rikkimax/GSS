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

package gss.run.servers.connectors

import gss.socket.SocketClient
import com.esotericsoftware.kryonet.Connection
import gss.socket.ServerSocket
import org.apache.commons.lang.StringUtils

class KryoNetClient extends SocketClient {
    private Connection connection;
    private Long receivedTime;

    KryoNetClient(KryoNetServer serverSocket, Connection connection) {
        super(serverSocket);
        this.connection = connection;
        receivedTime = System.currentTimeMillis();
    }

    String getIP() {
        return connection.getRemoteAddressTCP().getAddress().getHostAddress();
    }

    int getPort() {
        return connection.getRemoteAddressTCP().getPort();
    }

    void close() {
        connection.close();
    }

    Connection getConnection() {
        return connection;
    }
    /**
     * Returns the ID of the connection
     * Uses the port from the ip
     * (ip=192.168.0.1,port=34527)34527.19216801
     * @return The string containing the ID of the connection.
     */
    String getID() {
        String ip = getIP().replace(".", "");
        ipPort = getPort() + "." + ip;
        ipPort = StringUtils.reverse(ipPort);
        return ipPort;
    }

    ServerSocket getServerSocket() {
        return serverSocket;
    }

    void sendMessage(Object message) {
        connection.sendTCP(message);
    }

    Long getTimeReceived() {
        return receivedTime;
    }
}
