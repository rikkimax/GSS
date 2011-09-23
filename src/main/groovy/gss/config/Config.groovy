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

import yaml.handling.Yaml2

import org.apache.commons.vfs.FileObject
import org.apache.commons.vfs.VFS
import org.hibernate.cfg.AnnotationConfiguration
import java.util.logging.Logger
import com.esotericsoftware.kryo.Kryo

class Config {
    private FileObject directory;
    private ArrayList<gss.config.Server> servers;
    private AnnotationConfiguration annotationConfiguration;
    private Kryo kryo;

    void setDirectory(File directory) {
        if (directory.isDirectory() && directory.exists()) {
            this.directory = VFS.getManager().toFileObject(directory);
        }
    }

    FileObject getDirectory() {
        return directory;
    }

    void load() {
        //how are we storing it?
        //does it need to be dynamic?
        Yaml2 yaml = new Yaml2();
        servers = yaml.load(directory.resolveFile("servers.yml").getContent().getInputStream(), Server.class);
        Map common = yaml.load(directory.resolveFile("common.yml").getContent().getInputStream());
        //lets dyanamically set which classes will be used for the database
        List serializedClasses = common.get("SerializedClasses");
        setupHibernateFactory(serializedClasses);
    }

    ArrayList<gss.config.Server> getServers() {
        return servers;
    }

    void setupHibernateFactory(List serializedClasses) {
        kryo = new Kryo();
        annotationConfiguration = new AnnotationConfiguration();
        for (String s: serializedClasses) {
            Class clasz = Eval.me("return " + s + ".class;");
            kryo.register(clasz);
            Logger.getLogger(this.getClass().getName()).info("Adding class " + clasz + " to annotated list");
            annotationConfiguration.addAnnotatedClass(clasz);
        }
    }

    AnnotationConfiguration getAnnotationConfiguration() {
        return annotationConfiguration;
    }

    Kryo getKryo() {
        return kryo;
    }
}
