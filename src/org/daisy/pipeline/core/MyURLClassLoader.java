/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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
package org.daisy.pipeline.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.Enumeration;

/**
 * Just for debugging...
 * @author Linus Ericson
 */
public class MyURLClassLoader extends URLClassLoader {

	public MyURLClassLoader(URL[] arg0) {
		super(arg0);
		System.err.println("MyURLClassLoader:");
		for (URL url : arg0) {
			System.err.println("  url: " + url);
		}
	}

	public MyURLClassLoader(URL[] arg0, ClassLoader arg1) {
		super(arg0, arg1);		
		System.err.println("MyURLClassLoader (parent: " + arg1 + "):");
		for (URL url : arg0) {
			System.err.println("  url: " + url);
		}
	}

	public MyURLClassLoader(URL[] arg0, ClassLoader arg1, URLStreamHandlerFactory arg2) {
		super(arg0, arg1, arg2);	
		System.err.println("MyURLClassLoader:");
		for (URL url : arg0) {
			System.err.println("  url: " + url);
		}
	}

	@Override
	public URL findResource(String name) {
		System.err.println("[MyURLClassLoader] findResource '" + name + "'");
		URL res = super.findResource(name); 
		if (res == null) {
			System.err.println("[MyURLClassLoader] findResource ...not found. (null)");
		} else {
			System.err.println("[MyURLClassLoader] findResource ...found!");
		}
		return res;
	}

	@Override
	public Enumeration<URL> findResources(String name) throws IOException {
		System.err.println("[MyURLClassLoader] findResources '" + name + "'");
		return super.findResources(name);
	}

	@Override
	public URL getResource(String name) {
		System.err.println("[MyURLClassLoader] getResource '" + name + "'");
		URL res = super.getResource(name);
		if (res == null) {
			System.err.println("[MyURLClassLoader] getResource ...not found. (null)");
		} else {
			System.err.println("[MyURLClassLoader] getResource ...found!");
		}
		return res;
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		System.err.println("[MyURLClassLoader] getResourceAsStream '" + name + "'");
		return super.getResourceAsStream(name);
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		System.err.println("[MyURLClassLoader] getResources '" + name + "'");
		return super.getResources(name);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[MyURLClassLoader] toString");		
		for (URL url : this.getURLs()) {
			sb.append("[MyURLClassLoader]   - url: " + url);
		}
		return sb.toString();
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		System.err.println("[MyURLClassLoader] findClass '" + name + "'");
		return super.findClass(name);
	}

}
