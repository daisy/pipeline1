package org_pef_pef2text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Stack;

import javax.print.PrintException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX implementation of pef2text.xsl 
 * 
 * @author  Joel Hakansson, TPB
 * @version 2 jul 2008
 * @since 1.0
 */
/* TODO: Allow setting another braille table
 * TODO: Implement 8-dot fallback methods
 * TODO: Implement support for row gap
 */
public class PEFParser {
	/*
	 * Always use upper case in enum values
	 */
	public enum Embosser {NONE, INDEX_EVEREST, INDEX_BASIC};
	public enum BrailleTable {US_ASCII, UNICODE_PATTERNS, SV_SE_CX};
	public enum LineBreaks {DOS, UNIX, MAC};
	public enum EightDotFallbackMethod {MASK, REPLACE, REMOVE};

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length<2) {
			System.out.println("PEFParser input output [options ...]");
			System.out.println();
			System.out.println("Arguments");
			System.out.println("  input               path to the input file");
			System.out.println("  output              path to the output file");
			System.out.println();
			System.out.println("Options");
			System.out.println("  -embosser value     target embosser, available values are:");
			boolean first=true;
			for (Embosser e : Embosser.values()) {
				System.out.println("                          \"" + e.toString().toLowerCase() + "\"" + (first?" (default)":""));
				first=false;
			}
			System.out.println("  -table value        braille code table, available values are:");
			first=true;
			for (BrailleTable t : BrailleTable.values()) {
				System.out.println("                          \"" + t.toString().toLowerCase() + "\"" + (first?" (default)":""));
				first=false;
			}			
			System.out.println("  -breaks value       line break style, available values are:");
			first=true;
			for (LineBreaks b : LineBreaks.values()) {
				System.out.println("                          \"" + b.toString().toLowerCase() + "\"" + (first?" (default)":""));
				first=false;
			}
			System.out.println("  -range from[-to]    output a range of pages");
			System.out.println("  -fallback value     8-dot fallback method, available values are:");
			first=true;
			for (EightDotFallbackMethod f : EightDotFallbackMethod.values()) {
				System.out.println("                          \"" + f.toString().toLowerCase() + "\"" + (first?" (default)":""));
				first=false;
			}
			System.out.println("  -replacement value  replacement pattern, value in range 2800-283F");
			System.out.println("                      (default is 2800)");
			System.out.println();
			System.out.println("Note that the \"table\" and \"breaks\" options depend on target embosser.");

		} else {
			try {
				parse(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	static class Range {
		private int from;
		private int to;
		
		/**
		 * Create a new range.
		 * @param from first page, inclusive
		 * @param to last page, inclusive
		 */
		public Range(int from, int to) {
			init(from, to);
		}
		
		/**
		 * Create a new range.
		 * @param from first page, inclusive
		 */
		public Range(int from) {
			init(from, Integer.MAX_VALUE);
		}
		
		private void init(int from, int to) {
			if (to<from || from<1 || to<1) {
				throw new IllegalArgumentException("Illegal range: " + from + "-" + to);
			}
			this.from = from;
			this.to = to;
		}
		
		/**
		 * 
		 * @param range
		 * @return
		 */
		public static Range parseRange(String range) {
			String[] str = range.split("-");
			if (str.length==1) {
				if (range.indexOf("-")>0){
					return new Range(Integer.parseInt(str[0]));
				} else {
					return new Range(Integer.parseInt(str[0]), Integer.parseInt(str[0]));
				}
			} else {
				if ("".equals(str[0])) {
					return new Range(1, Integer.parseInt(str[1]));
				} else {
					return new Range(Integer.parseInt(str[0]), Integer.parseInt(str[1]));
				}
			}
		}
		
		/**
		 * Test if a value is in range
		 * @param value
		 * @return returns true if value is in range, false otherwise
		 */
		public boolean inRange(int value) {
			if (value>=from && value<=to) return true;
			return false;
		}

	}
	
	/**
	 * String based method matching main args
	 * @param args
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws NumberFormatException 
	 */
	public static void parse(String[] args) throws NumberFormatException, ParserConfigurationException, SAXException, IOException {
		if (args.length < 2 || args.length % 2 != 0) {
			throw new IllegalArgumentException("Wrong number of arguments");
		} else {
			String input = args[0];
			String output = args[1];
			String embosser = Embosser.values()[0].toString();
			String table = BrailleTable.values()[0].toString();
			String breaks = LineBreaks.values()[0].toString();
			String range = "1";
			String fallback = EightDotFallbackMethod.values()[0].toString();
			String replacement = "2800";
			for (int i=0; i<(args.length-2)/2; i++) {
				if ("-embosser".equals(args[2+i*2])) {
					embosser = args[3+i*2];
				} else if ("-table".equals(args[2+i*2])) {
					table = args[3+i*2];
				} else if ("-breaks".equals(args[2+i*2])) {
					breaks = args[3+i*2];
				} else if ("-range".equals(args[2+i*2])) {
					range = args[3+i*2];
				} else if ("-fallback".equals(args[2+i*2])) {
					fallback = args[3+i*2];
				} else if ("-replacement".equals(args[2+i*2])) {
					replacement = args[3+i*2];
				} else {
					throw new IllegalArgumentException("Unknown option \"" + args[2+i*2] + "\"");
				}
			}
			parse(new File(input), new File(output), Embosser.valueOf(embosser.toUpperCase()), BrailleTable.valueOf(table.toUpperCase()), LineBreaks.valueOf(breaks.toUpperCase()), Range.parseRange(range), EightDotFallbackMethod.valueOf(fallback.toUpperCase()), (char)Integer.parseInt(replacement, 16));
			BrailleDevice bd = new BrailleDevice("PPunkt", true);
			try {
				bd.emboss(new File(output));
			} catch (PrintException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param input
	 * @param output
	 * @param embosser
	 * @param table
	 * @param breaks
	 * @param range
	 * @param fallback
	 * @param replacement
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void parse(File input, File output, Embosser embosser, BrailleTable table, LineBreaks breaks, Range range, EightDotFallbackMethod fallback, char replacement) throws ParserConfigurationException, SAXException, IOException {
		if (embosser.equals(Embosser.NONE)) {
			parse(input, output, table, breaks, range);
		} else {
			parse(input, output, embosser, range);
		}
	}

	/**
	 * 
	 * @param input
	 * @param output
	 * @param table
	 * @param breaks
	 * @param range 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void parse(File input, File output, BrailleTable table, LineBreaks breaks, Range range) throws ParserConfigurationException, SAXException, IOException {
		PEFHandler ph = new PEFHandler(output, table, breaks, range);
		doParse(input, ph);
	}

	/**
	 * 
	 * @param input
	 * @param output
	 * @param embosser
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void parse(File input, File output, Embosser embosser) throws ParserConfigurationException, SAXException, IOException {
		parse(input, output, embosser, new Range(1));
	}
	
	/**
	 * 
	 * @param input
	 * @param output
	 * @param embosser
	 * @param range
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void parse(File input, File output, Embosser embosser, Range range) throws ParserConfigurationException, SAXException, IOException {
		PEFHandler ph = new PEFHandler(output, embosser, range);
		doParse(input, ph);
	}
	
	/**
	 * 
	 * @param input
	 * @param ph
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static void doParse(File input, PEFHandler ph) throws SAXException, IOException, ParserConfigurationException {
		if (!input.exists()) {
			throw new IllegalArgumentException("Input does not exist");
		}
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		SAXParser sp = spf.newSAXParser();
		sp.parse(input, ph);
	}
	
	static class PEFHandler extends DefaultHandler {
		private FileOutputStream os;
		private String tableDef;
		private byte[] header;
		private byte[] footer;
		private byte[] newline;
		private byte[] formfeed;
		private Stack<Element> elements;
		private Element rowParent;
		private int inputPages;
		private Range range;
		private Charset charset;
		
		class Element {
			private String uri;
			private String localName;
			
			/**
			 * 
			 * @param uri
			 * @param localName
			 */
			public Element(String uri, String localName) {
				this.uri = uri;
				this.localName = localName;
			}
			
			/**
			 * 
			 * @return
			 */
			public String getUri() {
				return uri;
			}

			/**
			 * 
			 * @return
			 */
			public String getLocalName() {
				return localName;
			}
		}

		/**
		 * 
		 * @param output
		 * @param embosser
		 * @throws FileNotFoundException
		 */
		public PEFHandler(File output, Embosser embosser) throws FileNotFoundException {
			init(output, BrailleTable.US_ASCII, embosser, LineBreaks.DOS, new Range(1, Integer.MAX_VALUE));
		}
		
		/**
		 * 
		 * @param output
		 * @param embosser
		 * @param range
		 * @throws FileNotFoundException
		 */
		public PEFHandler(File output, Embosser embosser, Range range) throws FileNotFoundException {
			init(output, BrailleTable.US_ASCII, embosser, LineBreaks.DOS, range);
		}

		/**
		 * 
		 * @param output
		 * @param table
		 * @param breaks
		 * @throws FileNotFoundException
		 */
		public PEFHandler(File output, BrailleTable table, LineBreaks breaks) throws FileNotFoundException {
			init(output, table, Embosser.NONE, breaks, new Range(1, Integer.MAX_VALUE));
		}
		
		/**
		 * 
		 * @param output
		 * @param table
		 * @param breaks
		 * @param range
		 * @throws FileNotFoundException
		 */
		public PEFHandler(File output, BrailleTable table, LineBreaks breaks, Range range) throws FileNotFoundException {
			init(output, table, Embosser.NONE, breaks, range);
		}
		
		private void init(File output, BrailleTable table, Embosser embosser, LineBreaks breaks, Range range) throws FileNotFoundException {
			this.os = new FileOutputStream(output);
	        switch (breaks) {
	        	case UNIX: newline = "\n".getBytes(); break;
	        	case DOS: newline = "\r\n".getBytes(); break;
	        	case MAC: newline = "\r".getBytes(); break;
	        	default: newline = System.getProperty("line.separator", "\r\n").getBytes();
	        }
	        this.tableDef = "";
	        this.header = null;
	        this.footer = null;
	        this.formfeed = new byte[]{0x0c};
	        this.elements = new Stack<Element>();
	        switch (embosser) {
	        	case NONE:
	        		break;
	        	case INDEX_BASIC:
	        	case INDEX_EVEREST:
	    			header = new byte[]{0x1b, 0x0f, 0x02, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x1b, 0x0f, 0x1b, 0x0f, 0x02, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x39, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x1b, 0x0f, 0x1b, 0x0f, 0x02, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x1b, 0x0f, 0x00};
	    			footer = new byte[]{0x1a};
	        		break;
	        }
	        switch (table) {
	        	case UNICODE_PATTERNS:
	        		// unity character mapping
	        		this.tableDef = new String("⠀⠁⠂⠃⠄⠅⠆⠇⠈⠉⠊⠋⠌⠍⠎⠏⠐⠑⠒⠓⠔⠕⠖⠗⠘⠙⠚⠛⠜⠝⠞⠟⠠⠡⠢⠣⠤⠥⠦⠧⠨⠩⠪⠫⠬⠭⠮⠯⠰⠱⠲⠳⠴⠵⠶⠷⠸⠹⠺⠻⠼⠽⠾⠿⠀⠁⠂⠃⠄⠅⠆⠇⠈⠉⠊⠋⠌⠍⠎⠏⠐⠑⠒⠓⠔⠕⠖⠗⠘⠙⠚⠛⠜⠝⠞⠟⠠⠡⠢⠣⠤⠥⠦⠧⠨⠩⠪⠫⠬⠭⠮⠯⠰⠱⠲⠳⠴⠵⠶⠷⠸⠹⠺⠻⠼⠽⠾⠿⠀⠁⠂⠃⠄⠅⠆⠇⠈⠉⠊⠋⠌⠍⠎⠏⠀⠁⠂⠃⠄⠅⠆⠇⠈⠉⠊⠋⠌⠍⠎⠏⠀⠁⠂⠃⠄⠅⠆⠇⠈⠉⠊⠋⠌⠍⠎⠏\u2800\u2801\u2802\u2803\u2804\u2805\u2806\u2807\u2808\u2809\u280A\u280B\u280C\u280D\u280E\u280F\u2810\u2811\u2812\u2813\u2814\u2815\u2816\u2817\u2818\u2819\u281A\u281B\u281C\u281D\u281E\u281F\u2820\u2821\u2822\u2823\u2824\u2825\u2826\u2827\u2828\u2829\u282A\u282B\u282C\u282D\u282E\u282F\u2830\u2831\u2832\u2833\u2834\u2835\u2836\u2837\u2838\u2839\u283A\u283B\u283C\u283D\u283E\u283F");
	        		break;
	        	case US_ASCII:
	        		this.tableDef = new String(" a1b'k2l@cif/msp\"e3h9o6r^djg>ntq,*5<-u8v.%[$+x!&;:4\\0z7(_?w]#y)=");
	        		break;
	        	case SV_SE_CX:
	        		this.tableDef = new String(" a,b.k;l^cif/msp'e:h*o!r~djgäntq_å?ê-u(v@îöë§xèç\"û+ü)z=à|ôwï#yùé");
	        		break;
	        }
	        this.rowParent = null;
	        this.inputPages = 0;
	        this.range = range;
	        this.charset = Charset.forName("UTF-8");
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
					writeInRange(String.valueOf(tableDef.charAt((int)(c & 0x003F))).getBytes(charset));
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

}
