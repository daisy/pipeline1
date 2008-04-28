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
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;

import org.apache.batik.css.parser.Parser;
import org.daisy.util.fileset.CssFile;
import org.daisy.util.fileset.ManifestFile;
import org.daisy.util.fileset.exception.FilesetFileErrorException;
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
import org.daisy.util.fileset.exception.FilesetFileWarningException;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;

/**
 * @author Markus Gylling
 */

final class CssFileImpl extends FilesetFileImpl implements DocumentHandler, ErrorHandler, CssFile, ManifestFile {
	//private ErrorHandler listeningErrorHandler = null;
	
	private Parser parser;
	
	CssFileImpl(URI uri) throws CSSException, FileNotFoundException, IOException {		
		super(uri, CssFile.mimeStringConstant);
		initialize();
	}
		
	private void initialize() {
		parser = new Parser();
		parser.setLocale(Locale.getDefault()); 
		parser.setDocumentHandler(this);        		
		parser.setErrorHandler(this);				  
	}	
	
	public void parse() throws CSSException, IOException {
		InputSource is = null;
		try{
			is = this.asSacInputSource();			
			parser.parseStyleSheet(is);
		}finally{
			if(is.getByteStream()!=null)is.getByteStream().close();
			if(is.getCharacterStream()!=null)is.getCharacterStream().close();
		}
	}
	
	@SuppressWarnings("unused")
	public void property(String name, LexicalUnit value, boolean important) throws CSSException {
		try {
			//collect all properties that contain url() statements  
			if (regex.matches(regex.CSS_PROPERTIES_WITH_URLS,name)) {
				try {
					String str = value.getStringValue();
					if (regex.matches(regex.FILE_IMAGE,str)) {
						  putUriValue(value.getStringValue());
					}
				}catch (Exception e) {
					//System.err.println("value.getStringValue failed in css");					
				}				 
		 	}
		} catch (Exception e) {			
			myExceptions.add(new FilesetFileWarningException(this,new CSSParseException("css property event",null,e)));
		}
	}
	
	@SuppressWarnings("unused")
	public void importStyle(String inuri, SACMediaList media, String defaultNamespaceURI) { 
		this.putUriValue(inuri);		
	}
	
	public void error(CSSParseException cpe) throws CSSException {
        myExceptions.add(new FilesetFileErrorException(this,cpe)); 
	}
	
	public void fatalError(CSSParseException cpe) throws CSSException {
        myExceptions.add(new FilesetFileFatalErrorException(this,cpe));	
    }
	
	public void warning(CSSParseException cpe) throws CSSException {
		myExceptions.add(new FilesetFileWarningException(this,cpe));	
	}
		
	public org.w3c.css.sac.InputSource asSacInputSource() throws FileNotFoundException {
		org.w3c.css.sac.InputSource sacis = new org.w3c.css.sac.InputSource(new FileReader(this));
		sacis.setURI(this.toURI().toASCIIString());
		return sacis;
	}
	
	@SuppressWarnings("unused")	
	public void startDocument(InputSource source) throws CSSException {}	
	@SuppressWarnings("unused")
	public void endDocument(InputSource source) throws CSSException {}
	@SuppressWarnings("unused")
	public void startSelector(SelectorList selectors) throws CSSException {}
	@SuppressWarnings("unused")
	public void endSelector(SelectorList selectors) throws CSSException {}
	@SuppressWarnings("unused")
	public void comment(String text) throws CSSException {}
	@SuppressWarnings("unused")
	public void startPage(String name, String pseudo_page) throws CSSException {}
	@SuppressWarnings("unused")
	public void endPage(String name, String pseudo_page) throws CSSException {}
	@SuppressWarnings("unused")
	public void ignorableAtRule(String atRule) throws CSSException {}    
	@SuppressWarnings("unused")
	public void namespaceDeclaration(String prefix, String uri) throws CSSException {}
	@SuppressWarnings("unused")
	public void startFontFace() throws CSSException {}
	@SuppressWarnings("unused")
	public void endFontFace() throws CSSException {}
	@SuppressWarnings("unused")
	public void startMedia(SACMediaList media) throws CSSException {}
	@SuppressWarnings("unused")
	public void endMedia(SACMediaList media) throws CSSException {}	
	
	private static final long serialVersionUID = -9074302258050588711L;
}