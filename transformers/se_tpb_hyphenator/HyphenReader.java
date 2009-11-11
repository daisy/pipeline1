/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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

package se_tpb_hyphenator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.util.xml.stax.StaxFilter;

public class HyphenReader extends StaxFilter {
	private Stack<String> lang;
	private Stack<Integer> elementPos;
	private boolean root;
	private net.davidashen.text.Hyphenator hyphenator;
	private boolean bypass;
	private int beginLimit;
	private int endLimit;
	private Properties tables;
	private HashMap<String, net.davidashen.text.Hyphenator> loaded;
	private int pos;

	public HyphenReader(Hyphenator t, XMLInputFactory inFactory, FileInputStream input, FileOutputStream output, int breakLimitBegin, int breakLimitEnd) throws XMLStreamException, InvalidPropertiesFormatException, IOException {
		super(inFactory.createXMLEventReader(input), output);
		lang = new Stack<String>();
		elementPos = new Stack<Integer>();
		pos = 0;
		root = true;
		bypass = false;
		this.hyphenator = null;
		this.beginLimit = breakLimitBegin;
		this.endLimit = breakLimitEnd;
		loaded = new HashMap<String, net.davidashen.text.Hyphenator>();
		init();
	}

	private void init() throws InvalidPropertiesFormatException, IOException {
        tables = new Properties();
        URL tablesURL = this.getClass().getResource("hyphenation_tables.xml");
        if(tablesURL!=null){
        	tables.loadFromXML(tablesURL.openStream());
        } else {
        	throw new IOException("Cannot locate hyphenation tables");
        }
	}

    protected StartElement startElement(StartElement event) {
    	elementPos.push(pos+1);
    	pos = 0;
    	Attribute a = event.getAttributeByName(new QName("http://www.w3.org/XML/1998/namespace", "lang"));
    	if (a!=null) {
    		this.loadHyphenationRules(a.getValue(), false);
    		lang.push(a.getValue());
    	} else {
	    	if (root) {
    			EventBus.getInstance().publish(new MessageEvent(this, "No lang on root.", MessageEvent.Type.ERROR));
	    	} else {
	    		try {
	    			String s = lang.peek();
	    			lang.push(s);
	    		} catch (EmptyStackException e) {
	    			e.printStackTrace();
	    		}
	    	}
    	}

    	if (root) {	root = false; }
		return event;
    }
    
    protected EndElement endElement(EndElement event) {
    	if (lang.size()>0) {
    		String popLang = lang.pop();
    		pos = elementPos.pop();
    		if (lang.size()>0) {
	    		String currLang = lang.peek();
	    		if (!popLang.equals(currLang)) {
	    			this.loadHyphenationRules(currLang, true);
	    		}
    		}
    	}
    	return event;
    }

    protected Characters characters(Characters event) {
    	if (bypass) { return event; }
    	else {
    		if (hyphenator==null) {
    			return event;
    		}
    		return getEventFactory().createCharacters(hyphenator.hyphenate(event.getData(), beginLimit, endLimit));
    	}
    }

    private void loadHyphenationRules(String locale, boolean isEnd) {
        String languageFileRelativePath = tables.getProperty(locale);
        if(languageFileRelativePath==null) {
        	EventBus.getInstance().publish(new MessageEvent(this, "Language not supported: " + locale + ". Text nodes in this language will not be hyphenated.", MessageEvent.Type.ERROR));
        	hyphenator = null;
        } else {
        	StringBuffer siblingPath = new StringBuffer();
        	for (int i: elementPos) {
        		siblingPath.append("/*[" + i + "]");
        	}
        	if (isEnd) {
        		siblingPath.append("/*[" + pos + "]");
        	}
        	if (loaded.containsKey(locale)) {
        		hyphenator = loaded.get(locale);
        	} else {
        		EventBus.getInstance().publish(new MessageEvent(this, "Loading hyphenation file: " + languageFileRelativePath, MessageEvent.Type.DEBUG));
	        	hyphenator = new net.davidashen.text.Hyphenator();
		        InputStream language = this.getClass().getResourceAsStream(languageFileRelativePath);
		        hyphenator.setErrorHandler(new HyphenatorErrorHandler(languageFileRelativePath));
		        try {
					hyphenator.loadTable(language);
					loaded.put(locale, hyphenator);
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	EventBus.getInstance().publish(new MessageEvent(this, "Using hyphenator for '" + locale + "' beginning at the " + (isEnd ? "end of element: " : "start of element: ") + siblingPath, MessageEvent.Type.DEBUG));
        }
    }
}