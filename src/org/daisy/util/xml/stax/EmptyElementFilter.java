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

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * @author Linus Ericson
 */
public class EmptyElementFilter extends EventWriterCache {

	private XMLEvent first = null;
    private boolean filterFirst = false;
    
    private XMLEvent second = null;
    private boolean filterSecond = false;
	
	public EmptyElementFilter(XMLEventWriter writer) {
		super(writer);
	}
	
	public void writeEvent(XMLEvent ev, boolean filter)	throws XMLStreamException {
        if (first!=null && first.isStartElement() && filterFirst &&
                second!=null && second.isEndElement() && filterSecond) {
            first = null;
            filterFirst = false;
            second = null;
            filterSecond = false;            
        } else if (first!=null && first.isStartElement() && filterFirst &&
                second!=null && second.isCharacters() && second.asCharacters().getData().matches("\\s*") &&
                ev!=null && ev.isEndElement() && filter) {
            first = null;
            filterFirst = false;
            ev = null;
            filter = false;
        }
        if (first != null) {
        	write(first);
        }
        first = second;
        filterFirst = filterSecond;
        second = ev;
        filterSecond = filter;
	}

	public void flush() throws XMLStreamException {
		writeEvent(null, false);
        writeEvent(null, false);
	}

}
