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



package yaml.handling;


import org.apache.commons.beanutils.BeanUtils
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.composer.Composer
import org.yaml.snakeyaml.parser.ParserImpl
import org.yaml.snakeyaml.reader.StreamReader
import org.yaml.snakeyaml.reader.UnicodeReader

/**
 * This class merges BeanUtils and SnakeyYaml to provide a filled class object with values also being able to be classes.
 * @author rikki
 */
public class Yaml2 extends Yaml {

    /**
     * Parse the only YAML document in a String and produce the corresponding
     * Java object. (Because the encoding in known BOM is not respected.)
     *
     * @param yaml YAML data to load from (BOM must not be present)
     * @param clazz The class to use to export to
     * @return parsed object
     */
    Object load(String yaml, Class clazz) {
        return load(loadFromReader(new StreamReader(yaml)), clazz);
    }

    /**
     * Parse the only YAML document in a stream and produce the corresponding
     * Java object.
     *
     * @param io data to load from (BOM is respected and removed)
     * @param clazz The class to use to export to
     * @return parsed object
     */
    Object load(InputStream io, Class clazz) {
        return load(loadFromReader(new StreamReader(new UnicodeReader(io))), clazz);
    }

    /**
     * Parse the only YAML document in a stream and produce the corresponding
     * Java object.
     *
     * @param io data to load from (BOM must not be present)
     * @param clazz The class to use to export to
     * @return parsed object
     */
    Object load(Reader io, Class clazz) {
        return load(loadFromReader(new StreamReader(io)), clazz);
    }

    /**
     * simplifying the whole loading part
     *
     * @param o the object loaded
     * @param clazz The class to use to export to
     * @return parsed object
     */
    Object load(Object o, Class clazz) {
        Object ret;
        if (o instanceof List) {
            ArrayList<Object> ret2 = new ArrayList<Object>();
            for (Object o2: o) {
                if (o2 instanceof Map) {
                    Object add = clazz.newInstance();
                    BeanUtils.populate(add, o2);
                    ret2.add(add)
                }
            }
            ret = ret2;
        } else if (o instanceof Map) {
            ret = clazz.newInstance();
            BeanUtils.populate(ret, o);
        }
        return ret;
    }

    /**
     * This method was just a copy paste as it was private and couldn't use it..
     */
    Object loadFromReader(StreamReader sreader) {
        Composer composer = new Composer(new ParserImpl(sreader), resolver);
        constructor.setComposer(composer);
        return constructor.getSingleData(Object.class);
    }
}
