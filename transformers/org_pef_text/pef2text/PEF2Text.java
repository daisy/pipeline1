package org_pef_text.pef2text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.DocAttributeSet;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FileJuggler;
import org.daisy.util.file.TempFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;
import org.xml.sax.SAXException;
import org_pef_text.BrailleFormat;
import org_pef_text.BrailleFormat.EightDotFallbackMethod;
import org_pef_text.pef2text.PEFHandler.Embosser;
import org_pef_text.pef2text.PEFHandler.LineBreaks;

/**
 * The purpose of this transformer is to convert a PEF 2008-1 file into plain text.
 * Transformer wrapper for PEFParser
 * 
 * @author Joel Hakansson, TPB
 */
public class PEF2Text extends Transformer {

	/**
	 * 
	 * @param inListener
	 * @param isInteractive
	 */
	public PEF2Text(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}
	
	@Override
	protected boolean execute(Map<String, String> parameters) throws TransformerRunException {
        File input = new File(parameters.remove("xml"));
        String outFileName = parameters.remove("out");
        String breaks = parameters.remove("breaks");
        String embosser = parameters.remove("embosser");
        String range = parameters.remove("pageRange");
        String table = parameters.remove("table");
        String pad = parameters.remove("pad");
        
        String deviceName = parameters.remove("deviceName");
        
		sendMessage("This implementation does not support row gap.", MessageEvent.Type.WARNING);
		sendMessage("This implementation does not support duplex.", MessageEvent.Type.WARNING);
		sendMessage("This implementation does not support volumes.", MessageEvent.Type.WARNING);
		File output=null;
		try {
			if ("".equals(outFileName)) {
				output = TempFile.create();
			} else {
				output = new File(outFileName);
			}
			PEFHandler.Builder builder = new PEFHandler.Builder(output);
			if (embosser!=null && !"".equals(embosser)) {
				builder.embosser(Embosser.valueOf(embosser.toUpperCase()));
			}
			if (breaks!=null && !"".equals(breaks)) {
				builder.breaks(LineBreaks.valueOf(breaks.toUpperCase()));
			}
			if (range!=null && !"".equals(range)) {
				builder.range(Range.parseRange(range));
			}
			if (table!=null && !"".equals(table)) {
				builder.mode(BrailleFormat.Mode.valueOf(table.toUpperCase()));
			}
			if (pad!=null && !"".equals(pad)) {
				builder.padNewline("true".equals(pad));
			}
			PEFHandler ph = builder.build();
			PEFParser.parse(input, ph);
			progress(0.5);
			if (deviceName!=null && !"".equals(deviceName)) {
				BrailleDevice bd = new BrailleDevice(deviceName, true);
				bd.emboss(output);
			}
		} catch (ParserConfigurationException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (IOException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (PrintException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} finally {
			if ("".equals(outFileName) && output!=null) {
				output.delete();
			}
		}
		progress(1);
        return true;
	}

	/**
	 * @deprecated
	 * @param file
	 * @param embosserName
	 * @return
	 * @throws TransformerRunException
	 */
	private boolean emboss(File file, String embosserName) throws TransformerRunException {
		RawByteDoc doc;
		try {
			doc = new RawByteDoc(file);
		} catch (FileNotFoundException e) {
			throw new TransformerRunException(e.getMessage(), e);
		}
		PrintService[] printers = PrintServiceLookup.lookupPrintServices(doc.getDocFlavor(), null);
		for (PrintService p : printers) {
			if (p.getName().contains(embosserName)) {
				sendMessage("Found a matching embosser: \"" + p.getName() + '"', MessageEvent.Type.INFO);
				DocPrintJob dpj = p.createPrintJob();
				sendMessage("Embossing...", MessageEvent.Type.INFO);
				
				try {
					dpj.print(doc, null);
				} catch (PrintException e) {
					throw new TransformerRunException(e.getMessage(), e);
				}
				
				sendMessage("Done!", MessageEvent.Type.INFO);
				return true;
			}
		}
		sendMessage("Cound not find embosser.", MessageEvent.Type.ERROR);
		if (printers.length>0) {
			sendMessage("Found these devices on your system:", MessageEvent.Type.INFO);
			for (PrintService p : printers) {
				sendMessage('"'+p.getName()+'"', MessageEvent.Type.INFO);
			}
		}
		return false;
	}
	
	/**
	 * @deprecated
	 * ... beskrivning ...
	 * 
	 * @author  Joel Hakansson, TPB
	 * @version 3 sep 2008
	 * @since 1.0
	 */
	private class RawByteDoc implements Doc {
		FileInputStream stream;
		
		/**
		 * Default constructor
		 * @param file
		 * @throws FileNotFoundException
		 */
		public RawByteDoc(File file) throws FileNotFoundException {
			 stream = new FileInputStream(file);
		}

		public DocAttributeSet getAttributes() {
			return null;
		}

		public DocFlavor getDocFlavor() {
			return DocFlavor.BYTE_ARRAY.AUTOSENSE;
		}

		public Object getPrintData() throws IOException {
			return null; //new String("object");
		}

		public Reader getReaderForText() throws IOException {
			return null; //new StringReader("reader");
		}

		public InputStream getStreamForBytes() throws IOException {
			return stream; 
			//new ByteArrayInputStream(new byte[]{0x40, 0x41, 0x42, 0x43, 0x44}) ;
		}
	}
}
