/*
 * Copyright © 2012, GSS team
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

package gss.node.management

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Nodes implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    /**
     * When was this item added to the queue?
     */
    private Long created;

    /**
     * Has this item been read yet?
     */
    private Boolean read = false;

    /**
     * The hostname of the node.
     */
    String host;

    /**
     * List of ips this node has access to (local).
     */
    private List<String> ipAddresses;

    Nodes() {
    }

    Nodes(String host) {
        this.host = host;
        ipAddresses = new ArrayList<String>();
    }
    /**
     * When this item was added to the queue.
     * @return Time this item was added to the queue.
     */
    synchronized Long getCreated() {
        return this.created
    }

    /**
     * Set when this item was added to the queue.
     * @param time The time when this item was added to the queue.
     */
    synchronized void setCreated(Long time) {
        created = time;
    }

    /**
     * Get if this item been read yet?
     * @return Has this item been read yet?
     */
    synchronized Boolean getRead() {
        return read;
    }

    /**
     * Set if this item has been read yet.
     * @param read If this item has been read yet.
     */
    synchronized void setRead(Boolean read) {
        this.read = read;
    }

    /**
     * Get the id of the node.
     * @return The id of the node.
     */
    synchronized Long getId() {
        return id;
    }

    /**
     * Set the id of the node.
     * @param id The id to set to.
     */
    synchronized void setId(Long id) {
        this.id = id;
    }

    /**
     * IP addresses the node has access to.
     * @return The IP addresses a node has access to.
     */
    synchronized List<String>getIpAddresses(){
        return ipAddresses
    }

    /**
     * Set the ip addresses a node has.
     * @param ipAddresses The IP addresses a node has.
     */
    synchronized void setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    /**
     * Add an ip address which the node has access to.
     * @param ip The ip address to add.
     */
    synchronized void addIp(String ip) {
        if (!ipAddresses.contains(ip))
            ipAddresses.add(ip);
    }

    /**
     * Remove the specified ip.
     * @param ip The ip to remove.
     */
    synchronized void removeIp(String ip){
        ipAddresses.remove(ip);
    }

    /**
     * Clears all ip addresses associated with this address.
     */
    synchronized void clearIps() {
        ipAddresses.clear();
    }

    /**
     * Does the node have access to this ip address?
     * @param ip The ip address to compare against.
     * @return
     */
    synchronized Boolean containsIp(String ip) {
        return ipAddresses.contains(ip);
    }
}
