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
package org.daisy.util.location;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

import org.daisy.util.text.URIUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;

/**
 *
 * @author Markus Gylling
 */
public class LocationUtils {

	/**
	 * Convert an arbitrary resource identifier into a URL.
	 * @param identifier The resource identifier
	 * @return the URL if identification was possible, else null.
	 */
	public static URL identifierToURL(String identifier) {
		return LocationUtils.identifierToAbsoluteURL(identifier, null);
	}

	/**
	 * Convert an arbitrary resource identifier into an absolute URL.
	 * @param identifier The resource identifier
	 * @param base A resource where the identifier occured. May be null.
	 * @return An absolute URL if identification was possible, else null. Note - remote URLs (http etc) will <strong>not</strong> checked for existance.
	 */
	
	public static URL identifierToAbsoluteURL(String identifier, URL base) {
		
		/*
		 * examples of inparams here:
		 *   http://www.example.com/example.html
		 *   example.html
		 *   ../stuff/example.html
		 *   ./stuff/example.html
		 *   D:/example.html
		 *   file://D:/example.sch
		 *   jarfile://D:/example.jar!example.html
		 *   bundle://06/path/example.html
		 *   -//NISO//DTD dtbook 2005-1//EN
		 */

		if(identifier==null) throw new NullPointerException(); 	
		
		identifier = identifier.trim();		
		URL returnURL = null;
		
		//Check if identifier exists in catalog
		try {
			returnURL = CatalogEntityResolver.getInstance().resolveEntityToURL(identifier);
		} catch (Exception e) {
			
		}		
		if (returnURL != null) return returnURL;
		
		
		//Check if identifier can be resolved to a file
		File file = null;
		try{	
			//absolute
			file = LocationUtils.toFile(identifier);
			if(!file.exists()) {
				file=null;
				throw new IOException("");
			}
		}catch (Throwable t) {
			if(base!=null){
				try {
					//relative
					URI baseURI = new URI(base.toExternalForm());				
					URI absolute = URIUtils.resolve(baseURI, identifier);
					file = new File(absolute);
				} catch (URISyntaxException e) {
					file=null;
				}
			}
		}
				
		if(file!=null && file.exists()) {
			try {
				returnURL = file.toURI().toURL();
				return returnURL;
			} catch (MalformedURLException e) {
				returnURL = null;
			}
		}
		
		
		//Try a bare URL instantiation
		try {
			returnURL = new URL(identifier);
			if (base!=null && !returnURL.toURI().isAbsolute()) {				
				returnURL = URIUtils.resolve(base.toURI(), returnURL.toURI()).toURL();
			}
		} catch (Exception e) {			
			returnURL = null;
		}
				
		return returnURL;
		
	}
	
    private static File toFile(String filenameOrFileURI) throws URISyntaxException, IllegalArgumentException {        
        if (schemePattern.matcher(filenameOrFileURI).matches()) {
           File f = new File(new URI(filenameOrFileURI));
           return f;        
        } 
        return new File(filenameOrFileURI);        
    }
    
    private static Pattern schemePattern = Pattern.compile("[a-z]{2,}:.*");
}
