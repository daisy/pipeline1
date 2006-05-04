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
