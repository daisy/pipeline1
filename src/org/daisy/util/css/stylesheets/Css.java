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
package org.daisy.util.css.stylesheets;

import java.net.URL;

/**
 * A provider of canonical CSS stylesheets
 * who has their home in this directory. Note; the URL returned
 * may be a URL to a file in a jar; therefore use url.openStream() 
 * to get to the data.
 * @author Markus Gylling
 */

public class Css {

	private static final String D202_DEFAULT_CSS = "d202.basic.css";
	private static final String Z3986_DEFAULT_CSS = "dtbook.2005.basic.css";
	
	/**
	 * @param localName local (no path specified) name of a CSS stylesheet placed in the same folder as this class.
	 * @return a URL of the stylesheet if found, else null.
	 */
	public static URL get(String localName) {
		return Css.class.getResource(localName);
	}
	
	/**
	 * @param docType the document type to provide a stylesheet for
	 * @return a URL of a default CSS stylesheet for inparam DocumentType.
	 */
	public static URL get(DocumentType docType) {
		if(docType == DocumentType.D202_XHTML) {
			return Css.class.getResource(D202_DEFAULT_CSS);
		}else
		if(docType == DocumentType.Z3986_DTBOOK) {
			return Css.class.getResource(Z3986_DEFAULT_CSS);
		}
		throw new IllegalArgumentException();
	}
	
	public static enum DocumentType {
		Z3986_DTBOOK,
		D202_XHTML;
	}
}
