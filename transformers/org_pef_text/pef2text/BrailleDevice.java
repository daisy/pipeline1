package org_pef_text.pef2text;

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
	//private final static DocFlavor FLAVOR = DocFlavor.BYTE_ARRAY.AUTOSENSE;
	private final static DocFlavor FLAVOR = DocFlavor.INPUT_STREAM.AUTOSENSE;
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
		PrintService[] printers = PrintServiceLookup.lookupPrintServices(FLAVOR, null);
		return printers;
	}

	/**
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 * @throws PrintException
	 */
	public void emboss(File file) throws FileNotFoundException, PrintException {
		InputStreamDoc doc = new InputStreamDoc(file);
		dpj.print(doc, null);
	}
	
	private class InputStreamDoc implements Doc {
		private File file;
		private InputStream stream;
		
		/**
		 * Default constructor
		 * @param file
		 * @throws FileNotFoundException
		 */
		public InputStreamDoc(File file) throws FileNotFoundException {
			this.file = file;
		}

		public DocAttributeSet getAttributes() {
			return null;
		}

		public DocFlavor getDocFlavor() {
			return FLAVOR;
		}

		public Object getPrintData() throws IOException {
			return getStreamForBytes();
		}

		public Reader getReaderForText() throws IOException {
			return null;
		}

		public InputStream getStreamForBytes() throws IOException {
			synchronized (this) {
				if (stream==null) {
					stream = new FileInputStream(file);
				}
				return stream;
			}
		}
	}
}
