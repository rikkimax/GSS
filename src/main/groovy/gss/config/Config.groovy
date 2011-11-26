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
//import org.hibernate.ogm.cfg.OgmConfiguration

/**
 * The purpose of this class is to provide a means in which configuration of the GSS framework is defined and stored.
 */
class Config {

    /**
     *  The working directory where the configuration files are present.
     */
    private FileObject directory;

    /**
     * A list of servers to be used by this instance, created from configuration files.
     */
    private ArrayList<gss.config.Server> servers;

    /**
     * Common data shared between server.
     */
    private Map common;

    /**
     * Annotation configuration for Hibernate for working with classes defined in configuration file.
     */
    private AnnotationConfiguration annotationConfiguration;

    /**
     * Configuration for Hibernate OGM if used. For working with classes defined in configuration file.
     */
    //private OgmConfiguration ogmConfiguration;

    /**
     * Kryo serializer to be used with KryoNet server connector.
     * Classes are loaded from configuration file and should be identifical to annotationConfiguration.
     */
    private Kryo kryo;

    /**
     * A list of serialized classes to use with e.g. Hibernate, Kryo.
     */
    private List<String> serializedClasses;

    /**
     * Set the current working directory of the configuration files
     * @param directory The directory to use to set to.
     */
    void setDirectory(File directory) {
        if (directory.isDirectory() && directory.exists()) {
            this.directory = VFS.getManager().toFileObject(directory);
        }
    }

    /**
     * Get the current working directory for configuration files.
     * @return The FileObject representing the current working configuration directory.
     */
    FileObject getDirectory() {
        return directory;
    }

    /**
     * Load settings from configuration files
     */
    void load() {
        //how are we storing it?
        //does it need to be dynamic?
        Yaml2 yaml = new Yaml2();
        servers = yaml.load(directory.resolveFile("servers.yml").getContent().getInputStream(), Server.class);
        common = yaml.load(directory.resolveFile("common.yml").getContent().getInputStream());
        //lets dyanamically set which classes will be used for the database
        serializedClasses = common.get("SerializedClasses");
        common.get("queues", new HashMap()).each {key, value ->
            serializedClasses.add(key);
        }
        setupHibernateFactory();
    }

    /**
     * Returns a list of servers currently configured to work with this instance from configuration file.
     * @return The list of servers from configuration files.
     */
    final ArrayList<gss.config.Server> getServers() {
        return servers;
    }

    /**
     * Create and configures AnnotationConfiguration for use with Hibernate as well as registers the serialized classes
     * to Kryo and AnnotationConfiguration.
     */
    void setupHibernateFactory() {
        kryo = new Kryo();
        annotationConfiguration = new AnnotationConfiguration();
        //ogmConfiguration = new OgmConfiguration();
        serializedClasses.each {
            Class clasz = Eval.me("return ${it}.class;");
            if (clasz != null) {
                kryo.register(clasz);
                Logger.getLogger(this.getClass().getName()).info("Adding class " + clasz + " to annotated list");
                annotationConfiguration.addAnnotatedClass(clasz);
                ogmConfiguration.addAnnotatedClass(clasz);
            }
        }
    }

    /**
     * Gets the AnnotationConfiguration to be used with Hibernate.
     * @return The AnnotationConfiguration to be used.
     */
    AnnotationConfiguration getAnnotationConfiguration() {
        return annotationConfiguration;
    }

    /**
     * Gets the AnnotationConfiguration to be used with Hibernate.
     * @return The AnnotationConfiguration to be used.
     */
    /*OgmConfiguration getOgmConfiguration() {
        return ogmConfiguration;
    }*/

    /**
     * Gets the Kryo instance to be used with KryoNet.
     * @return The Kryo instance to be used.
     */
    Kryo getKryo() {
        return kryo;
    }

    /**
     * Get a list of  serialized classes to use with e.g. Hibernate, Kryo.
     * @return A list of serialized classes to use with e.g. Hibernate, Kryo.
     */
    List<String> getSerializedClasses() {
        return serializedClasses;
    }

    /**
     * Common data shared between all server nodes.
     * @return Common data shared between all server nodes in map format.
     */
    Map getCommon() {
        return common;
    }
}
