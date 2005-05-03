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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A class loader that can load classes and resources from specified
 * directories that are not in the classpath.
 * @author Linus Ericson
 */
public class DirClassLoader extends ClassLoader {

	private File classDir;
	private File resourceDir;
	
	/**
	 * Creates a new class loader that can also load classes and resources 
	 * from the specified directories.
	 * @param a_classDir directory for class files
	 * @param a_resourceDir directory for resource files
	 */
	public DirClassLoader(File a_classDir, File a_resourceDir) {
		classDir = a_classDir;
		resourceDir = a_resourceDir;
	}
	
	/**
	 * Finds a class on the file system.
	 * @param a_classname name of the Class to load
	 * @return the Class
	 * @throws ClassNotFoundException if the specified class is not found
	 */
	public Class findClass(String a_classname) throws ClassNotFoundException {
		String _separator = File.separator;		
		_separator = _separator.replaceAll("\\\\", "\\\\\\\\");         // Aargh!
		String _newClassName = a_classname.replaceAll("\\.", _separator);
		File _classFile = new File(classDir, _newClassName + ".class");
		if (!_classFile.exists()) {
			throw new ClassNotFoundException("Can't locate class at: " + _classFile.getAbsolutePath());
		}
		byte[] data = new byte[(int)_classFile.length()];
		try {
			FileInputStream _fis = new FileInputStream(_classFile);
			int _num = _fis.read(data);
			if (_num != _classFile.length()) {
				System.err.println("The file was not completely read");
			}
		} catch (FileNotFoundException e) {
			throw new ClassNotFoundException("Can't locate class at: " + _classFile.getAbsolutePath());
		} catch (IOException e) {
			throw new ClassNotFoundException("Can't read class at: " + _classFile.getAbsolutePath());
		}
		return defineClass(a_classname, data, 0, data.length);
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
	 * @param a_className class name of the Class to load
	 * @return the loaded Class 
	 * @throws if the specified class is not found
	 */
	public Class loadClass(String a_className) throws ClassNotFoundException {
		Class _loadedClass = findLoadedClass(a_className);
		if (_loadedClass != null) {
			return _loadedClass;
		}
		try {
			_loadedClass = findClass(a_className);
		} catch (ClassNotFoundException e) {
			_loadedClass = super.loadClass(a_className);
		}		
		return _loadedClass;
	}

}
