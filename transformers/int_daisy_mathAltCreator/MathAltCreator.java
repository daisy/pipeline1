package int_daisy_mathAltCreator;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.StaxEntityResolver;


/**
 * Factory-based discovery of implementations of the IMathMLAltCreator interface
 * @author Markus Gylling
 */
public class MathAltCreator extends Transformer {

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
		
			/*
			 * Find out whether the input doc has all math:alttext and math:altimg
			 * already set. If so, copy input to output and return.
			 */
			int incomplete = getAltIncompleteIslands(inputDoc);
			if(incomplete==0) {
				FileUtils.copyFile(inputDoc, outputDoc);
				return true;
			}
			
			/* If not alt complete, we need to locate a provider of the 
			 * IMathAltCreator service.
			 */
			
	    	boolean result = false;
	    	IMathAltCreator service = MathAltCreatorFactory.newInstance().newMathAltCreator();
	    	if(service!=null) {
	    		service.configure(inputDoc,outputDoc,null);	    				    			
		    	
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