package se_tpb_dtbookFix;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Controller;
import net.sf.saxon.event.MessageEmitter;
import net.sf.saxon.expr.FirstItemExpression;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;


/**
 * A utility class for representing a dtbook fix executor based on XSLT
 * @author Markus Gylling
 */
public class DTBookFixExecutorXSLT extends DTBookFixExecutor implements ErrorListener{
	private URL mXSLT=null;
	private String[] mSupportedVersions = null;
	private TransformerDelegateListener mTransformer = null;
	private URIResolver mResolver = null;
	private MessageEmitter mEmitter = null;
	
	public DTBookFixExecutorXSLT(Map<String, String> parameters, URL xslt, 
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
				
	public boolean supportsVersion(String version) {
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
	public void execute(Source source,Result result) throws TransformerRunException {
		
		try{
			TransformerFactory tfac = TransformerFactory.newInstance();
	        tfac.setAttribute("http://saxon.sf.net/feature/version-warning", Boolean.FALSE);
	
	        StreamSource xslt = new StreamSource(mXSLT.openStream());
	        if(xslt.getSystemId()==null)
	        	xslt.setSystemId(mXSLT.toURI().toASCIIString());
	        javax.xml.transform.Transformer saxon = tfac.newTransformer(xslt); 
	        
	        saxon.setErrorListener(this);
	        saxon.setURIResolver(mResolver);
	        ((Controller)saxon).setMessageEmitter(mEmitter);
	       
	        if (mParameters != null) {
	            for (Iterator it = mParameters.entrySet().iterator(); it.hasNext(); ) {
	                Map.Entry paramEntry = (Map.Entry)it.next();
	                saxon.setParameter((String)paramEntry.getKey(), paramEntry.getValue());
	            }
	        }	        
	        saxon.transform(source, result);
		}catch (Exception e) {
			throw new TransformerRunException(e.getMessage(),e);
		}	
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#error(javax.xml.transform.TransformerException)
	 */
	public void error(TransformerException exception) throws TransformerException {
		// TODO Auto-generated method stub		
	}

	/*
	 * (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#fatalError(javax.xml.transform.TransformerException)
	 */
	public void fatalError(TransformerException exception) throws TransformerException {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#warning(javax.xml.transform.TransformerException)
	 */
	public void warning(TransformerException exception) throws TransformerException {
		// TODO Auto-generated method stub		
	}
}
