package org.daisy.util.xml.stax;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.i18n.CharUtils;
import org.daisy.util.text.CircularFifoStringBuilder;
import org.daisy.util.xml.pool.StAXInputFactoryPool;

/**
 * Generate extended Locators in various XML document types
 * <p>For performance reasons, this object uses cached readers - users of this object must therefore make sure to call the {@link #reset()} method at lifecycle end.</p>
 * @author Markus Gylling
 */
public class ExtendedLocationProvider {
	private static Map<URL, ConvenientXMLEventReader> mReaderCache = null;
	private static Set<StartElement> mHeadingNameMap = null;
	private static Set<StartElement> mPageNameMap = null;
 	    
	/**
	 * Constructor.
	 * @throws IOException 
	 * @throws XMLStreamException 
	 */
	public ExtendedLocationProvider() throws XMLStreamException, IOException {		
		mReaderCache = new HashMap<URL, ConvenientXMLEventReader>();
		mHeadingNameMap = new HashSet<StartElement>();
		mPageNameMap = new HashSet<StartElement>();		
		readConfigFile();
	}
	
	/**
	 * Generate an extended Location object.
	 * @param loc the Location to extend.
	 */
	public ExtendedLocationImpl generate(Location loc) throws XMLStreamException, IOException {
		
		ExtendedLocationImpl extLoc = new ExtendedLocationImpl(loc);
		
		CircularFifoStringBuilder fifoBuffer = new CircularFifoStringBuilder(64);
		
		//if reader cache has a reader for the incoming URL
		//and readers pos is before loc lin+col, use that
		//else create a new reader and add to cache
				
		URL currentFileURL = FilenameOrFileURI.toURI(loc.getSystemId()).toURL();
		if(currentFileURL == null) throw new IOException(loc.getSystemId());
			
		ConvenientXMLEventReader cxer = null;
		
		if(mReaderCache.containsKey(currentFileURL)) {
			ConvenientXMLEventReader test = mReaderCache.get(currentFileURL);
			if(test.getCurrentEventLocation().getLineNumber()< loc.getLineNumber()) {
				//use this reader; test above could check for cols, but beware, often -1 etc
				//System.err.println("Reusing a reader for context info");
				cxer = test;
			}else{
				//right doc but cursor too late, remove from cache		
				//System.err.println("Not reusing a reader for context info: cursor too late");
				test.close();
				mReaderCache.remove(currentFileURL);
			}
		}
		
		if(cxer==null) {
			Map properties = null;
			XMLInputFactory xif = null;
			try{
				properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
				xif = StAXInputFactoryPool.getInstance().acquire(properties);									
				XMLEventReader xer = xif.createXMLEventReader(currentFileURL.openStream());			
				cxer = new ConvenientXMLEventReader(currentFileURL,xer); 
				mReaderCache.put(currentFileURL,cxer);
			}finally{
				StAXInputFactoryPool.getInstance().release(xif, properties);
			}
		}
		
		//now run through doc until line+col is found
		//when found, pop and finalize the extended locator, return
		
		while(cxer.hasNext()) {
			XMLEvent xe = cxer.nextEvent();
			
			//terminate if we are past cursor
			if(xe.getLocation().getLineNumber()>loc.getLineNumber()) {
				//System.err.println("breaking when cursor is at line " + xe.getLocation().getLineNumber() + " and locator line is " + loc.getLineNumber());
				break;			
			}
			
			if(xe.getLocation().getLineNumber()==loc.getLineNumber() 
					&& xe.getLocation().getColumnNumber()>= loc.getLineNumber()) {
				//System.err.println("breaking when cursor and loc is at same line (" + loc.getLineNumber() + "); cursor col is " + xe.getLocation().getColumnNumber() + " and locator col is " + loc.getColumnNumber());
				break;
			}
			
			switch (xe.getEventType()) {
				case XMLEvent.CHARACTERS: 
					Characters chars = xe.asCharacters();			
					if(!CharUtils.isXMLWhiteSpace(chars.getData())) {
						for (int i = 0; i < chars.getData().length(); i++) {
							fifoBuffer.append(chars.getData().charAt(i));
						}					
						fifoBuffer.append('|');
					}
					break;
				case XMLEvent.START_ELEMENT:
					if(matches(xe.asStartElement(),mHeadingNameMap)) {
						extLoc.setExtendedLocationInfo(ExtendedLocationImpl.InformationType.PRECEDING_HEADING, 
								getElementText(xe.asStartElement(),cxer));
					}else if (matches(xe.asStartElement(),mPageNameMap)) {
						extLoc.setExtendedLocationInfo(ExtendedLocationImpl.InformationType.PRECEDING_PAGE, 
								getElementText(xe.asStartElement(),cxer));
					}
					break;
				default: break;	
			}//switch
		}//while
		
		//add the path
		extLoc.setExtendedLocationInfo(ExtendedLocationImpl.InformationType.XPATH, cxer.getContextStack().getContextXPath());
		
		//add the char buffer
		if(fifoBuffer.length()>0){
			extLoc.setExtendedLocationInfo(ExtendedLocationImpl.InformationType.PRECEDING_TEXT, fifoBuffer.toString());
		}
				
		return extLoc;
	}
		
	/**
	 * Resets the instance by closing all open file handles. The config file is untouched.
	 * <p><b>Note - always call this method at lifecycle end to avoid lingering file locks</b></p>
	 * @throws XMLStreamException 
	 */
	public void reset() throws XMLStreamException {
		for (Iterator iter = mReaderCache.keySet().iterator(); iter.hasNext();) {
			URL u = (URL)iter.next();
			XMLEventReader xer = mReaderCache.get(u);
			xer.close();			
		}
		mReaderCache.clear();		
		System.gc();
	}
	
	/**
	 * Build the token sets using the external configuration file.
	 * @throws XMLStreamException  
	 * @throws IOException 
	 */
	private void readConfigFile() throws XMLStreamException, IOException {
		
		URL config = this.getClass().getResource("ExtendedLocationTokens.xml");
		
		XMLInputFactory xif = null;
		Map properties = null;		
		try {
			properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xif = StAXInputFactoryPool.getInstance().acquire(properties);									
			XMLEventReader xer = xif.createXMLEventReader(config.openStream());
			
			boolean inCategorySection = false;
			String theme = null;

			while(xer.hasNext()) {				
				XMLEvent xe = xer.nextEvent();
			    if(xe.isStartElement() && xe.asStartElement().getName().getLocalPart().equals("category")) {
			    	inCategorySection = true;
			    	theme = xe.asStartElement().getAttributeByName(new QName("theme")).getValue();
			    	continue;
			    }else if(xe.isEndElement() && xe.asEndElement().getName().getLocalPart().equals("category")) {
			    	inCategorySection = false;
			    	continue;
			    }
			    				    
			    if(inCategorySection && xe.isStartElement() && theme != null) {
			    	if(theme.equals("heading")) {
			    		mHeadingNameMap.add(xe.asStartElement());
			    	}else if(theme.equals("page")) {
			    		mPageNameMap.add(xe.asStartElement());
			    	}			    	
			    }	
			}			
		}finally {
			StAXInputFactoryPool.getInstance().release(xif, properties);
		}				
	}
	
	private String getElementText(StartElement start, ConvenientXMLEventReader wxer) throws XMLStreamException {
		
		StringBuilder sb = new StringBuilder();
		while(wxer.hasNext()) {
			XMLEvent xe = wxer.nextEvent();
			if(xe.isEndElement() && xe.asEndElement().getName().equals(start.getName())) {
				break;
			}else if(xe.isCharacters()) {
				if(!CharUtils.isXMLWhiteSpace(xe.asCharacters().getData())) {
					sb.append(xe.asCharacters().getData());
				}
			}			
		}			
		return sb.toString();
	}
	
	/**
	 * @return true if param element matches one entry in headingNameMap. 
	 * Param element must match on QName, and must have at least all attributes of a map entry.
	 */	
	private boolean matches(StartElement test, Set<StartElement> headingNameMap) {
		for(StartElement mapEntry : headingNameMap) {
			//match on QName first
			if(mapEntry.getName().equals(test.getName())) {
				//if we dont need to match on attributes, return true
				if(!mapEntry.getAttributes().hasNext()) return true;
				//else loop over attributes and make sure test has all of the map entry
				boolean matchesAll = true;
				for (Iterator iter = mapEntry.getAttributes(); iter.hasNext();) {
					Attribute mapEntryAttr = (Attribute) iter.next();
					Attribute testAttr = test.getAttributeByName(mapEntryAttr.getName()); 
					if(testAttr == null) {
						matchesAll = false;
					}else{
						if (!testAttr.getValue().matches(mapEntryAttr.getValue())){
							matchesAll = false;
						}
					}
				}
				if(matchesAll) {
					//System.err.println("match on " + test.getName().toString());
					return true;
				}
			}			
		}//for (StartElement)
		return false;
	}
}
