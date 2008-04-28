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

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.Z3986SmilFile;
import org.daisy.util.xml.SmilClock;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
final class Z3986SmilFileImpl extends SmilFileImpl implements Z3986SmilFile {

	public Z3986SmilFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri,Z3986SmilFile.mimeStringConstant);	
		mCustomTestIDs = new HashSet<String>();
	}
		
	public SmilClock getStatedTotalElapsedTime() {
		return myStatedTotalElapsedTime;
	}
	
	public Set<String> getCustomTestIDs() {
		return mCustomTestIDs;
	}
	
	private static final long serialVersionUID = 1360195717746938439L;
}
