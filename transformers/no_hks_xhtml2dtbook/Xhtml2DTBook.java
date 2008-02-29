package no_hks_xhtml2dtbook;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.TransformerFactoryConstants;
import org.daisy.util.xml.xslt.stylesheets.Stylesheets;

/**
 *
 * Main transformer class. See doc/transformers/no_hks_xhtml2dtbook for details.
 * @author Per Sennels
 */
public class Xhtml2DTBook extends Transformer implements FilesetErrorHandler {

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
			/*
			 * TODO add any pre-canonical (xhtml-xhtml) XSLTs using inparams
			 */	
	
			//get the canonical xslt
//			URL xslt = this.getClass().getResource("xhtml2dtbook.xsl");
			URL xsltURL = Stylesheets.get("xhtml2dtbook.xsl");
			
			//get input file
			File input = FilenameOrFileURI.toFile((String) parameters.remove("input"));

			//prep output dir 
			File output = FilenameOrFileURI.toFile((String) parameters.remove("output"));			
			FileUtils.createDirectory(output.getParentFile());
						
			//copy any referred files over
			EFolder outputFolder = new EFolder(output.getParentFile());
			Fileset fileset = new FilesetImpl(input.toURI(),this,false,false);
			outputFolder.addFileset(fileset, true);
			//remove the manifest file from output dir
			File remove = new File(outputFolder,input.getName());
			remove.delete();
				
			//apply the canonical xslt
			Stylesheet.apply(input.getAbsolutePath(), xslt, output.getAbsolutePath(), 
					TransformerFactoryConstants.SAXON8, parameters, 
						CatalogEntityResolver.getInstance());
			
					
		} catch (Exception e) {
			String message = i18n("ERROR_ABORTING", e.getMessage());
			throw new TransformerRunException(message, e);
		}

		return true;
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */
	public void error(FilesetFileException ffe) throws FilesetFileException {
		this.sendMessage(ffe);		
	}

}
