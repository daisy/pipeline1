package int_daisy_dtbookMigrator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import javazoom.jl.decoder.BitstreamException;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.TempFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.Z3986DtbookFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetFileFactory;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;
import org.xml.sax.SAXException;

/**
 *  
 * @deprecated - use us_rfbd_dtbookMigrator instead
 * @author Per Sennels
 */
public class DtbookMigrator extends Transformer implements FilesetErrorHandler{
	private static final String XSLT_FACTORY = "net.sf.saxon.TransformerFactoryImpl";	
	private DtbookMigratorXSLTManager xsltManager = null; 
	
	public DtbookMigrator(InputListener inListener, Set eventListeners, Boolean isInteractive) {
		super(inListener, eventListeners, isInteractive);
	}

	protected boolean execute(Map parameters) throws TransformerRunException {		
		
		xsltManager = new DtbookMigratorXSLTManager();
		
		try {
			EFile inputFile = new EFile((String)parameters.remove("input"));
			//create a fileset to get all the satellite files and assure the manifest is wellformed
			Fileset dtbookFileset = new FilesetImpl(inputFile.toURI(),this,true,false);
			Z3986DtbookFile inputDtbookFile = (Z3986DtbookFile) dtbookFileset.getManifestMember(); 
			//could check that input is DTD valid here, but doesnt hurt to try even if not...
			//create the outputdir
			EFolder outputDir = (EFolder) FileUtils.createDirectory(new EFolder((String) parameters.remove("output")));
			//create the result (which will be a tempfile)
			Z3986DtbookFile result = createResult(inputDtbookFile, (String)parameters.remove("outputVersion"));									
			//add the fileset to the output dir
			outputDir.addFileset(dtbookFileset, true); 
			//add the xslt result tempfile
			outputDir.addFile((File)result, true, inputFile.getName());
			//done.					
		} catch (Exception e) {			
			throw new TransformerRunException(e.getMessage(),e);
		}
		return true;
	}

	private Z3986DtbookFile createResult(Z3986DtbookFile inputDtbookFile, String desiredOutputVersion) throws XSLTException, IOException, FilesetFatalException, BitstreamException, SAXException {
		if(!xsltManager.supportsOutputVersion(desiredOutputVersion)) throw new XSLTException("output version " + desiredOutputVersion + " not supported");

		//run stylesheet.apply until we have got the correct version
		if(!inputDtbookFile.getRootVersion().equals(desiredOutputVersion)) {
			//else, run a pass
			File temp = TempFile.create();
			URL xsltURL = xsltManager.getStylesheet(inputDtbookFile.getRootVersion());
			Stylesheet.apply(inputDtbookFile.getFile().getAbsolutePath(), xsltURL, temp.getAbsolutePath(), XSLT_FACTORY, null, CatalogEntityResolver.getInstance());
			//recurse
			Z3986DtbookFile dtbook = (Z3986DtbookFile)FilesetFileFactory.newInstance().newFilesetFile("Z3986DtbookFile", temp.toURI());
			dtbook.parse();
			return createResult(dtbook,desiredOutputVersion);
		}
		return inputDtbookFile;
	}

	/**
	 * FilesetErrorHandler impl
	 */
	public void error(FilesetFileException ffe) throws FilesetFileException {
		this.sendMessage(ffe);
	}

}
