package org.daisy.util.xml.xslt.stylesheets;

import java.net.URL;

/**
 * A static getter for access to the canonical stylesheets
 * who has their home in this directory. Note; the URL returned
 * may be a URL to a file in a jar; therefore use url.openStream()
 * as new File(url.toURI()) will break if in jar.
 * @author Markus Gylling
 */
public class Stylesheets {

	/**
	 * @param localName local (no path specified) name of a stylesheet placed in the same folder as this class.
	 * @return a URL of the stylesheet if found, else null.
	 */
	public static URL get(String localName) {
		return Stylesheets.class.getResource(localName);
	}
	
}
