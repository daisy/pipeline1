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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.EmptyStackException;
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
	private boolean root;
	private net.davidashen.text.Hyphenator hyphenator;
	private boolean bypass;

	public HyphenReader(Hyphenator t, XMLInputFactory inFactory, File input, File output) throws XMLStreamException, FileNotFoundException {
		super(inFactory.createXMLEventReader(new FileInputStream(input)), new FileOutputStream(output));
		lang = new Stack<String>();
		root = true;
		bypass = false;
		this.hyphenator = new net.davidashen.text.Hyphenator();
	}
	
    protected StartElement startElement(StartElement event) {
    	Attribute a = event.getAttributeByName(new QName("http://www.w3.org/XML/1998/namespace", "lang"));
    	if (a!=null) {
    		this.loadHyphenationRules(a.getValue());
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
    		lang.pop();
    	}
    	return event;
    }
    
    protected Characters characters(Characters event) {
    	if (bypass) return event;
    	else return getEventFactory().createCharacters(hyphenator.hyphenate(event.getData()));
    }
    
    private void loadHyphenationRules(String locale) {
        Properties tables = new Properties();
        URL tablesURL = this.getClass().getResource("hyphenation_tables.xml");
        if(tablesURL!=null){
            try {
				tables.loadFromXML(tablesURL.openStream());
			} catch (InvalidPropertiesFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else {
        	
        }
        String languageFileRelativePath = tables.getProperty(locale);
        if(languageFileRelativePath==null){
        	EventBus.getInstance().publish(new MessageEvent(this, "Language not supported.", MessageEvent.Type.ERROR));
        } else { 
	        InputStream language = this.getClass().getResourceAsStream(languageFileRelativePath);
	        hyphenator.setErrorHandler(new HyphenatorErrorHandler(languageFileRelativePath));
	        try {
				hyphenator.loadTable(language);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    

}
