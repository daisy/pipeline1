package org.daisy.pipeline.gui.messages;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.daisy.dmfc.core.event.BusListener;
import org.daisy.dmfc.core.event.EventBus;
import org.daisy.dmfc.core.event.MessageEvent;

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

    public void init() {
        EventBus.getInstance().subscribe(this, MessageEvent.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.dmfc.core.event.BusListener#recieved(java.util.EventObject)
     */
    public void recieved(EventObject event) {
        if (event instanceof MessageEvent) {
            MessageEvent me = (MessageEvent) event;
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
}
