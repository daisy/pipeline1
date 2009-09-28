package org_pef_dtbook2pef.system.tasks.textnode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org_pef_dtbook2pef.system.InternalTask;
import org_pef_dtbook2pef.system.tasks.layout.text.StringFilterHandler;

/**
 * Task that runs a list of StringFilters on the character data of the input file.
 * 
 * @author  Joel Hakansson
 * @version 4 maj 2009
 * @since 1.0
 */
public class TextNodeTask extends InternalTask {
	private StringFilterHandler filters;

	/**
	 * Create a new TextNodeTask.
	 * @param name task name
	 * @param filters ArrayList of StringFilters
	 */
	public TextNodeTask(String name, StringFilterHandler filters) {
		super(name);
		this.filters = filters;
	}

	@Override
	public void execute(File input, File output, HashMap<String, String> options)
			throws TransformerRunException {
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

		TextNodeFilter tnf;

		try {
			tnf = new TextNodeFilter(inFactory.createXMLEventReader(new FileInputStream(input)), new FileOutputStream(output), filters);
			tnf.filter();
			tnf.close();
		} catch (FileNotFoundException e) {
			throw new TransformerRunException("FileNotFoundException:", e);
		} catch (XMLStreamException e) {
			throw new TransformerRunException("XMLStreamException:", e);
		} catch (IOException e) {
			throw new TransformerRunException("IOException:", e);
		}

	}

}
