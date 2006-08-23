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

package org.daisy.util.fileset.util;

/**
 * This class does not duplicate java.net.URI,
 * but provides highspeed substring getters for URIs in string form.
 * @author Markus Gylling
 */
public class URIStringParser {
	
	public static String stripFragment(String uri) {				
		StringBuilder sb = new StringBuilder();
		int length = uri.length();
		char hash = '#';
		for (int i = 0; i < length; i++) {
			if (uri.charAt(i)==hash) {
				return sb.toString();
			}
			sb.append(uri.charAt(i));			
		}
		return sb.toString();								
	}

	public static String getFragment(String uri) {					
		StringBuilder sb = new StringBuilder();
		char hash = '#';		
		int hashPos = -1;
		
		for (int i = 0; i < uri.length(); i++) {
			if (uri.charAt(i)==hash) {
				hashPos = i;
			}else{
				if (hashPos > -1) sb.append(uri.charAt(i));
			}  
		}
		return sb.toString();								
	}
	
}
