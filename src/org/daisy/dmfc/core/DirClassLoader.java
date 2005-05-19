/*
 * DMFC - The DAISY Multi Format Converter
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
package org.daisy.dmfc.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * A class loader that can load classes and resources from specified
 * directories that are not in the classpath.
 * @author Linus Ericson
 */
public class DirClassLoader extends URLClassLoader {

	private File classDir;
	private File resourceDir;
	
	/**
	 * Creates a new class loader that can also load classes and resources 
	 * from the specified directories.
	 * @param a_classDir directory for class files
	 * @param a_resourceDir directory for resource files
	 */
	public DirClassLoader(File a_classDir, File a_resourceDir) {
		super(new URL[]{fileToURL(a_classDir)});
		classDir = a_classDir;
		resourceDir = a_resourceDir;
	}
	
	private static URL fileToURL(File a_file) {
	    try {
            return a_file.toURL();
        } catch (MalformedURLException e) {
            // Nothing
        }
        return null;
	}
		
	public void addJar(File a_jar) {
	    this.addURL(fileToURL(a_jar));
	}
	
	public String toString() {
		return super.toString() + "(" + classDir.getAbsolutePath() + ", " + resourceDir.getAbsolutePath() + ")";
	}
	
	/**
	 * Gets a resource from the file system.
	 * @param a_resource name of the resource to load
	 * @return a URL to the resource, or <code>null</code> if the resource is not found
	 */
	public URL getResource(String a_resource) {
		//System.err.println("Trying to fetch " + a_resource);
		File _resourceFile = new File(resourceDir, a_resource);
		if (_resourceFile.canRead()) {
			try {
				System.err.println("Found resource at " + _resourceFile.getAbsolutePath());
				return _resourceFile.toURL();
			} catch (MalformedURLException e) {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Load a class. This implementation differs from the default implementation
	 * in the order of how the classes are searched for. This function tries to 
	 * load the class from the directory specified in the constructor <b>before</b>
	 * it calls the parent class loader.
	 * @param className class name of the Class to load
	 * @return the loaded Class 
	 * @throws if the specified class is not found
	 */
	public Class loadClass(String className) throws ClassNotFoundException {
		Class foundClass = findLoadedClass(className);
		if (foundClass != null) {		   
			return foundClass;
		}
		try {
			foundClass = findClass(className);			
		} catch (ClassNotFoundException e) {		    
			foundClass = super.loadClass(className);
		}		
		return foundClass;
	}

}
