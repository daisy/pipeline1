package org_pef_text.pef2text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
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
import org.daisy.util.file.TempFile;
import org.xml.sax.SAXException;

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

		sendMessage("This implementation does not support volumes.", MessageEvent.Type.WARNING);
		File output=null;
		try {
			if ("".equals(outFileName)) {
				output = TempFile.create();
			} else {
				output = new File(outFileName);
			}
			Range rangeObj = null;
			EmbosserFactory ef = new EmbosserFactory();
			if (embosser!=null && !"".equals(embosser)) {
				ef.setEmbosserType(EmbosserFactory.EmbosserType.valueOf(embosser.toUpperCase()));
			}
			ef.setProperty("breaks", breaks);
			if (range!=null && !"".equals(range)) {
				rangeObj = Range.parseRange(range);
			}
			ef.setProperty("table", table);
			ef.setProperty("padNewline", pad);
			AbstractEmbosser embosserObj = ef.newEmbosser(new FileOutputStream(output));
			PEFHandler.Builder builder = new PEFHandler.Builder(embosserObj);
			builder.range(rangeObj);
			PEFHandler ph = builder.build();
			PEFParser.parse(input, ph);
			progress(0.5);
			if (deviceName!=null && !"".equals(deviceName)) {
				PrinterDevice bd = new PrinterDevice(deviceName, true);
				bd.transmit(output);
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
}
