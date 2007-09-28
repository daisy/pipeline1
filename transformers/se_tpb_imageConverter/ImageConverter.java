package se_tpb_imageConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;

public class ImageConverter extends Transformer {

	public ImageConverter(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@Override
	protected boolean execute(Map parameters) throws TransformerRunException {
		progress(0);
		File input = new File((String)parameters.remove("input"));
        File output = new File((String)parameters.remove("output"));
        String converter = (String)parameters.remove("converter");
        String arguments = ((String)parameters.remove("arguments")).trim();
        String ext = ((String)parameters.remove("extension")).trim();
        String placeholderInput = ((String)parameters.remove("placeholderInput")).trim();
        String placeholderOutput = ((String)parameters.remove("placeholderOutput")).trim();
        String tag = ((String)parameters.remove("lookForTag")).trim();
        String att = ((String)parameters.remove("lookForAttribute")).trim();
        
        XMLInputFactory inFactory = XMLInputFactory.newInstance();
		inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);        
        inFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        inFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
        inFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        
    	try {
			inFactory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
		} catch (CatalogExceptionNotRecoverable e1) {
			e1.printStackTrace();
		}

		if (converter==null || "".equals(converter)) converter = System.getProperty("pipeline.imageMagick.converter.path");

		if (output.isDirectory()) {
			if (!output.exists()) {
				output.mkdirs();
			} else if (output.list().length>0) {
				sendMessage("Directory not empty", MessageEvent.Type.ERROR);
				return false;				
			}
		} else {
			sendMessage("Output is not a directory", MessageEvent.Type.ERROR);
			return false;
		}
		if (placeholderInput.matches(".*\\s+.*") || placeholderOutput.matches(".*\\s+.*")) {
			sendMessage("Configuration error: Placeholder cannot contain whitespace", MessageEvent.Type.ERROR);
			return false;			
		}
		if (tag.matches(".*\\s+.*")) {
			sendMessage("Configuration error: Tag cannot contain whitespace", MessageEvent.Type.ERROR);
			return false;			
		}
		if (att.matches(".*\\s+.*")) {
			sendMessage("Configuration error: Attribute cannot contain whitespace", MessageEvent.Type.ERROR);
			return false;			
		}

        try {
			ImageConverterReader ir = new ImageConverterReader(this, inFactory, input, new File(output, input.getName()), 
					converter + " " + arguments,
					tag, att, ext, 
					placeholderInput, 
					placeholderOutput);
			ir.filter();
			ir.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		progress(1);
		return true;
	}
	
	public void setProgress(double value) {
		progress(value);
	}

}
