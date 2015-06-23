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

import java.net.URL;

import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


/**
 * A convenience wrapper for an XMLEventReader that keeps an autopopped ContextStack, a Location for cursor position when at rest, and a document URL.
 * @author Markus Gylling
 */
public class ConvenientXMLEventReader implements XMLEventReader  {

	private XMLEventReader mBaseReader = null;
	private URL mDocumentURL = null;
	private ContextStack mContextStack = null;
	private Location mCurrentEventLocation = null;
	
	/**
	 * Constructor
	 * @param doc The URL of the doc being read
	 * @param xer The plain XMLEventReader to use.
	 */
	public ConvenientXMLEventReader(URL doc, XMLEventReader xer) {
		mBaseReader = xer;
		mDocumentURL = doc;		
		mContextStack = new ContextStack(true);
	}
	
	/**
	 * Get the URL of the document currently being read.
	 */
	public URL getDocumentURL() {
		return mDocumentURL;
	}

	/**
	 * Get the ContextStack of the document currently being read.
	 */
	public ContextStack getContextStack() {
		return mContextStack;
	}

	/**
	 * Get the Location of the current cursor postition.
	 */
	public Location getCurrentEventLocation() {
		return mCurrentEventLocation;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see javax.xml.stream.XMLEventReader#close()
	 */
	public void close() throws XMLStreamException {
		mBaseReader.close();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.xml.stream.XMLEventReader#getElementText()
	 */
	public String getElementText() throws XMLStreamException {		
		return mBaseReader.getElementText();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.xml.stream.XMLEventReader#getProperty(java.lang.String)
	 */
	public Object getProperty(String arg0) throws IllegalArgumentException {		
		return mBaseReader.getProperty(arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.xml.stream.XMLEventReader#hasNext()
	 */
	public boolean hasNext() {		
		return mBaseReader.hasNext();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.xml.stream.XMLEventReader#nextEvent()
	 */
	public XMLEvent nextEvent() throws XMLStreamException {
		XMLEvent xe = mBaseReader.nextEvent();
		mContextStack.addEvent(xe);
		mCurrentEventLocation = xe.getLocation();
		return xe; 		
	}

	/*
	 * (non-Javadoc)
	 * @see javax.xml.stream.XMLEventReader#nextTag()
	 */
	public XMLEvent nextTag() throws XMLStreamException {
		XMLEvent xe = mBaseReader.nextTag();
		mContextStack.addEvent(xe);
		mCurrentEventLocation = xe.getLocation();
		return xe; 		
	}

	/*
	 * (non-Javadoc)
	 * @see javax.xml.stream.XMLEventReader#peek()
	 */
	public XMLEvent peek() throws XMLStreamException {
		return mBaseReader.peek();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public Object next() {		
		Object o = mBaseReader.next();
		if(o instanceof XMLEvent) {
			XMLEvent xe = (XMLEvent)o;
			mContextStack.addEvent(xe);
			mCurrentEventLocation = xe.getLocation();
		}
		return o; 		
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		mBaseReader.remove();		
	}

}
