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

import gss.run.Booter
import org.apache.commons.vfs.FileObject
import java.util.concurrent.ConcurrentHashMap

/**
 * The point of this class is to allow scripted classes to load at boot time.
 */
class ScriptedBootLoader {
    /**
     * The booter that started this application.
     */
    protected Booter booter;

    /**
     * The directory we monitor for any files.
     */
    protected FileObject directory;

    /**
     * Given a FileObject a classes cache.
     */
    protected ConcurrentHashMap<FileObject, Class> eventsObjects = new ConcurrentHashMap<FileObject, Class>();
        /**
     * Constructor.
     * @param booter The booter that started this application.
     */
    ScriptedBootLoader(Booter booter) {
        this.booter = booter;
        this.directory = booter.config.getDirectory().resolveFile("boot_code");
        load();
    }

    /**
     * Constructor.
     * @param booter The booter that started this application.
     * @param directory The directory we have to get the files from.
     */
    ScriptedBootLoader(Booter booter, FileObject directory) {
        this.booter = booter;
        this.directory = directory;
        load();
    }

    /**
     * Load the classes into cache from file.
     */
    void load() {

    }
}
