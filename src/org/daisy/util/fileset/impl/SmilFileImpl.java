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
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.SmilFile;
import org.daisy.util.fileset.exception.FilesetFileErrorException;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.xml.SmilClock;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Markus Gylling
 */

class SmilFileImpl extends XmlFileImpl implements SmilFile {
	private SmilClock myStatedDuration = null;
	protected SmilClock myStatedTotalElapsedTime = null;
	protected String myMetaTitle = null;	
	private SmilClock audioClipBegin = null; //does not gather anything outside startelement; here for optim 	
	private SmilClock audioClipEnd = null; //does not gather anything outside startelement; here for optim	 
	private long myCalculatedDuration = 0;
	/** id attr values of any occuring customTest elements in head */
	protected Set<String> mCustomTestIDs = null;

	SmilFileImpl(URI uri, String mimeStringConstant) throws ParserConfigurationException, SAXException, IOException {
		super(uri, mimeStringConstant);
	}

	SmilFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri, SmilFile.mimeStringConstant);
	}

	public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		// sName = sName.intern();
		super.startElement(namespaceURI, sName, qName, attrs);
		for (int i = 0; i < attrs.getLength(); i++) {
			attrName = attrs.getQName(i);
			attrValue = attrs.getValue(i).intern(); // for some reason

			if (attrName == "id") {
				QName q = new QName(namespaceURI, sName);
				this.putIdAndQName(attrValue, q);
				if(sName=="customTest") {
					//the element customTest in head, not the attribute in body
					mCustomTestIDs.add(attrValue);
				}
			} else if (FilesetRegex.getInstance().matches(FilesetRegex.getInstance().SMIL_ATTRIBUTES_WITH_URIS, attrName)) {
				putUriValue(attrValue);
			}

			try {
				if (sName == "meta") {
					if (attrValue == "ncc:timeInThisSmil") {
						myStatedDuration = new SmilClock(attrs.getValue("content"));
					} else if (attrValue == "ncc:totalElapsedTime"
							|| attrValue == "dtb:totalElapsedTime") {
						myStatedTotalElapsedTime = new SmilClock(attrs.getValue("content"));
					} else if (attrName == "name" && attrValue == "title") {						
						myMetaTitle = attrs.getValue("content");
						//System.err.println(myMetaTitle);
					}
				}
				if (sName == "audio") {
					// collect the audio element values, and check them later
					// outside for loop
					if ((attrName == "clip-begin" || attrName == "clipBegin")) {
						audioClipBegin = new SmilClock(attrValue);
					} else if ((attrName == "clip-end" || attrName == "clipEnd")) {
						audioClipEnd = new SmilClock(attrValue);
					}
				}
			} catch (Exception nfe) {
				SAXParseException spe = new SAXParseException("exception when calculating " + attrValue, null);
				myExceptions.add(new FilesetFileErrorException(this,spe));				
			}
		}
		// all attributes of this element have now been looped through
		// test the audio dur stuff
		if (sName == "audio") {
			if (audioClipBegin != null
					&& audioClipEnd != null) { 
				// means we had a standard dtb audio element
				myCalculatedDuration += (audioClipEnd.millisecondsValue() - audioClipBegin.millisecondsValue());
				// reset
				audioClipBegin = null;
				audioClipEnd = null;
			}
		}
		
	}

	public SmilClock getCalculatedDuration() {
		return new SmilClock(myCalculatedDuration);
	}

	public SmilClock getStatedDuration() {
		return myStatedDuration;
	}
	
	private static final long serialVersionUID = -2947911387580984356L;

}
