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

/**
 * Used for retrieval of MimeType objects.
 * MimeTypeFactory can only return MimeType objects
 * that are represented in the local MimeTypeRegistry.
 * @author Markus Gylling
 */
public class MIMETypeFactory {
		
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

}
