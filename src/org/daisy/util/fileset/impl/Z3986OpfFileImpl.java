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

import org.daisy.util.fileset.Z3986OpfFile;
import org.daisy.util.xml.SmilClock;
import org.xml.sax.SAXException;

final class Z3986OpfFileImpl extends OpfFileImpl implements Z3986OpfFile{
	
	Z3986OpfFileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		super(uri, Z3986OpfFile.mimeStringConstant);
	}

	public SmilClock getStatedDuration() {		
		return statedDuration;
	}

	public String getMetaDtbMultiMediaType() {
		return statedMultiMediaType;
	}

	private static final long serialVersionUID = 7449994487926699133L;
}