package org.daisy.util.xml.dom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.daisy.util.file.FileUtils;
import org.daisy.util.xml.pool.LSSerializerPool;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/**
 * utility wrappers to serialize a DOM document using <code>org.w3c.dom.ls.LSSerializer</code>
 * @author Markus Gylling
 */
public class Serializer {
	
	public static void serialize(Document doc, File destination, String encoding, Map<String, Object> domConfigParameters) throws IOException {
		LSSerializer serializer = null;
		FileOutputStream fis = null;
		try{		
			FileUtils.createDirectory(destination.getParentFile());
			serializer = LSSerializerPool.getInstance().acquire();	
			
			if(domConfigParameters != null && !domConfigParameters.isEmpty()) {
				DOMConfiguration dc = serializer.getDomConfig();
				for (Iterator<String> iterator = domConfigParameters.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					Object val = domConfigParameters.get(key);
					if(dc.canSetParameter(key, val)) {
						dc.setParameter(key, val);
					}else{
						System.err.println("Warning: org.daisy.util.xml.dom.Serializer could not set domconfig param " + key);
					}
				} 
			}			
			fis = new FileOutputStream(destination);			
			LSOutput lsOutput = LSSerializerPool.getInstance().getDOMImplementationLS().createLSOutput();
			lsOutput.setByteStream(fis);
			if(encoding!=null)lsOutput.setEncoding(encoding);		
			serializer.write(doc,lsOutput);			
		}finally{
			LSSerializerPool.getInstance().release(serializer);
			fis.close();			
		}		
	}
	
	public static void serialize(Document doc, File destination, DOMErrorHandler errH) throws IOException {
		Map<String, Object> domConfigParameters = new HashMap<String, Object>();
		domConfigParameters.put("error-handler", errH);		
		Serializer.serialize(doc,destination, null,domConfigParameters);		
	}
	
	public static void serialize(Document doc, File destination, String encoding, DOMErrorHandler errH) throws IOException {
		Map<String, Object> domConfigParameters = new HashMap<String, Object>();
		domConfigParameters.put("error-handler", errH);
		Serializer.serialize(doc,destination,encoding,domConfigParameters);		
	}
	
	public static void serialize(Document doc, File destination) throws IOException {		
		Serializer.serialize(doc,destination, null, new HashMap<String,Object>());		
	}
}
