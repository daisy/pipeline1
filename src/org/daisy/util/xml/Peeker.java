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

import java.io.IOException;
import java.net.URI;

import javax.xml.namespace.QName;

import org.xml.sax.SAXException;
/**
 * Peeks into an XML file, returns 
 * prolog and root information.
 * @author Markus Gylling
 * @deprecated Use org.daisy.util.xml.peek.PeekerPool instead
 */

public interface Peeker {
	/**
 * @deprecated Use org.daisy.util.xml.peek.PeekerPool instead
	 */
    public void peek(URI uri) throws SAXException, IOException;
    /**
     * @return the first encountered public id while parsing the file; that of the document entity
     * @deprecated Use org.daisy.util.xml.peek.PeekerPool instead
     */
    public String getFirstPublicId();
    /**
     * @return the first encountered system id while parsing the file; that of the document entity
     * @deprecated Use org.daisy.util.xml.peek.PeekerPool instead
     */
    public String getFirstSystemId();
    
    /**
     * @return the local name of the documents root element
     * @deprecated Use org.daisy.util.xml.peek.PeekerPool instead
     */
    public String getRootElementLocalName();

    /**
     * @return the namespace URI of the root element
     * @deprecated Use org.daisy.util.xml.peek.PeekerPool instead
     */
    public String getRootElementNsUri();

    /**
     * @return the root element represented as a QName object
     * @deprecated Use org.daisy.util.xml.peek.PeekerPool instead
     */
    public QName getRootElementQName();
    
    /**
     * @return the XML version as stated in the XML declaration pseudoattribute
     * @deprecated Use org.daisy.util.xml.peek.PeekerPool instead
     */ 
    public String getXMLVersion();
    /**
     * @return the encoding if stated in prolog, null if no encoding pseudoattr
     * @deprecated Use org.daisy.util.xml.peek.PeekerPool instead
     */
    public String getEncoding();
    
    /**
     * @return the standalone value as stated in the XML declaration pseudoattribute
     * @deprecated Use org.daisy.util.xml.peek.PeekerPool instead
     */ 
    public boolean getStandalone();
    
    /**
     * @deprecated Use org.daisy.util.xml.peek.PeekerPool instead 
     */
    public void reset();
}