package org_pef_pef2text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.DocAttributeSet;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.exception.TransformerRunException;

/**
 * 
 * ... beskrivning ...
 * 
 * @author  Joel Hakansson, TPB
 * @version 3 jul 2008
 * @since 1.0
 */
public class BrailleDevice {
	private final static DocFlavor FLAVOR = DocFlavor.BYTE_ARRAY.AUTOSENSE;
	private DocPrintJob dpj;
	
	/**
	 * 
	 * @param embosserName
	 * @param fuzzyLookup
	 */
	public BrailleDevice(String embosserName, boolean fuzzyLookup) {
		PrintService[] printers = PrintServiceLookup.lookupPrintServices(FLAVOR, null);
		for (PrintService p : printers) {
			if (p.getName().equals(embosserName) ||
					(p.getName().contains(embosserName) && fuzzyLookup)) {
				dpj = p.createPrintJob();
				return;
			}
		}
		throw new IllegalArgumentException("Could not find embosser.");
	}
	
	/**
	 * 
	 * @return
	 */
	public static PrintService[] getDevices() {
		PrintService[] printers = PrintServiceLookup.lookupPrintServices(DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
		return printers;
	}

	/**
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 * @throws PrintException
	 */
	public void emboss(File file) throws FileNotFoundException, PrintException {
		RawByteDoc doc = new RawByteDoc(file);
		dpj.print(doc, null);
	}
	
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
			return FLAVOR;
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
