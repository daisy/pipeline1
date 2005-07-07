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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * A XMLEventReader with bookmark functionality. When any bookmarks exist, all
 * events from the <code>XMLEventReader</code> are saved in a linked list. When
 * the <code>gotoBookmark(String)</code> or 
 * <code>gotoAndRemoveBookmark(String)</code> function is called, the
 * <code>BookmarkedXMLEventReader</code> will replay the saved events instead
 * of reading new ones from the stream. 
 * @author Linus Ericson
 */
public class BookmarkedXMLEventReader implements XMLEventReader {
   
    private XMLEventReader xer;
    
    private Map bookmarks = new HashMap();    
    private XMLEvent lastEvent = null;
    private LinkedEvent current = null;
    
    /* ---------- Constructors ---------- */
    
    /**
     * Creates a new <code>BookmarkedXMLEventReader</code> from a 
     * <code>XMLEventReader</code>.
     */
    public BookmarkedXMLEventReader(XMLEventReader eventReader) {
        xer = eventReader;
    }
    
    /* ---------- New methods ---------- */
    
    /**
     * Sets a new bookmark at the current position in the event stream.
     * @param name the name of the bookmark.
     */
    public void setBookmark(String name) {
        if (current == null) {            
            current = new LinkedEvent(lastEvent, null);
        }
        bookmarks.put(name, current);
    }
    
    /**
     * Jumps to the specified bookmark in the event stream.
     * @param name the name of the bookmark.
     */
    public void gotoBookmark(String name) {
        LinkedEvent le = (LinkedEvent)bookmarks.get(name);
        if (le == null) {
            throw new IllegalArgumentException("Unknown bookmark: " + name);
        }
        current = le;
    }
    
    /**
     * Jumps to and then removes the specified bookmark from the event stream.
     * @param name the name of the bookmark
     */
    public void gotoAndRemoveBookmark(String name) {
        gotoBookmark(name);
        removeBookmark(name);
    }
    
    /**
     * Removes the specified bookmark.
     * @param name the name of the bookmark
     */
    public void removeBookmark(String name) {
        if (bookmarks.remove(name) == null) {
            throw new IllegalArgumentException("Unknown bookmark: " + name);
        }        
    }
    
    /**
     * Removes all bookmarks.
     */
    public void removeAllBookmarks() {
        bookmarks.clear();
    }
    
    /**
     * Gets all bookmark names.
     * @return the current set of bookmark names.
     */
    public Set getBookmarkNames() {
        return bookmarks.keySet();
    }
    
    /* ---------- Methods from XMLEventReader ---------- */
    
    /**
     * Gets the next event from the stream or from the saved list of events.
     */
    public XMLEvent nextEvent() throws XMLStreamException {
        if (current == null || !current.hasNext()) {
            lastEvent = xer.nextEvent();
            if (bookmarks.size() > 0) {
                current = new LinkedEvent(lastEvent, current);
            } else {
                current = null;
            }
        } else {
            current = current.nextLinkedEvent();
            lastEvent = current.getXMLEvent();
        }
        return lastEvent;
    }

    /**
     * Checks if there are any more events.
     */
    public boolean hasNext() {
        if (current == null || !current.hasNext()) {
            return xer.hasNext();
        } 
        return true;
    }

    /**
     * Gets the next event without marking it as read.
     */
    public XMLEvent peek() throws XMLStreamException {
        if (current == null || !current.hasNext()) {
            return xer.peek();
        } 
        return current.nextLinkedEvent().getXMLEvent();
    }

    /**
     * Gets the value of a text only element.
     */
    public String getElementText() throws XMLStreamException {
        StringBuffer buffer = new StringBuffer();
        XMLEvent next = nextEvent();
        if (!next.isStartElement()) {
            throw new XMLStreamException("Current event is not a start element");
        }
        while (this.hasNext()) {
            next = this.peek();
            if (next.isCharacters()) {
                buffer.append(next.asCharacters().getData());
            } else if (next.isEndElement()) {
                return buffer.toString();
            } else if (next.isStartElement()) {
                throw new XMLStreamException("Start element found while expecting end element or characters");
            }            
            this.nextEvent(); // consume
        } 
        throw new XMLStreamException("Unexpected end of Document");        
    }

    /**
     * Ignores white space character elements until a start tag or end tag is found.
     */
    public XMLEvent nextTag() throws XMLStreamException {
        while (this.hasNext()) {
            XMLEvent next = this.nextEvent();
            if (next.isCharacters() && !next.asCharacters().isWhiteSpace()) {
                throw new XMLStreamException("Non whitespace text found.");
            }
            if (next.isStartElement() || next.isEndElement()) {
                return next;
            }
        }
        throw new XMLStreamException("End of document found.");        
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return xer.getProperty(name);
    }

    public void close() throws XMLStreamException {
        xer.close();
    }

    public Object next() {
        try {
            return this.nextEvent();
        } catch (XMLStreamException e) {
            return null;
        }
    }

    /**
     * @throws UnsupportedOperationException
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }    

}
