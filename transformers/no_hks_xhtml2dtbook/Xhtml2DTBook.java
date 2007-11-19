package no_hks_xhtml2dtbook;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.TransformerFactoryConstants;

/**
 *
 * Main transformer class. See doc/transformers/no_hks_xhtml2dtbook for details.
 * @author Per Sennels
 * @author Markus Gylling
 */
public class Xhtml2DTBook extends Transformer {

	public Xhtml2DTBook(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);		
	}

	/**
	 * Run a 1-n length chain of stylesheets.  
	 * <p>The <strong>last</strong> one is the static and canonical XHTML to DTBook transform.</p>
	 * <p>Any preceding ones are XHTML to XHTML, aiming at normalization. These
	 * preceding ones can be overriden/exchanged by the user using transformer inparams.</p>
	 * 
	 * <p>Note - use URLs to reference XSLTS as to support jarness.</p>
	 */
	@Override
	protected boolean execute(Map parameters) throws TransformerRunException {
		try{
		
//			Chain chain = new Chain(TransformerFactoryConstants.SAXON8, CatalogEntityResolver.getInstance());
//			//TODO add any pre-tweak (xhtml-xhtml) XSLTs using inparams			
//			// add the static canonical XSLT 
//			chain.addStylesheet(new StreamSource(this.getClass().getResource("xhtml2dtbook.xsl").openStream()));
	
			//get xslt
			URL xslt = this.getClass().getResource("xhtml2dtbook.xsl");
			
			//get input file
			File input = FilenameOrFileURI.toFile((String) parameters.remove("input"));

			//prep output dir 
			File output = FilenameOrFileURI.toFile((String) parameters.remove("output"));			
			FileUtils.createDirectory(output.getParentFile());
						
			Stylesheet.apply(input.getAbsolutePath(), xslt, output.getAbsolutePath(), 
					TransformerFactoryConstants.SAXON8, null, 
						CatalogEntityResolver.getInstance());
			
//			chain.applyChain(input, output);
					
		} catch (Exception e) {
			String message = i18n("ERROR_ABORTING", e.getMessage());
			throw new TransformerRunException(message, e);
		}

		return true;
		
	}

}
