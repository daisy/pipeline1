package org_pef_text.pef2text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author  Joel Hakansson, TPB
 * @version 3 sep 2008
 * @since 1.0
 */
/*
 * NOTE: Always use upper case in enum values
 */
public class PEFHandler extends DefaultHandler {
	private static final String PEF_NS="http://www.daisy.org/ns/2008/pef";
	private AbstractEmbosser embosser;
	private Stack<Element> elements;
	private Element rowParent;
	private Element pageParent;
	private int inputPages;
	private Range range;
	
	public static class Builder {
		//required params
		private AbstractEmbosser embosser;
		//optional params
		private Range range = new Range(1);

		/**
		 * Create a new PEFHandler builder
		 * @param output
		 */
		public Builder(AbstractEmbosser embosser) {
			this.embosser = embosser;
		}
		
		//init optional params here
		public Builder range(Range value) {
			if (value!=null && !"".equals(value)) {
				range = value;
			}
			return this; 
		}

		public PEFHandler build() throws IOException {
			return new PEFHandler(this);
		}
	}
	
	private PEFHandler(Builder builder) throws IOException {
		range = builder.range;
		embosser = builder.embosser;
        this.elements = new Stack<Element>();
        this.rowParent = null;
        this.pageParent = null;
        this.inputPages = 0;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		HashMap<String, String> atts = new HashMap<String, String>();
		if (!elements.isEmpty()) {
			for (int i=0; i<attributes.getLength(); i++) {
				String attNS = attributes.getURI(i);
				addKey(atts, attNS, attributes.getLocalName(i), attributes.getValue(i));
			}
			inheritKey(atts, "", "rowgap");
			inheritKey(atts, "", "duplex");
		}
		if (PEF_NS.equals(uri)) {
			if (elements.isEmpty() && !"pef".equals(localName)) {
				throw new RuntimeException("Wrong root element.");
			} else if ("row".equals(localName)) {
				if (rowParent==elements.peek() && range.inRange(inputPages)) {
					try {
						embosser.newLine();
					} catch (IOException e) {
						throw new SAXException(e);
					}
				}
				embosser.setRowGap(Integer.parseInt(getKey(atts, "", "rowgap")));
			} else if ("page".equals(localName)) {
				inputPages++;
				if (range.inRange(inputPages)) {
					try {
						if (pageParent==elements.peek()) {
							embosser.newPage();
						} else if (pageParent!=null) {
							embosser.newSectionAndPage("true".equals(getKey(atts, "", "duplex")));
						} else {
							embosser.open("true".equals(getKey(atts, "", "duplex")));
						}
					} catch (IOException e) {
						throw new SAXException(e);
					}
				}
			}
		}
		elements.push(new Element(uri, localName, atts));
	}
	
	private String toKey(String uri, String localName) {
		return uri+">"+localName;
	}
	private void inheritKey(HashMap<String, String> to, String uri, String localName) {
		String key = toKey(uri, localName);
		if (!to.containsKey(key)) {
			Element e = elements.peek();
			if (e.getAttributes().containsKey(key)) {
				addKey(to, uri, localName, e.getAttributes().get(key));
			}
		}
	}
	
	private void addKey(HashMap<String, String> map, String uri, String localName, String value) {
		map.put(toKey(uri, localName), value);
	}
	
	private String getKey(HashMap<String, String> map, String uri, String localName) {
		return map.get(toKey(uri, localName));
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		elements.pop();
		if (PEF_NS.equals(uri)) {
			if ("page".equals(localName) && range.inRange(inputPages)) {
				pageParent = elements.peek();
			} else if ("row".equals(localName) && range.inRange(inputPages)) {
				rowParent = elements.peek();
			}
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		Element context = elements.peek();
		if (PEF_NS.equals(context.getUri()) 
				&& "row".equals(context.getLocalName())
				&& range.inRange(inputPages)) {
			String text = new String(ch, start, length);
			try {
				embosser.write(text);
			} catch (IOException e) {
				throw new SAXException(e);
			}
		}
	}

	public void endDocument() throws SAXException {
		try {
			embosser.newPage();
			embosser.close();
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}
}
