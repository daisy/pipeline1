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

import org.daisy.util.fileset.ManifestFile;
import org.daisy.util.fileset.Xhtml10File;
import org.daisy.util.fileset.exception.FilesetFileErrorException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */

//class Xhtml10FileImpl extends XmlFileImpl implements TextualContentFile, Xhtml10File, ManifestFile {
class Xhtml10FileImpl extends XmlFileImpl implements Xhtml10File, ManifestFile {
	private int currentHeadingLevel =0;
	private boolean correctHeadingSequence = true;
	protected boolean parsingBody = false;
	private boolean inHeadTitle = false;
	private String mTitleString = null;
	private String mDcTitleString = null;
	private String mDcIdentifierString = null;
	
	Xhtml10FileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri,Xhtml10File.mimeStringConstant); 
	}	
	
	Xhtml10FileImpl(URI uri, String mimeStringConstant) throws ParserConfigurationException, SAXException, IOException {
		super(uri,mimeStringConstant); 
	}	
						
	public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		super.startElement(namespaceURI, sName, qName, attrs);
		if(sName=="body") parsingBody = true;
		if(sName=="title") inHeadTitle = true;
		
		for (int i = 0; i < attrs.getLength(); i++) {
			attrName = attrs.getQName(i);
			attrValue = attrs.getValue(i).intern(); //for some reason	
						
			if (attrName=="id") {
				QName q = new QName(namespaceURI,sName);
				this.putIdAndQName(attrValue,q);				
			}else if(regex.matches(regex.XHTML_ATTRS_WITH_URIS,attrName)) {
				putUriValue(attrValue);
			}else if (attrName=="xml:lang") {
				this.mXmlLangValues.add(attrValue);
			}
			
			if(sName == "meta") {			
				if(attrName=="name" && attrValue.toLowerCase()=="dc:title") {
					mDcTitleString = attrs.getValue("", "content");
				}
				if(attrName=="name" && attrValue.toLowerCase()=="dc:identifier") {
					mDcIdentifierString = attrs.getValue("", "content");
				}
			}
			
		} //for (int i
				
		
		
	}
	
	public boolean hasHierarchicalHeadingSequence() {
		return correctHeadingSequence;
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		if(inHeadTitle){
			if(mTitleString==null) {
				mTitleString = String.copyValueOf(ch,start,length);
			}else{
				mTitleString += String.copyValueOf(ch,start,length);
			}	
    	}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(localName=="body") parsingBody = false;
		if(localName=="title") inHeadTitle = false;
		if (correctHeadingSequence == true){
			if(regex.matches(regex.XHTML_HEADING_ELEMENT,localName)) {
				try{					
					int newHeadingLevel = Integer.parseInt(localName.substring(1,2));						
					if(newHeadingLevel-1 > currentHeadingLevel){
						correctHeadingSequence = false;	
					}
					currentHeadingLevel = newHeadingLevel;
				}catch (Exception e) {
					myExceptions.add(new FilesetFileErrorException(this,e));
				}				
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.Xhtml10File#getTitle()
	 */
	public String getTitle() {	
		if(mDcTitleString!=null) return mDcTitleString;
		return mTitleString;
	}
	
	private static final long serialVersionUID = 699491683089715725L;

	public String getIdentifier() {		
		return mDcIdentifierString;
	}


}
