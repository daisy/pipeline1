package int_daisy_mathAltCreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.Directory;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.ManifestFile;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.xml.NamespaceReporter;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.StaxEntityResolver;


/**
 * Factory-based discovery of implementations of the IMathMLAltCreator interface
 * @author Markus Gylling
 */
public class MathAltCreator extends Transformer implements FilesetErrorHandler {

	public MathAltCreator(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@Override
	protected boolean execute(Map<String, String> parameters) throws TransformerRunException {

		try{
			/*
			 * Get input and output
			 */
			File inputDoc = FilenameOrFileURI.toFile(parameters.get("input"));
			File outputDoc = FilenameOrFileURI.toFile(parameters.get("output"));
			boolean overwrite = Boolean.parseBoolean(parameters.get("overwrite"));
			Directory outputDir = new Directory(outputDoc.getParentFile());
		
			// Build a fileset
			Fileset fileset = new FilesetImpl(inputDoc.toURI(), this, false, false);
			
			// Check if input document contains any math. If not, just pass the doc through
			NamespaceReporter namespaceReporter = new NamespaceReporter(inputDoc.toURL());
			boolean containsMath = (namespaceReporter.getPrefix(Namespaces.MATHML_NS_URI) != null);
			if(!containsMath) {
			    FileUtils.copyFile(inputDoc, outputDoc);
			    this.copyReferredFiles(fileset, outputDir);
			    return true;
			}
			
			/*
			 * Find out whether the input doc has all math:alttext and math:altimg
			 * already set. If so, and the overwrite option is set to false, copy
			 * input to output and return.
			 */
			int incomplete = getAltIncompleteIslands(inputDoc);
			if(incomplete==0 && !overwrite) {
				FileUtils.copyFile(inputDoc, outputDoc);
				this.copyReferredFiles(fileset, outputDir);
				return true;
			}
					
			/* If not alt complete, we need to locate a provider of the 
			 * IMathAltCreator service.
			 */
			
	    	boolean result = false;
	    	IMathAltCreator service = MathAltCreatorFactory.newInstance().newMathAltCreator();
	    	Map<String,Object> params = new HashMap<String,Object>();
	    	params.put("overwrite", overwrite);
	    	if(service!=null) {
	    		service.configure(inputDoc, outputDoc, params);	    				    			
		    	
	    		this.sendMessage(i18n("USING_SERVICE",
	    				incomplete,service.getNiceName()), 
	    					MessageEvent.Type.INFO_FINER);    
		    		
	    		service.execute();	    		
	    		result = true;	    	
	    	}
	    			    	
	    	if(!result) throw new TransformerRunException(i18n("ERROR_ABORTING",i18n("SERVICE_UNAVAILABLE")));
	    		    		    	
		}catch (Exception e) {
			if(e instanceof TransformerRunException) throw (TransformerRunException)e;
			throw new TransformerRunException(e.getMessage(),e);
		}
		return true;
	}

	/**
	 * @return The number of MathML Islands without altimg and/or alttext
	 */
	private int getAltIncompleteIslands(File input) {
		int count = 0;
		Map<String,Object> xifProperties = null;	
		XMLInputFactory xif = null;
		FileInputStream fis = null;
		XMLStreamReader reader = null;
		try{
			xifProperties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xif = StAXInputFactoryPool.getInstance().acquire(xifProperties);
			xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
			fis = new FileInputStream(input);
			reader = xif.createXMLStreamReader(fis);
			while(reader.hasNext()) {
				reader.next();
				if(reader.isStartElement() && reader.getLocalName()=="math") {
					String alttext = reader.getAttributeValue(null, "alttext");
					String altimg = reader.getAttributeValue(null, "altimg");
					if(alttext == null || alttext.length() == 0 
							|| altimg == null || altimg.length() == 0) {
						count++;
					}
				}
			}		
		}catch (Exception e) {
    		this.sendMessage(i18n("ERROR",
					e.getMessage()), 
						MessageEvent.Type.WARNING);
			return 1;
		}finally{
			try{
				if(reader!=null)reader.close();
				if(fis!=null)fis.close();
			}catch (Exception e) {}
			StAXInputFactoryPool.getInstance().release(xif, xifProperties);;
		}
		return count;
	}
	
	/**
	 * Copy any referred files (such as images) to the output directory.
	 * @param fileset file fileset to copy
	 * @param outputDir the output directory
	 * @throws IOException
	 */
	private void copyReferredFiles(Fileset fileset, Directory outputDir) throws IOException {
	    ManifestFile manifest = fileset.getManifestMember();
	    if (manifest.getParentFolder().getAbsolutePath().equals(outputDir.getAbsolutePath())) {
	        // inputDir == outputDir, we don't need to copy anything
	        return;
	    }
	    FileUtils.createDirectory(outputDir);
	    for (FilesetFile fsf : fileset.getLocalMembers()) {
	        if (fsf != manifest) {
	            URI relative = manifest.getFile().toURI().relativize(fsf.getFile().toURI());
	            URI outUri = outputDir.toURI().resolve(relative);
	            FileUtils.copyFile(fsf.getFile(), new File(outUri));
	        }
	    }
	}

    public void error(FilesetFileException ffe) throws FilesetFileException {
        this.sendMessage(ffe);
    }

}

//Service<IMathAltCreator> serviceLocator = new Service<IMathAltCreator>(IMathAltCreator.class);
//Enumeration<IMathAltCreator> providers = serviceLocator.getProviders();
//
//boolean result = false;
//while (providers.hasMoreElements()) {
//	IMathAltCreator service = providers.nextElement();
//	try{
//		service.configure(inputDoc,outputDoc,null);	    				    			
//	}catch (IllegalStateException e) {	
//		continue;
//	}
//	
//	this.sendMessage(i18n("USING_SERVICE",
//			incomplete,service.getNiceName()), 
//				MessageEvent.Type.INFO_FINER);    
//	
//	service.execute();
//	result = true;
//}