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
import java.net.URI;
import java.net.URISyntaxException;
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
import org.daisy.util.xml.stax.AttributeByName;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Utility for extracting all values of src attributes from an xml-file.
 * @author Martin Blomberg
 *
 */
public class SrcExtractor {

//	private QName srcAttrName = new QName("src");
//	private QName hrefAttrName = new QName("href");
	private File xmlFile;	
	private Set<String> srcValues = new HashSet<String>();
	private Set<String> extSrcValues;
	private Set<QName> uriCarriers;
	
	/**
	 * @param xmlFile the file in which to look for references.
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws CatalogExceptionNotRecoverable
	 */
	public SrcExtractor(File xmlFile) throws XMLStreamException, IOException, CatalogExceptionNotRecoverable {
		this.xmlFile = xmlFile;
		
		/*
		 * All known carriers of URIs in dtbook manuscripts, and resource files
		 * TODO this whole class can be discarded, use Fileset instead
		 */
		this.uriCarriers = new HashSet<QName>();
		uriCarriers.add(new QName("src"));	   //dtbook, resourcefile
		uriCarriers.add(new QName("href"));	   //dtbook
		uriCarriers.add(new QName("altimg"));  //math
						
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
				StartElement se = event.asStartElement();
				extractURI(se);
			} else if (event.isProcessingInstruction()) {
				extractProcIns((ProcessingInstruction)event);
			}
		}
		
		reader.close();
		fileInputStream.close();
	}
	
	private void extractURI(StartElement se) {
		Attribute attrib;
		for(QName q : uriCarriers) {
			if ((attrib = AttributeByName.get(q,se)) != null) {
				String val = attrib.getValue();
				if (val != null && val.trim().length() > 0) {
					srcValues.add(val);
				}
			}	
		}		
	}

	private void extractProcIns(ProcessingInstruction event) {
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
	 * Returns a set containing strings. The strings are 
	 * the values of the URI carrier attributes.
	 * @return a set containing strings. The strings are 
	 * the values of the URI carrier attributes.
	 */
	public Set<String> getSrcValues() {
		return srcValues;
	}
	
	/**
	 * Returns a set of URI string values of the external URI carrier attributes
	 * pointing to local relative resources.
	 * 
	 * @return a set of URI string values of the external URI carrier attributes
	 *         pointing to local relative resources.
	 */
	public Set<String> getRelativeResources() {
		synchronized (srcValues) {
			if (extSrcValues == null) {
				extSrcValues = new HashSet<String>();
				for (String src : srcValues) {
					try {
						URI uri = new URI(src);
						if (!uri.isAbsolute()) {
							String path = uri.getPath();
							if (path != null && path.length() > 0) {
								extSrcValues.add(path);
							}
						}
					} catch (URISyntaxException e) {
						throw new IllegalArgumentException(e);
					}
				}
			}
		}
		return extSrcValues;
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
