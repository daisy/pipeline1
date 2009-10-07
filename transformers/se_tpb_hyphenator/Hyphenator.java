package se_tpb_hyphenator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * 
 * ... beskrivning ...
 * 
 * @author  Joel Hakansson, TPB
 * @version 16 maj 2008
 * @since 1.0
 */
public class Hyphenator extends Transformer {

	/**
	 * Default constructor
	 * @param inListener
	 * @param isInteractive
	 */
	public Hyphenator(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@Override
	protected boolean execute(Map<String, String> parameters) throws TransformerRunException {
		File input = new File(parameters.remove("input"));
        File output = new File(parameters.remove("output"));
        int breakLimitBegin = Integer.parseInt(parameters.get("breakLimitBegin"));
        int breakLimitEnd = Integer.parseInt(parameters.get("breakLimitEnd"));
        output.getParentFile().mkdirs();
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

		try {
			HyphenReader hr = new HyphenReader(this, inFactory, input, output, breakLimitBegin, breakLimitEnd);
			hr.filter();
			hr.close();
		} catch (FileNotFoundException e) {
			throw new TransformerRunException(e.getMessage(),e);			
		} catch (XMLStreamException e) {
			throw new TransformerRunException(e.getMessage(),e);
		} catch (IOException e) {
			throw new TransformerRunException(e.getMessage(),e);
		}
		return true;
	}

}
