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

package org.daisy.util.file;

import java.io.IOException;

import org.daisy.util.mime.MIMEType;
import org.daisy.util.mime.MIMETypeException;

/**
 * An interface defining org.daisy.util.file.EFile  
 * @author Markus Gylling 
 */
public abstract interface IEFile {
			
	/**
	 * An extension to java.io.File.
	 */
	public boolean isSymLink() throws IOException ;

	/**
	 * An extension to java.io.File.
	 * @return the MimeType if it is set, else null.
	 */
	public MIMEType getMimeType();

	/**
	 * An extension to java.io.File.
	 * Attempts to retrieve a org.daisy.util.mime.MimeType object
	 * based on the inparam string.
	 * Note - no analysis of the relational validity of the MimeType object vis-a-vis the File is performed.
	 * @param 
	 * 		mime A MIME string identifier that may or may not be
	 * represented in the local MIME registry (org.daisy.util.mime.MimeTypeRegistry).
	 * @return 
	 * 		the MimeType object if set succeeded, else a MimeTypeException
	 * @see #getMimeType()
	 */
	public MIMEType setMimeType(String mime) throws MIMETypeException;
	
//	/**
//	 * An extension to java.io.File.
//	 * Attempts to retrieve a org.daisy.util.mime.MimeType object
//	 * by heuristical analysis of properties of this File.
//	 * @return 
//	 * 		the MimeType object if set succeeded, else a MimeTypeException
//	 */
//	public MIMEType setMimeType() throws MIMETypeException;
	
	/**
	 * An extension to java.io.File.
	 * Sets a org.daisy.util.mime.MimeType object as an attribute of this File.
	 * Note - no analysis of the relational validity of the MimeType object vis-a-vis the File is performed. 
	 * @see #setMimeType()
	 * @see #getMimeType()
	 */
	public void setMimeType(MIMEType mime);
	
	/**
	 * An extension to java.io.File.
	 */
	public EFolder getParentFolder() throws IOException ;
	
	public String getName();
	
	/**
	 * An extension to java.io.File.
	 * @return the name of the File
	 * minus extension; ("foo" from "foo.bar").
	 * If the name has no extension (no period character)
	 * then this method returns the same String as File.getName().
	 */
	public String getNameMinusExtension();

	/**
	 * An extension to java.io.File.
	 * @return the extension of the File; ("bar" from "foo.bar").
	 * If the name has no extension (no period character)
	 * then this method returns null.
	 */
	public String getExtension();

}
