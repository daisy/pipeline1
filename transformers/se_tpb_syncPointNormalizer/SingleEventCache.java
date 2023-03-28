/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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
package se_tpb_syncPointNormalizer;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * An {@link se_tpb_syncPointNormalizer.EventWriterCache} that caches one event before writing it
 * to the {@link javax.xml.stream.XMLEventWriter}. If a start element and an
 * end element immediately follows each other, and both events have the
 * <code>isSpan</code> parameter set to true, the events are ignored.
 * @author Linus Ericson
 * @see se_tpb_syncPointNormalizer.DoubleEventCache
 */
class SingleEventCache extends EventWriterCache {

    private XMLEvent cachedEvent = null;
    private boolean cachedIsSpan = false;
    
    /**
     * @param writer
     */
    protected SingleEventCache(XMLEventWriter writer) {
        super(writer);
    }


    public void writeEvent(XMLEvent ev, boolean isSpan) throws XMLStreamException {
        if (cachedEvent!=null && cachedEvent.isStartElement() && cachedIsSpan && ev!=null && ev.isEndElement() && isSpan) {
            //System.err.println("Trowing away [" + tagInfo(cachedEvent) + "] [" + tagInfo(ev) + "]");
            cachedEvent = null;
            cachedIsSpan = false;
            return;
        } else if (cachedEvent != null) {
            //System.err.println("Writing [" + tagInfo(cachedEvent) + "]");
            xew.add(cachedEvent);            
        }
        //System.err.println("Caching [" + tagInfo(ev) + "]");
        cachedEvent = ev;
        cachedIsSpan = isSpan;
    }

    public void flush() throws XMLStreamException {
        writeEvent(null, false);
    }

}
