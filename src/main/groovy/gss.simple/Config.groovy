/*
 * Copyright © 2012, GSS team
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

package gss.simple

import yaml.handling.Yaml2

/**
 * Extends the config class from the gss.config package allowing for a simpler interaction.
 */
class Config extends gss.config.Config{
    protected String configFile;
    void load() {
        //how are we storing it?
        //does it need to be dynamic?
        Yaml2 yaml = new Yaml2();
        servers = new ArrayList<gss.config.Server>();
        common = yaml.load(directory.resolveFile(configFile).getContent().getInputStream());
        //lets dyanamically set which classes will be used for the database
        serializedClasses = common.get("SerializedClasses");
        common.get("queues", new HashMap()).each {key, value ->
            serializedClasses.add(key);
        }
        Map database = common.getClass("database");
        if (database!= null) {
            gss.config.Server server = new gss.config.Server();
            server.setType("database");
            server.setOther((HashMap<String, Object>)database);
        }
        setupHibernateFactory();
    }

    void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    String getConfigFile() {
        return configFile;
    }
}
