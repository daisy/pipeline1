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
package se_tpb_filesetcreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Utility for extracting all values of src attributes from an xml-file.
 * @author Martin Blomberg
 *
 */
public class SrcExtractor {

	private QName srcAttrName = new QName("src");
	private QName hrefAttrName = new QName("href");
	private File xmlFile;
	private Set<String> srcValues = new HashSet<String>();
	
	/**
	 * @param xmlFile the file in which to look for references.
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws CatalogExceptionNotRecoverable
	 */
	public SrcExtractor(File xmlFile) throws XMLStreamException, IOException, CatalogExceptionNotRecoverable {
		this.xmlFile = xmlFile;
		
		// open a stream to xmlFile
		// check if the are any src attributes
		// if so, put them in some kind of set
		FileInputStream fileInputStream = new FileInputStream(xmlFile);
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
		inputFactory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
		XMLEventReader reader = inputFactory.createXMLEventReader(fileInputStream);
		
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			if (event.isStartElement()) {
				extractSrc(event.asStartElement());
				
				//mg20080401, may refer to css here...
				if(event.asStartElement().getName().getLocalPart()=="link") {					
					extractLink(event.asStartElement());
				}
			} else if (event.isProcessingInstruction()) {
				//added by mgylling 20080401
				extractProcIns((ProcessingInstruction)event);
			}
		}
		
		reader.close();
		fileInputStream.close();
	}
	
	private void extractProcIns(ProcessingInstruction event) {
		//added by mgylling 20080401
		//<?xml-stylesheet href="dtbookbasic.css" type="text/css"?>
		try{
			if(event.getTarget().equals("xml-stylesheet")) {
				String data = event.getData().replace("'", "\"");			
				int begin = data.indexOf("href=")+6;
				String src = data.subSequence(begin, data.indexOf("\"", begin+1)).toString();
				srcValues.add(src);
			}	
		}catch (Exception e) {

		}
	}

	/**
	 * Extracts the value of the attribute with <code>srcAttrName</code>
	 * if present. 
	 * @param se the start element to search for <code>srcAttrName</code>.
	 */
	private void extractSrc(StartElement se) {
		Attribute attrib;
		if ((attrib = se.getAttributeByName(srcAttrName)) != null) {
			String val = attrib.getValue();
			if (val != null && val.trim().length() > 0) {
				srcValues.add(val);
			}
		}
	}
	
	/**
	 * Extracts the value of the attribute with <code>hrefAttrName</code>
	 * if present. 
	 * @param se the start element to search for <code>hrefAttrName</code>.
	 */
	private void extractLink(StartElement linkElem) {
		Attribute attrib;
		if ((attrib = linkElem.getAttributeByName(hrefAttrName)) != null) {
			String val = attrib.getValue();
			if (val != null && val.trim().length() > 0 && val.toLowerCase().endsWith("css")) {
				//we want to avoid adding anything else than css for now (such as sibling dtbook docs)
				srcValues.add(val);
			}
		}
	}
	
	
	/**
	 * Returns a set containing strings. The strings are 
	 * the values of the src attrubutes.
	 * @return a set containing strings. The strings are 
	 * the values of the src attrubutes.
	 */
	public Set<String> getSrcValues() {
		return srcValues;
	}
	
	/**
	 * Returns the base directory to which the relative paths are
	 * relative.
	 * @return the base directory to which the relative paths are
	 * relative.
	 */
	public File getBaseDir() {
		return xmlFile.getParentFile();
	}
}
