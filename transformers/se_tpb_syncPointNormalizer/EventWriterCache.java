/*
 * DMFC - The DAISY Multi Format Converter
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
package se_tpb_syncPointNormalizer;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * Base class and factory for caching event writers.
 * @author Linus Ericson
 */
abstract class EventWriterCache {

    public static final int STRICT = 0;
    public static final int RELAXED = 1;
    public static final int NONE = 2;
    
    protected XMLEventWriter xew = null;
    
    protected EventWriterCache(XMLEventWriter writer) {
        xew = writer;
    }
    
    public static EventWriterCache newInstance(XMLEventWriter writer, int strictness) {
        if (strictness == NONE) {
            return new NoEventCache(writer);
        }
        if (strictness == STRICT) {
            return new SingleEventCache(writer);
        }
        return new DoubleEventCache(writer);
    }
    
    public abstract void writeEvent(XMLEvent ev, boolean isSpan) throws XMLStreamException;
    public abstract void flush() throws XMLStreamException;
        
    protected String tagInfo(XMLEvent ev) {
        StringBuffer buffer = new StringBuffer();
        switch (ev.getEventType()) {
        case XMLStreamConstants.START_ELEMENT:
            buffer.append("StartElement: ");
        	buffer.append(ev.asStartElement().getName().getLocalPart());        	
            break;
        case XMLStreamConstants.END_ELEMENT:
            buffer.append("EndElement: ");
        	buffer.append(ev.asEndElement().getName().getLocalPart());
            break;
        case XMLStreamConstants.CHARACTERS:
            buffer.append("Characters: ");
        	buffer.append(ev.asCharacters().getData());
            break;
        case XMLStreamConstants.START_DOCUMENT:
            buffer.append("StartDocument. ");
            break;
        case XMLStreamConstants.END_DOCUMENT:
            buffer.append("EndDocument. ");
            break;       
        default:
            buffer.append("Other. ");
            break;
        }
        return buffer.toString();
    }
}
