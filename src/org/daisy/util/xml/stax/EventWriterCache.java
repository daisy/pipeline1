/*
 * org.daisy.util - The DAISY java utility library
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
package org.daisy.util.xml.stax;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * @author Linus Ericson
 */
public abstract class EventWriterCache {
	
	private XMLEventWriter xew = null;
	
	protected EventWriterCache(XMLEventWriter writer) {
        xew = writer;
    }
	
	protected void write(XMLEvent event) throws XMLStreamException {
		xew.add(event);
	}
	
	public abstract void writeEvent(XMLEvent ev, boolean filter) throws XMLStreamException;
    public abstract void flush() throws XMLStreamException;    

}
