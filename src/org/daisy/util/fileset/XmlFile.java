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

package org.daisy.util.fileset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.daisy.util.mime.MIMEConstants;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
public interface XmlFile extends FilesetFile, Referring {
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_XML;

	/**
	 * @return the prolog doctype public id, or null if no doctype public id exists in this document
	 */
	public String getPrologPublicId();

	/**
	 * @return the prolog doctype system id, or null if no doctype system id exists in this document
	 */
	public String getPrologSystemId();
	
	/**
	 * @return the prolog encoding pseudoattr, or null if not encountered
	 */
	public String getPrologEncoding();

	/**
	 * @return the prolog version pseudoattr, or null if not encountered
	 */
	public String getPrologXmlVersion();
	
	/**
	 * @return the namespace uri of the root element
	 */
	public String getRootElementNsUri();
	
	/**
	 * @return the localname of the root element
	 */
	public String getRootElementLocalName();

	/**
	 * @return the qname of the root element
	 */
	public String getRootElementqName();

	/**
	 * @return the qname of the root element
	 */
	public QName getRootElementQName();

	/**
	 * @return the attributes of the root element
	 */
	public Attributes getRootElementAttributes();
	
	/**
	 *@return a collection&lt;String&gt; of all xml:lang values in this XML document. 
	 * If no xml:lang values exist in this document, the return will
	 * be an emtpy collection, not null. The collection contains only unique items, and
	 * values are the untampered-with values of the xml:lang attribute.
	 */
	public Collection getXmlLangValues();

	/**
	 *@return a collection&lt;javax.xml.namespace.QName&gt; of all unique namespaces occuring in this XML document. 
	 * <p>If no namespaces exist in this document, the return will
	 * be an emtpy collection, not null. The collection contains only unique items, and
	 * the QName objects therein will always return null on getLocalPart(), and will (for a default namespace entry) return
	 * null on getPrefix(). The getPrefix() method is guaranteed to be a String representation of the namespace URI. 
	 */
	public Collection getNamespaces();
	
	/**
	 *@return true if <code>idval</code> exists as the value of an attribute named <code>id</code> in the document, false otherwise
	 *@see #hasIDValueOnQName(String, QName)
	 */
	public boolean hasIDValue(String idval);
	
	/**
	 *@return true if <code>idval</code> exists as the value of an attribute named <code>id</code> on an element <code>qName</code> in the document, false otherwise
	 *@see #hasIDValue(String)
	 */
	public boolean hasIDValueOnQName(String idval, QName qName);
	
	/**
	 *@return The QName of an element on which <code>idval</code> is the value of an attribute named <code>id</code>, or null if no element with attribute <code>id</code> valued <code>idval</code> exists.
	 *@see #hasIDValue(String)
	 */
	public QName getQName(String idval);
	
	/**
	 * @return a Map of inline schema URIs. 
	 * <p>Note - this collection does not include schema URIs occuring in document prolog, only on document root (i.e. typically xsi refs).
	 * <p>If the document contains no inline schema URIs, the returne value is an empty Map, not null.</p>
	 * <p>Map structure is: UnresolvedSchemaUriString, SchemaNamespaceURI</p>
	 */
	public Map getInlineSchemaURIs();
	

				
	/**
	 *@return true if the document has been parsed and found wellformed; false if the document has been parsed and found malformed
	 *@throws IllegalStateException if the document has not been parsed
	 *@see #isParsed()
	 */
	public boolean isWellformed() throws IllegalStateException;
	
	/**
	 *@return true if the document has been parsed and found DTD valid; false if the document has been parsed and found DTD invalid
	 *@throws IllegalStateException if the document has not been parsed, or has been parsed but with validation disabled
	 *@see #isParsed()
	 */
	public boolean isDTDValid() throws IllegalStateException;
		
	/**
	 *@return true if the document has been DTD validated, regardless of the result of the validation
	 *@see #isDTDValid()
	 *@see #isWellformed()
	 */
	public boolean isDTDValidated();
	
	/**
	 *@return this file as an {@link org.w3c.dom.Document} instance
	 */
	public Document asDocument(boolean namespaceAware) throws ParserConfigurationException, SAXException, IOException;
	
	/**
	 **@return this file as a {@link javax.xml.transform.dom.DOMSource}
	 */
	public DOMSource asDOMSource() throws ParserConfigurationException, SAXException, IOException;	
	
	/**
	 **@return this file as a {@link javax.xml.transform.sax.SAXSource}
	 */
	public SAXSource asSAXSource() throws FileNotFoundException;	
	
	/**
	 **@return this file as a {@link javax.xml.transform.stream.StreamSource}
	 */
	public StreamSource asStreamSource() throws FileNotFoundException;
	
}
