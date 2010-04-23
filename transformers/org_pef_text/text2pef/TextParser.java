package org_pef_text.text2pef;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org_pef_text.TableFactory;

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
//TODO: Add rows and cols params. Implement support for maximum page size. If exceeded, break row or page.

public class TextParser {
	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Command line entry point.
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length<2) {
			System.out.println("TextParser input output [options ...]");
			System.out.println();
			System.out.println("Arguments");
			System.out.println("  input               path to the input file");
			System.out.println("  output              path to the output file");
			System.out.println();
			System.out.println("Options");
			System.out.println("  -mode value        input braille code, available values are:");
			System.out.println("                          \"detect\" (default)");
			for (TableFactory.TableType t : TableFactory.TableType.values()) {
				System.out.println("                          \"" + t.toString().toLowerCase() + "\"");
			}
			System.out.println("  -author value       the author of the publication");
			System.out.println("  -title value        the title of the publication");
			System.out.println("  -identifier value   the publications unique identifier. If no value is supplied, it will be a generated.");
			System.out.println("  -language value     set the document language (as defined by IETF RFC 3066)");
			System.out.println("  -duplex value       set the documents duplex property. Default is \"true\"");
			System.out.println("  -date value         set the publication date using the form \"yyyy-MM-dd\"");
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
			TextHandler.Builder builder = new TextHandler.Builder(new File(args[0]), new File(args[1]));
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
			TextHandler tp = builder.build();
			tp.parse();
		}
	}	

}
