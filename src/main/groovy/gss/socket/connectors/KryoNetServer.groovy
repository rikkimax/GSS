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

package gss.socket.connectors

import gss.socket.ServerSocket
import gss.socket.SocketClient
import com.esotericsoftware.kryonet.Server
import gss.run.Booter
import com.esotericsoftware.kryonet.Connection

class KryoNetServer extends ServerSocket {
    Server server = new Server(16384, 2048, Booter.getConfig().getKryo());
    private def clojureReceived = Eval.x(this, "return new com.esotericsoftware.kryonet.Listener() {public void received(com.esotericsoftware.kryonet.Connection connection, Object object) {x.received(connection, object);}};");

    synchronized SocketClient[] getClients() {
        ArrayList<KryoNetClient> clients = new ArrayList<KryoNetClient>();
        for (Connection connection: server.connections) {
            clients.add(new KryoNetClient(this, connection));
        }
        return clients.toArray();
    }

    synchronized SocketClient getClient(String id) {
        KryoNetClient[] clients = getClients();
        for (KryoNetClient client: clients) {
            if (client.getID() == id)
                return client;
        }
    }

    synchronized void setValues(HashMap<Object, Object> values) {
        int tcp;
        int udp;
        if (values.get("tcp") != null) {
            tcp = (Integer) values.get("tcp");
            if (values.get("udp") != null) {
                udp = (Integer) values.get("udp");
            }
        } else if (values.get("port") != null) {
            tcp = (Integer) values.get("port");
        }
        if (tcp != null && udp != null)
            server.bind(tcp, udp);
        else if (tcp != null && udp == null)
            server.bind(tcp);
        else
        //we are invalid so lets remove it from list...
            Booter.getServers().removeSocket(this);
    }

    synchronized void start() {
        server.addListener(clojureReceived);
    }

    synchronized void stop() {
        server.stop();
        server.removeListener(clojureReceived);
    }

    protected void received(Connection connection, Object message) {
        KryoNetClient kryoNetClient = new KryoNetClient(this, connection);

    }
}
