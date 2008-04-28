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
 */
package org.daisy.util.xml.peek;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.transform.Source;

import org.daisy.util.file.EFile;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A Peeker attempts to, in a cost-minimized way, retrieve XML document prolog and head information.
 * <p>Use PeekerPool to retrieve implementations of this interface.</p>
 * @author Markus Gylling
 */
public interface Peeker {

	/**
	 * Peek and attempt retrieval of document prolog and head information.
	 * <p>The peek consists of a parse that will be aborted directly after the root element of the XML document.</p>
	 * @param document URL of XML document to be peeked
	 * @return TODO
	 * @throws SAXException
	 * @throws IOException
	 */
	public PeekResult peek(URL document) throws SAXException, IOException;
	
	/**
	 * Peek and attempt retrieval of document prolog and head information.
	 * <p>The peek consists of a parse that will be aborted directly after the root element of the XML document.</p>
	 * @param document URI of XML document to be peeked
	 * @return TODO
	 * @throws SAXException
	 * @throws IOException
	 */
	public PeekResult peek(URI document) throws SAXException, IOException;
	
	/**
	 * Peek and attempt retrieval of document prolog and head information.
	 * <p>The peek consists of a parse that will be aborted directly after the root element of the XML document.</p>
	 * @param document File of XML document to be peeked
	 * @return TODO
	 * @throws SAXException
	 * @throws IOException
	 */
	public PeekResult peek(File document) throws SAXException, IOException;
	
	/**
	 * Peek and attempt retrieval of document prolog and head information.
	 * <p>The peek consists of a parse that will be aborted directly after the root element of the XML document.</p>
	 * @param document EFile of XML document to be peeked
	 * @return TODO
	 * @throws SAXException
	 * @throws IOException
	 */
	public PeekResult peek(EFile document) throws SAXException, IOException;
		
	/**
	 * Peek and attempt retrieval of document prolog and head information.
	 * <p>The peek consists of a parse that will be aborted directly after the root element of the XML document.</p>
	 * @param document InputSource of XML document to be peeked
	 * @return TODO
	 * @throws SAXException
	 * @throws IOException
	 */
	public PeekResult peek(InputSource document) throws SAXException, IOException;

	
	/**
	 * Peek and attempt retrieval of document prolog and head information.
	 * <p>The peek consists of a parse that will be aborted directly after the root element of the XML document.</p>
	 * @param document InputStream of XML document to be peeked
	 * @return TODO
	 * @throws SAXException
	 * @throws IOException
	 */
	public PeekResult peek(InputStream document) throws SAXException, IOException;
	
	/**
	 * Peek and attempt retrieval of document prolog and head information.
	 * <p>The peek consists of a parse that will be aborted directly after the root element of the XML document.</p>
	 * @param document Source of XML document to be peeked
	 * @return TODO
	 * @throws SAXException
	 * @throws IOException
	 */
	public PeekResult peek(Source document) throws SAXException, IOException;
		
}
