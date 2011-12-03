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

import gss.login.socket.ServerConnection
import gss.run.LoginNode
import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.transport.socket.nio.NioSocketConnector
import org.apache.mina.core.session.IoSession
import org.apache.mina.core.future.ConnectFuture
import org.apache.mina.core.RuntimeIoException
import org.apache.commons.lang.StringUtils
import org.apache.mina.filter.codec.ProtocolCodecFilter
import java.nio.charset.Charset
import org.apache.mina.filter.codec.textline.TextLineCodecFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Provides a connection to a normal server.
 */
class PlainServerConnection extends ServerConnection {

    /**
     * Is the ID of the client a simple one?
     */
    protected String id;

    /**
     * The server socket.
     */
    protected NioSocketConnector socketConnector;

    /**
     * Handles data from client.
     */
    protected IoHandlerAdapter ioHandler;

    /**
     * Time out of the connection.
     */
    protected Long timeout = 30 * 1000L;

    protected ConcurrentHashMap<Object, Object> otherSettings = new ConcurrentHashMap<Object, Object>();

    /**
     * The session to the server.
     */
    IoSession session;

    PlainServerConnection(LoginNode loginNode) {
        super(loginNode);
    }
    /**
     * Sets the values of the socket.
     * @param values The values to set
     */
    synchronized void setValues(HashMap<Object, Object> values) {
        String tcp;
        int port;
        if (values.get("host") != null)
            tcp = (String) values.get("host");
        else if (values.get("ip") != null)
            tcp = (String) values.get("ip");
        if (values.get("port") != null)
            port = (Integer) values.get("port");
        this.server = tcp;
        this.port = port;
        Boolean simpleID = false;
        if (values.get("simpleID") != null)
            simpleID = values.get("simpleID");
        else if (values.get("simpleId") != null)
            simpleID = values.get("simpleId");
        else if (values.get("simpleid") != null)
            simpleID = values.get("simpleid");
        String ip = server.replace(".", "");
        String ipPort = port + "." + ip;
        if (!simpleID)
            id = ipPort;
        else {
            ipPort = StringUtils.reverse(ipPort);
            ipPort = "plain." + ipPort + ".message";
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
            id = out;
        }
        if (values.get("handler") != null) {
            def result = Eval.xy(this, loginNode, "return new ${values.get("handler")}(x, y);");
            if (result != null)
                if (result instanceof PlainServerConnectionHandler)
                    ioHandler = result;
        }
        otherSettings = new ConcurrentHashMap((Map) values);
        if (values.get("timeout") != null)
            timeout = new Long(values.get("timeout").toString());
        if (tcp == null || port == null)
            loginNode.getServersConnections().removeSocket(this);
    }

    /**
     * Start the connection
     */
    synchronized void start() {
        socketConnector = new NioSocketConnector();
        socketConnector.setConnectTimeoutMillis(timeout);
        socketConnector.getSessionConfig().setReadBufferSize(2048);
        socketConnector.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        if (ioHandler != null)
            socketConnector.setHandler(ioHandler);
        Thread.start {
            for (;;) {
                try {
                    ConnectFuture future = socketConnector.connect(new InetSocketAddress(server, port));
                    future.awaitUninterruptibly();
                    session = future.getSession();
                    break;
                } catch (RuntimeIoException e) {
                    e.printStackTrace();
                    Thread.sleep(5000);
                }
            }
            session?.getCloseFuture()?.awaitUninterruptibly();
            socketConnector.dispose();
        }
    }

    /**
     * Stop the connection
     */
    synchronized void stop() {
        if (session != null)
            session.close();
    }

    /**
     * Get the id of the server / connection.
     * @return The id of this connection.
     */
    synchronized String getId() {
        return id;
    }

    /**
     * Send a message to the server.
     * @param message The message to send.
     */
    synchronized void sendMessage(Object message) {
        if (session != null)
            if (message instanceof String)
                session.write(message + "\r\n");
            else
                session.write(message);
    }

    synchronized ConcurrentHashMap<Object, Object> getOtherSettings() {
        return otherSettings;
    }
}
