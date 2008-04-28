/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
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
	protected boolean execute(Map<String,String> parameters) throws TransformerRunException {
		progress(0);
		File input = new File(parameters.remove("input"));
        File output = new File(parameters.remove("output"));
        String converter = parameters.remove("converter");
        String arguments = parameters.remove("arguments").trim();
        String ext = parameters.remove("extension").trim();
        String placeholderInput = parameters.remove("placeholderInput").trim();
        String placeholderOutput = parameters.remove("placeholderOutput").trim();
        String tag = parameters.remove("lookForTag").trim();
        String att = parameters.remove("lookForAttribute").trim();
        
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
			throw new TransformerRunException(e.getMessage(),e);			
		} catch (XMLStreamException e) {
			throw new TransformerRunException(e.getMessage(),e);
		} catch (IOException e) {
			throw new TransformerRunException(e.getMessage(),e);
		}
		progress(1);
		return true;
	}
	
	public void setProgress(double value) {
		progress(value<1 ? value : 1);
	}

}
