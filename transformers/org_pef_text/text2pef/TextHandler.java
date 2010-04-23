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
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;

import org_pef_text.BrailleTable;
import org_pef_text.TableFactory;
import org_pef_text.TableFactory.TableType;

public class TextHandler {
	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private File input;
	private File output;
	private String title;
	private String author;
	private String language;
	private String identifier;
	private BrailleTable mode;
	private boolean duplex;
	private Date date;
	
	private int maxRows;
	private int maxCols;

	/**
	 * 
	 * Implements the builder pattern for TextHandler
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
		private boolean duplex = true;
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
			BitSet set = new BitSet(256);
			int val;
			while ((val=is.read())>-1) {
				set.set(val);
			}
			return set;
		}
		
		private TableType detect() {
			try {
				FileInputStream is = new FileInputStream(input);
				BitSet inputThumbprint = analyze(is);
				is.close();
				inputThumbprint.clear(0x0a); //LF
				inputThumbprint.clear(0x0c); //FF
				inputThumbprint.clear(0x0d); //CF
				inputThumbprint.clear(0x1a); //SUB
				StringBuffer brailleChars = new StringBuffer();
				for (int i=0; i<256; i++) {
					brailleChars.append((char)(0x2800+i));
				}
				BrailleTable table;
				TableFactory factory = new TableFactory();
				BitSet tableThumbprint;
				TableType t1 = null;
				for (TableType type : TableFactory.TableType.values()) {
					 table = factory.newTable(type);
					 ByteArrayInputStream bis = new ByteArrayInputStream(table.toText(brailleChars.toString()).getBytes(table.getPreferredCharset().name()));
					 tableThumbprint = analyze(bis);
					 tableThumbprint.and(inputThumbprint);
					 if (tableThumbprint.equals(inputThumbprint)) {
						 if (t1==null) {
							 System.out.println("Input matches " + type.name());
							 t1 = type;
						 } else {
							 System.out.println("Warning: Input also matches " + type.name());
						 }
					 }
				}
				if (t1!=null) {
					System.out.println("Using " + t1.name());
					return t1;
				}
				System.out.println("None found:");
				for (int i=0; i<inputThumbprint.length(); i++) {
					if (inputThumbprint.get(i)) {
						System.out.print(""+(char)i);
					}
				}
				System.out.println();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		/**
		 * Builds a TextParser using the settings of this Builder
		 * @return returns a new TextParser
		 * @throws UnsupportedEncodingException
		 */
		public TextHandler build() throws UnsupportedEncodingException {	return new TextHandler(this); }
	}

	private TextHandler(Builder builder) throws UnsupportedEncodingException {
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
	 * Parse using current settings
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
				// if page is already closed, this is an empty page
				if (pageClosed) {
					if (pw!=null) { pw.println("				<page>"); }
					pageClosed=false;
				}
				if (pw!=null) { pw.println("				</page>"); }
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