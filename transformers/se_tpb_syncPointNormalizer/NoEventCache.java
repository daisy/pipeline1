/*
 * Created on 2006-feb-13
 */
package se_tpb_syncPointNormalizer;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


/**
 * @author linus
 */
class NoEventCache extends EventWriterCache {

    public NoEventCache(XMLEventWriter writer) {
        super(writer);
    }

    public void writeEvent(XMLEvent ev, boolean isSpan) throws XMLStreamException {
        //System.out.println(tagInfo(ev));
        xew.add(ev);
    }

    public void flush() throws XMLStreamException {
    }

}
