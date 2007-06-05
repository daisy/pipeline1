/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.pipeline.gui.model;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.daisy.pipeline.core.event.BusListener;
import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.gui.jobs.runner.JobRunnerJob;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Romain Deltour
 * 
 */
public class MessageManager implements BusListener {
    private static MessageManager _default = new MessageManager();
    private Queue<MessageEvent> messages;
    private int capacity;
    private List<IMessageManagerListener> listeners;

    public MessageManager() {
        messages = new LinkedList<MessageEvent>();
        capacity = 100;// TODO retrieve capacity from prefs
        listeners = new ArrayList<IMessageManagerListener>();
    }

    public static MessageManager getDefault() {
        return _default;
    }

    public void dipose() {
        EventBus.getInstance().unsubscribe(this, MessageEvent.class);
    }

    public void init() {
        EventBus.getInstance().subscribe(this, MessageEvent.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.core.event.BusListener#received(java.util.EventObject)
     */
    public void received(EventObject event) {
        if (event instanceof MessageEvent) {
            MessageEvent me = (MessageEvent) event;
            // TODO shouldn't rely on threading here
            Job thread = JobRunnerJob.getJobManager().currentJob();
            if (thread != null && thread instanceof JobRunnerJob) {
                JobInfo jobInfo = ((JobRunnerJob) thread).getJobInfo();
                if (jobInfo != null) {
                    me = new JobMessageEvent(me, jobInfo);
                }
            }
            if (messages.size() == capacity) {
                messages.poll();
            }
            messages.offer(me);
            fireMessageAdded(me);
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity < 0) {
            return;
        }
        this.capacity = capacity;
    }

    public Queue<MessageEvent> getMessages() {
        return messages;
    }

    public void addMessageManagerListener(IMessageManagerListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeMessageManagerListener(IMessageManagerListener listener) {
        listeners.remove(listener);
    }

    private void fireMessageAdded(MessageEvent message) {
        for (IMessageManagerListener listener : listeners) {
            listener.messageAdded(message);
        }
    }

    public void clear() {
        messages.clear();
    }
}
