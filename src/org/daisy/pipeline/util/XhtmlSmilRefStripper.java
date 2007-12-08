package org.daisy.pipeline.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.daisy.util.file.EFile;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.SimpleNamespaceContext;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.dom.Serializer;
import org.daisy.util.xml.pool.LSParserPool;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.ls.LSParser;


/**
 * Remove any anchors in an XHTML doc whose values contain 'smil'
 * @author Markus Gylling
 */
public class XhtmlSmilRefStripper implements DOMErrorHandler {

	public XhtmlSmilRefStripper(EFile file, EFile output) throws IOException {
		Map<String, Object> domConfigMap = null;
		LSParser parser = null;
		domConfigMap = LSParserPool.getInstance().getDefaultPropertyMap(Boolean.FALSE);
		parser = LSParserPool.getInstance().acquire(domConfigMap);
		DOMConfiguration domConfig = parser.getDomConfig();						
		domConfig.setParameter("error-handler", this);
		domConfig.setParameter("entities", Boolean.FALSE);						
		Document doc = parser.parseURI(file.toURI().toString());	
		
		SimpleNamespaceContext snc = new SimpleNamespaceContext();
		snc.declareNamespace("xht", Namespaces.XHTML_10_NS_URI);
		
		NodeList anchors = XPathUtils.selectNodes(doc.getDocumentElement(),
				"//xht:a[contains(@href,'smil')]",snc);
		
		for (int i = 0; i < anchors.getLength(); i++) {
			Node anchor = anchors.item(i);
			Node parent = anchor.getParentNode();
			Text text = doc.createTextNode(anchor.getTextContent());			
			parent.replaceChild(text, anchor);						
		}
								
		/*
		 * Serialize the result.
		 */
		Map<String,Object> props = new HashMap<String,Object>();
		props.put("namespaces", Boolean.FALSE); 					
		props.put("error-handler", this);	
		props.put("format-pretty-print", Boolean.TRUE);					
		Serializer.serialize(doc, output, "utf-8", props);
		
	}

	public static void main(String[] args) {
		System.err.println("Running anchorstripper...");
		try {
			EFile file = new EFile(args[0]);
			EFile output = new EFile(args[1]);
			if(!file.exists()) {
				throw new IOException(file.toString());
			}
		
			XhtmlSmilRefStripper dig = new XhtmlSmilRefStripper(file, output);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("anchorstripper done.");
	}

	public boolean handleError(DOMError error) {				
		System.err.println(error.getMessage());	    		
		return false;
	}

}
