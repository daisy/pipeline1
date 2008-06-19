package org_pef_pef2text;

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

/**
 * The purpose of this transformer is to convert a PEF 2008-1 file into plain text.
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
        String xmlFileName = parameters.remove("xml");
        String xsltFileName = parameters.remove("xslt");
        String outFileName = parameters.remove("out");
        String factory = parameters.remove("factory");
        String breaks = parameters.remove("breaks");
        String deviceName = parameters.remove("deviceName");
        String embosser = parameters.remove("embosser");
        String pageRange = parameters.remove("pageRange");
        
        if (deviceName==null) {
        	deviceName="";
        }
        if (pageRange==null) {
        	pageRange="";
        }
        if (!"".equals(pageRange)) {
        	String[] pages = pageRange.split("-");
        	parameters.put("min", pages[0]);
        	if (pages.length>1) {
        		if (Integer.parseInt(pages[0])>Integer.parseInt(pages[1])) {
        			sendMessage("Illegal page range.", MessageEvent.Type.ERROR);
        			return false;
        		}
        		parameters.put("max", pages[1]);
        	}
        }
        if (!"".equals(deviceName) && deviceName.length()<=3) {
        	deviceName="";
        	sendMessage("Device name too short!", MessageEvent.Type.ERROR);
        }
        if ("none".equals(deviceName)) {
        	deviceName="";
        }
        byte[] header = null;
        byte[] footer = null;
        String newline;
		if ("unix".equals(breaks)) {
			newline = "\n";
		} else if ("dos".equals(breaks)) {
			newline = "\r\n";
		} else if ("mac".equals(breaks)) {
			newline = "\r";
		} else {
			newline = System.getProperty("line.separator", "\r\n");
		}
		sendMessage("This implementation does not support row gap.", MessageEvent.Type.WARNING);
		sendMessage("This implementation does not support duplex.", MessageEvent.Type.WARNING);
		sendMessage("This implementation does not support volumes.", MessageEvent.Type.WARNING);
		if ("everest".equals(embosser)) {
			header = new byte[]{0x1b, 0x0f, 0x02, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x1b, 0x0f, 0x1b, 0x0f, 0x02, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x39, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x1b, 0x0f, 0x1b, 0x0f, 0x02, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x1b, 0x0f, 0x00};
			footer = new byte[]{0x1a};
		} 
		try {
			File output;
			if ("".equals(outFileName)) {
				output = TempFile.create();
			} else {
				output = new File(outFileName);
			}
			FileJuggler tmpFiles = new FileJuggler(new File(xmlFileName), output);
	        // convert into text
			Map<String,Object> xslParams = new HashMap<String,Object>();
			xslParams.putAll(parameters);
		    Stylesheet.apply(tmpFiles.getInput().getAbsolutePath(), xsltFileName, tmpFiles.getOutput().getAbsolutePath(), factory, xslParams, CatalogEntityResolver.getInstance());
			// change line breaks and add headers and footers
			tmpFiles.swap();
			LineNumberReader lr = new LineNumberReader(new FileReader(tmpFiles.getInput()));
			FileOutputStream pw = new FileOutputStream(tmpFiles.getOutput());
			if (header!=null) {
				pw.write(header);
			}
			String line;
			boolean first=true;
			do {
				line = lr.readLine();
				if (line!=null) {
					if (!first) {
						pw.write(newline.getBytes());
					}
					pw.write(line.getBytes());
					first = false;
				}
			} while (line!=null);
			lr.close();
			if (footer!=null) {
				pw.write(footer);
			}
			pw.close();
			// emboss?
			boolean ok=true;
			if (deviceName!=null && !"".equals(deviceName)) {
				tmpFiles.swap();
				ok = emboss(tmpFiles.getInput(), deviceName);
			}
			// finalize result
			tmpFiles.close();
			
			// delete output?
			if ("".equals(outFileName)) {
				output.delete();
			}
			if (!ok) return false;
        } catch (XSLTException e) {
            throw new TransformerRunException(e.getMessage(), e);
		} catch (CatalogExceptionNotRecoverable e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (IOException e) {
			throw new TransformerRunException(e.getMessage(), e);
		}
        return true;
	}

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
