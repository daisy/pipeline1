/*
 * org.daisy.util - The DAISY java utility library
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
package org.daisy.util.fileset.validation.delegate.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.fileset.validation.delegate.ValidatorDelegateImplAbstract;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;
import org.daisy.util.fileset.validation.message.ValidatorWarningMessage;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Abstract base class for delegates checking the XML declaration.
 * @author Linus Ericson
 */
public abstract class AbstractXMLDeclarationDelegate extends ValidatorDelegateImplAbstract {

	private String mXmlVersion;
	private String mXmlEncoding;
	private Boolean mXmlStandalone;
	private boolean mXmlEncodingMayBeSpecified;
	private boolean mXmlStandaloneMayBeSpecified;
	private boolean mXmlEncodingMustBeSpecified;
	private boolean mXmlStandaloneMustBeSpecified;
	
	/**
	 * Creates a new AbstractXMLDeclarationDelegate.
	 * Creating an AbstractXMLDeclarationDelegate with the parameters
	 * <code>
	 * AbstractXMLDeclarationDelegate(null, null, null, true, false, true, false) 
	 * </code> 
	 * will check for nothing whatsoever.
	 * @param version the required XML version, or null if no specific version is required.
	 * @param encoding the required encoding, or null if no specific encoding is required.
	 * @param standalone Boolean.TRUE/FALSE if the standalone property should be 'yes'/'no' respectively, or null if no specific value is required.
	 * @param encodingMayBeSpecified true if the encoding may be specified, false otherwise
	 * @param encodingMustBeSpecified true if the encoding must be specified, false otherwise
	 * @param standaloneMayBeSpecified true if the standalone property may be specified, false otherwise
	 * @param standaloneMustBeSpecified true if the standalone property must be specified, false otherwise
	 */
	public AbstractXMLDeclarationDelegate(String version, 
			                              String encoding, 
			                              Boolean standalone, 
			                              boolean encodingMayBeSpecified, 
			                              boolean encodingMustBeSpecified, 
			                              boolean standaloneMayBeSpecified, 
			                              boolean standaloneMustBeSpecified) {
		mXmlVersion = version;
		mXmlEncoding = encoding;
		mXmlStandalone = standalone;
		mXmlEncodingMayBeSpecified = encodingMayBeSpecified;
		mXmlEncodingMustBeSpecified = encodingMustBeSpecified;
		mXmlStandaloneMayBeSpecified = standaloneMayBeSpecified;
		mXmlStandaloneMustBeSpecified = standaloneMustBeSpecified;
		if (!mXmlEncodingMayBeSpecified && mXmlEncodingMustBeSpecified) {
			throw new IllegalArgumentException();
		}
		if (!mXmlStandaloneMayBeSpecified && mXmlStandaloneMustBeSpecified) {
			throw new IllegalArgumentException();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.delegate.ValidatorDelegate#isFilesetTypeSupported(org.daisy.util.fileset.FilesetType)
	 */
	public boolean isFilesetTypeSupported(FilesetType type) {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.delegate.ValidatorDelegateImplAbstract#execute(org.daisy.util.fileset.interfaces.Fileset)
	 */
	public void execute(Fileset fileset) throws ValidatorNotSupportedException, ValidatorException {		
		super.execute(fileset);
		
		Collection members = fileset.getLocalMembers();
		for (Iterator it = members.iterator(); it.hasNext(); ) {
			FilesetFile filesetFile = (FilesetFile)it.next();
			if (filesetFile instanceof XmlFile) {
				try {
					this.checkXMLDeclaration(filesetFile.getFile().toURI().toURL());
				} catch (MalformedURLException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (IllegalCharsetNameException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (UnsupportedCharsetException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (CatalogExceptionNotRecoverable e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (URISyntaxException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (XMLStreamException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (IOException e) {
					throw new ValidatorException(e.getMessage(), e);
				}
			} else {
				//System.err.println("Skipping: " + filesetFile.toString());
			}
		}		
	}

	/**
	 * Checks the XML declaration according to the rules defined by the constructor.
	 * @param url the file to check.
	 * @throws URISyntaxException
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws CatalogExceptionNotRecoverable
	 */
	private void checkXMLDeclaration(URL url) throws URISyntaxException, XMLStreamException, IOException, CatalogExceptionNotRecoverable {		
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();        
        inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);        
        inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);        
        inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
        inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        inputFactory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
        inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
        
        // Collect encoding declarations
        XMLEventReader reader = inputFactory.createXMLEventReader(url.openStream());
        String xmlDeclarationEncoding = null;
        
        while (reader.hasNext()) {
        	XMLEvent event = reader.nextEvent();
        	
        	if (event.isStartDocument()) {
        		StartDocument sd = (StartDocument)event;
        		
        		// XML version
        		if (mXmlVersion != null) {
        			if (!mXmlVersion.equals(sd.getVersion())) {
        				this.report(new ValidatorErrorMessage(url.toURI(), "Incorrect XML version. Found '" + sd.getVersion() + "', expected '" + mXmlVersion + "'.", sd.getLocation().getLineNumber(), sd.getLocation().getColumnNumber()));
        			}
        		}
        		
        		// XML encoding
        		if (sd.encodingSet()) {
    				if (!mXmlEncodingMayBeSpecified) {
        				this.report(new ValidatorWarningMessage(url.toURI(), "Encoding may not be specified in the XML declaration.", sd.getLocation().getLineNumber(), sd.getLocation().getColumnNumber()));
        			}
        			xmlDeclarationEncoding = sd.getCharacterEncodingScheme();        			
        		} else {
        			if (mXmlEncodingMustBeSpecified) {
        				this.report(new ValidatorWarningMessage(url.toURI(), "Encoding must be specified in the XML declaration. Assuming utf-8.", sd.getLocation().getLineNumber(), sd.getLocation().getColumnNumber()));
        			}
        			xmlDeclarationEncoding = "utf-8";        			        			
        		}
        		if (mXmlEncoding != null) {        			
        			if (!mXmlEncoding.equalsIgnoreCase(xmlDeclarationEncoding)) {
            			this.report(new ValidatorErrorMessage(url.toURI(), xmlDeclarationEncoding + " encoding found when " + mXmlEncoding + " was expected.", sd.getLocation().getLineNumber(), sd.getLocation().getColumnNumber()));
            		}
        		}
        		
        		// XML standalone
        		if (sd.standaloneSet()) {
    				if (!mXmlStandaloneMayBeSpecified) {
    					this.report(new ValidatorWarningMessage(url.toURI(), "The standalone property may not be specified in the XML declaration.", sd.getLocation().getLineNumber(), sd.getLocation().getColumnNumber()));
    				}
    			} else {
    				if (mXmlStandaloneMustBeSpecified) {
    					this.report(new ValidatorWarningMessage(url.toURI(), "The standalone property is not specified in the XML declaration. Assuming 'no'.", sd.getLocation().getLineNumber(), sd.getLocation().getColumnNumber()));
    				}
    			}
        		if (mXmlStandalone != null) {        			
        			if (sd.isStandalone() != mXmlStandalone.booleanValue()) {
        				this.report(new ValidatorErrorMessage(url.toURI(), "Incorrect value of standalone property in the XML declaration", sd.getLocation().getLineNumber(), sd.getLocation().getColumnNumber()));
        			}
        		}        		
        		
        		break;
        	} 
        }
        reader.close();
	}

}
