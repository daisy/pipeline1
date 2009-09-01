package org_pef_dtbook2pef.setups.sv_SE.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org_pef_dtbook2pef.system.InternalTask;
import org_pef_dtbook2pef.system.tasks.textnode.filters.StringFilterHandler;

public class VolumeCoverPageTask extends InternalTask {
	private StringFilterHandler filters;

	public VolumeCoverPageTask(String name, StringFilterHandler filters) {
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
		
		try {
			VolumeCoverPageFilter pf = new VolumeCoverPageFilter(
					inFactory.createXMLEventReader(new FileInputStream(input)), 
					new FileOutputStream(output),
					filters,
					"titelll", new ArrayList<String>(), 30, 29);
			pf.filter();
			pf.close();
		} catch (FileNotFoundException e) {
			throw new TransformerRunException("FileNotFoundException:", e);
		} catch (XMLStreamException e) {
			throw new TransformerRunException("XMLStreamException:", e);
		} catch (IOException e) {
			throw new TransformerRunException("IOException:", e);
		}
	}

}
