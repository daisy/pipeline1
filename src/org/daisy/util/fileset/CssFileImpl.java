package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import org.apache.batik.css.parser.Parser;
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

class CssFileImpl extends FilesetFileImpl implements DocumentHandler, ErrorHandler, CssFile {
	
	CssFileImpl(URI uri) throws CSSException {
		super(uri);
		if (this.exists() && this.canRead()){
			Parser parser = new Parser();
			parser.setLocale(Locale.getDefault()); 
			parser.setDocumentHandler(this);        
			parser.setErrorHandler(this);
			
			try {
				parser.parseStyleSheet(new InputSource(this.toURI().toString()));
			} catch (IOException ioe) {
				FilesetObserver.getInstance().errorEvent(ioe);
			} catch (CSSException ce) {
				FilesetObserver.getInstance().errorEvent(ce);
			}
		}//(this.exists() && this.canRead()) --> else parent AbstractFile already reported nonexistance or notreadable
	}
	
	public void property(String name, LexicalUnit value, boolean important) throws CSSException {
		try {
			//get all properties that contain url() statements  
			if(matches(Regex.getInstance().CSS_PROPERTIES_WITH_URLS,name)) {
				try{
					if (matches(Regex.getInstance().FILE_IMAGE,value.getStringValue())) {
						if (!matches(Regex.getInstance().URI_REMOTE,value.getStringValue())) {
							putLocalURI(value.getStringValue());
							URI uri = resolveURI(value.getStringValue());
							Object o = FilesetObserver.getInstance().getCurrentListener().getLocalMember(uri); 
							if (o!=null) { 
								//already added to listener fileset, so only put to local references collection
								putReferencedMember(uri, o);
							}else{    
								try {
									putReferencedMember(uri, new ImageFileImpl(uri));
								} catch (Exception e) {
									throw new CSSException(e);
								}
							}
						}else{
							putRemoteURI(value.getStringValue());						
						}
					}	
				}catch (IllegalStateException ise){
					//happens when value.getStringValue is nonavailable
				}
			}          
		} catch (Exception e) {
			FilesetObserver.getInstance().errorEvent(e);
		}
	}
	
	public void importStyle(String inuri, SACMediaList media, String defaultNamespaceURI) { //throws CSSException {
		if (!matches(Regex.getInstance().URI_REMOTE,inuri)) {
			putLocalURI(inuri);
			URI uri = resolveURI(inuri);        
			Object o = FilesetObserver.getInstance().getCurrentListener().getLocalMember(uri); 
			if (o!=null) { 
				//already added to listener fileset, so only put to local references collection
				putReferencedMember(uri, o);
			}else{    
				try {
					putReferencedMember(uri, new CssFileImpl(uri)); 
				} catch (Exception e) {
					throw new CSSException(e);
				}
			}
		}else{
			putRemoteURI(inuri);
		}
	}
	
	public void error(CSSParseException exception) throws CSSException {
		System.err.println("css error: " + exception.getMessage());
	}
	public void fatalError(CSSParseException exception) throws CSSException {
		System.err.println("css fatal error: " + exception.getMessage()); //TODO send to errorevent
		
	}
	public void warning(CSSParseException exception) throws CSSException {
		System.err.println("css warning: "+ exception.getMessage());
	}
	
	public void startDocument(InputSource source) throws CSSException {}
	
	public void endDocument(InputSource source) throws CSSException {}
	
	public void startSelector(SelectorList selectors) throws CSSException {}
	
	public void endSelector(SelectorList selectors) throws CSSException {}
	
	public void comment(String text) throws CSSException {}
	
	public void startPage(String name, String pseudo_page) throws CSSException {}
	
	public void endPage(String name, String pseudo_page) throws CSSException {}
	
	public void ignorableAtRule(String atRule) throws CSSException {}    
	
	public void namespaceDeclaration(String prefix, String uri) throws CSSException {}
	
	public void startFontFace() throws CSSException {}
	
	public void endFontFace() throws CSSException {}
	
	public void startMedia(SACMediaList media) throws CSSException {}
	
	public void endMedia(SACMediaList media) throws CSSException {}
	
}
