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

/**
 * 
 * Printer device class of type DocFlavor.INPUT_STREAM.AUTOSENSE
 * 
 * This class can be used when sending a file to a printer.
 * 
 * @author  Joel Hakansson, TPB
 * @version 3 jul 2008
 * @since 1.0
 */
public class PrinterDevice {
	//private final static DocFlavor FLAVOR = DocFlavor.BYTE_ARRAY.AUTOSENSE;
	private final static DocFlavor FLAVOR = DocFlavor.INPUT_STREAM.AUTOSENSE;
	private DocPrintJob dpj;
	
	/**
	 * Create a device with the provided name.
	 * @param deviceName the name of the device
	 * @param fuzzyLookup If true, the returned device is any device whose name contains the 
	 * supplied deviceName. If false, the returned device name equals the supplied deviceName. 
	 * @throws IllegalArgumentException if no device is found.
	 */
	public PrinterDevice(String deviceName, boolean fuzzyLookup) {
		PrintService[] printers = PrintServiceLookup.lookupPrintServices(FLAVOR, null);
		for (PrintService p : printers) {
			if (p.getName().equals(deviceName) ||
					(p.getName().contains(deviceName) && fuzzyLookup)) {
				dpj = p.createPrintJob();
				return;
			}
		}
		throw new IllegalArgumentException("Could not find embosser.");
	}
	
	/**
	 * List available devices
	 * @return returns a list of available devices that accepts DocFlavor.INPUT_STREAM.AUTOSENSE 
	 */
	public static PrintService[] getDevices() {
		PrintService[] printers = PrintServiceLookup.lookupPrintServices(FLAVOR, null);
		return printers;
	}

	/**
	 * Transmit a file to the device
	 * @param file the file to transmit
	 * @throws FileNotFoundException
	 * @throws PrintException
	 */
	public void transmit(File file) throws FileNotFoundException, PrintException {
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
