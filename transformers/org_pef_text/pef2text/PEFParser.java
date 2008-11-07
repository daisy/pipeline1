package org_pef_text.pef2text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org_pef_text.TableFactory;
import org_pef_text.TableFactory.EightDotFallbackMethod;

/**
 * SAX implementation of pef2text.xsl 
 * 
 * @author  Joel Hakansson, TPB
 * @version 2 jul 2008
 * @since 1.0
 */
/* TODO: Allow setting another braille table
 * TODO: Implement support for row gap
 */
public class PEFParser {

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
			for (EmbosserFactory.EmbosserType e : EmbosserFactory.EmbosserType.values()) {
				System.out.println("                          \"" + e.toString().toLowerCase() + "\"" + (first?" (default)":""));
				first=false;
			}
			System.out.println("  -table value        braille code table, available values are:");
			first=true;
			for (TableFactory.TableType t : TableFactory.TableType.values()) {
				System.out.println("                          \"" + t.toString().toLowerCase() + "\"" + (first?" (default)":""));
				first=false;
			}			
			System.out.println("  -breaks value       line break style, available values are:");
			first=true;
			for (LineBreaks.Type b : LineBreaks.Type.values()) {
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
			Range range = null;
			EmbosserFactory ef = new EmbosserFactory();
			for (int i=0; i<(args.length-2)/2; i++) {
				if ("-embosser".equals(args[2+i*2])) {
					ef.setEmbosserType(EmbosserFactory.EmbosserType.valueOf(args[3+i*2].toUpperCase()));
				} else if ("-table".equals(args[2+i*2])) {
					ef.setProperty("table", args[3+i*2]);
				} else if ("-breaks".equals(args[2+i*2])) {
					ef.setProperty("breaks", args[3+i*2]);
				} else if ("-range".equals(args[2+i*2])) {
					range = Range.parseRange(args[3+i*2]);
				} else if ("-fallback".equals(args[2+i*2])) {
					ef.setProperty("fallback", args[3+i*2]);
				} else if ("-replacement".equals(args[2+i*2])) {
					ef.setProperty("replacement", args[3+i*2]);
				} else if ("-pad".equals(args[2+i*2])) {
					ef.setProperty("pad_newline", args[3+i*2]);
				} else {
					throw new IllegalArgumentException("Unknown option \"" + args[2+i*2] + "\"");
				}
			}
			AbstractEmbosser embosser = ef.newEmbosser(new FileOutputStream(args[1]));
			PEFHandler.Builder builder = new PEFHandler.Builder(embosser);
			builder.range(range);
			PEFHandler ph = builder.build();
			parse(new File(args[0]), ph);
		}
	}
	
	/**
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void parse(File input, PEFHandler ph) throws ParserConfigurationException, SAXException, IOException {
		if (!input.exists()) {
			throw new IllegalArgumentException("Input does not exist");
		}
		/*
		if (embosser.equals(Embosser.NONE)) {
			ph = new PEFHandler(output, mode, breaks, range);
		} else {
			ph = new PEFHandler(output, embosser, range);
		}*/
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		SAXParser sp = spf.newSAXParser();
		sp.parse(input, ph);
	}

}
