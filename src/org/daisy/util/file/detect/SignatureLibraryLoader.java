/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
package org.daisy.util.file.detect;

import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.mime.MIMEType;
import org.daisy.util.mime.MIMETypeRegistry;
import org.daisy.util.mime.MIMETypeRegistryException;
import org.daisy.util.xml.pool.StAXInputFactoryPool;

/**
 * Utility class to create a SignatureLibrary instance of an XML serialization. 
 * @author Markus Gylling
 */
/*package*/ class SignatureLibraryLoader {

	/*package*/static Set<Signature> load(URL doc) throws SignatureLibraryException {
				
		XMLInputFactory xif = null;
		Map<String,Object> properties = null;
		try {			
			properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xif = StAXInputFactoryPool.getInstance().acquire(properties);					        
			XMLEventReader reader = xif.createXMLEventReader(doc.openStream());
			return buildSet(reader);						
		} catch (Exception e) {
			throw new SignatureLibraryException(e.getMessage(),e);		
		} finally {
			StAXInputFactoryPool.getInstance().release(xif, properties);
		}						
	}
	
//	/**
//	 * Validate an XML instance of a SignatureLibrary against SignatureLibrary.rng
//	 */
//	/*package*/ static void validate(URL doc, ErrorHandler errH) throws SAXException, TransformerException, ValidationException {
//		URL schema = SignatureLibraryLoader.class.getResource("SignatureLibrary.rng");
//		SimpleValidator validator = new SimpleValidator(schema, errH);
//		validator.validate(doc);
//	}
	
	/**
	 * Read a SignatureRegistry.rng compliant document and build a Set&lt;Signature&gt; of it.
	 * @param reader
	 * @throws XMLStreamException
	 * @throws MIMETypeRegistryException 
	 */
	private static Set<Signature> buildSet(XMLEventReader reader) throws XMLStreamException, MIMETypeRegistryException {
		Set<Signature> set = new HashSet<Signature>();

		boolean xmlHeaderOpen = false;
		boolean xmlAnywhereOpen = false;
		boolean byteHeaderOpen = false;
		MIMEType currentMime = null;
		String currentNameRegex = null;
		String currentImplementors = null;
		String currentNiceName = null;
		StartElement currentStartElement = null;
		String currentPublicId = null;
		String currentSystemId = null;
		byte[] currentByteList = null;
		List<XMLEvent> currentXmlAnyWhereList = null;
		
		Set<XMLRootToken> xmlRootTokens = new HashSet<XMLRootToken>();
		Set<ByteHeaderToken> byteTokens = new HashSet<ByteHeaderToken>();
		Set<XMLExtendedToken> xmlExtendedTokens = new HashSet<XMLExtendedToken>();
		
		while (reader.hasNext()) {			
		    XMLEvent event = reader.nextEvent();
		    if (event.isEndDocument()) {
		       reader.close();
		       break;
		    }
	    	
		    if(xmlAnywhereOpen && (!event.isEndElement() || (event.isEndElement() && !event.asEndElement().getName().getLocalPart().equals("xmlAnywhere")))) {
	    		currentXmlAnyWhereList.add(event);
	    	}
	    	
		    else if (event.isStartElement()) {
		    	StartElement se = event.asStartElement();
		    	if(xmlHeaderOpen) {
		    		currentStartElement = se;
		    	}
		    	else if(se.getName().getLocalPart().equals("signature")) {		
		    		
		    		currentStartElement=null;
		    		currentByteList=null;
		    		
		    		//TODO assure dont crash if these values are ""
		    		currentMime = MIMETypeRegistry.getInstance().getEntryById(se.getAttributeByName(new QName("mimeRef")).getValue());
		    		currentNameRegex = se.getAttributeByName(new QName("nameRegex")).getValue();
		    		currentImplementors = se.getAttributeByName(new QName("implementors")).getValue();
		    		currentNiceName = se.getAttributeByName(new QName("niceName")).getValue();
		    	}
		    	else if(se.getName().getLocalPart().equals("xmlHeader")) {
		    		xmlHeaderOpen = true;	
		    		Attribute a = null;
		    		a = se.getAttributeByName(new QName("publicId"));
		    		if(a!=null) {
		    			currentPublicId = a.getValue();
		    		}
		    		a = se.getAttributeByName(new QName("systemId"));
		    		if(a!=null) {
		    			currentSystemId = a.getValue();
		    		}
		    	}else if(se.getName().getLocalPart().equals("xmlAnywhere")) {
		    		currentXmlAnyWhereList = new LinkedList<XMLEvent>();
		    		xmlAnywhereOpen = true;
		    	}
		    	else if(se.getName().getLocalPart().equals("byteHeader")) {
		    		byteHeaderOpen = true;		    				    		
		    	}
		    }
		    else if (event.isEndElement()) {
		    	EndElement ee = event.asEndElement();
		    	if(ee.getName().getLocalPart().equals("signature")) {
		    		Signature sig = null;
		    		if(currentStartElement!=null) {
		    			sig = new XMLSignature(currentMime,currentNameRegex,currentImplementors,currentNiceName);
		    			for(XMLRootToken t : xmlRootTokens) {
		    				((XMLSignature)sig).addHeaderToken(t);
		    			}
		    			for(XMLExtendedToken t : xmlExtendedTokens) {
		    				((XMLSignature)sig).addExtendedToken(t);
		    			}
		    			xmlRootTokens.clear();
		    			xmlExtendedTokens.clear();
		    		}else if (currentByteList != null) {
		    			sig = new ByteHeaderSignature(currentMime,currentNameRegex,currentImplementors,currentNiceName);
		    			for(ByteHeaderToken t : byteTokens) {
		    				((ByteHeaderSignature)sig).addSignatureToken(t);
		    			}
		    			byteTokens.clear();
		    		}else{
		    			//we have a signature with neither xml nor byte children (only mime, nameregex and implementors)
		    			sig = new WeakSignature(currentMime,currentNameRegex,currentImplementors,currentNiceName);
		    		}
		    		
		    		assert(sig!=null);
		    		
		    		set.add(sig);
		    		
		    		currentMime=null;
		    		currentImplementors=null;
		    		currentNameRegex=null;
		    		currentNiceName=null;
		    		currentPublicId=null;
		    		currentSystemId=null;
		    		currentXmlAnyWhereList = null;
		    	}
		    	else if(ee.getName().getLocalPart().equals("xmlHeader")) {
		    		xmlRootTokens.add(new XMLRootToken(currentStartElement,currentPublicId,currentSystemId));
		    		xmlHeaderOpen = false;	
		    	}else if(ee.getName().getLocalPart().equals("xmlAnywhere")) {	
		    		xmlExtendedTokens.add(new XMLExtendedToken(currentXmlAnyWhereList));
		    		xmlAnywhereOpen = false;	
		    	}
		    	else if(ee.getName().getLocalPart().equals("byteHeader")) {
		    		byteTokens.add(new ByteHeaderToken(currentByteList));
		    		byteHeaderOpen = false;		    				    		
		    	}
		    }
		    else if (event.isCharacters() && byteHeaderOpen) {
		    	String[] stringArray = event.asCharacters().getData().split(" ");
		    	byte[] byteArray = new byte[stringArray.length];
		    	for (int i = 0; i < byteArray.length; i++) {
		    		Integer a = Integer.decode(stringArray[i]);
		    		byteArray[i] = a.byteValue();
				}
		    	currentByteList = byteArray;		    			    	
		    }		    
		} // while
			
		return set;
	}
}
