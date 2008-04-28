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
package se_tpb_zed2daisy202;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Gives the final touch to a NCC.
 * The ncc:totalTime is updated and the NCC gets indented. 
 * @author Linus Ericson
 */
class NccFixer {

    private XMLInputFactory factory = null;
    private XMLEventFactory eventFactory = null;
    private XMLOutputFactory outFactory = null;
    
    public NccFixer() throws CatalogExceptionNotRecoverable {
        factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        //factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        factory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
        
        eventFactory = XMLEventFactory.newInstance();        
        outFactory = XMLOutputFactory.newInstance();
        
        NccItem.setEventFactory(eventFactory);
    }
    
    /**
     * Fixes the NCC. The ncc:totalTime is inserted and the file is indented.
     * @param nccIn
     * @param nccOut
     * @param totalElapsedTime
     * @throws XMLStreamException
     * @throws IOException
     */
    public void fix(File nccIn, File nccOut, long totalElapsedTime) throws XMLStreamException, IOException {
        XMLEventReader nccReader = factory.createXMLEventReader(new FileInputStream(nccIn));        
        XMLEventWriter nccWriter = outFactory.createXMLEventWriter(new FileOutputStream(nccOut), "utf-8");

        nccWriter.add(eventFactory.createStartDocument("utf-8", "1.0"));

        // Skip to the start of body
        this.skipTo(nccReader, "body");
        
        Collection<NccItem> nccItems = new ArrayList<NccItem>();
        
        // Read the items of the NCC
        NccItem nccItem = this.getNccItem(nccReader);
        while (nccItem != null) {            
            nccItems.add(nccItem);            
            nccItem = this.getNccItem(nccReader);
        }
        nccReader.close();
        
        // Copy the head and update ncc:totalTime
        this.copyNccHead(nccIn, totalElapsedTime, nccWriter);
        
        // Write the items
        this.writeNccItems(nccItems, nccWriter);
        
        // Write the end
        this.writeNccEnd(nccWriter);        
        
        nccWriter.close();
    }
    
    /**
     * Copies the head of the NCC.
     * @param nccIn
     * @param totalElapsedTime
     * @param writer
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    private void copyNccHead(File nccIn, long totalElapsedTime, XMLEventWriter writer) throws FileNotFoundException, XMLStreamException {
        XMLEventReader nccReader = factory.createXMLEventReader(new FileInputStream(nccIn));
        String indentation = "\t\t\t\t\t\t\t";
        int level = 0;
        while (nccReader.hasNext()) {
            XMLEvent event = nccReader.nextEvent();
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();                
                writer.add(eventFactory.createCharacters("\n" + indentation.substring(0, level)));
                level++;
                if ("meta".equals(se.getName().getLocalPart())) {
                    Attribute name = se.getAttributeByName(new QName("name"));
                    if (name!=null && "ncc:totalTime".equals(name.getValue())) {
                        Collection<Attribute> attrs = new ArrayList<Attribute>();
                        attrs.add(name);
                        attrs.add(eventFactory.createAttribute("content", new SmilClock((totalElapsedTime+500)-((totalElapsedTime+500)%1000)).toString(SmilClock.FULL)));
                        writer.add(eventFactory.createStartElement(new QName(null, "meta", ""), attrs.iterator(), null));                        
                    } else {
                    	Collection<Attribute> attrs = new ArrayList<Attribute>();
                    	this.addAttribute(attrs, se, "name");
                    	this.addAttribute(attrs, se, "http-equiv");
                    	this.addAttribute(attrs, se, "content");
                    	this.addAttribute(attrs, se, "scheme");
                    	writer.add(eventFactory.createStartElement(new QName(null, "meta", ""), attrs.iterator(), se.getNamespaces()));
                        //writer.add(se);
                    }                    
                } else if ("body".equals(se.getName().getLocalPart())) {
                    writer.add(se);
                    writer.add(eventFactory.createCharacters("\n"));
                    nccReader.close();
                    return;
                } else {
                    writer.add(se);
                }
            } else if (event.isCharacters()) {
                Characters ch = event.asCharacters();
                if (!ch.getData().matches("\\s+")) {
                    writer.add(ch);
                } 
            } else if (event.isStartDocument()) {
                // Ignore. This has already been handled.
            } else if (event.isEndElement()) {
                level--;
                if ("head".equals(event.asEndElement().getName().getLocalPart())) {
                    writer.add(eventFactory.createCharacters("\n" + indentation.substring(0, level)));
                }
                writer.add(event);
            } else {
                writer.add(event);
            }
        }
    }
    
    private void addAttribute(Collection<Attribute> coll, StartElement se, String attribute) {
    	for (Iterator<?> it = se.getAttributes(); it.hasNext(); ) {
    		Attribute attr = (Attribute)it.next();
    		if (attribute.equals(attr.getName().getLocalPart())) {
    			coll.add(attr);
    		}
    	}
    }
    
    /**
     * Writes the items of the NCC.
     * @param nccItems
     * @param writer
     * @throws XMLStreamException
     */
    private void writeNccItems(Collection<NccItem> nccItems, XMLEventWriter writer) throws XMLStreamException {
        boolean first = true;
        for (Iterator<NccItem> iter = nccItems.iterator(); iter.hasNext(); ) {
            NccItem item = iter.next();            
            item.writeItem(writer, first);
            first = false;
        }
    }
    
    /**
     * Writes the end of the NCC.
     * @param writer
     * @throws XMLStreamException
     */
    private void writeNccEnd(XMLEventWriter writer) throws XMLStreamException {
        writer.add(eventFactory.createCharacters("\t"));
        writer.add(eventFactory.createEndElement(new QName(null, "body", ""), null));
        writer.add(eventFactory.createCharacters("\n"));
        writer.add(eventFactory.createEndElement(new QName(null, "html", ""), null));
        writer.add(eventFactory.createCharacters("\n"));
    }
    
    private void skipTo(XMLEventReader reader, String localName) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();
                if (localName.equals(se.getName().getLocalPart())) {
                    return;
                }
            }
        }
        throw new XMLStreamException("Start tag " + localName + " not found.");
    }
    
    
    private NccItem getNccItem(XMLEventReader reader) throws XMLStreamException {
        if (!reader.hasNext()) {
            return null;
        }
        
        // Skip any leading whitespace
        XMLEvent event = reader.nextEvent();
        while (event.isCharacters() && reader.hasNext()) {
            event = reader.nextEvent();
        }
        
        int type = 0;
        String id = "";
        String uri = "";
        String text = "";
        int level = 0;
        while (event != null) {
            if (event.isStartElement()) {
                level++;
                StartElement se = event.asStartElement();
                if ("h1".equals(se.getName().getLocalPart())) {
                    type = NccItem.H1;
                    id = se.getAttributeByName(new QName("id")).getValue();
                } else if ("h2".equals(se.getName().getLocalPart())) {
                    type = NccItem.H2;
                    id = se.getAttributeByName(new QName("id")).getValue();
                } else if ("h3".equals(se.getName().getLocalPart())) {
                    type = NccItem.H3;
                    id = se.getAttributeByName(new QName("id")).getValue();
                } else if ("h4".equals(se.getName().getLocalPart())) {
                    type = NccItem.H4;
                    id = se.getAttributeByName(new QName("id")).getValue();
                } else if ("h5".equals(se.getName().getLocalPart())) {
                    type = NccItem.H5;
                    id = se.getAttributeByName(new QName("id")).getValue();
                } else if ("h6".equals(se.getName().getLocalPart())) {
                    type = NccItem.H6;
                    id = se.getAttributeByName(new QName("id")).getValue();
                } else if ("span".equals(se.getName().getLocalPart())) {
                    String classAttr = se.getAttributeByName(new QName("class")).getValue();
                    id = se.getAttributeByName(new QName("id")).getValue();
                    if ("page-front".equals(classAttr)) {
                        type = NccItem.PAGE_FRONT;                        
                    } else if ("page-normal".equals(classAttr)) {
                        type = NccItem.PAGE_NORMAL;
                    } else if ("page-special".equals(classAttr)) {
                        type = NccItem.PAGE_SPECIAL;
                    } else if ("sidebar".equals(classAttr)) {
                        type = NccItem.SIDEBAR;
                    } else if ("optional-prodnote".equals(classAttr)) {
                        type = NccItem.OPTIONAL_PRODNOTE;
                    } else if ("noteref".equals(classAttr)) {
                        type = NccItem.NOTEREF;
                    }
                } else if ("a".equals(se.getName().getLocalPart())) {
                    Attribute href = se.getAttributeByName(new QName("href"));
                    uri = href.getValue();
                } else {
                    return null;
                }
            } else if (event.isEndElement()) {
                level--;
                if (level == 0) {
                    return new NccItem(type, id, uri, text);
                }
            } else if (event.isCharacters()) {
                if (!event.asCharacters().getData().matches("\\s+")) {
                    text = event.asCharacters().getData().trim();
                }
            }
            
            if (reader.hasNext()) {
                event = reader.nextEvent();
            } else {
                event = null;
            }
        }
        
        return null;
    }
    
}
