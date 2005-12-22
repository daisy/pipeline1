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
package se_tpb_mixedContentNormalizer;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * An {@link se_tpb_mixedContentNormalizer.EventWriterCache} that caches two
 * events. If a start element is followed by an end event (with an optional
 * whitespace only text event in between), and both events have the
 * <code>isSpan</code> parameter set to true, the events are never written
 * to the {@link javax.xml.stream.XMLEventWriter}.
 * @author Linus Ericson
 */
class DoubleEventCache extends EventWriterCache {

    private XMLEvent first = null;
    private boolean firstIsSpan = false;
    
    private XMLEvent second = null;
    private boolean secondIsSpan = false;
    
    /**
     * @param writer
     */
    protected DoubleEventCache(XMLEventWriter writer) {
        super(writer);
    }

    public void writeEvent(XMLEvent ev, boolean isSpan) throws XMLStreamException {
        /*
        if (second!=null && second.isStartElement() && secondIsSpan &&
                ev!=null && ev.isEndElement() && isSpan) {
            second = null;
            secondIsSpan = false;
            return;
        }*/
        if (first!=null && first.isStartElement() && firstIsSpan &&
                second!=null && second.isEndElement() && secondIsSpan) {
            first = null;
            firstIsSpan = false;
            second = null;
            secondIsSpan = false;            
        } else if (first!=null && first.isStartElement() && firstIsSpan &&
                second!=null && second.isCharacters() && second.asCharacters().getData().matches("\\s*") &&
                ev!=null && ev.isEndElement() && isSpan) {
            first = null;
            firstIsSpan = false;
            /*second = null;
            secondIsSpan = false;
            */
            ev = null;
            isSpan = false;
        }
        if (first != null) {
            xew.add(first);
        }
        first = second;
        firstIsSpan = secondIsSpan;
        second = ev;
        secondIsSpan = isSpan;
    }

    public void flush() throws XMLStreamException {
        writeEvent(null, false);
        writeEvent(null, false);
    }

}
