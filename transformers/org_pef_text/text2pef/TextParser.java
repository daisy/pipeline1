package org_pef_text.text2pef;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;

import org_pef_text.AbstractTable;
import org_pef_text.TableFactory;
import org_pef_text.TableFactory.TableType;

/**
 * Reads an ASCII file and parses it into a basic PEF file.
 * 
 * In addition to the 64/256 defined code points defined in translation Mode, the
 * characters 0x0a, 0x0d (new row) and 0x0c (new page) may occur in the file. 
 * 
 * @author  Joel Hakansson, TPB
 * @version 28 aug 2008
 * @since 1.0
 */
//TODO: Add rows and cols params
public class TextParser {
	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private File input;
	private File output;
	private String title;
	private String author;
	private String language;
	private String identifier;
	private AbstractTable mode;
	private boolean duplex;
	private Date date;
	
	private int maxRows;
	private int maxCols;

	/**
	 * 
	 * Implements the builder pattern for TextParser
	 * 
	 * @author  Joel Hakansson, TPB
	 * @version 3 sep 2008
	 * @since 1.0
	 */
	public static class Builder {
		// required params
		private File input;
		private File output;

		// optional params
		private String title = "";
		private String author = "";
		private String language = "";
		private String identifier = "";
		private TableFactory.TableType mode = null;
		private boolean duplex = false;
		private Date date = new Date();

		/**
		 * Create a new TextParser builder
		 * @param input
		 * @param output
		 */
		public Builder(File input, File output) {
			this.input = input;
			this.output = output;
			String s = (new Long((Math.round(Math.random() * 1000000000)))).toString();
			char[] chars = s.toCharArray();
			char[] dest = new char[] {'0','0','0','0','0','0','0','0','0'};
			System.arraycopy(chars, 0, dest, 9-chars.length, chars.length);
			this.identifier = "AUTO_ID_" + new String(dest);
		}

		//init optional params here
		public Builder title(String value) {
			if (value==null) throw new IllegalArgumentException("Null value not accepted.");
			title = value; return this; 
		}
		public Builder author(String value) {
			if (value==null) throw new IllegalArgumentException("Null value not accepted.");
			author = value; return this;
		}
		public Builder language(String value) {
			if (value==null) throw new IllegalArgumentException("Null value not accepted.");
			language = value; return this;
		}
		public Builder identifier(String value) {
			if (value==null) throw new IllegalArgumentException("Null value not accepted.");
			identifier = value; return this;
		}
		public Builder mode(TableFactory.TableType value) {
			mode = value;
			return this;
		}
		public Builder duplex(boolean value) {
			duplex = value; return this;
		}
		public Builder date(Date value) {
			if (value==null) throw new IllegalArgumentException("Null value not accepted.");
			date = value; return this;
		}
		
		private BitSet analyze(InputStream is) throws IOException {
			BitSet bs = new BitSet(256);
			int c;
			while ((c=is.read())>-1) {
				bs.set(c);
			}
			return bs;
		}
		
		private TableType detect() {
			try {
				FileInputStream is = new FileInputStream(input);
				BitSet in = analyze(is);
				is.close();
				in.clear(0x0a);
				in.clear(0x0c);
				in.clear(0x0d);
				//in.clear(239); // byte order mark
				AbstractTable at;
				TableFactory f = new TableFactory();
				//TODO: f.setFallback(EightDotFallbackMethod.FAIL);
				StringBuffer tmp = new StringBuffer();
				for (int i=0; i<256; i++) {
					tmp.append((char)(0x2800+i));
				}
				BitSet ta;
				for (TableType tt : TableFactory.TableType.values()) {
					 at = f.newTable(tt);
					 ByteArrayInputStream bs = new ByteArrayInputStream(at.toText(tmp.toString()).getBytes(at.getPreferredCharset().name()));
					 ta = analyze(bs);
					 ta.and(in);
					 if (ta.equals(in)) {
						 System.out.println("Input matches " + tt.name());
						 return tt;
					 }
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		/**
		 * Builds a TextParser using the settings of this Builder
		 * @return returns a new TextParser
		 * @throws UnsupportedEncodingException
		 */
		public TextParser build() throws UnsupportedEncodingException {	return new TextParser(this); }
	}
	
	private TextParser(Builder builder) throws UnsupportedEncodingException {
		input = builder.input;
		output = builder.output;
		title = builder.title;
		author = builder.author;
		language = builder.language;
		identifier = builder.identifier;
		if (builder.mode==null) {
			builder.mode = builder.detect();
			if (builder.mode==null) {
				throw new UnsupportedEncodingException("Cannot detect table.");
			}
		}
		TableFactory b = new TableFactory();
		mode = b.newTable(builder.mode);
		duplex = builder.duplex;
		date = builder.date;
	}

	/**
	 * @param args
	 */
	//TODO: Add command line help text
	public static void main(String[] args) {
		if (args.length<2) {
			System.out.println("Text2PEF input output [options ...]");
		} else {
			try {
				parse(args);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * String based method matching main args
	 * @param args
	 * @throws IOException 
	 */
	public static void parse(String args[]) throws IOException {
		if (args.length < 2 || args.length % 2 != 0) {
			throw new IllegalArgumentException("Wrong number of arguments");
		} else {
			Builder builder = new Builder(new File(args[0]), new File(args[1]));
			for (int i=0; i<(args.length-2)/2; i++) {
				if ("-title".equals(args[2+i*2])) {
					builder.title(args[3+i*2]);
				} else if ("-author".equals(args[2+i*2])) {
					builder.author(args[3+i*2]);
				} else if ("-identifier".equals(args[2+i*2])) {
					builder.identifier(args[3+i*2]);
				} else if ("-mode".equals(args[2+i*2])) {
					builder.mode(TableFactory.TableType.valueOf(args[3+i*2].toUpperCase()));
				} else if ("-language".equals(args[2+i*2])) {
					builder.language(args[3+i*2]);
				} else if ("-duplex".equals(args[2+i*2])) {
					builder.duplex("true".equals(args[3+i*2].toLowerCase()));
				}else if ("-date".equals(args[2+i*2])) {
					try {
						builder.date(DATE_FORMAT.parse(args[3+i*2]));
					} catch (ParseException e) {
						throw new IllegalArgumentException(e);
					}
				} else {
					throw new IllegalArgumentException("Unknown option \"" + args[2+i*2] + "\"");
				}
			}
			TextParser tp = builder.build();
			tp.parse();
		}
	}

	/**
	 * Parse using the current settings 
	 * @throws IOException
	 */
	public void parse() throws IOException {
		if (date==null) {
			date = new Date();
		}
		FileInputStream is = new FileInputStream(input);
		PrintWriter pw = new PrintWriter(output, "utf-8");
		LineNumberReader lr = new LineNumberReader(new InputStreamReader(is, mode.getPreferredCharset()));
		// determine max rows/page and chars/row

		read(lr, null);
		
		// reset input
		is = new FileInputStream(input);
		lr = new LineNumberReader(new InputStreamReader(is, mode.getPreferredCharset()));

		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		pw.println("<pef version=\"2008-1\" xmlns=\"http://www.daisy.org/ns/2008/pef\">");
		pw.println("	<head>");
		pw.println("		<meta xmlns:dc=\"http://purl.org/dc/elements/1.1/\">");
		if (!"".equals(title)) {
			pw.println("			<dc:title>"+title+"</dc:title>");
		}
		if (!"".equals(author)) {
			pw.println("			<dc:creator>"+author+"</dc:creator>");
		}
		if (!"".equals(language)) {
			pw.println("			<dc:language>"+language+"</dc:language>");
		}
		pw.println("			<dc:date>"+DATE_FORMAT.format(date)+"</dc:date>");
		pw.println("			<dc:format>application/x-pef+xml</dc:format>");
		if (!"".equals(identifier)) {
			pw.println("			<dc:identifier>"+identifier+"</dc:identifier>");
		}
		pw.println("		</meta>");
		pw.println("	</head>");
		pw.println("	<body>");
		pw.println("		<volume cols=\""+maxCols+"\" rows=\""+maxRows+"\" rowgap=\"0\" duplex=\""+duplex+"\">");
		pw.println("			<section>");
		
		read(lr, pw);
		pw.println("			</section>");
		pw.println("		</volume>");
		pw.println("	</body>");
		pw.println("</pef>");
		pw.flush();
		pw.close();
	}
	
	private void read(LineNumberReader lr, PrintWriter pw) throws IOException {
		maxRows=0;
		maxCols=0;
		int cRows=0;
		boolean pageClosed = true;
		int eofIndex = -1;
		cRows++;
		String line = lr.readLine();
		while (line!=null) {
			eofIndex = line.indexOf(0x1A);
			if (eofIndex>-1) {
				line = line.substring(0, eofIndex); //remove trailing characters beyond eof-mark (CTRL+Z)
			}
			if ("\f".equals(line)) { // if line consists of a single form feed character. Just close the page (don't add rows yet).
				if (pw!=null) {	pw.println("				</page>");	}
				pageClosed=true;
				cRows--; // don't count this row
				if (cRows>maxRows) { maxRows=cRows;	}
				cRows=0;
			} else {
				String[] pieces = line.split("\\f", -1); //split on form feed
				int i = 1;
				for (String p : pieces) {
					if (i>1) { // there were form feeds
						if (pw!=null) {	pw.println("				</page>");	}
						pageClosed=true;
						cRows--; // don't count this row
						if (cRows>maxRows) { maxRows=cRows;	}
						cRows=0;
					}
					if (pageClosed) {
						if (pw!=null) { pw.println("				<page>"); }
						pageClosed=false;
					}
					if (p.length()>maxCols) {
						maxCols=p.length();
					}
					// don't output if row contains form feeds and this segment equals ""
					if (!(pieces.length>1 && (i==pieces.length || i==1) && "".equals(p))) {
						if (pw!=null) {
							pw.print("					<row>");
							pw.print(mode.toBraille(p));
							pw.println("</row>");
						}
					}
					i++;
				}
			}
			if (eofIndex>-1) {
				// End of file reached. Stop reading.
				line = null;
			} else {
				line = lr.readLine();
				cRows++;
			}
		}
		lr.close();
		if (!pageClosed) {
			if (pw!=null) { pw.println("				</page>"); }
			pageClosed=true;	
			cRows--; // don't count this row
			if (cRows>maxRows) { maxRows=cRows;	}
			cRows=0;
		}
	}
	
	

}
