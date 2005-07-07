/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
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
package org.daisy.util.xml.stax;

import java.util.Iterator;

import javax.xml.stream.events.XMLEvent;

/**
 * A class for creating linked lists of <code>XMLEvent</code>s.
 * @author Linus Ericson
 */
/* package */ class LinkedEvent implements Iterator {
    
    private XMLEvent xe;
    private LinkedEvent next;
    
    /**
     * Creates a new item in a linked list of <code>XMLEvent</code>.
     * New items will be placed at the end of the list.
     * @param event the <code>XMLEvent</code> append.
     * @param tail the current last element (tail) of the list.
     */
    public LinkedEvent(XMLEvent event, LinkedEvent tail) {
        xe = event;
        next = null;
        if (tail != null) {
            tail.next = this;
        }
    }
    
    /**
     * Gets the XMLEvent for the current LinkedEvent.
     * @return a XMLEvent
     */
    public XMLEvent getXMLEvent() {
        return xe;
    }

    /**
     * Gets the next LinkedEvent.
     * @return the next LinkedEvent.
     */
    public LinkedEvent nextLinkedEvent() {
        return next;
    }
    
    /* ----- Methods from java.util.Iterator ----- */
    
    public boolean hasNext() {
        return next!=null;
    }

    public Object next() {
        return next;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
    
}
