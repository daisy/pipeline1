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
 */package org.daisy.util.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * Simple implementation of a NamespaceContext. New namespace mappings, 
 * prefix -> uri, are added arbitrary. The prefixes <tt>xmlns</tt> and 
 * <tt>xml</tt> are mapped to namespace URI:s as described in the 
 * <tt>NamespaceContext</tt> javadoc, without the need of specifically
 * adding those two.
 * 
 * @author Martin Blomberg
 *
 */
public class SimpleNamespaceContext implements NamespaceContext {

	/**
	 * Mapping namespace prefixes to namespace URI:s.
	 */
	private Map<String,String> mNamespaces;
	
	/**
	 * Default constructor. 
	 */
	public SimpleNamespaceContext() {
		mNamespaces = new HashMap<String, String>();
	}
	
	/**
	 * Constructs a new namespace context given the mappings/namespaces 
	 * in <tt>namespaces</tt>.
	 * 
	 * @param namespaces the initial prefix->nsURI mapping.
	 */
	public SimpleNamespaceContext(Map<String,String> namespaces) {
		if (namespaces == null) {
			String msg = "The namespace prefix->uri map must not be null!";
			throw new IllegalArgumentException(msg);
		}
		
		mNamespaces = new HashMap<String, String>();
		mNamespaces.putAll(namespaces);
	}
	
	/**
	 * Adds a namespace to the context. If <tt>namespacePrefix</tt> already occurrs in
	 * the context, this new namespace mapping will replace the previous one.
	 * 
	 * @param namespacePrefix the namespace prefix.
	 * @param namespaceURI the namespace URI.
	 */
	public void declareNamespace(String namespacePrefix, String namespaceURI) {
		if (namespacePrefix == null) {
			String msg = "The namespace prefix must not be null!";
			throw new IllegalArgumentException(msg);
		}
		
		if (namespaceURI == null) {
			String msg = "The namespace URI must not be null!";
			throw new IllegalArgumentException(msg);
		}
		
		mNamespaces.put(namespacePrefix, namespaceURI);
	}
	
		
	/* (non-Javadoc)
	 * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
	 */
	public String getNamespaceURI(String prefix) {
		if (prefix != null && prefix.equals("xml")) {
			return XMLConstants.XML_NS_URI;
		}
		
		return mNamespaces.get(prefix);
	}

	/* (non-Javadoc)
	 * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
	 */
	public String getPrefix(String nsURI) {
		if (nsURI == null) {
			String msg = "The namespace URI must not be null!";
			throw new IllegalArgumentException(msg);
		}
		
		if (nsURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
			return XMLConstants.XMLNS_ATTRIBUTE;
		}
		
		if (nsURI.equals(XMLConstants.XML_NS_URI)) {
			return XMLConstants.XML_NS_PREFIX;
		}
		
		Set<String> prefs = mNamespaces.keySet();
		for (String prefix : prefs) {
			if (mNamespaces.get(prefix).equals(nsURI)) {
				return prefix;
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
	 */
	public Iterator<?> getPrefixes(String nsURI) {
		if (nsURI == null) {
			String msg = "The namespace URI must not be null!";
			throw new IllegalArgumentException(msg);
		}
		
		Set<String> prefixes = new HashSet<String>();
		if (nsURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
			prefixes.add(XMLConstants.XMLNS_ATTRIBUTE);
		} else if (nsURI.equals(XMLConstants.XML_NS_URI)) {
			prefixes.add(XMLConstants.XML_NS_PREFIX);
		} else {
			Set<String> prefs = mNamespaces.keySet();
			for (String prefix : prefs) {
				if (mNamespaces.get(prefix).equals(nsURI)) {
					prefixes.add(prefix);
				}
			}
		}
		
		return prefixes.iterator();
	}

}
