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

package gss.queueing

import gss.eventing.Event
import gss.run.Booter
import org.hibernate.Criteria
import org.hibernate.Session
import org.hibernate.Transaction
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions

/**
 * The point of this class is to provide an easy manager of queues
 */
class QueueManager<T> {
    /**
     * The booter that created this instance.
     */
    private Booter booter;

    /**
     * The class we are working with.
     */
    private Class clasz;

    /**
     * Primary event (overides event system).
     */
    public Event event;

    /**
     * Initiation method.
     * @param booter The booter that created this instance.
     */
    QueueManager(Class clasz, Booter booter, Event event) {
        this.booter = booter;
        this.event = event;
        this.clasz = clasz;
        if (event != null)
            event.create(clasz.getCanonicalName(), booter);
        else
            booter.getEventManager().trigger(T.getClass().getCanonicalName(), booter);
    }

    /**
     * Get the last item in the queue.
     * @return The last item in the queue.
     */
    synchronized T getLast() {
        Session session = booter.getSession();
        if (session != null) {
            session.beginTransaction();
            Criteria criteria = session.createCriteria(clasz);
            criteria.add(Restrictions.eq("read", false));
            criteria.addOrder(Order.desc("created"));
            criteria.setMaxResults(1);
            List ret = criteria.list();
            session.close();
            if (ret.size() > 0) {
                mark((T) ret.get(0));
                return (T) ret.get(0);
            }
        }
        return null;
    }

    /**
     * Save an item.
     * @param object The item to save.
     */
    synchronized void save(T object) {
        Session session = booter.getSession();
        if (session != null) {
            Transaction transaction = session.beginTransaction();
            session.save(object);
            transaction.commit();
        }
    }

    /**
     * Delete an item.
     * @param object The item to delete.
     */
    synchronized void delete(T object) {
        Session session = booter.getSession();
        if (session != null) {
            Transaction transaction = session.beginTransaction();
            session.delete(object);
            transaction.commit();
        }
    }

    /**
     * Mark an item as read.
     * @param object The item to mark.
     */
    synchronized void mark(T object) {
        Eval.x(object, "x.setRead(true);");
        save(object);
    }

    /**
     * Unmark an item as read.
     * @param object An item to unmark.
     */
    synchronized void unmark(T object) {
        Eval.x(object, "x.setRead(false);");
        save(object);
    }

    /**
     * The class we are using.
     * @return Class we are using.
     */
    synchronized Class<T> getClassUsed() {
        return clasz;
    }
}
