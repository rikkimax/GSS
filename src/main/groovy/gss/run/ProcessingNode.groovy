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

package gss.run

import gss.processing.queueing.QueueManager

/**
 *
 */
class ProcessingNode extends Booter {

    /**
     * The instance for singleton running.
     */
    static ProcessingNode instance;

    /**
     * The manager that handles that handles queued items for events.
     */
    QueueManager queueManager;

    /**
     * Start up initiation method but as a singleton..
     * @param args Arguments given to application
     */
    static void main(String... args) {
        if (instance == null) {
            instance = new ProcessingNode();
            instance.boot(args);
        }
    }

    /**
     * An overidden method to provide extra start up procedures.
     */
    @Override
    void startup() {
        super.startup();
        if (keepGoingStartUp)
            startUpQueueing();
    }

    /**
     * Starts up queueing listening.
     */
    void startUpQueueing() {
         queueManager = new QueueManager(this);
    }

    /**
     * Lets get the instance of this booter.
     * @return The instance of this booter.
     */
    ProcessingNode getInstance() {
        return instance;
    }

    /**
     * Get the type of the server
     * @return The type of the server
     */
    @Override
    String getType() {
        return "processing";
    }
}
