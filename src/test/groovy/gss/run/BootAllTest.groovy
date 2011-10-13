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

package gss.run;

import org.junit.Test
import static gss.run.BootAll.*

public class BootAllTest {

    @Test
    public void testHelp() {
        println("-------------------------------------------");
        println("testHelp - Will output the help contents of");
        println("    the BootAll CLI.");
        println("-------------------------------------------");
        main("--help");
    }

    @Test
    public void nothing() {
        println("-------------------------------------------");
        println("nothing - Nothing passed so show help");
        println("-------------------------------------------");
        main([] as String[]);
    }

    @Test
    public void nullNode() {
        println("-------------------------------------------");
        println("nullNode - No nodes should be loaded as one");
        println("    specified is invalid.");
        println("-------------------------------------------");
        main("test");
    }

    @Test
    public void loginKeepGoingNodes() {
        println("-------------------------------------------");
        println("loginKeepGoingNodes - Should start a login");
        println("    node and a keep going node.");
        println("-------------------------------------------");
        main("login", "keepgoing");
    }

    @Test
    public void databaseLoginKeepGoingNodes() {
        println("-------------------------------------------");
        println("databaseLoginKeepGoingNodes - should start");
        println("    and HDSQLDB database, login and a keep");
        println("    going node.");
        println("-------------------------------------------");
        main("--db=9001", "login", "keepgoing");
    }
}
