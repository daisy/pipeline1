package se_tpb_dtbookFix;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.Location;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Controller;
import net.sf.saxon.event.MessageEmitter;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.xml.LocusTransformer;


/**
 * A utility class for representing a dtbook fix executor based on XSLT.
 * @author Markus Gylling
 */
class XSLTExecutor extends Executor implements ErrorListener{
	private URL mXSLT=null;
	private String[] mSupportedVersions = null;
	private TransformerDelegateListener mTransformer = null;
	private URIResolver mResolver = null;
	private MessageEmitter mEmitter = null;
	
	public XSLTExecutor(Map<String, String> parameters, URL xslt, 
			String[] supportedVersions, String niceName, 
			TransformerDelegateListener transformer, URIResolver resolver, 
			MessageEmitter emitter) {
		
		super(parameters, niceName);
		mXSLT = xslt;
		mSupportedVersions = supportedVersions;
		mTransformer = transformer;
		mResolver = resolver;
		mEmitter = emitter;
	}
		
	@Override
	boolean supportsVersion(String version) {
		for (int i = 0; i < mSupportedVersions.length; i++) {
			if(mSupportedVersions[i].contentEquals(version)) return true;
		}
		return false;
	}
		
	/**
	 * @throws TransformerRunException 
	 * @throws IOException 
	 * @throws TransformerException 
	 */
	@Override
	void execute(Source source,Result result) throws TransformerRunException {
		//we assume that Saxon is the XSLT processor used,
		//but catch+message in case its not.
		
		try{
			TransformerFactory tfac = TransformerFactory.newInstance();
			try{
				tfac.setAttribute("http://saxon.sf.net/feature/version-warning", Boolean.FALSE);
			}catch (Exception e){
				mTransformer.delegateMessage(this, e.getLocalizedMessage()
						, MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM, null);
			}
	
	        StreamSource xslt = new StreamSource(mXSLT.openStream());
	        if(xslt.getSystemId()==null)
	        	xslt.setSystemId(mXSLT.toURI().toASCIIString());
	        javax.xml.transform.Transformer processor = tfac.newTransformer(xslt); 
	        
	        processor.setErrorListener(this);
	        processor.setURIResolver(mResolver);
	        try{   
	        	((Controller)processor).setMessageEmitter(mEmitter);
	        }catch (Exception e){
	        	mTransformer.delegateMessage(this, e.getLocalizedMessage()
					, MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM, null);
	        }
	     	       
	        if (mParameters != null) {
	            for (Iterator it = mParameters.entrySet().iterator(); it.hasNext(); ) {
	                Map.Entry paramEntry = (Map.Entry)it.next();
	                try{   
	                	processor.setParameter((String)paramEntry.getKey(), paramEntry.getValue());
	    	        }catch (Exception e){
	    	        	mTransformer.delegateMessage(this, e.getLocalizedMessage()
	    					, MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM, null);
	    	        }	
	            }
	        }	        
	        processor.transform(source, result);
		}catch (Exception e) {
			throw new TransformerRunException(e.getMessage(),e);
		}	
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#error(javax.xml.transform.TransformerException)
	 */
	public void error(TransformerException te) throws TransformerException {
		Location loc = LocusTransformer.newLocation(te);
		mTransformer.delegateMessage(this, te.getLocalizedMessage()
				, MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM, loc);				
	}

	/*
	 * (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#fatalError(javax.xml.transform.TransformerException)
	 */
	public void fatalError(TransformerException te) throws TransformerException {
		Location loc = LocusTransformer.newLocation(te);
		mTransformer.delegateMessage(this, te.getLocalizedMessage()
				, MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM, loc);	
		throw te;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#warning(javax.xml.transform.TransformerException)
	 */
	public void warning(TransformerException te) throws TransformerException {
		Location loc = LocusTransformer.newLocation(te);
		mTransformer.delegateMessage(this, te.getLocalizedMessage()
				, MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM, loc);	
	}
}
