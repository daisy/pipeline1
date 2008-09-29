package org_pef_text.pef2text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org_pef_text.BrailleFormat;
import org_pef_text.BrailleFormat.EightDotFallbackMethod;

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
	public enum Embosser {NONE, INDEX_EVEREST, INDEX_BASIC};
	public enum LineBreaks {DOS, UNIX, MAC};
	public enum Padding {BOTH, BEFORE, AFTER, NONE};
	
	private FileOutputStream os;
	private BrailleFormat bf;
	//private String tableDef;
	private byte[] header;
	private byte[] footer;
	private byte[] newline;
	private byte[] formfeed;
	private Stack<Element> elements;
	private Element rowParent;
	private int inputPages;
	private Range range;
	//private Charset charset;
	
	public static class Builder {
		//required params
		private File output;
		//optional params
		private Embosser embosser = Embosser.values()[0];
		private BrailleFormat.Mode mode = BrailleFormat.Mode.values()[0];
		private Range range = new Range(1);
		private LineBreaks breaks = LineBreaks.values()[0];
		private Padding padNewline = Padding.values()[0];
		private EightDotFallbackMethod fallback = EightDotFallbackMethod.values()[0];
		private char replacement = '\u2800';

		/**
		 * Create a new PEFHandler builder
		 * @param output
		 */
		public Builder(File output) {
			this.output = output;
		}
		
		//init optional params here
		public Builder embosser(Embosser value) { embosser = value; return this; }
		public Builder mode(BrailleFormat.Mode value) { mode = value; return this; }
		public Builder range(Range value) { range = value; return this; }
		public Builder breaks(LineBreaks value) { breaks = value; return this; }
		public Builder padNewline(Padding value) { padNewline = value; return this; }
		public Builder fallback(EightDotFallbackMethod value) { fallback = value; return this; }
		public Builder replacement(char value) { replacement = value; return this; }
		
		public PEFHandler build() throws FileNotFoundException, UnsupportedEncodingException { return new PEFHandler(this); }
	}
	
	private PEFHandler(Builder builder) throws FileNotFoundException, UnsupportedEncodingException {
		range = builder.range;
		this.os = new FileOutputStream(builder.output);
        switch (builder.breaks) {
        	case UNIX: newline = "\n".getBytes(); break;
        	case DOS: newline = "\r\n".getBytes(); break;
        	case MAC: newline = "\r".getBytes(); break;
        	default: newline = System.getProperty("line.separator", "\r\n").getBytes();
        }
        BrailleFormat.Builder bfb = new BrailleFormat.Builder(builder.mode);
        bfb.fallback(builder.fallback);
        bfb.replacement(builder.replacement);
        this.bf = bfb.build();
        this.header = null;
        this.footer = null;
        ArrayList<Byte> ffArray = new ArrayList<Byte>();
        switch (builder.padNewline) {
        	case BEFORE:
        		ffArray.addAll(Arrays.asList(newline[0]));
        	case NONE:
        		ffArray.add((byte)0x0c); 
        		break;
        	case BOTH:
        		ffArray.addAll(Arrays.asList(newline[0]));
        	case AFTER:
        		ffArray.add((byte)0x0c); 
        		ffArray.addAll(Arrays.asList(newline[0]));
        		break;        	
        }
        this.formfeed = new byte[ffArray.size()];
        for (int i=0; i<ffArray.size(); i++) {
        	this.formfeed[i] = ffArray.get(i);
        }
        this.elements = new Stack<Element>();
        switch (builder.embosser) {
        	case NONE:
        		break;
        	case INDEX_BASIC:
        	case INDEX_EVEREST:
    			header = new byte[]{0x1b, 0x0f, 0x02, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x1b, 0x0f, 0x1b, 0x0f, 0x02, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x39, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x1b, 0x0f, 0x1b, 0x0f, 0x02, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x1b, 0x0f, 0x00};
    			footer = new byte[]{0x1a};
        		break;
        }
        this.rowParent = null;
        this.inputPages = 0;
	}

	private void writeInRange(byte[] b) throws SAXException {
		if (range.inRange(inputPages)) {
			try {
				os.write(b);
			} catch (IOException e) {
				throw new SAXException(e);
			}
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("http://www.daisy.org/ns/2008/pef".equals(uri)) {
			if (elements.isEmpty() && !"pef".equals(localName)) {
				throw new RuntimeException("Wrong root element.");
			} else if ("row".equals(localName)) {
				if (rowParent==elements.peek()) {
					writeInRange(newline);
				}
			} else if ("page".equals(localName)) {
				inputPages++;
			}
		}
		elements.push(new Element(uri, localName));
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		elements.pop();
		if ("http://www.daisy.org/ns/2008/pef".equals(uri)) {
			if ("page".equals(localName)) {
				writeInRange(formfeed);
			} else if ("row".equals(localName)) {
				rowParent = elements.peek();
			}
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		Element context = elements.peek();
		if ("http://www.daisy.org/ns/2008/pef".equals(context.getUri()) 
				&& "row".equals(context.getLocalName())) {
			String text = new String(ch, start, length);
			for (char c : text.toCharArray()) {
				try {
					writeInRange(String.valueOf(bf.toText(c)).getBytes(bf.getPreferredCharset().name()));
				} catch (UnsupportedEncodingException e) {
					throw new SAXException(e);
				}
			}
		}
	}

	public void startDocument() throws SAXException {
		try {
			if (header!=null) {
				os.write(header);
			}
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	public void endDocument() throws SAXException {
		try {
			if (footer!=null) {
				os.write(footer);
			}
			os.close();
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}
}
