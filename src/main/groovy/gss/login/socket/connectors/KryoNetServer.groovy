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

import gss.login.socket.ServerSocket
import gss.login.socket.SocketClient
import com.esotericsoftware.kryonet.Server
import gss.run.LoginNode
import com.esotericsoftware.kryonet.Connection
import gss.run.Booter

/**
 * This class connects GSS networking server sockets API with KryoNet networking library.
 */
class KryoNetServer extends ServerSocket {

    /**
     * Is the ID of the client a simple one?
     */
    private Boolean simpleID;
    /**
     * Provides the server to listen with.
     */
    Server server = new Server(16384, 2048, loginNode.getConfig().getKryo());
    /**
     * Provides a link function to the received function in this class.
     * Allows for dynamic function listening.
     * With clean IDE integration.
     */
    private def clojureReceived = Eval.x(this, "return new com.esotericsoftware.kryonet.Listener() {public void received(com.esotericsoftware.kryonet.Connection connection, Object object) {x.received(connection, object);}};");

    /**
     * We need to store who created us.
     * @param booter The booter who created us.
     */
    KryoNetServer(LoginNode loginNode) {
        super(loginNode);
    }

    /**
     * Get all clients currently connected in a SocketClient interface (wrapper).
     * @return A list of a wrapped client connection.
     */
    synchronized SocketClient[] getClients() {
        ArrayList<KryoNetClient> clients = new ArrayList<KryoNetClient>();
        server.connections.each {
            clients.add(new KryoNetClient(this, it, simpleID));
        }
        return clients.toArray();
    }

    /**
     * Gets a client currently connected in a SocketClient interface (wrapper).
     * @param id The unique ID of the client.
     * @return The SocketClient of the client if it exists otherwise null.
     */
    synchronized SocketClient getClient(String id) {
        getClients().each() {
            if (it.getID() == id)
                return it;
        }
        return null;
    }

    /**
     * Is the client currently connected?
     * @param id The unique ID of the client.
     * @return If the client is connected or not.
     */
    synchronized Boolean isClientConnected(String id) {
        getClients().each() {
            if (it.getID() == id)
                return true;
        }
        return false;
    }

    /**
     * Is the client ID simple?
     * @return If the client ID is simple.
     */
    Boolean isSimpleID() {
        return simpleID;
    }

    /**
     * Set the values of the server instance from configuration.
     * @param values The values to set to.
     */
    synchronized void setValues(HashMap<Object, Object> values) {
        int tcp;
        int udp;
        if (values.get("tcp") != null) {
            tcp = (Integer) values.get("tcp");
            if (values.get("udp") != null) {
                udp = (Integer) values.get("udp");
            }
        } else if (values.get("port") != null)
            tcp = (Integer) values.get("port");

        if (values.get("simpleID") != null)
            simpleID = values.get("simpleID");
        else if (values.get("simpleId") != null)
            simpleID = values.get("simpleId");
        else if (values.get("simpleid") != null)
            simpleID = values.get("simpleid");
        if (tcp != null && udp != null)
            server.bind(tcp, udp);
        else if (tcp != null)
            server.bind(tcp);
        else
        //we are invalid so lets remove it from list...
            loginNode.getServers().removeSocket(this);
    }

    /**
     * Start the server listening
     */
    synchronized void start() {
        server.addListener(clojureReceived);
        server.start();
    }

    /**
     * Stop the server listening
     */
    synchronized void stop() {
        server.stop();
        server.removeListener(clojureReceived);
    }

    /**
     * This gets called from KryoNet server function connector, and only from there.
     * @param connection The connection that sent it.
     * @param message The message object.
     */
    protected void received(Connection connection, Object message) {
        KryoNetClient kryoNetClient = new KryoNetClient(this, connection, simpleID);
        loginNode.eventManager.trigger(message, kryoNetClient, message);
    }
}
