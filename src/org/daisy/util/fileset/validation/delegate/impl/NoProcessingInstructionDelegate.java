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
 */package org.daisy.util.fileset.validation.delegate.impl;

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
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.XmlFile;
import org.daisy.util.fileset.validation.delegate.ValidatorDelegateImplAbstract;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * This delegate reports any processing instruction that appear in
 * any XML file of the Fileset.
 * @author Linus Ericson 
 */
public class NoProcessingInstructionDelegate extends
		ValidatorDelegateImplAbstract {

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.delegate.ValidatorDelegate#isFilesetTypeSupported(org.daisy.util.fileset.FilesetType)
	 */
	public boolean isFilesetTypeSupported(@SuppressWarnings("unused")FilesetType type) {		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.delegate.ValidatorDelegateImplAbstract#execute(org.daisy.util.fileset.interfaces.Fileset)
	 */
	public void execute(Fileset fileset) throws ValidatorNotSupportedException, ValidatorException {
		super.execute(fileset);
		
		Collection<FilesetFile> members = fileset.getLocalMembers();
		for (Iterator<FilesetFile> it = members.iterator(); it.hasNext(); ) {
			FilesetFile filesetFile = it.next();
			if (filesetFile instanceof XmlFile) {
				try {
					this.checkForProcessingInstruction(filesetFile.getFile().toURI().toURL());
				} catch (MalformedURLException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (IllegalCharsetNameException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (UnsupportedCharsetException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (CatalogExceptionNotRecoverable e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (XMLStreamException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (IOException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (URISyntaxException e) {
					throw new ValidatorException(e.getMessage(), e);
				}
			} else {
				//System.err.println("Skipping: " + filesetFile.toString());
			}
		}		
	}
	
	/**
	 * Checks for processing instructions in an XML file.
	 * @param url the URL to check.
	 * @throws CatalogExceptionNotRecoverable
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void checkForProcessingInstruction(URL url) throws CatalogExceptionNotRecoverable, XMLStreamException, IOException, URISyntaxException {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();        
        inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);        
        inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);        
        inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
        inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        inputFactory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
        inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
        
        XMLEventReader reader = inputFactory.createXMLEventReader(url.openStream());
        
        while (reader.hasNext()) {
        	XMLEvent event = reader.nextEvent();
        	
        	if (event.isProcessingInstruction()) {
        		this.report(new ValidatorErrorMessage(url.toURI(), "Processing instructions are not allowed", event.getLocation().getLineNumber(), event.getLocation().getColumnNumber()));
        	}
        }
	}

}
