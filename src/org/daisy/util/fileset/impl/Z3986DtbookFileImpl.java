package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.ManifestFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986DtbookFile;
import org.daisy.util.mime.MIMETypeException;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
class Z3986DtbookFileImpl extends XmlFileImpl implements Z3986DtbookFile, ManifestFile {
	private String dcTitle = null;
	private String dcIdentifier = null;
	private String dtbUid = null;
	private String doctitle = null;
	private String docauthor = null;
	private String dcCreator= null;
	private String dcPublisher= null;
	
	private boolean inDoctitle = false;
	private boolean inDocauthor = true;
	
	private String charCollector = "";
	
	Z3986DtbookFileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, MIMETypeException {
		super(uri,Z3986DtbookFile.mimeStringConstant);		
	}
	
	Z3986DtbookFileImpl(URI uri, ErrorHandler errh) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, MIMETypeException {
		super(uri, errh,Z3986DtbookFile.mimeStringConstant);		
	}
		
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		//qName = qName.intern();
		for (int i = 0; i < attrs.getLength(); i++) {
			attrName = attrs.getQName(i);
			attrValue = attrs.getValue(i).intern();
			
			if (sName=="meta") {
				if (attrName=="name"){
					if (attrValue.toLowerCase().equals("dc:title")) {				
						this.dcTitle = attrs.getValue("content");
					}else if(attrValue.toLowerCase().equals("dc:identifier")){
						this.dcIdentifier = attrs.getValue("content");
					}else if(attrValue.toLowerCase().equals("dc:creator")){
							this.dcCreator = attrs.getValue("content");	
					}else if(attrValue.toLowerCase().equals("dc:publisher")){
						this.dcPublisher = attrs.getValue("content");		
					}else if(attrValue.toLowerCase().equals("dtb:uid")){
						this.dtbUid = attrs.getValue("content");
					}	
				}
			}else if (sName == "doctitle") {
				inDoctitle = true;
			}else if (sName == "docauthor") {
				inDoctitle = true;
			}
			
			if (attrName=="id") {
				putIdAndQName(attrValue,qName);
			} else if (regex.matches(regex.DTBOOK_ATTRIBUTES_WITH_URIS,attrName)) {
			   putUriValue(attrValue);
			}									
		}//for (int i
	}//startElement

	public void endElement(String uri, String sName, String qName) throws SAXException {
		if (sName == "doctitle") {			
			this.doctitle = charCollector;
			charCollector="";
			inDoctitle = false;
		}else if (sName == "docauthor") {
			this.docauthor = charCollector;
			charCollector="";
			inDocauthor = false;
		}
	}

	public void characters(char[] chars, int start, int end) throws SAXException {
		if(inDoctitle||inDocauthor) {
			charCollector += String.copyValueOf(chars,start,end);
		}
	}

	public String getDcIdentifier() {
		return dcIdentifier;
	}

	public String getDcTitle() {
		return dcTitle;
	}

	public String getDcCreator() {
		return dcCreator;
	}
	
	public String getDcPublisher() {
		return dcPublisher;
	}
	
	public String getDocauthor() {
		return docauthor;
	}

	public String getDoctitle() {
		return doctitle;
	}

	public String getDtbUid() {
		return dtbUid;
	}



}
