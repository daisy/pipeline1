/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package se_tpb_dtbookFix;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.stream.Location;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Controller;
import net.sf.saxon.event.MessageEmitter;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.xml.LocusTransformer;
import org.daisy.util.xml.pool.SAXParserPool;
import org.daisy.util.xml.sax.SAXConstants;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;


/**
 * A utility class for representing a DTBook fix executor based on XSLT.
 * <p>For XSLT development guidelines, see <code>../../doc/transformers/se_tpb_dtbookfix.html<code></p>
 * @author Markus Gylling
 */
class XSLTExecutor extends Executor implements ErrorListener{
	private URL mXSLT=null;
	private String[] mSupportedVersions = null;
	private TransformerDelegateListener mTransformer = null;	
	private URIResolver mURIResolver = null;
	private EntityResolver mEntityResolver = null;
	private MessageEmitter mEmitter = null;
	
	public XSLTExecutor(Map<String, String> parameters, URL xslt, 
			String[] supportedVersions, String niceName, 
			TransformerDelegateListener transformer, URIResolver uriResolver, 
			EntityResolver entityResolver, MessageEmitter emitter) {
		
		super(parameters, niceName);
		mXSLT = xslt;
		mSupportedVersions = supportedVersions;
		mTransformer = transformer;
		mURIResolver = uriResolver;
		mEntityResolver = entityResolver;
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
		
		SAXSource saxSource = null; 
		Map<String, Object> features = null;
		Map<String, Object> properties = null;
		SAXParser parser = null;
		try{			
			
			/*
			 * Create a SAXSource, hook up an entityresolver
			 */
			if(!(source instanceof SAXSource)) {
				File input = FilenameOrFileURI.toFile(source.getSystemId());
				features = new HashMap<String, Object>();
				properties = new HashMap<String, Object>();
				features.put(SAXConstants.SAX_FEATURE_VALIDATION, Boolean.FALSE);		
				parser = SAXParserPool.getInstance().acquire(features, properties);                
		        saxSource = new SAXSource(parser.getXMLReader(), new InputSource(new FileInputStream(input)));        
		        saxSource.setSystemId(input.toString());
			}else{
				saxSource = (SAXSource) source;
			}						
			saxSource.getXMLReader().setEntityResolver(mEntityResolver);
	        
			
	        /*
	         * Create the TransformerFactory and ask it to be quiet
	         */
			TransformerFactory tfac = TransformerFactory.newInstance();
			try{
				tfac.setAttribute("http://saxon.sf.net/feature/version-warning", Boolean.FALSE);
			}catch (Exception e){
				mTransformer.delegateMessage(this, e.getLocalizedMessage()
						, MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM, null);
			}
	
			
			/*
			 * Create the stylesheet source
			 */
	        StreamSource xsltSource = new StreamSource(mXSLT.openStream());
	        if(xsltSource.getSystemId()==null) xsltSource.setSystemId(mXSLT.toURI().toASCIIString());
	        
	        
	        /*
	         * Create the xslt transform processor
	         */
	        javax.xml.transform.Transformer processor = tfac.newTransformer(xsltSource); 	        
	        processor.setErrorListener(this);	        
	        processor.setURIResolver(mURIResolver);
	        try{   
	        	((Controller)processor).setMessageEmitter(mEmitter);
	        }catch (Exception e){
	        	mTransformer.delegateMessage(this, e.getLocalizedMessage()
					, MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM, null);
	        }
	     	
	        
	        /*
	         * Set any parameters on the processor
	         */
	        if (mParameters != null) {
	            for (Iterator<Map.Entry<String,String>> it = mParameters.entrySet().iterator(); it.hasNext(); ) {
	                Map.Entry<String,String> paramEntry = it.next();
	                try{   
	                	processor.setParameter(paramEntry.getKey(), paramEntry.getValue());
	    	        }catch (Exception e){
	    	        	mTransformer.delegateMessage(this, e.getLocalizedMessage()
	    					, MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM, null);
	    	        }	
	            }
	        }	     
	        
	        /*
	         * Apply.
	         */
	        processor.transform(saxSource, result);		 
	        
		}catch (Exception e) {
			throw new TransformerRunException(e.getMessage(),e);
		} finally {
			if(parser!=null) {				
				SAXParserPool.getInstance().release(parser, features, properties);  
			}
		}
	}
		
	/*
	 * (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#error(javax.xml.transform.TransformerException)
	 */
	@SuppressWarnings("unused")
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
	@SuppressWarnings("unused")
	public void warning(TransformerException te) throws TransformerException {
		Location loc = LocusTransformer.newLocation(te);
		mTransformer.delegateMessage(this, te.getLocalizedMessage()
				, MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM, loc);	
	}
}
