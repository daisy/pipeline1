/*
 * org.daisy.util (C) 2005-2010 Daisy Consortium
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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.XmlFile;
import org.daisy.util.fileset.validation.delegate.ValidatorDelegateImplAbstract;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;

/**
 * This delegate reports missing DOCTYPE declarations in any XML file of the
 * Fileset.
 * 
 * @author Christian Egli
 * 
 */
public class NoDocTypeDeclarationDelegate extends ValidatorDelegateImplAbstract {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.daisy.util.fileset.validation.delegate.ValidatorDelegate#
	 * isFilesetTypeSupported(org.daisy.util.fileset.FilesetType)
	 */
	public boolean isFilesetTypeSupported(FilesetType type) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.daisy.util.fileset.validation.delegate.ValidatorDelegateImplAbstract
	 * #execute(org.daisy.util.fileset.Fileset)
	 */
	@Override
	public void execute(Fileset fileset) throws ValidatorNotSupportedException, ValidatorException {
		super.execute(fileset);

		Collection<FilesetFile> members = fileset.getLocalMembers();
		for (Iterator<FilesetFile> it = members.iterator(); it.hasNext();) {
			FilesetFile filesetFile = it.next();
			if (filesetFile instanceof XmlFile) {
				try {
					this.checkForDocTypeDeclaration(filesetFile.getFile().toURI().toURL());
				} catch (MalformedURLException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (XMLStreamException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (IOException e) {
					throw new ValidatorException(e.getMessage(), e);
				} catch (URISyntaxException e) {
					throw new ValidatorException(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Checks for processing instructions in an XML file.
	 * @param url the URL to check.
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private void checkForDocTypeDeclaration(URL url) throws XMLStreamException, IOException,
			URISyntaxException {
		boolean foundDocTypeDecl = false;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader eventReader = inputFactory.createXMLEventReader(url.openStream());

		while (!foundDocTypeDecl && eventReader.hasNext()) {
			XMLEvent event = (XMLEvent) eventReader.next();
			if (event.getEventType() == XMLStreamConstants.DTD
					&& ((DTD) event).getDocumentTypeDeclaration() != null) {
				foundDocTypeDecl = true;
			}
		}
		if (!foundDocTypeDecl) {
			this.report(new ValidatorErrorMessage(url.toURI(), "Document Type Declaration is required"));
		}
	}
}
