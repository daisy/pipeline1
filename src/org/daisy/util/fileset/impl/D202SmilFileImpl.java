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

package org.daisy.util.fileset.impl;

import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.xml.d202.D202SmilFile;
import org.daisy.util.xml.SmilClock;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Implementation of the Daisy 2.02 Smil file.
 * @author Markus Gylling
 */
final class D202SmilFileImpl extends SmilFileImpl implements D202SmilFile {

	public D202SmilFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri, D202SmilFile.mimeStringConstant);		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.impl.XmlFileImpl#resolveEntity(java.lang.String, java.lang.String)
	 */
	public InputSource resolveEntity(String publicId, String systemId) throws IOException {
		//override the XmlFileImpl method in order to substitute DTDs
		//from w3c smil to the subset one					
		publicId = "-//DAISY//DTD smil v2.02//EN";			
		return super.resolveEntity(publicId,systemId);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.xml.d202.D202SmilFile#getStatedTotalElapsedTime()
	 */
	public SmilClock getStatedTotalElapsedTime() {
		return myStatedTotalElapsedTime;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.xml.d202.D202SmilFile#getMetaTitle()
	 */
	public String getMetaTitle() {
		if (myMetaTitle == null || myMetaTitle.trim().length()<1) return null; 
		return myMetaTitle;
	}
	
	private static final long serialVersionUID = -4124499324926116684L;


}