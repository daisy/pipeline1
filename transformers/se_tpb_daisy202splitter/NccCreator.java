/*
 * Daisy Pipeline (C) 2005-2009 Daisy Consortium
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
package se_tpb_daisy202splitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.stax.AttributeByName;

/**
 * Creates the NCC.
 * @author Linus Ericson
 */
public class NccCreator {
	
	private XMLInputFactory xif;
	private XMLOutputFactory xof;
	private PromptSet promptSet;
	private Map<FilesetFile, Integer> smilVolumeNumberMap;
	private XMLEventFactory xef = XMLEventFactory.newInstance();
	private int numVolumes;
	
	
	public NccCreator(PromptSet promptSet, Map<FilesetFile, Integer> smilVolumeNumberMap,
			XMLInputFactory xif, XMLOutputFactory xof, int numVolumes) {
		this.xif = xif;
		this.xof = xof;
		this.promptSet = promptSet;
		this.smilVolumeNumberMap = smilVolumeNumberMap;
		this.numVolumes = numVolumes;
	}
	
	public void createNcc(D202NccFile nccFile, File outputFile, int volumeNumber, List<NccItem> nccItems) throws XMLStreamException, IOException {
		XMLEventReader reader = xif.createXMLEventReader(new FileInputStream(nccFile.getFile()));
	    OutputStream outputStream = new FileOutputStream(outputFile);
	    
	    XMLEventWriter writer = this.copyUntil(reader, "head", outputStream);
	    this.fixMeta(reader, writer, volumeNumber);
	    
	    writer.add(xef.createCharacters("\r\n\t"));
	    writer.add(xef.createStartElement(new QName(Namespaces.XHTML_10_NS_URI, "body", ""), null, null));
	    writer.add(xef.createCharacters("\r\n"));
	    this.writeNccItems(writer, nccItems, volumeNumber, nccFile);
	    writer.add(xef.createCharacters("\t"));
	    writer.add(xef.createEndElement(new QName(Namespaces.XHTML_10_NS_URI, "body", ""), null));
	    writer.add(xef.createCharacters("\n"));
	    writer.add(xef.createEndElement(new QName(Namespaces.XHTML_10_NS_URI, "html", ""), null));
	    	    
	    reader.close();
	    writer.close();
	    outputStream.close();
	}
	
	private XMLEventWriter copyUntil(XMLEventReader reader, String localName, OutputStream outputStream) throws XMLStreamException {
		XMLEventWriter writer = null;
        boolean rootElementSeen = false;
        boolean textSeen = false;
        boolean found = false;
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            XMLEvent writeEvent = null;
            switch (event.getEventType()) {
	            case XMLStreamConstants.START_DOCUMENT:
	                StartDocument sd = (StartDocument)event;
	            	if (sd.encodingSet()) {
	            	    writer = xof.createXMLEventWriter(outputStream, sd.getCharacterEncodingScheme());
	            	    writeEvent = event;
	            	} else {
	            	    writer = xof.createXMLEventWriter(outputStream, "utf-8");
	            	    writeEvent = xef.createStartDocument("utf-8", "1.0");
	            	}
	            	break;
	            case XMLStreamConstants.START_ELEMENT:                
	                if (!rootElementSeen && !textSeen) {
	                    writer.add(xef.createCharacters("\n"));
	                    rootElementSeen = true;
	                }
	                if (event.asStartElement().getName().getLocalPart().equals(localName)) {
	                	found = true;
	                }
	                writeEvent = event;
	            	break;
	            default:
	            	writeEvent = event;
            		break;
            }
            writer.add(writeEvent);
            if (found) {
            	break;
            }            
        }
        if (!found) {
        	throw new XMLStreamException("Start tag " + localName + " not found.");
        }
        return writer;
    }
	
	private void fixMeta(XMLEventReader reader, XMLEventWriter writer, int volumeNumber) throws XMLStreamException {
		boolean found = false;
		int level = 0;
		int skip = 0;
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			
			if (event.isStartElement()) {
				level++;
				StartElement se = event.asStartElement();
				String localName = se.getName().getLocalPart();
				if ("meta".equals(localName)) {
					Attribute nameAttr = AttributeByName.get(new QName("name"), se);
					Attribute contentAttr = AttributeByName.get(new QName("content"), se);
					if (nameAttr != null && contentAttr != null) {
						String name = nameAttr.getValue();
						if ("ncc:setInfo".equals(name) || "ncc:kByteSize".equals(name) || "ncc:files".equals(name)) {
							// Remove these meta data elements							
							event = null;
						}
					}
				}
				
				if (event == null) {
					skip = level;
				}
			} else if (event.isEndElement()) {
				level--;
				EndElement ee = event.asEndElement();
				String localName = ee.getName().getLocalPart();
				if ("head".equals(localName)) {
					// We are at the end of the meta section
					found = true;
				}
			}
			
			if (skip > 0) {
                while (reader.hasNext() && skip != 0) {
                    event = reader.nextEvent();
                    if (event.isStartElement()) {
                        level++;
                    } else if (event.isEndElement()) {
                        level--;
                    }
                    if (level < skip) {
                        skip = 0;
                    }
                }
            } else if (found) {
            	writer.add(xef.createCharacters("\t"));
            	this.createMeta(writer, "ncc:setInfo", volumeNumber + " of " + numVolumes);
            	writer.add(xef.createCharacters("\r\n\t\t"));
            	this.createMeta(writer, "prod:splitter", Daisy202Splitter.NAME);
            	writer.add(xef.createCharacters("\r\n\t"));
            	writer.add(event);
            	break;
            } else if (event != null) {
				writer.add(event);
			}
		}
	}
	
	private void createMeta(XMLEventWriter writer, String name, String content) throws XMLStreamException {
		QName qName = new QName(Namespaces.XHTML_10_NS_URI, "meta", "");
		List<Attribute> attrs = new ArrayList<Attribute>();
		attrs.add(xef.createAttribute("name", name));
		attrs.add(xef.createAttribute("content", content));
		writer.add(xef.createStartElement(qName, attrs.iterator(), null));
		writer.add(xef.createEndElement(qName, null));
	}
	
	private void writeNccItems(XMLEventWriter writer, List<NccItem> nccItems, 
			int currentVolumeNumber, D202NccFile nccFile) throws XMLStreamException {
		boolean first = true;
		for (NccItem nccItem : nccItems) {
			writer.add(xef.createCharacters("\t\t"));
			
			NccItem writeNccItem = new NccItem(nccItem);
			if (first && currentVolumeNumber != 1) {
				String uri = nccItem.getUri();
				String fragment = uri.substring(uri.indexOf("#"));
				writeNccItem = new NccItem(NccItem.Type.H1, nccItem.getIdAttr(), "title.smil" + fragment, nccItem.getText());
				writeNccItem.setClassAttr("title");
			} else {
				if (first) {
					writeNccItem.setClassAttr("title");
				}
				String hrefString = nccItem.getUri();
            	if (hrefString.indexOf("#") != -1) {
            		hrefString = hrefString.substring(0, hrefString.indexOf("#"));
            	}
                URI hrefURI = nccFile.getFile().toURI().resolve(hrefString);
                FilesetFile fsf = nccFile.getReferencedLocalMember(hrefURI);
                if (fsf != null) {
                	if (smilVolumeNumberMap.containsKey(fsf)) {
                        int volume = smilVolumeNumberMap.get(fsf);
                        if (volume != currentVolumeNumber) {
                        	String newHref = promptSet.getPromptVolume(volume).smilFile.getName() + "#cd" + volume;
                        	writeNccItem.setUri(newHref);
                        	writeNccItem.setRel(volume + " of " + numVolumes);
                        }
                	}
                }                
			}
			first = false;
			this.writeNccItem(writer, writeNccItem, xef);
			
			writer.add(xef.createCharacters("\r\n"));
		}
	}
	
	private void writeNccItem(XMLEventWriter writer, NccItem nccItem, XMLEventFactory xef) throws XMLStreamException {
		String name = null;
		String classAttr = null;
		switch (nccItem.getType()) {
			case H1:
				name = "h1";
				classAttr = nccItem.getClassAttr();
				break;
			case H2:
				name = "h2";
				break;
			case H3:
				name = "h3";
				break;
			case H4:
				name = "h4";
				break;
			case H5:
				name = "h5";
				break;
			case H6:
				name = "h6";
				break;
			case NOTEREF:
				name = "span";
				classAttr = "noteref";
				break;
			case OPTIONAL_PRODNOTE:
				name = "span";
				classAttr = "optional-prodnote";
				break;
			case PAGE_FRONT:
				name = "span";
				classAttr = "page-front";
				break;
			case PAGE_NORMAL:
				name = "span";
				classAttr = "page-normal";
				break;
			case PAGE_SPECIAL:
				name = "span";
				classAttr = "page-special";
				break;
			case SIDEBAR:
				name = "span";
				classAttr = "sidebar";
				break;
		}
		
		// Open ncc item
		QName qName = new QName(Namespaces.XHTML_10_NS_URI, name, "");
		List<Attribute> attrs = new ArrayList<Attribute>();
		if (classAttr != null) {
			attrs.add(xef.createAttribute("class", classAttr));
		}
		attrs.add(xef.createAttribute("id", nccItem.getIdAttr()));		
		writer.add(xef.createStartElement(qName, attrs.iterator(), null));
		
		// Open "a" element
		QName aName = new QName(Namespaces.XHTML_10_NS_URI, "a", "");
		attrs = new ArrayList<Attribute>();
		attrs.add(xef.createAttribute("href", nccItem.getUri()));
		if (nccItem.getRel() != null) {
			attrs.add(xef.createAttribute("rel", nccItem.getRel()));
		}
		writer.add(xef.createStartElement(aName, attrs.iterator(), null));
		
		// Write text
		writer.add(xef.createCharacters(nccItem.getText()));
		
		// Close "a" element
		writer.add(xef.createEndElement(aName, null));
		
		// Close ncc item
		writer.add(xef.createEndElement(qName, null));
	}	

}
