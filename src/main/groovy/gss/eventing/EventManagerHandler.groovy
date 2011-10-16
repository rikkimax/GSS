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

import gss.run.Booter
import org.apache.commons.vfs.FileObject
import org.apache.commons.vfs.impl.DefaultFileMonitor
import org.apache.commons.vfs.FileListener
import org.apache.commons.vfs.FileChangeEvent
import org.apache.commons.vfs.FileType
import java.util.logging.Logger

/**
 * This class is responsible for merging EventManager and ScriptedEventManager.
 */
class EventManagerHandler {
    /**
     * The ScriptedEventManager to use.
     */
    private ScriptedEventManager scriptedEventManager;
    /**
     * The EventManager.
     */
    private EventManager eventManager;
    /**
     * The booter that started this application.
     */
    private Booter booter;

    /**
     * A file monitor to check for changes in file system.
     */
    private DefaultFileMonitor fileMonitor;

    /**
     * A function for use with the file monitor.
     */
    private def fileMonitorFunction;

    /**
     * Initiation method of this class.
     * @param booter The booter that started this application.
     */
    EventManagerHandler(Booter booter) {
        this.booter = booter;
        scriptedEventManager = new ScriptedEventManager(booter);
        eventManager = new EventManager();
        /**
         * Listen for when file changes occur.
         */
        fileMonitorFunction = new FileListener() {
            /**
             * On file creation tell us.
             * @param fileChangeEvent The file change.
             */
            void fileCreated(FileChangeEvent fileChangeEvent) {
                if (fileChangeEvent.file.type == FileType.FILE)
                    if (fileChangeEvent.file.getName().extension == "groovy")
                        addEvent(UnknownEvent.class, fileChangeEvent.file);
            }

            /**
             * On file deletion tell us.
             * @param fileChangeEvent The file change.
             */
            void fileDeleted(FileChangeEvent fileChangeEvent) {
                if (fileChangeEvent.file.type == FileType.FILE)
                    if (fileChangeEvent.file.getName().extension == "groovy")
                        removeEvent(fileChangeEvent.getFile());
            }

            /**
             * On file change tell us.
             * @param fileChangeEvent The file change.
             */
            void fileChanged(FileChangeEvent fileChangeEvent) {
                if (fileChangeEvent.file.type == FileType.FILE)
                    if (fileChangeEvent.file.getName().extension == "groovy")
                        scriptedEventManager.reloadCache();
            }
        };
        fileMonitor = new DefaultFileMonitor(fileMonitorFunction);
        fileMonitor.setRecursive(true);
        fileMonitor.start();
    }

    /**
     * Gets the ScriptedEventManager we use.
     * @return The ScriptedEventManager we use.
     */
    ScriptedEventManager getScriptedEventManager() {
        return scriptedEventManager;
    }

    /**
     * Gets the EventManager we use.
     * @return The EventManager we use.
     */
    EventManager getEventManager() {
        return eventManager;
    }

    /**
     * Adds an event.
     * @param key The key to use.
     * @param event The event object to trigger.
     */
    void addEvent(String key, Event event) {
        eventManager.addEvent(key, event);
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
     * Add an event to the manager.
     * @param key The trigger key to use.
     * @param fileObject The event file to use.
     */
    void addEvent(Object key, FileObject fileObject) {
        addEvent(key.getClass(), fileObject);
    }

    /**
     * Add an event to the manager.
     * @param key The trigger key to use.
     * @param fileObject The event file to use.
     */
    void addEvent(Class key, FileObject fileObject) {
        addEvent(key.getCanonicalName(), fileObject);
    }

    /**
     * Add an event to the manager.
     * @param key The trigger key to use.
     * @param fileObject The event file to use.
     */
    void addEvent(String key, FileObject fileObject) {
        scriptedEventManager.addEvent(key, fileObject);
    }

    /**
     * Moves an event from the ScriptedEventManager control to EventManager.
     * @param key The key to remove from and to.
     * @param fileObject The FileObject representing the Event.
     */
    void moveEvent(String key, FileObject fileObject) {
        // We done want to use the existing version specifically if something happens...
        // like it gets deleted from ScriptedEventManager.
        // So by default we will make pass false as sameState value.
        moveEvent(key, fileObject, false);
    }

    /**
     * Moves an event from the ScriptedEventManager control to EventManager.
     * @param key The key to remove from and to.
     * @param fileObject The FileObject representing the Event.
     * @param sameState Are we going to use the same instance of the Event?
     */
    void moveEvent(String key, FileObject fileObject, Boolean sameState) {
        Event event = scriptedEventManager.getEvent(fileObject);
        if (event != null) {
            Event useEvent = event;
            // We done want to use the existing version specifically if something happens...
            // like it gets deleted from ScriptedEventManager.
            if (!sameState)
                useEvent = event.getClass().newInstance();
            scriptedEventManager.removeEvent(key, fileObject);
            eventManager.addEvent(key, useEvent);
        }
    }

    /**
     * Removes an event from all event managers.
     * @param fileObject The event FileObject representation to remove.
     */
    void removeEvent(FileObject fileObject) {
        Event event = scriptedEventManager.getEvent(fileObject);
        if (event != null)
            removeEvent(event);
        else
            scriptedEventManager.removeEvent(fileObject);
    }

    /**
     * Removes an event from all event managers.
     * @param event The event to remove.
     */
    void removeEvent(Event event) {
        FileObject fileObject = scriptedEventManager.getEventFile(event);
        if (fileObject != null)
            scriptedEventManager.removeEvent(fileObject);
        eventManager.removeEvent(event);
    }

    /**
     * Removes an Event by trigger.
     * @param key The event trigger to use.
     */
    void removeEvent(Object key) {
        removeEvent(key.getClass());
    }

    /**
     * Removes an Event by trigger.
     * @param key The event trigger to use.
     */
    void removeEvent(Class key) {
        removeEvent(key.getCanonicalName());
    }

    /**
     * Removes an Event by trigger.
     * @param key The event trigger to use.
     */
    void removeEvent(String key) {
        scriptedEventManager.removeEvent(key);
        eventManager.removeEvent(key);
    }

    /**
     * Removes an event from a trigger.
     * @param key The trigger key.
     * @param fileObject The FileObject representing the event.
     */
    void removeEvent(Object key, FileObject fileObject) {
        removeEvent(key.getClass(), fileObject);
    }

    /**
     * Removes an event from a trigger.
     * @param key The trigger key.
     * @param fileObject The FileObject representing the event.
     */
    void removeEvent(Class key, FileObject fileObject) {
        removeEvent(key.getCanonicalName(), fileObject);
    }

    /**
     * Removes an event from a trigger.
     * @param key The trigger key.
     * @param fileObject The FileObject representing the event.
     */
    void removeEvent(String key, FileObject fileObject) {
        Event event = scriptedEventManager.getEvent(fileObject);
        if (event != null)
            eventManager.removeEvent(key, event);
        scriptedEventManager.removeEvent(key, fileObject);
    }

    /**
     * Removes an event from a trigger.
     * @param key The trigger key.
     * @param event The event to remove.
     */
    void removeEvent(Object key, Event event) {
        removeEvent(key.getClass(), event);
    }

    /**
     * Removes an event from a trigger.
     * @param key The trigger key.
     * @param event The event to remove.
     */
    void removeEvent(Class key, Event event) {
        removeEvent(key.getCanonicalName(), event);
    }

    /**
     * Removes an event from a trigger.
     * @param key The trigger key.
     * @param event The event to remove.
     */
    void removeEvent(String key, Event event) {
        FileObject fileObject = scriptedEventManager.getEventFile(event);
        if (fileObject != null)
            scriptedEventManager.removeEvent(key, fileObject);
        eventManager.removeEvent(key, event);
    }

    /**
     * Gets a list of all events with there triggers.
     * @return A list of all events with there triggers.
     */
    Map<String, List<Event>> getEvents() {
        Map<String, List<Event>> ret = new HashMap<String, List<Event>>();
        eventManager.getEvents().each {key, events ->
            List<Event> eventList = new ArrayList<Event>();
            events.each {event ->
                if (!eventList.contains(event))
                    eventList.add(eventList);
            }
            ret.put(key, eventList);
        }
        scriptedEventManager.getEvents().each {key, events ->
            List<Event> eventList = ret.get(key);
            if (ret == null) {
                eventList = new ArrayList<Event>();
                ret.put(key, eventList);
            }
            events.each {event ->
                if (!eventList.contains(event))
                    eventList.add(eventList);
            }
        }
        return ret;
    }

    /**
     * Gets all events for a trigger.
     * @param key The trigger key.
     * @return A list of all events for a trigger.
     */
    List<Event> getEvents(Object key) {
        return getEvents(key.getClass());
    }

    /**
     * Gets all events for a trigger.
     * @param key The trigger key.
     * @return A list of all events for a trigger.
     */
    List<Event> getEvents(Class key) {
        return getEvents(key.getCanonicalName());
    }

    /**
     * Gets all events for a trigger.
     * @param key The trigger key.
     * @return A list of all events for a trigger.
     */
    List<Event> getEvents(String key) {
        List<Event> ret = eventManager.getEvents(key);
        if (ret == null)
            ret = new ArrayList<Event>();
        scriptedEventManager.getEvents(key).each {event ->
            if (!ret.contains(event))
                ret.add(event);
        }
    }

    /**
     * Triggers a list of events.
     * @param key The key to use as trigger.
     * @param context Who called this trigger.
     * @param pass Anything required to pass to the events.
     */
    synchronized void trigger(String key, Object context, Object... pass) {
        scriptedEventManager.trigger(key, context, pass);
    }

    /**
     * Triggers a list of events.
     * @param key The key to use as trigger.
     * @param pass Anything required to pass to the events.
     */
    void trigger(Class key, Object context, Object... pass) {
        trigger(key.getCanonicalName(), context, pass);
    }

    /**
     * Triggers a list of events.
     * @param key The key to use as trigger.
     * @param pass Anything required to pass to the events.
     */
    void trigger(Object key, Object context, Object... pass) {
        trigger(key.getClass(), context, pass);
    }

    /**
     * Does the trigger exist?
     * @param key The trigger key.
     * @return If the trigger key exists.
     */
    Boolean containsTrigger(Class key) {
        return containsTrigger(key.getCanonicalName());
    }

    /**
     * Does the trigger exist?
     * @param key The trigger key.
     * @return If the trigger key exists.
     */
    Boolean containsTrigger(Object key) {
        return containsTrigger(key.getClass());
    }

    /**
     * Does the trigger exist?
     * @param key The trigger key.
     * @return If the trigger key exists.
     */
    Boolean containsTrigger(String key) {
        Boolean ret = scriptedEventManager.containsTrigger(key);
        if (!ret)
            ret = eventManager.containsTrigger(key);
        return ret;
    }

    /**
     * Does the event exist?
     * @param event The event.
     * @return If the event exists.
     */
    Boolean containsEvent(Event event) {
        Boolean ret = scriptedEventManager.containsEvent(event);
        if (!ret)
            ret = eventManager.containsEvent(event);
        return ret;
    }

    /**
     * Does the event exist?
     * @param fileObject The event represented as a FileObject
     * @return If the event exists.
     */
    Boolean containsEvent(FileObject fileObject) {
        return scriptedEventManager.containsEventFile(fileObject);
    }

    /**
     * Clear all files being monitored.
     */
    void clearDirectoryMonitoring() {
        fileMonitor.stop();
        fileMonitor = new DefaultFileMonitor(fileMonitorFunction);
        fileMonitor.setRecursive(true);
        fileMonitor.start();
        Logger.getLogger(EventManagerHandler.getClass().getCanonicalName()).info("Cleared directory monitoring");
    }

    void addDirectoryMonitoring(FileObject fileObject) {
        if (fileObject.type == FileType.FOLDER) {
            fileMonitor.addFile(fileObject);
            Logger.getLogger(EventManagerHandler.getClass().getCanonicalName()).info("Adding new directory to monitoring ${fileObject}");
        }
    }

    void removeDirectoryMonitoring(FileObject fileObject) {
        fileMonitor.removeFile(fileObject);
        Logger.getLogger(EventManagerHandler.getClass().getCanonicalName()).info("Removed directory from monitoring ${fileObject}");
    }
}
