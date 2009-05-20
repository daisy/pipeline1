package org_pef_dtbook2pef.filters;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;

/**
 * Task that runs an XSLT conversion.
 * 
 * @author  Joel Hakansson
 * @version 4 maj 2009
 * @since 1.0
 */
public class XsltTask extends InternalTask {
	final URL url;
	final String factory;
	
	/**
	 * Create a new XSLT task.
	 * @param name task name
	 * @param url relative path to XSLT
	 * @param factory XSLT factory to use
	 */
	public XsltTask(String name, String url, String factory) {
		super(name);
		this.url = getTransformerDirectoryResource(url);
		this.factory = factory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(File input, File output, HashMap options)
			throws TransformerRunException {
		try {
			Stylesheet.apply(input.getAbsolutePath(), url, output.getAbsolutePath(), factory, options, null);
		} catch (XSLTException e) {
			throw new TransformerRunException("Error: ", e);
		}

	}

}
