package org.daisy.util.dtb.resource;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Getter for a set of static default Zed Resource files.
 * @author Markus Gylling
 */
public class ResourceFile {

	public static enum Type {
		TEXT_ONLY;
	}
	
	/**
	 * Get a set of URLs that represent a resource file and 
	 * its possible auxilliary files. One of the URLs returned
	 * is that of the resource file.
	 */
	public static Set<URL> get(Type type) {
		Set<String> filenames = new HashSet<String>();
		
		if(type == Type.TEXT_ONLY){
			filenames.add("text.res"); 
		}
		
		Set<URL> urls = new HashSet<URL>();
		
		for(String name : filenames) {
			urls.add(ResourceFile.class.getResource(name));
		}
		
		return urls;
	}
}
