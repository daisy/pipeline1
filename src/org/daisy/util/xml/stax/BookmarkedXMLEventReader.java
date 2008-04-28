/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.util.xml.stax;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
 * <p>
 * <b>Caution!</b> Remember not to leave any dangling bookmarks since all events
 * from the earliest bookmark are buffered.
 * </p>
 * @author Linus Ericson
 */
public class BookmarkedXMLEventReader implements XMLEventReader {
   
    private XMLEventReader xer;
    
    private Map<String,LinkedEvent> bookmarks = new HashMap<String,LinkedEvent>();    
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
        LinkedEvent le = bookmarks.get(name);
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
    public Set<String> getBookmarkNames() {
        return bookmarks.keySet();
    }
    
    /**
     * Checks if a given bookmark exists.
     * @param name the name of the bookmark
     * @return <code>true</code> if the bookmark exists, <code>false</code> otherwise.
     */
    public boolean bookmarkExists(String name) {
        return bookmarks.containsKey(name);
    }
    
    /**
     * Checks if the current position is at a bookmark.
     * Warning: this method performs a linear search over all bookmarks.
     */
    public boolean atBookmark() {
        for (Iterator<String> it = bookmarks.keySet().iterator(); it.hasNext(); ) {
            LinkedEvent le = bookmarks.get(it.next());
            if (le == current) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the current position in the event stream is at the specified bookmark.
     * @param name the name of the bookmark
     */
    public boolean atBookmark(String name) {
        if (!bookmarkExists(name)) {
            throw new IllegalArgumentException("Unknown bookmark: " + name);
        }
        LinkedEvent le = bookmarks.get(name);        
        return (le == current);
    }
    
    /**
     * Gets the name of all bookmarks at the current position.
     * Warning: this method performs a linear search over all bookmarks.
     * @return a Set of bookmark names
     */
    public Set<String> getBookmarkNamesHere() {
        Set<String> result = new HashSet<String>();
        for (Iterator<String> it = bookmarks.keySet().iterator(); it.hasNext(); ) {
            String bookmarkName = it.next();
            LinkedEvent le = bookmarks.get(bookmarkName);
            if (le == current) {
                result.add(bookmarkName);
            }
        }
        return result; 
    }
    
    public boolean isNextReadBuffered() {
        return (current != null && current.hasNext());
    }
    
    /**
     * Copy a bookmark.
     * @param from the bookmark to copy
     * @param to the name of the new bookmark
     */
    public void copyBookmark(String from, String to) {
        LinkedEvent le = bookmarks.get(from);
        if (le == null) {
            throw new IllegalArgumentException("Unknown bookmark: " + from);
        }
        bookmarks.put(to, le);
    }
    
    /**
     * Rename a bookmark.
     * @param from the bookmark to rename
     * @param to the new name of the bookmark
     */
    public void renameBookmark(String from, String to) {
        copyBookmark(from, to);
        removeBookmark(from);
    }
    
    /**
     * Checks if two bookmarks point to the same location in the stream.
     * @param name1 the name of the first bookmark
     * @param name2 the name of the second bookmark
     * @return <code>true</code> if the bookmarks point to the same location, <code>false</code> otherwise
     */
    public boolean bookmarksEqual(String name1, String name2) {
        LinkedEvent le1 = bookmarks.get(name1);
        if (le1 == null) {
            throw new IllegalArgumentException("Unknown bookmark: " + le1);
        }
        LinkedEvent le2 = bookmarks.get(name2);
        if (le2 == null) {
            throw new IllegalArgumentException("Unknown bookmark: " + le2);
        }
        return (le1 == le2);
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
