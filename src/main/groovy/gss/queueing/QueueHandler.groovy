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

import gss.run.Booter
import gss.eventing.Event

/**
 * This class handles a list of all of the queues.
 */
class QueueHandler {

    List<QueueManager> queueManagers;

    /**
     * The booter that created this instance.
     */
    Booter booter;

    /**
     * Minimum amount of memory per slot to keep spare.
     */
    private long minMemory = 5;

    /**
     * The last queue that was executed.
     * Get the next queue in list...
     */
    private int lastQueueExecuted = -1;

    /**
     * The maximun age for an item.
     */
    private int maxAge = -1;

    /**
     * Initiation method.
     * @param booter The booter that created this instance.
     */
    QueueHandler(Booter booter) {
        this.booter = booter;
        queueManagers = new ArrayList<gss.queueing.QueueManager>();
        booter.config.getServers().each {
            if (it.type == "current")
                minMemory = (Integer) it.getOther().get("min_spare_mem_slot", 30);
        }
        minMemory = minMemory * 1024l * 1024l;
        Thread.start {
            while (booter.getKeepGoing()) {
                // Do we have spare slots?
                //      If so check for a new items on the queue that does
                if (Runtime.getRuntime().freeMemory() >= minMemory && queueManagers.size() > 0) {
                    if (queueManagers.size() > lastQueueExecuted + 1)
                        lastQueueExecuted++;
                    else
                        lastQueueExecuted = 0;
                    if (queueManagers.size() > 0) {
                        QueueManager queueManager = queueManagers.get(lastQueueExecuted);
                        //check for the last item added and event it.
                        Object queuedItem = queueManager.getLast();
                        if (queueManager.event != null)
                            queueManager.event.run(queuedItem.getClass().getCanonicalName(), queueManager, queuedItem);
                        else
                            booter.eventManager.trigger(queuedItem.getClass().getCanonicalName(), queueManager, queuedItem);
                        // if we are monitoring age of items get the oldest and if older delete it.
                        if (maxAge > 0) {
                            queuedItem = queueManager.getOldest();
                            Eval.xyz(queuedItem, maxAge, queueManager, "if (x.created >= y) z.delete(x);");
                        }
                    }
                }
                Runtime.getRuntime().gc();
                sleep(100);
            }
        }
    }
    /**
     * Add a classes QueueHandler to the list to monitor.
     * @param clasz The class to use for the QueueHandler.
     */
    void addQueue(Class clasz) {
        addQueue(clasz, null);
    }
    /**
     * Add a classes QueueHandler to the list to monitor.
     * @param clasz The class to use for the QueueHandler.
     * @param event Primary event to use.
     */
    synchronized void addQueue(Class clasz, Event event) {
        QueueManager queueManager = (QueueManager) Eval.xyz(clasz, booter, event, "return new gss.queueing.QueueManager<${clasz.getCanonicalName()}>(x, y, z);");
        if (queueManager != null) {
            Boolean dont = false;
            queueManagers.each {
                if (it.getClass() == clasz)
                    dont = true;
            }
            if (!dont)
                queueManagers.add(queueManager);
        }
    }

    /**
     * Remove a classes QueueHandler from the list.
     * @param clasz The class to use to get the QueueHandler to remove.
     */
    synchronized void removeQueue(Class clasz) {
        queueManagers.each {
            if (it.getClass() == clasz)
                queueManagers.remove(it);
        }
    }

    /**
     * Get the max age of an item can be.
     * @return The max age of an item can be.
     */
    int getMaximumAge() {
        return maxAge;
    }

    /**
     * Set the maximum age of an item can be or -1 for disabled.
     * @param age The maximum age of an item can be.
     */
    synchronized setMaximumAge(int age) {
        maxAge = age;
    }
}
