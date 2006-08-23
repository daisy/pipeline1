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

package org.daisy.util.fileset.interfaces.xml.z3986;

import java.util.Collection;

import org.daisy.util.fileset.interfaces.xml.TextualContentFile;
import org.daisy.util.mime.MIMEConstants;

/**
 * Represents a Dtbook file in a Z3986 fileset, irrespective of Z3986 subversion
 * @author Markus Gylling
 */
public interface Z3986DtbookFile extends TextualContentFile{

	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_X_DTBOOK_XML;
	
	public String getDcIdentifier();

	public String getDcTitle();
	
	public String getDcCreator();
	
	public String getDcPublisher();

	public String getDocauthor();

	public String getDoctitle();

	public String getDtbUid();
	
	/**
	 *@return a collection&lt;String&gt; of all dc:language values in this Dtbook document. 
	 * If no dc:language values exist in this document, the return will
	 * be an emtpy collection, not null. The collection contains only unique items, and
	 * values are the untampered-with values of the dc:language attribute(s).
	 */
	public Collection getDcLanguages();
}
