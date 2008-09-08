package org_pef_text.text2pef;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org_pef_text.BrailleFormat;

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
	private BrailleFormat mode;
	private boolean duplex;
	private Date date;

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
		private BrailleFormat.Mode mode = BrailleFormat.Mode.values()[0];
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
		public Builder mode(BrailleFormat.Mode value) {
			if (value==null) throw new IllegalArgumentException("Null value not accepted.");
			mode = value; return this;
		}
		public Builder duplex(boolean value) {
			duplex = value; return this;
		}
		public Builder date(Date value) {
			if (value==null) throw new IllegalArgumentException("Null value not accepted.");
			date = value; return this;
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
		mode = new BrailleFormat.Builder(builder.mode).build();
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
					builder.mode(BrailleFormat.Mode.valueOf(args[3+i*2].toUpperCase()));
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
		int maxRows=0;
		int maxCols=0;
		int cRows=0;
		String line = lr.readLine();
		cRows++;
		boolean pageClosed=true;
		while (line!=null) {
			if (pageClosed) {
				pageClosed=false;
			}
			if (line.length()==1 && line.charAt(0)==0x0c) {
				pageClosed=true;
				cRows--; // don't count this row
				if (cRows>maxRows) {
					maxRows=cRows;
				}
				cRows=0;
			} else {
				if (line.length()>maxCols) {
					maxCols=line.length();
				}
			}
			line = lr.readLine();
			cRows++;
		}
		lr.close();
		if (!pageClosed) {
			pageClosed=true;
			cRows--; // don't count this row
			if (cRows>maxRows) {
				maxRows=cRows;
			}
			cRows=0;			
		}
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
		pageClosed = true;
		line = lr.readLine();
		while (line!=null) {
			if (pageClosed) {
				pw.println("				<page>");
				pageClosed=false;
			}
			if (line.length()==1 && line.charAt(0)==0x0c) {
				pw.println("				</page>");
				pageClosed=true;
			} else {
				pw.print("					<row>");
				pw.print(mode.toBraille(line));
				pw.println("</row>");
			}
			line = lr.readLine();
		}
		lr.close();
		if (!pageClosed) {
			pw.println("				</page>");
			pageClosed=true;			
		}
		pw.println("			</section>");
		pw.println("		</volume>");
		pw.println("	</body>");
		pw.println("</pef>");
		pw.close();
	}

}
