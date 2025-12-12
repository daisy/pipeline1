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
package org.daisy.util.fileset.validation.delegate.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.XmlFile;
import org.daisy.util.fileset.util.FilesetConstants;
import org.daisy.util.fileset.validation.delegate.ValidatorDelegateImplAbstract;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;
import org.daisy.util.fileset.validation.message.ValidatorSevereErrorMessage;
import org.daisy.util.i18n.CharsetDetector;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Makes sure all XML files in a file set use the encoding specified
 * in the xml declaration and possibly the metadata.
 * @author Linus Ericson
 */
public class CorrectXMLEncodingDeclarationDelegate extends ValidatorDelegateImplAbstract {
	
	private class EncodingLocation {
		public EncodingLocation(String nam, String enc, Location loc) {
			name = nam;
			encoding = enc;
			location = loc;
		}
		public String name;
		public String encoding = null;
		public Location location = null;
	}
	
	public CorrectXMLEncodingDeclarationDelegate() {
		
	}
	
	public boolean isFilesetTypeSupported(@SuppressWarnings("unused")FilesetType type) {	
		return true;		
	}

	public void execute(Fileset fileset) throws ValidatorNotSupportedException, ValidatorException {		
		super.execute(fileset);
		
		Collection<FilesetFile> members = fileset.getLocalMembers();
		for (Iterator<FilesetFile> it = members.iterator(); it.hasNext(); ) {
			FilesetFile filesetFile = it.next();
			if (filesetFile instanceof XmlFile) {
				try {
					this.checkEncoding(filesetFile.getFile().toURI().toURL());
				} catch (MalformedURLException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (IOException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (URISyntaxException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (CatalogExceptionNotRecoverable e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (XMLStreamException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (IllegalCharsetNameException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (UnsupportedCharsetException e) {
					throw new ValidatorException(e.getMessage(), e);
				}
			} else {
				//System.err.println("Skipping: " + filesetFile.toString());
			}
		}		
	}
	
	private void checkEncoding(URL url) throws IOException, URISyntaxException, CatalogExceptionNotRecoverable, XMLStreamException {
		String xmlDeclarationEncoding = null;
		List<EncodingLocation> encodingDeclarations = new ArrayList<EncodingLocation>();
		
		CharsetDetector detector = new CharsetDetector();		
		String charset = detector.detect(url);
		String[] probableCharsets = detector.getProbableCharsets();
		
		XMLInputFactory inputFactory = XMLInputFactory.newFactory(
			"javax.xml.stream.XMLInputFactory", getClass().getClassLoader());
        inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);        
        inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);        
        inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
        inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        inputFactory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
        inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
        
        // Collect encoding declarations
        InputStream is = null;
        XMLEventReader reader = null;
        try {
	        is = url.openStream();
	        reader = inputFactory.createXMLEventReader(is);
              
	        while (reader.hasNext()) {
	        	XMLEvent event = reader.nextEvent();
	        	
	        	// StartDocument
	        	if (event.isStartDocument()) {
	        		StartDocument sd = (StartDocument)event;
	        		if (sd.encodingSet()) {
	        			xmlDeclarationEncoding = sd.getCharacterEncodingScheme();        			
	        		} else {
	        			xmlDeclarationEncoding = "utf-8";        			        			
	        		}
	        		if (!this.charsetMatches(charset, probableCharsets, xmlDeclarationEncoding)) {
	        			this.report(new ValidatorSevereErrorMessage(url.toURI(), "Incorrect XML encoding declaration", sd.getLocation().getLineNumber(), sd.getLocation().getColumnNumber()));
	        		}
	        	}
	        	
	        	// StartElement
	        	if (event.isStartElement()) {
	        		StartElement se = event.asStartElement();
	        		// XHTML 1.0
	        		if (se.getName().getNamespaceURI().equals(FilesetConstants.NAMESPACEURI_XHTML10)) {
	        			if (se.getName().getLocalPart().equals("meta")) {
	        				Attribute name = se.getAttributeByName(new QName("name"));
	        				Attribute content = se.getAttributeByName(new QName("content"));
	        				if (name != null && name.getValue().equals("ncc:charset") && content != null) {
	        					EncodingLocation encLoc = new EncodingLocation("ncc:charset", content.getValue(), se.getLocation());
	        					encodingDeclarations.add(encLoc);        					
	        				}
	        			}        			
	        		}
	        	}
	        }
	        
	        // Make sure all collected encoding declarations have the same value.
	        for (Iterator<EncodingLocation> it = encodingDeclarations.iterator(); it.hasNext(); ) {
	        	EncodingLocation encLoc = it.next();
	        	if (!xmlDeclarationEncoding.equalsIgnoreCase(encLoc.encoding)) {
	        		this.report(new ValidatorErrorMessage(url.toURI(), encLoc.name + " differs from the XML encoding declaration.", encLoc.location.getLineNumber(), encLoc.location.getColumnNumber()));
	        	}
	        	if (!this.charsetMatches(charset, probableCharsets, encLoc.encoding)) {
	        		this.report(new ValidatorErrorMessage(url.toURI(), "Incorrect encoding declaration.", encLoc.location.getLineNumber(), encLoc.location.getColumnNumber()));
	        	}
	        }
        } finally {
        	if (reader != null) {
        		reader.close();
        	}
        	if (is != null) {
        		is.close();
        	}
        }
	}
	
	/**
	 * Checks if the declared encoding matches the detected charset.
	 * @param charset the detected charset, or null if multiple charsets are applicable
	 * @param probableCharsets a list of probable charsets
	 * @param declaredEncoding the declared encoding
	 * @return true if there is a match in the detected or probable encodings, false otherwise
	 * @throws IllegalCharsetNameException
	 * @throws UnsupportedCharsetException
	 */
	private boolean charsetMatches(String charset, String[] probableCharsets, String declaredEncoding) throws IllegalCharsetNameException, UnsupportedCharsetException {
		Charset declared = Charset.forName(declaredEncoding);
		if (charset != null) {
			Charset cs = Charset.forName(charset);
			return cs.equals(declared);			
		}
		for (int i = 0; i < probableCharsets.length; ++i) {
			Charset prob = Charset.forName(probableCharsets[i]);
			if (prob.equals(declared)) {
				return true;
			}
		}
		return false;
	}
}
