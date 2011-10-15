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

package gss.eventing

import java.util.concurrent.ConcurrentHashMap

/**
 * This class provides an event manager handling the Event class with triggers.
 */
class EventManager {
    /**
     * A list of Events to keys.
     */
    private static ConcurrentHashMap<String, ArrayList<Event>> events = new ConcurrentHashMap<String, ArrayList<Event>>();

    /**
     * Adds an event.
     * @param key The key to use.
     * @param event The event object to trigger.
     */
    void addEvent(String key, Event event) {
        if (!getEvents(key).contains(event)) {
            getEvents(key).add(event);
            event.create(key);
        }
    }

    /**
     * Adds an event.
     * @param key The key to use.
     * @param event The event object to trigger.
     */
    void addEvent(Class key, Event event) {
        addEvent(key.getCanonicalName(), event);
    }

    /**
     * Adds an event.
     * @param key The key to use.
     * @param event The event object to trigger.
     */
    void addEvent(Object key, Event event) {
        addEvent(key.getClass(), event);
    }

    /**
     * Removes all events for a trigger.
     * @param key The key to use.
     */
    void removeEvent(String key) {
        if (events.contains(key)) {
            events.get(key).each {
                it.destroy(key);
            }
        }
        events.remove(key);
    }

    /**
     * Removes all events for a trigger.
     * @param key The key to use.
     */
    void removeEvent(Class key) {
        removeEvent(key.getCanonicalName());
    }

    /**
     * Removes all events for a trigger.
     * @param key The key to use.
     */
    void removeEvent(Object key) {
        removeEvent(key.getClass());
    }

    /**
     * Removes an event from all triggers.
     * @param event The event to remove.
     */
    void removeEvent(Event event) {
        events.each {
            if (it.value.contains(event)) {
                event.destroy(it.key);
            }
            it.value.remove(event);
        }
    }

    /**
     * Removes and event from a trigger.
     * @param key The key to use as trigger.
     * @param event The event to remove.
     */
    void removeEvent(Class key, Event event) {
        removeEvent(key.getCanonicalName(), event);
    }
    /**
     * Removes and event from a trigger.
     * @param key The key to use as trigger.
     * @param event The event to remove.
     */
    void removeEvent(Object key, Event event) {
        removeEvent(key.getClass(), event);
    }
    /**
     * Removes and event from a trigger.
     * @param key The key to use as trigger.
     * @param event The event to remove.
     */
    void removeEvent(String key, Event event) {
        getEvents(key).remove(event);
    }

    /**
     * Gets all events and triggers.
     * @return All events with triggers.
     */
    ConcurrentHashMap<String, ArrayList<Event>> getEvents() {
        return events;
    }

    /**
     * Retruns a list of events for a trigger.
     * @param key The key to use as trigger.
     */
    void getEvents(Class key) {
        getEvents(key.getCanonicalName());
    }

    /**
     * Retruns a list of events for a trigger.
     * @param key The key to use as trigger.
     */
    void getEvents(Object key) {
        getEvents(key.getClass());
    }

    /**
     * Retruns a list of events for a trigger.
     * @param key The key to use as trigger.
     */
    ArrayList<Event> getEvents(String key) {
        if (events.contains(key)) {
            return events.get(key);
        } else {
            events.put(key, new ArrayList<Event>());
            return events.get(key);
        }
    }

    /**
     * Triggers a list of events.
     * @param key The key to use as trigger.
     * @param pass Anything required to pass to the events.
     */
    void trigger(Class key, Object... pass) {
        trigger(key.getCanonicalName(), pass);
    }

    /**
     * Triggers a list of events.
     * @param key The key to use as trigger.
     * @param pass Anything required to pass to the events.
     */
    void trigger(Object key, Object... pass) {
        trigger(key.getClass(), pass);
    }

    /**
     * Triggers a list of events.
     * @param key The key to use as trigger.
     * @param context Who called this trigger.
     * @param pass Anything required to pass to the events.
     */
    void trigger(String key, Object context, Object... pass) {
        for (Event event: getEvents(key)) {
            event.run(key, context, pass);
        }
    }
}
