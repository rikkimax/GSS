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

package gss.queueing

import javax.persistence.Id
import javax.persistence.Entity
import javax.persistence.GeneratedValue

@Entity
/**
 * The point of this class is to give an example of storing a string in a queue.
 */
class TestQueue {

    /**
     * When was this item added to the queue?
     */
    @Id
    @GeneratedValue
    protected Long created;
    /**
     * Has this item been read yet?
     */
    protected Boolean read = false;
    /**
     * The string to store.
     */
    String test;

    /**
     * Initiation method, sets created time.
     */
    TestQueue() {
       created = System.currentTimeMillis();
    }

    /**
     * When this item was added to the queue.
     * @return Time this item was added to the queue.
     */
    Long getCreated() {
        return this.created
    }

    /**
     * Set when this item was added to the queue.
     * @param time The time when this item was added to the queue.
     */
    void setCreated(Long time) {
        created = time;
    }

    /**
     * Get if this item been read yet?
     * @return Has this item been read yet?
     */
    Boolean getRead() {
        return read;
    }

    /**
     * Set if this item has been read yet.
     * @param read If this item has been read yet.
     */
    void setRead(Boolean read) {
        this.read = read;
    }

    /**
     * Set test string.
     * @param test The string to set to.
     */
    void setTest(String test) {
        this.test = test;
    }

    /**
     * Get the test string.
     * @return The test string.
     */
    String getTest() {
        return test;
    }
}
