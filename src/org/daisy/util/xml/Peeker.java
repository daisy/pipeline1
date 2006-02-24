/*
 * Created on 2005-jun-19
 */
package org.daisy.util.xml;

import java.io.IOException;
import java.net.URI;

import org.xml.sax.SAXException;
/**
 * Peeks into an XML file, returns 
 * root element nsuri, qname, localname and
 * first encountered public id
 * @author Markus Gylling
 */

public interface Peeker {
	public void peek(URI uri) throws SAXException, IOException;
	public String getFirstPublicId();
	public String getRootElementLocalName();
	public String getRootElementNsUri();
	public String getRootElementQName();
	public void reset();
}