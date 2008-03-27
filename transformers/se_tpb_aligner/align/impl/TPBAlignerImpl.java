package se_tpb_aligner.align.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.daisy.util.execution.Command;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.SimpleNamespaceContext;
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.dom.Serializer;
import org.daisy.util.xml.pool.LSParserPool;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSParser;

import se_tpb_aligner.align.Aligner;
import se_tpb_aligner.align.AlignerException;
import se_tpb_aligner.util.AudioSource;
import se_tpb_aligner.util.XMLResult;
import se_tpb_aligner.util.XMLSource;

/**
 * A wrapper around Kåre Sjölanders aligner.
 * <p>Location of the binary executable is done through the System property "pipeline.aligner.tpbaligner.path".</p>
 * @author Markus Gylling
 */
public class TPBAlignerImpl extends Aligner implements DOMErrorHandler {
	
	private String exePath = null;
	
	public TPBAlignerImpl() {
		super();
	}
	
	@Override
	public XMLResult process(XMLSource inputXML, AudioSource inputAudioFile, String inputLanguage, XMLResult result) throws AlignerException {
		try{
			/*
			 * Check that we have the binary available
			 */
			exePath = System.getProperty("pipeline.aligner.tpbaligner.path");
			File f = FilenameOrFileURI.toFile(exePath);
			if(!f.exists()) throw new AlignerException(exePath + " does not exist");
			
			/*
			 * Build the command string.
			 * tclsh path/dtbalign.tcl -inputXML pathspec -inputAudio pathspec -inputLanguage langspec -resultPath pathspec
			 */
			
	        ArrayList<String> arr = new ArrayList<String>();
	        arr.add("tclsh");
	        arr.add(exePath);        
	        arr.add("-inputXML");
	        arr.add(inputXML.getCanonicalPath());
	        arr.add("-inputAudio");
	        arr.add(inputAudioFile.getCanonicalPath());
	        arr.add("-inputLanguage");
	        arr.add(inputLanguage);
	        arr.add("-resultPath");
	        arr.add(result.getCanonicalPath());
	
	        /*
	         * Execute
	         */
	        int ret;                	
            ret = Command.execute((String[])(arr.toArray(new String[arr.size()])));
            if(ret == -1) {
            	throw new AlignerException(exePath + " returned -1");
            }
                      
            /*
             * Post tweak timing of the XML output from the aligner
             */
            result = tweakTiming(result);
            
		} catch (Exception e) {
			throw new AlignerException(e.getMessage(),e);
		}		
		return result;		
	}

	
	/**
	 * Load the Aligner output and tweak SMIL times to be adjacent.
	 * <p>Each clip start time is rewinded by a configurable value (default 30 ms).</p>
	 * <p>Each clips end time is moved to 1 ms before the next clips start time.</p>
	 * @throws CatalogExceptionNotRecoverable 
	 * @throws CatalogExceptionNotRecoverable 
	 * @throws IOException 
	 * @throws IOException 
	 */
	private XMLResult tweakTiming(XMLResult result) throws CatalogExceptionNotRecoverable, IOException  {
		
		final long REWIND = 30;  //millis to subtract from clipBegin values	
		
		//load
		Map<String,Object> domConfigMap = LSParserPool.getInstance().getDefaultPropertyMap(Boolean.FALSE);
		domConfigMap.put("resource-resolver", CatalogEntityResolver.getInstance());
		LSParser parser = LSParserPool.getInstance().acquire(domConfigMap);
		DOMConfiguration domConfig = parser.getDomConfig();						
		domConfig.setParameter("error-handler", this);
		domConfig.setParameter("entities", Boolean.FALSE);						
		Document doc = parser.parseURI(result.toURI().toString());
		
		//tweak
		SimpleNamespaceContext snc = new SimpleNamespaceContext();
		snc.declareNamespace("smil", Namespaces.SMIL_20_NS_URI);
		NodeList nodes = XPathUtils.selectNodes(doc.getDocumentElement(), "//*[@smil:sync]", snc);
		
		for (int i = 0; i < nodes.getLength(); i++) {
			Element e = (Element)nodes.item(i);
			Attr beginAttr = e.getAttributeNodeNS(Namespaces.SMIL_20_NS_URI,"clipBegin");
			SmilClock newBeginClock = rewind(new SmilClock(beginAttr.getValue()),REWIND);
			beginAttr.setNodeValue(newBeginClock.toString(SmilClock.FULL));
			if(i>0) {
				Element prev = (Element)nodes.item(i-1);
				Attr endAttr = prev.getAttributeNodeNS(Namespaces.SMIL_20_NS_URI,"clipEnd");
				SmilClock newEndClock = new SmilClock(newBeginClock.millisecondsValue()-1);				
				endAttr.setNodeValue(newEndClock.toString(SmilClock.FULL));
			}
		}
				
		//serialize
		Map<String,Object> props = new HashMap<String,Object>();
		props.put("namespaces", Boolean.FALSE); 					
		props.put("error-handler", this);					
		Serializer.serialize(doc, result, "utf-8", props);		
		
		LSParserPool.getInstance().release(parser, domConfigMap);			

		return result;
	}

	/**
	 * Create a SmilClock which represents inparam smilClock rewinded by second argument.
	 */
	private SmilClock rewind(SmilClock smilClock,long rewind) {		
		long sum = smilClock.millisecondsValue()-rewind;
		return sum<0 ? new SmilClock(0) : new SmilClock(sum);
	}

	@Override
	public boolean supportsLanguage(String language) {
		if(language.startsWith("sv")||language.startsWith("en")) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.DOMErrorHandler#handleError(org.w3c.dom.DOMError)
	 */
	public boolean handleError(DOMError error) {
		System.err.println(error.getMessage());
		if(error.getSeverity() == error.SEVERITY_WARNING) {
			return true;
		}		
		return false;
	}
	
}
