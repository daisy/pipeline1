/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
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

package org.daisy.util.mime;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.daisy.util.file.EFolder;


/**
 * Used for retrieval of MimeType objects.
 * MimeTypeFactory can only return MimeType objects
 * that are represented in the local MimeTypeRegistry.
 * @author Markus Gylling
 */
public class MIMETypeFactory {
	private boolean usingLooseHeuristics = false;
	
	public static MIMETypeFactory newInstance() {
		return new MIMETypeFactory();
	}
	
	/**
	 * Attempt to create a MimeType object based on the
	 * the inparam String.
	 * @param
	 * 	mime a MIME string ("application/pdf", "text/html").
	 * @return a MimeType object retrived from the MimeTypeRegistry, 
	 * or a MimeTypeFactoryException if MimeTypeRegistry retrieval failed. 
	 * @throws MIMETypeFactoryException
	 */
	public MIMEType newMimeType(String mime) throws MIMETypeFactoryException {
		try {
			//strip parameters in order to match against canonical name
			String stripped = MIMEConstants.dropParametersPart(mime);
			MIMETypeImpl test = (MIMETypeImpl)MIMETypeRegistry.getInstance().getEntryByName(stripped);			
			if (test != null) {
				//create a new instance (we dont want a pointer to the registry canonical MimeType
				//since wild mime string may contain parameters, and may mutate more down the road).
				MIMEType newMime = 	new MIMETypeImpl
					(mime,test.registryId,test.aliasIdrefs,test.parentIdrefs,test.namePatterns);				
				return newMime;
			}
			throw new MIMETypeFactoryException(stripped + " not represented in MimeTypeRegistry");
		} catch (MIMETypeRegistryException e) {
			throw new MIMETypeFactoryException(e.getMessage(),e);
		}		
	}

	/**
	 * Attempt to heuristically identify
	 * a MIME type for the inparam File. Note - this
	 * method may fail even if the MIME type
	 * is represented in the MimeTypeRegistry.
	 * @param
	 * 	file an object for which a MIME type should be detected.
	 * @return a MimeType object corresponding to
	 * the input File, or MimeTypeFactoryException if MimeTypeRegistry retrieval failed. 
	 * @throws MIMETypeFactoryException
	 */
	public MIMEType newMimeType(File file) throws MIMETypeFactoryException {		
		throw new MIMETypeFactoryException("not implemented yet");			
	}
	
	/**
	 * Attempts to heuristically identify
	 * a MIME type for the inparam resource. Note - this
	 * method may fail even if the MIME type
	 * is represented in the MimeTypeRegistry.
	 * @param
	 * 	url an object for which a MIME type should be detected.
	 * @return a MimeType object corresponding to
	 * the input resource, or MimeTypeFactoryException if MimeTypeRegistry retrieval failed. 
	 * @throws MIMETypeFactoryException
	 */
	public MIMEType newMimeType(URL url) throws MIMETypeFactoryException {
		throw new MIMETypeFactoryException("not implemented yet");	
	}

	/**
	 * Attempts to heuristically identify
	 * a collective "fileset" MIME type for the children (or a subset of the chldren)of the inparam Folder.
	 * Note - fileset MIME types are not the same as IANA Multipart MIME. 
	 * Note - this method may fail even if the MIME type
	 * is represented in the MimeTypeRegistry.
	 * @param
	 * 	folder a directory for whose children a "fileset" MIME type should be detected.
	 * @return a MimeType object corresponding to
	 * the input resource, or MimeTypeFactoryException if MimeTypeRegistry retrieval failed. 
	 * @throws MIMETypeFactoryException
	 */
	public MIMEType newMimeType(EFolder folder) throws MIMETypeFactoryException {
		throw new MIMETypeFactoryException("not implemented yet");
	}

	/**
	 * Specifies whether the MIME type detection should be permissive (loose heuristics) or hardnose academic.
	 */
	public void setUseLooseHeuristics(boolean loose) {
		usingLooseHeuristics = loose;		
	}

	/**
	 * Indicates whether the MIME type detection is permissive (loose heuristics) or hardnose academic.
	 */
	public boolean isUsingLooseHeuristics() {
		return usingLooseHeuristics;
	}

}
