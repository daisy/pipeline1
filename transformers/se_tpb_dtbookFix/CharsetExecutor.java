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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Map;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;
import org.daisy.util.xml.stax.StaxEntityResolver;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

/**
 * This executor deals with the case of erroneous stated encodings in XML documents.
 * <p>Attempt to detect the character set of an XML document, take the document
 * through a read-write cycle, ignoring any stated characterset in the document
 * itself.</p>
 * @author Markus Gylling
 */
class CharsetExecutor extends Executor {
	private TransformerDelegateListener mTransformer = null;
	
	CharsetExecutor(Map<String,String> parameters, String niceName, TransformerDelegateListener tdl) {
		super(parameters,niceName);
		mTransformer =tdl;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	void execute(Source source, Result result) throws TransformerRunException {
		EFile input = new EFile(FilenameOrFileURI.toFile(source.getSystemId()));
		File output = FilenameOrFileURI.toFile(result.getSystemId());
		
		
		try {
//			/*
//			 * Peek and get stated charset
//			 */
//	    	Peeker peeker = PeekerPool.getInstance().acquire();
//	    	PeekResult presult = peeker.peek(input);
//	    	String statedCharset = presult.getPrologEncoding();	    	
//	    	PeekerPool.getInstance().release(peeker);
//	    	if(statedCharset==null) statedCharset = "utf-8";
//	    	String message = mTransformer.delegateLocalize("CHARSET_INPUT", statedCharset);
//	    	mTransformer.delegateMessage(this, message, MessageEvent.Type.INFO, MessageEvent.Cause.INPUT, null);
	    	
	    	
			/*
			 * Get detected charset
			 */	    	
	    	CharsetDetector ibmDetector = new com.ibm.icu.text.CharsetDetector();    	
	    	ibmDetector.setText(input.asByteArray());
	    	CharsetMatch match = ibmDetector.detect(); 
	    	String detectedCharset = match.getName();
	    				
			if(detectedCharset == null) throw new UnsupportedCharsetException("detected charset is null");			
			String message = mTransformer.delegateLocalize("READING_USING_CHARSET", new Object[]{detectedCharset});
			mTransformer.delegateMessage(this, message, MessageEvent.Type.INFO, MessageEvent.Cause.INPUT, null);
						
			/*
			 * Transcode
			 */
			Map xifProperties = null;
			XMLInputFactory xif = null;
			Map xofProperties = null;			
			XMLOutputFactory xof = null;
			XMLEventFactory xef = null;
			FileInputStream fis = null;
			InputStreamReader isr = null;
			XMLEventReader xer = null;
			XMLEventWriter xew = null; 
			try{
				xifProperties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
				xif = StAXInputFactoryPool.getInstance().acquire(xifProperties);
				xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
				xofProperties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
				xof = StAXOutputFactoryPool.getInstance().acquire(xofProperties);
				xef = StAXEventFactoryPool.getInstance().acquire();												
				fis = new FileInputStream(input);				
				isr = new InputStreamReader(fis,detectedCharset);				
				xer = xif.createXMLEventReader(isr);				
				xew = xof.createXMLEventWriter(new FileOutputStream(output), detectedCharset);
				
				while(xer.hasNext()) {
					XMLEvent e = xer.nextEvent();
					if(e.getEventType() == XMLEvent.START_DOCUMENT) {
						StartDocument sd = (StartDocument) e;
						String version = sd.getVersion();
						e = xef.createStartDocument(detectedCharset,version);
					}
					xew.add(e);
				}					
			}finally{
				xew.flush();
				xew.close();
				fis.close();
				isr.close();
				StAXInputFactoryPool.getInstance().release(xif, xifProperties);
				StAXOutputFactoryPool.getInstance().release(xof, xofProperties);
				StAXEventFactoryPool.getInstance().release(xef);
			}			
		} catch (Exception e) {			
			String message = mTransformer.delegateLocalize("CHARSET_FAIL", new String[]{e.getMessage()});
			mTransformer.delegateMessage(this, message, MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM, null);
			try {
				FileUtils.copy(input, output);
			} catch (IOException ioe) {
				throw new TransformerRunException(ioe.getMessage(),ioe);
			}
		} 	
	}

	@Override
	boolean supportsVersion(@SuppressWarnings("unused")String version) {
		return true;
	}

}
