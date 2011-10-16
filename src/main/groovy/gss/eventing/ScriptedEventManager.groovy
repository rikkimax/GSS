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
import org.apache.commons.vfs.FileObject
import gss.run.Booter
import org.apache.commons.vfs.FileType
import org.apache.commons.vfs.impl.DefaultFileMonitor
import org.apache.commons.vfs.FileListener
import org.apache.commons.vfs.FileChangeEvent
import java.util.logging.Logger

/**
 * The point of this class is to provide a dynamic scripted events manager.
 */
class ScriptedEventManager {

    /**
     * A list of FileObjects of events to keys.
     */
    private ConcurrentHashMap<String, ArrayList<FileObject>> eventsFiles = new ConcurrentHashMap<String, ArrayList<FileObject>>();

    /**
     * A given Event value to a FileObject (cache).
     */
    private ConcurrentHashMap<FileObject, Event> eventsObjects = new ConcurrentHashMap<FileObject, Event>();

    /**
     * We really should also pass the booter to the event when its created
     */
    private Booter booter;

    /**
     * The classloader responsible for converting scripts to classes.
     */
    private GroovyClassLoader gcl = new GroovyClassLoader();

    /**
     * A file monitor to check for changes in file system.
     */
    private DefaultFileMonitor fileMonitor;

    /**
     * Lets create a new instance of this event manager.
     * @param booter The booter that started this program.
     */
    ScriptedEventManager(Booter booter) {
        this.booter = booter;

        /**
         * Listen for when file changes occur.
         */
        fileMonitor = new DefaultFileMonitor(new FileListener() {
            /**
             * On file creation tell us.
             * @param fileChangeEvent The file change.
             */
            void fileCreated(FileChangeEvent fileChangeEvent) {
            }

            /**
             * On file deletion tell us.
             * @param fileChangeEvent The file change.
             */
            void fileDeleted(FileChangeEvent fileChangeEvent) {
                reloadCache();
            }

            /**
             * On file change tell us.
             * @param fileChangeEvent The file change.
             */
            void fileChanged(FileChangeEvent fileChangeEvent) {
                reloadCache();
            }
        });
        fileMonitor.start();

        /**
         * Check to see it we have any rubbish floating around...
         */
        Thread.start {
            while (true) {
                checkObsoleteCache();
                sleep 1000;
            }
        }
    }

    /**
     * Retruns a list of events for a trigger.
     * @param key The key to use as trigger.
     */
    ArrayList<Event> getEvents(String key) {
        List<Event> ret = new ArrayList<Event>();
        List<FileObject> fileObjects = getFiles(key);
        fileObjects?.each {
            Object event = eventsObjects.get(it);
            if (event != null)
                ret.add(event);
        }
        if (fileObjects == null) {
            eventsFiles.put(key, new ArrayList<FileObject>());
        }
        return ret;
    }

    /**
     * Retruns a list of FileObject events for a trigger.
     * @param key The key to use as trigger.
     */
    ArrayList<FileObject> getFiles(String key) {
        List<Event> ret = eventsFiles.get(key);
        if (ret == null) {
            ret = new ArrayList<Event>();
            eventsFiles.put(key, ret);
        }
        return ret;
    }

    /**
     * Gets the event associated with the FileObject.
     * @param fileObject The FileObject to get the association of.
     * @return The event associated with the FileObject.
     */
    Event getEvent(FileObject fileObject) {
        return eventsObjects.get(fileObject);
    }

    /**
     * Gets the FileObject associated with the Event.
     * @param event The FileObject to get the association of.
     * @return The FileObject associated with the Event.
     */
    FileObject getEventFile(Event event) {
        eventsObjects.each {fileObject, cache ->
            if (cache.getClass().getCanonicalName() == event.getClass().getCanonicalName())
                return fileObject;
        }
        return null;
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
        if (fileObject.exists())
            if (fileObject.type == FileType.FILE) {
                // So this is a file YAY!
                // Lets add this file to be checked for...
                // No idea if its actually an event or not lol
                getFiles(key).add(fileObject);
                fileMonitor.addFile(fileObject);
                loadClassCache(fileObject);
            }
    }

    /**
     * Removes all events for a trigger.
     * @param key The key to use.
     */
    void removeEvent(String key) {
        eventsFiles.remove(key);
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
     * Removes an event from a trigger.
     * @param key The key to use as trigger.
     * @param event The event to remove.
     */
    void removeEvent(Class key, FileObject fileObject) {
        removeEvent(key.getCanonicalName(), fileObject);
    }

    /**
     * Removes an event from a trigger.
     * @param key The key to use as trigger.
     * @param event The event to remove.
     */
    void removeEvent(Object key, FileObject fileObject) {
        removeEvent(key.getClass(), fileObject);
    }

    /**
     * Removes an event from a trigger.
     * @param key The key to use as trigger.
     * @param event The event to remove.
     */
    void removeEvent(String key, FileObject fileObject) {
        getFiles(key).remove(fileObject);
    }

    /**
     * Removes an Event.
     * @param fileObject The FileObject representing the Event.
     */
    void removeEvent(FileObject fileObject) {
        eventsFiles.each {key, events ->
            events.each {eventFile ->
                if (eventFile == fileObject)
                    removeEvent(key, fileObject);
            }
        }
    }

    /**
     * Gets the events as files associated with there triggers.
     * @return The events as files associated with there triggers.
     */
    ConcurrentHashMap<String, ArrayList<FileObject>> getEventsFiles() {
        return eventsFiles;
    }

    /**
     * Gets Events objects with there file associateion.
     * @return
     */
    ConcurrentHashMap<FileObject, Event> getEventsObjects() {
        return eventsObjects;
    }

    /**
     * Reloads the WHOLE cache and clears out all unused ones.
     */
    private void reloadCache() {
        gcl.clearCache();
        eventsFiles.each {key, eventsList ->
            eventsList.each {eventFile ->
                Object returned = gcl.parseClass(eventFile.content.inputStream);
                boolean failed = true;
                if (returned != null)
                    if (returned instanceof Class) {
                        Object returnedObject = returned.newInstance();
                        if (returnedObject instanceof Event) {
                            eventsObjects.put(eventFile, returnedObject);
                            returnedObject.create(key, booter);
                            failed = false;
                            Logger.getLogger(ScriptedEventManager.getClass().getName()).info("Loaded " + returnedObject.getClass().getName() + " into cache");
                        }
                    }
                if (failed)
                    Logger.getLogger(ScriptedEventManager.getClass().getName()).info("Failed to load " + eventFile + " into cache");
            }
        }
        checkObsoleteCache();
    }

    /**
     * Checks for any obsolete cache and removes it.
     * Reloads any cache if its null.
     */
    public synchronized void checkObsoleteCache() {
        // Remove out all the unneeded cache...
        eventsObjects.each {fileObject, cache ->
            if (containsEventFile(fileObject)) {
                cache.destroy("");
                eventsObjects.remove(fileObject);
            }
        }
        // Trys to load some cache for a class...
        eventsFiles.each {key, eventsList ->
            eventsList.each {eventFile ->
                if (eventsObjects.get(eventFile) == null)
                    loadClassCache(eventFile);
            }
        }
    }
    /**
     * Load a class into cache.
     * @param fileObject The file to try and load.
     */
    private void loadClassCache(FileObject fileObject) {
        eventsFiles.each {key, eventsList ->
            eventsList.each {eventFile ->
                if (eventFile.getURL() == fileObject.getURL()) {
                    Object returned = gcl.parseClass(eventFile.content.inputStream);
                    boolean failed = true;
                    if (returned != null)
                        if (returned instanceof Class) {
                            Object returnedObject = returned.newInstance();
                            if (returnedObject instanceof Event) {
                                eventsObjects.put(eventFile, returnedObject);
                                returnedObject.create(key, booter);
                                failed = false;
                                Logger.getLogger(ScriptedEventManager.getClass().getName()).info("Loaded " + returnedObject.getClass().getName() + " into cache");
                            }
                        }
                    if (failed)
                        Logger.getLogger(ScriptedEventManager.getClass().getName()).info("Failed to load " + fileObject + " into cache");
                }
            }
        }
    }

    /**
     * Triggers a list of events.
     * @param key The key to use as trigger.
     * @param context Who called this trigger.
     * @param pass Anything required to pass to the events.
     */
    synchronized void trigger(String key, Object context, Object... pass) {
        List<Event> events = getEvents(key);
        events.each {event ->
            event.run(key, context, pass);
        }
        if (events.size() <= 0 && events.contains(UnknownEvent.class))
            trigger(UnknownEvent.class, context, pass);
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
        return eventsFiles.containsKey(key);
    }

    /**
     * Does the event FileObject exist?
     * @param event The event.
     * @return If the event exists.
     */
    Boolean containsEventFile(FileObject fileObject) {
        return eventsFiles.containsValue(fileObject);
    }

    /**
     * Does the event exist?
     * @param event The event.
     * @return If the event exists.
     */
    Boolean containsEvent(Event event) {
        eventsObjects.each {key, cache ->
            if (cache == event)
                return true;
        }
        return false;
    }

    /**
     * Does the trigger have the event?
     * @param key The trigger.
     * @param event The event.
     * @return If the event exists.
     */
    Boolean containsEvent(String key, Event event) {
        eventsObjects.each {eventFile, cache ->
            if (cache == event)
                eventsFiles.each {key3, eventFile2 ->
                    if (eventFile2 == eventFile && key == key3)
                        return true;
                }
        }
        return false;
    }
}
