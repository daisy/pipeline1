package org_pef_dtbook2pef.filters;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.execution.Command;
import org.daisy.util.execution.ExecutionException;

/**
 * Task that runs an XSL-FO to area tree conversion.
 * @author Joel Håkansson
 */
public class XslFoTask extends InternalTask {
	
	/**
	 * Create a new XSL-FO task.
	 */
	public XslFoTask() {
		super("XSL-FO to Area tree converter");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(File input, File output, HashMap<String, String> options)
			throws TransformerRunException {
		int ret;
		try {

			// Running via exec, since running through java is about 50 times slower
			// in the current environment.
			// TODO: Find the fop environment problem.
			String path = new File(new File(getTransformerDirectoryResource("../lib/fop").toURI()),"fop.jar").getAbsolutePath();
			ret = Command.execute(new String[]{"java", "-Xmx512m", "-jar", path, "-fo", input.getAbsolutePath(), "-at", output.getAbsolutePath()});
			if (ret!=0) throw new TransformerRunException("FOP error.");

			// This should be identical to the call above, but it runs much slower. Why?
			//org.apache.fop.cli.Main.main(new String[]{"java -jar " + path, "-fo", input.getAbsolutePath(), "-at", output.getAbsolutePath()});

			// This is the preferred method, but like calling Main.main, this too runs much slower. Why?
			//runFOP(input, output);

		} catch (Exception e) {
			throw new TransformerRunException("Error: ", e);
		}
	}

	/*
    private void runFOP(File input, File output) {
    	FopFactory fopFactory = FopFactory.newInstance();
    	fopFactory.setURIResolver(null);
    	FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
    	OutputStream out = null; 

        try {
        	out = new java.io.FileOutputStream(output);
            out = new java.io.BufferedOutputStream(out);
			Fop fop = fopFactory.newFop(MimeConstants.MIME_FOP_AREA_TREE, foUserAgent, out);
			
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(); // identity transformer

			
			// Setup input for XSLT transformation
			Source src = new StreamSource(input);
			
			// Resulting SAX events (the generated FO) must be piped through to FOP
			Result res = new SAXResult(fop.getDefaultHandler());
			
			// Start XSLT transformation and FOP processing
			transformer.transform(src, res);
        } catch (Exception e) {
        } finally {
        	try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }*/
}
