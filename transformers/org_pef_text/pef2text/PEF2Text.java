package org_pef_text.pef2text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.print.PrintException;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.braille.embosser.Embosser;
import org.daisy.braille.embosser.EmbosserCatalog;
import org.daisy.braille.embosser.EmbosserFeatures;
import org.daisy.braille.embosser.EmbosserWriter;
import org.daisy.braille.embosser.UnsupportedWidthException;
import org.daisy.braille.facade.PEFConverterFacade;
import org.daisy.braille.pef.PEFHandler;
import org.daisy.braille.pef.Range;
import org.daisy.paper.PageFormat;
import org.daisy.paper.PaperCatalog;
import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.printing.PrinterDevice;
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
        String copiesStr = parameters.remove("copies");
        
        String deviceName = parameters.remove("deviceName");

		Range rangeObj = null;
		EmbosserCatalog ef = EmbosserCatalog.newInstance();
		Embosser em = null;
		if (embosser!=null && !"".equals(embosser)) {
			em = ef.get(embosser);
		} else {
			em = ef.get("org_daisy.GenericEmbosserProvider.EmbosserType.NONE");
		}
		if (papersize != null && !"".equals(papersize)) {
			PaperCatalog pc = PaperCatalog.newInstance();
			em.setFeature(EmbosserFeatures.PAGE_FORMAT, new PageFormat(pc.get(papersize)));
		}
		em.setFeature("breaks", breaks);
		if (range!=null && !"".equals(range)) {
			rangeObj = Range.parseRange(range);
		}
		em.setFeature(EmbosserFeatures.TABLE, table);
		em.setFeature("padNewline", pad);
		em.setFeature("cellWidth", cellWidth);
		em.setFeature("cellHeight", cellHeight);
		
        File orIn = new File(parameters.remove("xml"));
		File[] in = getInput(orIn);
		boolean emboss = deviceName!=null && !"".equals(deviceName);
		boolean align = "true".equals(mirrorAlign);
		int offset = Integer.parseInt(alignmentOffset);
		int copies = 1;
		if (copiesStr != null && !"".equals(copiesStr)) {
			copies = Integer.parseInt(copiesStr);
		}
		if (copies < 1) {
			throw new IllegalArgumentException("Copies must be greater than zero: " + copies);
		}
		PrinterDevice bd = null;
		if (emboss) {
			bd = new PrinterDevice(deviceName, true);
		}
		int i = 1;
		for (File input : in) {
			File output=null;
			try {
				if ("".equals(outFileName)) {
					try {
						EmbosserWriter embosserObj = em.newEmbosserWriter(bd);
						for (int j=0; j<copies; j++) {
							convert(input, embosserObj, rangeObj, paperWidthFallback, align, offset);
						}
					} catch (TransformerRunException e) {
						output = TempFile.create();
						FileOutputStream os = new FileOutputStream(output);
						try {
							EmbosserWriter embosserObj = em.newEmbosserWriter(os);
							convert(input, embosserObj, rangeObj, paperWidthFallback, align, offset);
							if (emboss) {
								progress((i-0.5)/(double)in.length);
								for (int j=0; j<copies; j++) {
									bd.transmit(output);
								}
							}
						} catch (TransformerRunException e2) {
							throw new TransformerRunException(e2.getMessage(), e2);
						} finally {
							os.close();
						}
					}
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
					// if output is defined, use "single file" method, even if this means that some embossers cannot be supported.
					convert(input, output, em, rangeObj, paperWidthFallback, align, offset);
					if (emboss) {
						progress((i-0.5)/(double)in.length);
						for (int j=0; j<copies; j++) {
							bd.transmit(output);
						}
					}
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
	
	private void convert(File input, File output, Embosser em, Range rangeObj, String paperWidthFallback, boolean align, int offset) throws TransformerRunException {
		try {
			EmbosserWriter embosserObj = em.newEmbosserWriter(new FileOutputStream(output));
			convert(input, embosserObj, rangeObj, paperWidthFallback, align, offset);
		} catch (FileNotFoundException e) {
			throw new TransformerRunException(e.getMessage(), e);
		}
	}
	
	private void convert(File input, EmbosserWriter embosserObj, Range rangeObj, String paperWidthFallback, boolean align, int offset) throws TransformerRunException {
		try {
			PEFHandler ph = new PEFHandler.Builder(embosserObj)
				.range(rangeObj)
				.alignmentFallback(paperWidthFallback)
				.mirrorAlignment(align)
				.offset(offset)
				.build();
			PEFConverterFacade.parsePefFile(input, ph);
		} catch (ParserConfigurationException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (UnsupportedWidthException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (IOException e) {
			throw new TransformerRunException(e.getMessage(), e);
		}
	}
}
