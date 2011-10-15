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

import java.util.logging.Logger
import gss.run.Booter

/**
 * This class represents an unknown event or something that occured..
 * Nothing to do
 */
class UnknownEvent extends Event {

    /**
     * Run of an event code
     * @param trigger The trigger key that was used.
     * @param context Who triggered this trigger.
     * @param passed Any passed data provided by the trigger.
     */
    @Override
    void run(String trigger, Object context, Object[] passed) {
        Logger.getLogger(UnknownEvent.class.getName()).warning("Unknown event triggered...");
        Logger.getLogger(UnknownEvent.class.getName()).warning("Trigger: [${trigger}] Context: [${context}]");
        Logger.getLogger(UnknownEvent.class.getName()).warning("Passed values: [${passed}]");
    }

    /**
     * During creation or assimulation with a key to this event this gets called.
     * @param key The key being assimulated to.
     * @param booter The booter that started this application.
     */
    @Override
    void create(String key, Booter booter) {
    }

    /**
     * During destruction or deasimulation of an even to a key this method gets called.
     * @param key The key being deasimulated to.
     */
    @Override
    void destroy(String key) {
    }
}
