/*
 * DMFC - The DAISY Multi Format Converter
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
package se_tpb_zed2daisy202;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

/**
 * An item (a heading or span with a link to a smil position) in the NCC file.
 * @author Linus Ericson
 */
class NccItem {
    
    public static final int H1 = 1;
    public static final int H2 = 2;
    public static final int H3 = 3;
    public static final int H4 = 4;
    public static final int H5 = 5;
    public static final int H6 = 6;
    public static final int PAGE_FRONT = 7;
    public static final int PAGE_NORMAL = 8;
    public static final int PAGE_SPECIAL = 9;
    public static final int SIDEBAR = 10;
    public static final int OPTIONAL_PRODNOTE = 11;
    public static final int NOTEREF = 12;
    
    private static XMLEventFactory eventFactory = null;
    
    private int elementType;
    private String id;
    private String uri;
    private String text;
    
    public NccItem(int type, String itemId, String itemUri, String itemText) {
        elementType = type;
        id = itemId;
        uri = itemUri;
        text = itemText;        
    }
    
    /**
     * Write the item to the supplied XMLEventWriter
     * @param writer
     * @param addClassTitle
     * @throws XMLStreamException
     */
    public void writeItem(XMLEventWriter writer, boolean addClassTitle) throws XMLStreamException {
        writer.add(eventFactory.createCharacters("\t\t"));
        
        Collection attrs = new ArrayList();
        attrs.add(eventFactory.createAttribute("id", id));
        
        // Start tag
        switch (elementType) {
        case H1:
            if (addClassTitle) {
                attrs.add(eventFactory.createAttribute("class", "title"));
            }
            this.writeStartElement(writer, "h1", attrs);            
            break;
        case H2:
            this.writeStartElement(writer, "h2", attrs);
            break;
        case H3:
            this.writeStartElement(writer, "h3", attrs);            
            break;
        case H4:
            this.writeStartElement(writer, "h4", attrs);            
            break;
        case H5:
            this.writeStartElement(writer, "h5", attrs);            
            break;
        case H6:
            this.writeStartElement(writer, "h6", attrs);            
            break;
        case PAGE_FRONT:
            attrs.add(eventFactory.createAttribute("class", "page-front"));
            this.writeStartElement(writer, "span", attrs);            
            break;
        case PAGE_NORMAL:
            attrs.add(eventFactory.createAttribute("class", "page-normal"));
            this.writeStartElement(writer, "span", attrs);
            break;
        case PAGE_SPECIAL:
            attrs.add(eventFactory.createAttribute("class", "page-special"));
            this.writeStartElement(writer, "span", attrs);
            break;
        case SIDEBAR:
            attrs.add(eventFactory.createAttribute("class", "sidebar"));
            this.writeStartElement(writer, "span", attrs);
            break;
        case OPTIONAL_PRODNOTE:
            attrs.add(eventFactory.createAttribute("class", "optional-prodnote"));
            this.writeStartElement(writer, "span", attrs);
            break;
        case NOTEREF:
            attrs.add(eventFactory.createAttribute("class", "noteref"));
            this.writeStartElement(writer, "span", attrs);
            break;
        }

        attrs = new ArrayList();
        attrs.add(eventFactory.createAttribute("href", uri));
        
        // Link element
        this.writeStartElement(writer, "a", attrs);
        writer.add(eventFactory.createCharacters(text));        
        this.writeEndElement(writer, "a");
        
        // End tag
        switch (elementType) {
        case H1:
            this.writeEndElement(writer, "h1");            
            break;
        case H2:
            this.writeEndElement(writer, "h2");            
            break;
        case H3:
            this.writeEndElement(writer, "h3");            
            break;
        case H4:
            this.writeEndElement(writer, "h4");            
            break;
        case H5:
            this.writeEndElement(writer, "h5");            
            break;
        case H6:
            this.writeEndElement(writer, "h6");            
            break;
        case PAGE_FRONT:        
        case PAGE_NORMAL:
        case PAGE_SPECIAL:
        case SIDEBAR:
        case OPTIONAL_PRODNOTE:
        case NOTEREF:
            this.writeEndElement(writer, "span");            
            break;
        }
        
        // Newline at the end
        writer.add(eventFactory.createCharacters("\n"));
    }
    
    private void writeStartElement(XMLEventWriter writer, String name, Collection coll) throws XMLStreamException {
        writer.add(eventFactory.createStartElement(new QName(null, name, ""), coll.iterator(), null));
    }
    
    private void writeEndElement(XMLEventWriter writer, String name) throws XMLStreamException {
        writer.add(eventFactory.createEndElement("", null, name));
    }
    
    public static void setEventFactory(XMLEventFactory factory) {
        eventFactory = factory;
    }    
}
