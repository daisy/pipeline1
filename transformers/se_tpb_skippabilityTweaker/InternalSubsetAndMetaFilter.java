/*
 * Daisy Pipeline
 * Copyright (C) 2008  Daisy Consortium
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
package se_tpb_skippabilityTweaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.StartElement;

import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;
import org.daisy.util.xml.stax.DoctypeParser;
import org.daisy.util.xml.stax.StaxFilter;

/**
 * Removes the internal subset of the doctype declaration and updates the
 * ncc:totalTime. The bodyref attribute is also removed from the content doc.
 * 
 * @author Linus Ericson
 */
/*package*/ class InternalSubsetAndMetaFilter  {
	
	private StAXInputFactoryPool staxInPool;
    private StAXOutputFactoryPool staxOutPool;
    private StAXEventFactoryPool staxEvPool;
    private Map<String,Object> staxInProperties;
    private Map<String,Object> staxOutProperties;
	
	public InternalSubsetAndMetaFilter(StAXInputFactoryPool staxInPool,
			Map<String, Object> staxInProperties,
			StAXOutputFactoryPool staxOutPool,
			Map<String, Object> staxOutProperties,
			StAXEventFactoryPool staxEvPool) {
		this.staxInPool = staxInPool;
		this.staxInProperties = staxInProperties;
		this.staxOutPool = staxOutPool;
		this.staxOutProperties = staxOutProperties;
		this.staxEvPool = staxEvPool;
	}
	
	public void filter(File input, File output, long totalElapsedTime) throws XMLStreamException, IOException {
		XMLOutputFactory xof = null;
	    XMLEventFactory xef = null;
	    XMLEventReader reader = null;
	    XMLInputFactory xif = null; 
        OutputStream os = null;
        XMLEventWriter writer = null;
		try {
            xif = staxInPool.acquire(staxInProperties);
            xof = staxOutPool.acquire(staxOutProperties);
            xef = staxEvPool.acquire();
            
            reader = xif.createXMLEventReader(new FileInputStream(input));
            os = new FileOutputStream(output);
            
            DTDAndMetaFilter filter = new DTDAndMetaFilter(reader, xef, xof, os, totalElapsedTime);
            filter.filter();
            filter.close();
            
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (XMLStreamException e) {                
            }
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (XMLStreamException e) {                
            }
            staxInPool.release(xif,staxInProperties);
            staxOutPool.release(xof, staxOutProperties);
            staxEvPool.release(xef);
        }
	}
	
	private class DTDAndMetaFilter extends StaxFilter {
	
        private long totalElapsedTime;
        
		public DTDAndMetaFilter(XMLEventReader xer, XMLEventFactory xef,
				XMLOutputFactory xof, OutputStream outStream, long totalElapsedTime)
				throws XMLStreamException {
			super(xer, xef, xof, outStream);
            this.totalElapsedTime = totalElapsedTime;
		}

		
		/* (non-Javadoc)
		 * @see org.daisy.util.xml.stax.StaxFilter#dtd(javax.xml.stream.events.DTD)
		 */
		@Override
		protected DTD dtd(DTD event) {
			// Filter out the internal subset
			DoctypeParser parser = new DoctypeParser(event.getDocumentTypeDeclaration());
			StringBuilder builder = new StringBuilder();
			builder.append("<!DOCTYPE ").append(parser.getRootElem()).append(" ");
			if (parser.getPublicId() != null) {
				builder.append("PUBLIC \"").append(parser.getPublicId()).append("\" \"").append(parser.getSystemId()).append("\"");
			} else if (parser.getSystemId() != null) {
				builder.append("SYSTEM \"").append(parser.getSystemId()).append("\"");
			}
			builder.append(">");
			return getEventFactory().createDTD(builder.toString());
		}
	
		/* (non-Javadoc)
		 * @see org.daisy.util.xml.stax.StaxFilter#startElement(javax.xml.stream.events.StartElement)
		 */
		@Override
		protected StartElement startElement(StartElement event) {
            StartElement result = event;
            
            // Update ncc:totalTime
            if ("meta".equals(event.getName().getLocalPart())) {
                Attribute nameAttr = event.getAttributeByName(new QName("name"));
                if (nameAttr != null && "ncc:totalTime".equals(nameAttr.getValue())) {
                    List<Attribute> attrs = new ArrayList<Attribute>();
                    attrs.add(nameAttr);
                    String content = new SmilClock((totalElapsedTime+500)-((totalElapsedTime+500)%1000)).toString(SmilClock.FULL);
                    attrs.add(getEventFactory().createAttribute("content", content));
                    result = getEventFactory().createStartElement(event.getName(), attrs.iterator(), event.getNamespaces());
                }
            }
            
            // Filter out the bodyref attribute
            if ("span".equals(event.getName().getLocalPart())) {
                if (event.getAttributeByName(new QName("bodyref")) != null) {
                    List<Attribute> attrs = new ArrayList<Attribute>();
                    for (Iterator<?> attrItr = event.getAttributes(); attrItr.hasNext(); ) {
                        Attribute attr = (Attribute)attrItr.next();
                        if (!"bodyref".equals(attr.getName().getLocalPart())) {
                            attrs.add(attr);
                        }
                    }
                    result = getEventFactory().createStartElement(event.getName(), attrs.iterator(), event.getNamespaces());
                }
            }
			return result;
		}
	
	}

}
