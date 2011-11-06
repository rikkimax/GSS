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
import gss.run.LoginNode
import org.apache.mina.transport.socket.nio.NioSocketAcceptor
import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.filter.logging.LoggingFilter
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.codec.textline.TextLineCodecFactory
import java.nio.charset.Charset
import org.apache.mina.core.session.IdleStatus
import java.util.logging.Logger

/**
 * The point of this class is to provide a simple NIO server.
 */
class PlainServer extends ServerSocket {
    /**
     * Is the ID of the client a simple one?
     */
    private Boolean simpleID;

    /**
     * The port that we are running on.
     */
    private int port;

    /**
     * The server socket.
     */
    private NioSocketAcceptor socketAcceptor;

    /**
     * Handles data from client.
     */
    private IoHandlerAdapter ioHandler;

    /**
     * Initiation constructor method.
     * @param loginNode The login node that created this.
     */
    PlainServer(LoginNode loginNode) {
        super(loginNode);
    }

    /**
     * Get all the clients.
     * @return All the clients.
     */
    @Override
    SocketClient[] getClients() {
        List<SocketClient> clients = new ArrayList<SocketClient>();
        socketAcceptor.getManagedSessions().values().each {
            clients.add(new PlainClient(this, it, simpleID));
        }
        return (SocketClient[]) clients.toArray();
    }

    /**
     * Get a client asociated with the ID.
     * @param id The clients ID.
     * @return The client.
     */
    @Override
    SocketClient getClient(String id) {
        getClients().each() {
            if (it.getID() == id)
                return it;
        }
        return null;
    }

    /**
     * Set the values for the server.
     * @param values The values for the server.
     */
    @Override
    void setValues(HashMap<Object, Object> values) {
        if (values.get("simpleID") != null)
            simpleID = values.get("simpleID");
        else if (values.get("simpleId") != null)
            simpleID = values.get("simpleId");
        else if (values.get("simpleid") != null)
            simpleID = values.get("simpleid");
        if (values.get("handler") != null) {
            def result = Eval.xy(this, loginNode, "return new " + values.get("handler") + "(x, y);");
            if (result != null)
                if (result instanceof PlainClientHandler)
                    ioHandler = result;
        }
        int tcp;
        if (values.get("tcp") != null)
            tcp = (Integer) values.get("tcp");
        else if (values.get("port") != null)
            tcp = (Integer) values.get("port");
        if (tcp != null)
            port = tcp;
        else
            loginNode.getServers().removeSocket(this);
    }

    /**
     * Start the server.
     */
    @Override
    void start() {
        socketAcceptor = new NioSocketAcceptor();
        socketAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
        socketAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        if (ioHandler != null)
            socketAcceptor.setHandler(ioHandler);
        socketAcceptor.getSessionConfig().setReadBufferSize(2048);
        socketAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        if (ioHandler != null)
            socketAcceptor.bind(new InetSocketAddress(port));
        else
            Logger.getLogger("PlainServer").warning("This plain socket server has no handler so won't start it.");
    }

    /**
     * Stop the server.
     */
    @Override
    void stop() {
        socketAcceptor.unbind();
        socketAcceptor = null;
    }

    /**
     * Are we going to be using a simple id for clients?
     * @return Are we?
     */
    Boolean getSimpleID() {
        return simpleID;
    }
}
