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

import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.core.session.IoSession
import gss.run.LoginNode

/**
 * The point of this class is to handle
 */
abstract class PlainClientHandler extends IoHandlerAdapter {
    /**
     * The login node that started all this.
     */
    LoginNode loginNode;

    /**
     * The socket server that needs this handler.
     */
    PlainServer server;

    /**
     * Constructor method.
     * @param server The socket server that needs this handler.
     * @param loginNode The LoginNode that started this.
     */
    PlainClientHandler(PlainServer server, LoginNode loginNode) {
        this.server = server;
        this.loginNode = loginNode;
    }

    /**
     * When a message is received do run this.
     * @param session The client.
     * @param message The message received.
     */
    @Override
    synchronized void messageReceived(IoSession session, Object message) {
        Object messageToEvent = message;
        Boolean eventThis = false;
        PlainClient plainClient = new PlainClient(server, session, server.getSimpleID());
        messageToEvent = messageProcess(plainClient, message);
        if (messageToEvent == null)
            eventThis = false;
        if (eventThis)
            loginNode.eventManager.trigger(message, plainClient, messageToEvent);
    }

    abstract Object messageProcess(PlainClient plainClient, Object message);
}
