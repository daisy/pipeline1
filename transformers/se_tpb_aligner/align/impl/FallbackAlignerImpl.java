package se_tpb_aligner.align.impl;

import java.util.HashMap;
import java.util.Map;

import org.daisy.util.fileset.AudioFile;
import org.daisy.util.fileset.impl.FilesetFileFactory;
import org.daisy.util.xml.SimpleNamespaceContext;
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
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
 * A fallback aligner that only knows how to sync an entire fragment with an entire audiofile.
 * @author Markus Gylling
 */
public class FallbackAlignerImpl extends Aligner implements DOMErrorHandler {
	private final String smilNSURI = "http://www.w3.org/2001/SMIL20/";
	@SuppressWarnings("unchecked")
	@Override
	public XMLResult process(XMLSource inputXML, AudioSource inputAudioFile, @SuppressWarnings("unused")String inputLanguage, XMLResult result) throws AlignerException {						
		try {
			AudioFile audioFile = (AudioFile)FilesetFileFactory.newInstance().newFilesetFile(inputAudioFile);
			audioFile.parse();
			SmilClock audioClock = audioFile.getLength();
			//SmilClock zero = new SmilClock(0);

			Map domConfigMap = LSParserPool.getInstance().getDefaultPropertyMap(Boolean.FALSE);
			domConfigMap.put("resource-resolver", CatalogEntityResolver.getInstance());
			LSParser parser = LSParserPool.getInstance().acquire(domConfigMap);
			DOMConfiguration domConfig = parser.getDomConfig();						
			domConfig.setParameter("error-handler", this);
			domConfig.setParameter("entities", Boolean.FALSE);						
			Document doc = parser.parseURI(inputXML.toURI().toString());
			
			doc.getDocumentElement().setAttribute("xmlns:smil", smilNSURI);
			
			//add erroneous syncpoints
			SimpleNamespaceContext nsc = new SimpleNamespaceContext(); 
			nsc.declareNamespace("smil", smilNSURI);
			NodeList toSyncList = XPathUtils.selectNodes(doc.getDocumentElement(), "//*[@smil:sync]", nsc);
						
			for (int i = 0; i < toSyncList.getLength(); i++) {
				Element toSync = (Element)toSyncList.item(i);
				toSync.removeAttributeNS(smilNSURI, "sync");
				//create src
				Attr srcAttr = doc.createAttributeNS(smilNSURI, "smil:src");
				srcAttr.setNodeValue(inputAudioFile.getAbsolutePath());
				toSync.setAttributeNodeNS(srcAttr);
				//create clipBegin
				Attr beginAttr = doc.createAttributeNS(smilNSURI, "smil:clipBegin");
				beginAttr.setNodeValue("0:00:00.0");
				toSync.setAttributeNodeNS(beginAttr);
				//create clipEnd
				Attr endAttr = doc.createAttributeNS(smilNSURI, "smil:clipEnd");
				endAttr.setNodeValue(audioClock.toString(SmilClock.FULL));
				toSync.setAttributeNodeNS(endAttr);
			}		
					
			//serialize
			Map<String,Object> props = new HashMap<String,Object>();
			props.put("namespaces", Boolean.FALSE); 					
			props.put("error-handler", this);					
			Serializer.serialize(doc,  result, "utf-8",props);
			
			LSParserPool.getInstance().release(parser, domConfigMap);
			
		} catch (Exception e) {
			throw new AlignerException(e.getMessage(),e);
		}
		return result;		
	}
		
	@Override
	public boolean supportsLanguage(@SuppressWarnings("unused")String language) {			
		return false;		
	}

	public boolean handleError(DOMError error) {		
		System.err.println(error.getMessage());					    		
		if(error.getSeverity()==DOMError.SEVERITY_WARNING) {
		   return true;
	    }
		return false; 
		
	}
}
