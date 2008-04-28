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
package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.SvgFile;
import org.xml.sax.SAXException;

/**
 * @author jpritchett
 *
 * File implementation for SVG image files
 */
class SvgFileImpl extends XmlFileImpl implements SvgFile {

	public SvgFileImpl(URI uri) throws FileNotFoundException, IOException, SAXException, ParserConfigurationException {
		super(uri, SvgFile.mimeStringConstant);
	}

	private static final long serialVersionUID = 7770131283591848886L;	
}
