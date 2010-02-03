package org_pef_text.pef2text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.print.PrintException;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.TempFile;
import org.xml.sax.SAXException;

// TODO: Implement variable braille cell dimensions
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
        String outFileName = parameters.remove("out");
        String breaks = parameters.remove("breaks");
        String embosser = parameters.remove("embosser");
        String range = parameters.remove("pageRange");
        String table = parameters.remove("table");
        String pad = parameters.remove("pad");
        String alignmentOffset = parameters.remove("alignmentOffset");
        String mirrorAlign = parameters.remove("mirrorAlign");
        String paperWidthFallback = parameters.remove("paperWidthFallback");
        String papersize = parameters.remove("papersize");
        String cellWidth = parameters.remove("cellWidth");
        String cellHeight = parameters.remove("cellHeight");
        
        String deviceName = parameters.remove("deviceName");

		sendMessage("This implementation does not support volumes.", MessageEvent.Type.WARNING);

		Range rangeObj = null;
		EmbosserFactory ef = new EmbosserFactory();
		if (embosser!=null && !"".equals(embosser)) {
			ef.setEmbosserType(EmbosserFactory.EmbosserType.valueOf(embosser.toUpperCase()));
		}
		if (papersize != null && !"".equals(papersize)) {
			ef.setPaperSize(Paper.PaperSize.valueOf(papersize.toUpperCase()));
		}
		ef.setProperty("breaks", breaks);
		if (range!=null && !"".equals(range)) {
			rangeObj = Range.parseRange(range);
		}
		ef.setProperty("table", table);
		ef.setProperty("padNewline", pad);
		ef.setProperty("cellWidth", cellWidth);
		ef.setProperty("cellHeight", cellHeight);
		
        File orIn = new File(parameters.remove("xml"));
		File[] in = getInput(orIn);
		boolean emboss = deviceName!=null && !"".equals(deviceName);
		int i = 1;
		for (File input : in) {
			File output=null;
			try {
				if ("".equals(outFileName)) {
					output = TempFile.create();
				} else {
					output = new File(outFileName);
					if (orIn.isDirectory()) {
						// input is a directory, therefore output must be too.
						if (output.isDirectory() || output.mkdirs()) {
							output = new File(output, input.getName() + ".txt");
						} else {
							throw new TransformerRunException("Unable to create output directory.");
						}
					}
				}
				convert(input, output, ef, rangeObj, paperWidthFallback, "true".equals(mirrorAlign), Integer.parseInt(alignmentOffset));
				if (emboss) {
					progress((i-0.5)/(double)in.length);
					PrinterDevice bd = new PrinterDevice(deviceName, true);
					bd.transmit(output);
				}
				progress(i/(double)in.length);
				i++;
			} catch (IOException e) {
				throw new TransformerRunException(e.getMessage(), e);
			} catch (PrintException e) {
				throw new TransformerRunException(e.getMessage(), e);
			} finally {
				if ("".equals(outFileName) && output!=null) {
					output.delete();
				}
			}
		}
		
		progress(1);
        return true;
	}
	
	private File[] getInput(File input) {
		if (input.isDirectory()) {
			return input.listFiles();
		} else {
			return new File[]{input};
		}
	}
	
	private void convert(File input, File output, EmbosserFactory ef, Range rangeObj, String paperWidthFallback, boolean align, int offset) throws TransformerRunException {
		try {
			AbstractEmbosser embosserObj = ef.newEmbosser(new FileOutputStream(output));
			PEFHandler.Builder builder = new PEFHandler.Builder(embosserObj)
				.range(rangeObj)
				.alignmentFallback(paperWidthFallback)
				.mirrorAlignment(align).
				offset(offset);
			PEFHandler ph = builder.build();
			PEFParser.parse(input, ph);
		} catch (ParserConfigurationException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (EmbosserFactoryException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (UnsupportedWidthException e) {
			throw new TransformerRunException(e.getMessage(), e);
		}  catch (IOException e) {
			throw new TransformerRunException(e.getMessage(), e);
		}
	}
}
