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
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.stax.AttributeByName;

import se_tpb_daisy202splitter.NccItem.Type;

/**
 * Builds a list of NccItems
 * @author Linus Ericson
 */
public class NccItemBuilder {

    private XMLInputFactory xif;
    
    private List<NccItem> nccItems;
    private Map<File, NccItem.Type> smilLevelMap;
    
    private String dcLanguage;
    
    /**
     * Creates a new NCC item builder
     * @param xif an XMLInputFactory
     * @param input the ncc file to parse
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
	public NccItemBuilder(XMLInputFactory xif, File input) throws FileNotFoundException, XMLStreamException {
	    this.xif = xif;
		this.smilLevelMap = new HashMap<File, NccItem.Type>();
		this.nccItems = createNccItemList(input);
	}
	
	/**
	 * Gets the NCC item list
	 * @return the NCC item list
	 */
	public List<NccItem> getNccItemList() {
		return nccItems;
	}
	
	/**
	 * Gets the mapping between a SMIL file and its level (H1-H6)
	 * when first referenced from the NCC
	 * @return the SMIL-&gt;level map
	 */
	public Map<File, NccItem.Type> getSmilLevelMap() {
		return smilLevelMap;
	}
	
	/**
	 * Gets the language (the first dc:language found in the ncc) of the book
	 * @return the language of the book
	 */
	public String getLanguage() {
	    return dcLanguage;
	}
    
	/**
	 * Creates the NCC item list
	 * @param input the NCC file
	 * @return a list of NCC items
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	private List<NccItem> createNccItemList(File input) throws FileNotFoundException, XMLStreamException {      
        List<NccItem> nccItems = new ArrayList<NccItem>(); 
        
        XMLEventReader reader = null;
        try {
            reader = xif.createXMLEventReader(new FileInputStream(input));
            
            this.forwardTo(reader, "head");
            dcLanguage = this.findLanguage(reader);
            this.forwardTo(reader, "body");
            
            // Get the first NCC item
            NccItem nccItem = this.getNccItem(reader);
            
            while (nccItem != null) {
            	File smilFile = getSmilFile(input, nccItem.getUri());
            	
            	// Add the SMIL file to the SMIL->level map if it doen't exist already
            	if (!smilLevelMap.containsKey(smilFile)) {
	            	switch (nccItem.getType()) {
	            		case H1:
	            		case H2:
	            		case H3:
	            		case H4:
	            		case H5:
	            		case H6:
	            			//System.err.println("Smil " + smilFile.getName() + " is on level " + nccItem.getType().toString());
	            			smilLevelMap.put(smilFile, nccItem.getType());
	            			break;
	            		default:
	            			break;
	            	}
            	}
            	
            	nccItems.add(nccItem);            
                nccItem = this.getNccItem(reader);
            }
            
        } finally {
            try {
                reader.close();
            } catch (XMLStreamException e) {                
            }
        }
        
        return nccItems;
    }
    
	/**
	 * Finds the language of the book (the first dc:language metadata found)
	 * @param reader the XMLEventReader
	 * @return the language
	 * @throws XMLStreamException
	 */
	private String findLanguage(XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();
                if ("meta".equals(se.getName().getLocalPart())) {
                    Attribute nameAttr = AttributeByName.get(new QName("name"), se);
                    Attribute contentAttr = AttributeByName.get(new QName("content"), se);
                    if (nameAttr != null && "dc:language".equals(nameAttr.getValue()) && contentAttr != null) {
                        return contentAttr.getValue();
                    }
                }
            } else if (event.isEndElement()) {                
                if ("head".equals(event.asEndElement().getName().getLocalPart())) {
                    // No dc:language found
                    return null;
                }
            }
        }
        throw new XMLStreamException("This is not supposed to happen");
    }

	/**
	 * Forwards the XMLEventReader until a start element with the specified local name
	 * has been found.
	 * @param reader the XMLEventReader
	 * @param localName the local name to forward to
	 * @throws XMLStreamException
	 */
    private void forwardTo(XMLEventReader reader, String localName) throws XMLStreamException {
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
	
    /**
     * Resolves a SMIL file from a string uri in the ncc
     * @param nccFile the NCC file
     * @param uri the string uri
     * @return the SMIL file
     */
	private File getSmilFile(File nccFile, String uri) {
		URI nccUri = nccFile.toURI();
		int hashIndex = uri.indexOf("#");
		if (hashIndex != -1) {
			uri = uri.substring(0, hashIndex);
		}
		URI smilUri = nccUri.resolve(uri);
		File smilFile = new File(smilUri);
		return smilFile;
	}
	
	/**
	 * Gets the next NCC item from the XMLEventReader
	 * @param reader the XMLEventReader
	 * @return a NCC item
	 * @throws XMLStreamException
	 */
	private NccItem getNccItem(XMLEventReader reader) throws XMLStreamException {
		if (!reader.hasNext()) {
            return null;
        }
        
        XMLEvent event = reader.nextEvent();
        Type type = null;
        String uri = "";
        String idAttr = null;
        String text = "";
        int level = 0;
        boolean inA = false;
        
        // Skip any leading whitespace
        while (event.isCharacters() && reader.hasNext()) {
            event = reader.nextEvent();
        }        
        while (event != null) {
            if (event.isStartElement()) {
                level++;
                StartElement se = event.asStartElement();
                String localName = se.getName().getLocalPart();
                if ("h1".equals(localName)) {
                	type = Type.H1;
                	Attribute attr = AttributeByName.get(new QName("id"), se);
                	if (attr != null) {
                		idAttr = attr.getValue();
                	}
                } else if ("h2".equals(localName)) {
                	type = Type.H2;
                	Attribute attr = AttributeByName.get(new QName("id"), se);
                	if (attr != null) {
                		idAttr = attr.getValue();
                	}
                } else if ("h3".equals(localName)) {
                	type = Type.H3;
                	Attribute attr = AttributeByName.get(new QName("id"), se);
                	if (attr != null) {
                		idAttr = attr.getValue();
                	}
                } else if ("h4".equals(localName)) {
                	type = Type.H4;
                	Attribute attr = AttributeByName.get(new QName("id"), se);
                	if (attr != null) {
                		idAttr = attr.getValue();
                	}
                } else if ("h5".equals(localName)) {
                	type = Type.H5;
                	Attribute attr = AttributeByName.get(new QName("id"), se);
                	if (attr != null) {
                		idAttr = attr.getValue();
                	}
                } else if ("h6".equals(localName)) {
                	type = Type.H6;
                	Attribute attr = AttributeByName.get(new QName("id"), se);
                	if (attr != null) {
                		idAttr = attr.getValue();
                	}
                } else if ("span".equals(localName)) {
                    String classAttr = AttributeByName.get(new QName("class"), se).getValue();
                    if ("page-front".equals(classAttr)) {
                        type = Type.PAGE_FRONT;
                    } else if ("page-normal".equals(classAttr)) {
                        type = Type.PAGE_NORMAL;
                    } else if ("page-special".equals(classAttr)) {
                        type = Type.PAGE_SPECIAL;
                    } else if ("sidebar".equals(classAttr)) {
                        type = Type.SIDEBAR;
                    } else if ("optional-prodnote".equals(classAttr)) {
                        type = Type.OPTIONAL_PRODNOTE;
                    } else if ("noteref".equals(classAttr)) {
                        type = Type.NOTEREF;
                    }
                    Attribute attr = AttributeByName.get(new QName("id"), se);
                	if (attr != null) {
                		idAttr = attr.getValue();
                	}
                } else if ("a".equals(localName)) {
                    Attribute href = AttributeByName.get(new QName("href"), se);
                    uri = href.getValue();
                    inA = true;
                    text = "";
                } else {
                    return null;
                }
            } else if (event.isEndElement()) {            	
                level--;
                if (level == 0) {
                    return new NccItem(type, idAttr, uri, text);
                }
                if ("a".equals(event.asEndElement().getName().getLocalPart())) {
            		inA = false;            		
            	}
            } else if (event.isCharacters() && inA) {
            	text = text + event.asCharacters().getData();
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
