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

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.D202MasterSmilFile;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
final class D202MasterSmilFileImpl extends SmilFileImpl implements D202MasterSmilFile {
	private boolean inBody = false;
	
	public D202MasterSmilFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri, D202MasterSmilFile.mimeStringConstant);
	}
	
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		super.startElement(namespaceURI, sName, qName, attrs);
		if(inBody){
			for (int i = 0; i < attrs.getLength(); i++) {							
				attrName = attrs.getQName(i);
				attrValue = attrs.getValue(i).intern();
				if (attrName=="id") {
					QName q = new QName(namespaceURI,sName);
					this.putIdAndQName(attrValue,q);
				}else if (attrName=="src") {
					this.putUriValue(attrValue);
				}	
			}//for int i
		}//if(inBody)
	}//startElement
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(qName.equals("head")) inBody = true;
	}
	
	public InputSource resolveEntity(String publicId, String systemId) throws IOException {
		//override the XmlFileImpl method in order to substitute DTDs
		//from w3c smil to the subset one		
		publicId = "-//DAISY//DTD msmil v2.02//EN";	
		return super.resolveEntity(publicId,systemId);
	}
	
	
	private static final long serialVersionUID = 411345505256718208L;
}
