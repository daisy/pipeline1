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

package org.daisy.util.xml.xslt.stylesheets;

import java.net.URL;

import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;

/**
 * A static getter for access to the canonical stylesheets
 * who has their home in this directory. Note; the URL returned
 * may be a URL to a file in a jar; therefore use url.openStream()
 * as new File(url.toURI()) will break if in jar.
 * @author Markus Gylling
 */
public class Stylesheets {
		
	/**
	 * Get the URL of an XSLT stylesehet
	 * @param identifier A system id of the wanted stylesheet (check ./catalog.xml).
	 * @return a URL of the stylesheet if found, else null
	 */
	public static URL get(String identifier) {
		try {
			return StylesheetResolver.getInstance().resolve(identifier);
		} catch (CatalogExceptionNotRecoverable e) {
			return null;
		}								
	}
	
}
