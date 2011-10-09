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

class EventManager {
    private static ConcurrentHashMap<String, ArrayList<Event>> events = new ConcurrentHashMap<String, ArrayList<Event>>();

    void addEvent(String key, Event event) {
        if (!getEvents(key).contains(event)) {
            getEvents(key).add(event);
            event.create(key);
        }
    }

    void addEvent(Class clasz, Event event) {
        addEvent(clasz.getCanonicalName(), event);
    }

    void addEvent(Object key, Event event) {
        addEvent(key.getClass(), event);
    }

    void removeEvent(String key) {
        if (events.contains(key)) {
            events.get(key).each {
                it.destroy(key);
            }
        }
        events.remove(key);
    }

    void removeEvent(Class clasz) {
        removeEvent(clasz.getCanonicalName());
    }

    void removeEvent(Object key) {
        removeEvent(key.getClass());
    }

    void removeEvent(Event event) {
        events.each {
            if (it.value.contains(event)) {
                event.destroy(it.key);
            }
            it.value.remove(event);
        }
    }

    void removeEvent(Class clasz, Event event) {
        removeEvent(clasz.getCanonicalName(), event);
    }

    void removeEvent(Object key, Event event) {
        removeEvent(key.getClass(), event);
    }

    void removeEvent(String key, Event event) {
        getEvents(key).remove(event);
    }

    ConcurrentHashMap<String, ArrayList<Event>> getEvents() {
        return events;
    }

    void getEvents(Class clasz) {
        getEvents(clasz.getCanonicalName());
    }

    void getEvents(Object key) {
        getEvents(key.getClass());
    }

    ArrayList<Event> getEvents(String key) {
        if (events.contains(key)) {
            return events.get(key);
        } else {
            events.put(key, new ArrayList<Event>());
            return events.get(key);
        }
    }

    void trigger(Class clasz, Object... pass) {
        trigger(clasz.getCanonicalName(), pass);
    }

    void trigger(Object key, Object... pass) {
        trigger(key.getClass(), pass);
    }

    void trigger(String key, Object... pass) {
        for (Event event: getEvents(key)) {
            event.run(key, pass);
        }
    }
}
