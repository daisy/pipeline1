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
package org.daisy.pipeline.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.daisy.util.file.EFile;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.SimpleNamespaceContext;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.dom.Serializer;
import org.daisy.util.xml.pool.LSParserPool;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSParser;


/**
 * Generate an index in a singular XHTML file. All headings with an id are included.
 * The result is put as the second child of body.
 * @author Markus Gylling
 */
public class IndexGenerator implements DOMErrorHandler {

	public IndexGenerator(EFile file) throws IOException {
		Map<String, Object> domConfigMap = null;
		LSParser parser = null;
		domConfigMap = LSParserPool.getInstance().getDefaultPropertyMap(Boolean.FALSE);
		try {
			domConfigMap.put("resource-resolver", CatalogEntityResolver.getInstance());
		} catch (CatalogExceptionNotRecoverable e) {
			e.printStackTrace();
		}
		parser = LSParserPool.getInstance().acquire(domConfigMap);
		DOMConfiguration domConfig = parser.getDomConfig();						
		domConfig.setParameter("error-handler", this);
		domConfig.setParameter("entities", Boolean.FALSE);						
		Document doc = parser.parseURI(file.toURI().toString());	
		
		SimpleNamespaceContext snc = new SimpleNamespaceContext();
		snc.declareNamespace("xht", Namespaces.XHTML_10_NS_URI);
		
		NodeList headings = XPathUtils.selectNodes(doc.getDocumentElement(),
				"//xht:h1[@id]|//xht:h2[@id]|//xht:h3[@id]|//xht:h4[@id]|//xht:h5[@id]|//xht:h6[@id]",snc);
		
		Node preExisting = XPathUtils.selectSingleNode(doc.getDocumentElement(),
				"//xht:ul[@class='toc_auto']",snc);
		if(preExisting!=null) {
			preExisting.getParentNode().removeChild(preExisting);
		}
		
		Element ul = doc.createElement("ul");		
		ul.setAttribute("class", "toc_auto");
		for (int i = 0; i < headings.getLength(); i++) {
			Node heading = headings.item(i);
			Element li = doc.createElement("li");			
			Element a = doc.createElement("a");
			a.setTextContent(heading.getTextContent());
			a.setAttribute("href", "#"+heading.getAttributes().getNamedItem("id").getNodeValue());
			li.appendChild(a);
			ul.appendChild(li);
		}
		
		
		//Node pos = doc.getDocumentElement().getLastChild().getFirstChild().getNextSibling();
		Node body = XPathUtils.selectSingleNode(doc.getDocumentElement(), "xht:body");
		Node pos = XPathUtils.selectSingleNode(body, "xht:*");
		pos.getParentNode().insertBefore(ul, pos.getNextSibling());
		
		/*
		 * Serialize the result.
		 */
		Map<String,Object> props = new HashMap<String,Object>();
		props.put("namespaces", Boolean.FALSE); 					
		props.put("error-handler", this);	
		props.put("format-pretty-print", Boolean.TRUE);					
		Serializer.serialize(doc, file, "utf-8", props);
		
	}

	public static void main(String[] args) {
		System.err.println("Running IndexGenerator...");
		try {
			EFile file = new EFile(args[0]);
			if(!file.exists()) {
				throw new IOException(file.toString());
			}
		
			new IndexGenerator(file);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("IndexGenerator done.");
	}

	public boolean handleError(DOMError error) {				
		System.err.println(error.getMessage());	    		
		return false;
	}

}
