package org.daisy.util.file.detect;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.DoctypeParser;

/**
 * Parse a to-be detected resource and expose its XML root or byte header properties 
 * @author Markus Gylling
 */
/*package*/ class ResourceParser {

	private static final int maxHeaderLength = 30;
	/**
	 * Parse the incoming resource.
	 * @return a ResourceXMLProperties or a ResourceByteProperties object, where xml takes precedence.
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws XMLStreamException 
	 */
	/*package*/ static ResourceProperties parse(URL resource) throws URISyntaxException, IOException, XMLStreamException {
		/*
		 * Assume the resource is an XML file.
		 * If its prolog and root start element can be read without an exception,
		 * return a ResourceXMLProperties object.
		 * Else, return a ResourceByteProperties object.
		 */
		
		String fileName = getFileName(resource);
		
		XMLInputFactory xif = null;
		Map properties = null;
		XMLEventReader reader = null;
		try {			
			properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xif = StAXInputFactoryPool.getInstance().acquire(properties);					        
			reader = xif.createXMLEventReader(resource.openStream());
			String publicID = null;
			String systemID = null;
			StartElement startElement = null;
			while (reader.hasNext()) {			
			    XMLEvent event = reader.nextEvent();
			    if (event.isEndDocument()) {			       
			       break;
			    }
			    else if (event.getEventType() == XMLEvent.DTD) {
			    	DTD dtd = (DTD)event;
			    	DoctypeParser dtp = new DoctypeParser(dtd.getDocumentTypeDeclaration());
			    	publicID = dtp.getPublicId();
			    	systemID = dtp.getSystemId();
			    }
			    else if (event.isStartElement()) {
			    	startElement = event.asStartElement();
			    	return new ResourceXMLProperties(fileName, publicID,systemID,startElement);
			    } 	
			}
		}catch (Exception e) {
			
		} finally {
			reader.close();
			StAXInputFactoryPool.getInstance().release(xif, properties);
		}
		
		return new ResourceByteProperties(fileName,getByteData(resource, maxHeaderLength));
		
	}
	
	private static String getFileName(URL url) throws URISyntaxException {		
		File f = new File(url.toURI());
		return f.getName();
	}
	
	private static byte[] getByteData(URL url, int length) throws IOException {
		InputStream is = url.openStream();		
		byte[] bb = new byte[length];
		is.read(bb,0,length);
		is.close();
		return bb;
	}

	/**
	 * Return true if resource contains list, post whitespace trim.
	 * It is assumed that inparam list contains no whitespace but leading and trailing.
	 * @throws IOException 
	 * @throws XMLStreamException 
	 */
	public static boolean matches(URL resource, List<XMLEvent> list) throws XMLStreamException, IOException {
		CircularFifoXMLEventBuffer fifo = new CircularFifoXMLEventBuffer(list.size());
		XMLInputFactory xif = null;
		Map properties = null;
		XMLEventReader reader = null;
		try {			
			properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xif = StAXInputFactoryPool.getInstance().acquire(properties);					        
			reader = xif.createXMLEventReader(resource.openStream());
			while (reader.hasNext()) {			
			    XMLEvent xe = reader.nextEvent();
			    if(!isWhitespace(xe)) {
			    	//add to buffer
			    	fifo.append(xe);
			    	//check for equality
			    	if(fifo.equals(list)) {
			    		return true;
			    	}
			    }
			}			
		} finally {
			reader.close();
			StAXInputFactoryPool.getInstance().release(xif, properties);
		}
		return false;
	}
	
	private static boolean isWhitespace(XMLEvent xe) {
		if (xe.getEventType() == XMLEvent.SPACE) return true;
		if(xe.isCharacters() && Space.isXMLWhiteSpace(xe.asCharacters().getData())) return true;
		return false;
	}
	
	/**
	 * Remove leading and trailing whitespace events. Trim leading and trailing whitespace in non-whitespace Character events.
	 */
	public static List<XMLEvent> trim(List<XMLEvent> nodes) {						
		while(!nodes.isEmpty()) {
			XMLEvent e = nodes.get(0);
			if(isWhitespace(e)){				
				nodes.remove(0);	
			}else{
				break;
			}
		}
		
		while(!nodes.isEmpty()) {
			XMLEvent e = nodes.get(nodes.size()-1);
			if(isWhitespace(e)){				
				nodes.remove(nodes.size()-1);	
			}else{
				break;
			}
		}
		
		
		for(XMLEvent xe : nodes) {
			if(xe.isCharacters()) {
				trim(xe);		
			}	
		}		
		return nodes;
	}
	
	/**
	 * Trim a non-whitespace characters event so that any leading and trailing whitespace is removed.
	 * If the event is all whitespace or a non-character event, the event is returned untouched.
	 */
	public static XMLEvent trim(XMLEvent node) {						
		if(node.isCharacters()) {
			if(!isWhitespace(node)) {
				String value = node.asCharacters().getData().trim();
				if(value.length()!= node.asCharacters().getData().length()) {
					XMLEventFactory xef = StAXEventFactoryPool.getInstance().acquire();
					Characters ret = xef.createCharacters(value);
					StAXEventFactoryPool.getInstance().release(xef);
					return ret;
				}
			}
		}
		return node;
	}
}
