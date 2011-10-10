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

package gss.config

import java.util.concurrent.ConcurrentHashMap
import org.apache.commons.beanutils.BeanUtils

/**
 *  This classes provides a means to store server information.
 */
class Server {
    /**
     *  The type of server it is.
     */
    public String type;
    /**
     * The host (IP/hostname) of the server.
     */
    public String host;
    /**
     * The port we used to connect to the server.
     */
    public int port;
    /**
     * The password to use if required.
     */
    public String pass;
    /**
     * Any other settings can be stored here.
     */
    public HashMap<String, Object> other;

    /**
     * Required for BeanUtils to work properly.
     */
    Server() {
    }

    /**
     * Get the servers type.
     * @return The servers type.
     */
    String getType() {
        return type;
    }

    /**
     * Set the type of the server.
     * (For BeanUtils)
     * @param serverType The type to use for the server.
     */
    void setType(String serverType) {
        this.type = serverType;
    }

    /**
     * Gets the hostname / IP of server.
     * @return The hostname / IP of server.
     */
    String getHost() {
        return host;
    }

    /**
     * Sets the hostname / IP of server.
     * (For BeanUtils)
     * @param host The hostname / IP of server to set to.
     */
    void setHost(String host) {
        this.host = host;
    }

    /**
     * Gets the port of server.
     * @return The port of the server.
     */
    int getPort() {
        return port;
    }

    /**
     * Sets the port of the server.
     * (For BeanUtils)
     * @param port The port to set to.
     */
    void setPort(int port) {
        this.port = port;
    }
    /**
     * Gets the password of the server.
     * @return The password of the server.
     */
    String getPass() {
        return pass;
    }
    /**
     * Sets the password of the server.
     * (For BeanUtils)
     * @param pass The password of the server to use.
     */
    void setPass(String pass) {
        this.pass = pass;
    }
    /**
     * Overides toString so we can get some real information when we output this class or convert to string.
     * @return The description of this server.
     */
    String toString() {
        return BeanUtils.describe(this);
    }

    /**
     * Get the other settings stored for the server.
     * @return A ConcurrentHashMap which stores the other settings.
     */
    HashMap<String, Object> getOther() {
        if (other == null) {
            other = new ConcurrentHashMap<String, Object>();
        }
        if (!other instanceof ConcurrentHashMap) {
            other = new ConcurrentHashMap<String, Object>(other);
        }
        return other;
    }

    /**
     * Sets the other settings stored for the server.
     * (For BeanUtils)
     * @param other The new settings to use.
     */
    void setOther(HashMap other) {
        this.other = other;
    }
}
