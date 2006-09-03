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

package org.daisy.util.fileset.interfaces.sgml;

import javax.xml.namespace.QName;

import org.daisy.util.mime.MIMEConstants;

public interface HtmlFile extends SgmlFile {
	static String mimeStringConstant = MIMEConstants.MIME_TEXT_HTML;
	
	/**
	 *@return true if <code>idval</code> exists as the value of an attribute named <code>id</code> in the document, false otherwise
	 *@see #hasIDValueOnQName(String, QName)
	 */
	public boolean hasIDValue(String idval);
	
	/**
	 *@return true if <code>idval</code> exists as the value of an attribute named <code>id</code> on an element <code>qName</code> in the document, false otherwise
	 *@see #hasIDValue(String)
	 */
	public boolean hasIDValueOnQName(String idval, QName qName);
}
