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
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;
import org.daisy.util.xml.stax.StaxFilter;

/**
 * Updates the note body SMIL references. This is only applied when the note
 * bodies are moved instead of copied. Since the moved SMIL notes body
 * is represented by just a single par, all SMIL references within a note
 * body in the dontent document must point to the same par.
 * 
 * @author Linus Ericson
 */
/*package*/ class ContentDocSmilRepointer  {
	
	private StAXInputFactoryPool staxInPool;
    private StAXOutputFactoryPool staxOutPool;
    private StAXEventFactoryPool staxEvPool;
    private Map<String,Object> staxInProperties;
    private Map<String,Object> staxOutProperties;
	
	public ContentDocSmilRepointer(StAXInputFactoryPool staxInPool,
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
	
	public void repoint(File input, File output, Map<String,String> bodyIdSmilIdMap) throws XMLStreamException, IOException {
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
            
            NotebodySmilRefRepointFilter filter = new NotebodySmilRefRepointFilter(reader, xef, xof, os, bodyIdSmilIdMap);
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
	
	private class NotebodySmilRefRepointFilter extends StaxFilter {
	
		private Map<String,String> bodyIdSmilIdMap = null;
		private String smilNoteBodyId = null;
		private int level = 0;
		private int noteBodyLevel = -1;
		
		public NotebodySmilRefRepointFilter(XMLEventReader xer, XMLEventFactory xef,
				XMLOutputFactory xof, OutputStream outStream, Map<String,String> bodyIdSmilIdMap)
				throws XMLStreamException {
			super(xer, xef, xof, outStream);
			this.bodyIdSmilIdMap = bodyIdSmilIdMap;
		}
	
		/* (non-Javadoc)
		 * @see org.daisy.util.xml.stax.StaxFilter#startElement(javax.xml.stream.events.StartElement)
		 */
		@Override
		protected StartElement startElement(StartElement event) {
			level++;
			StartElement result = event;
			Attribute idAttr = event.getAttributeByName(new QName("id"));
			if (idAttr != null && bodyIdSmilIdMap.containsKey(idAttr.getValue())) {
				// We have found the start of a note body. Now we need to change all
				// SMIL links until we reach the end of the note body.
				smilNoteBodyId =  bodyIdSmilIdMap.get(idAttr.getValue());
				noteBodyLevel = level;
			}
			if (smilNoteBodyId != null && "a".equals(event.getName().getLocalPart())) {
				Attribute hrefAttr = event.getAttributeByName(new QName("href"));
				if (hrefAttr != null) {
					String fileRef = hrefAttr.getValue().substring(0, hrefAttr.getValue().indexOf("#"));
					if (fileRef.endsWith(".smil")) {
						List<Attribute> attrs = new ArrayList<Attribute>();
						attrs.add(getEventFactory().createAttribute("href", smilNoteBodyId));
						Iterator<?> attrItr = event.getAttributes();
						while (attrItr.hasNext()) {
							Attribute attr = (Attribute)attrItr.next();
							if (!"href".equals(attr.getName().getLocalPart())) {
								attrs.add(attr);
							}
						}
						result = getEventFactory().createStartElement(event.getName(), attrs.iterator(), event.getNamespaces());
					}
				}
			}
			return result;
		}

		/* (non-Javadoc)
		 * @see org.daisy.util.xml.stax.StaxFilter#endElement(javax.xml.stream.events.EndElement)
		 */
		@Override
		protected EndElement endElement(EndElement event) {
			level--;
			if (smilNoteBodyId != null && level < noteBodyLevel) {
				smilNoteBodyId = null;
				noteBodyLevel = -1;
			}
			return super.endElement(event);
		}
	
	}

}
