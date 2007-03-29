package org.daisy.dmfc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * Autogenerate content for the /doc/index.html file, and the doc/scripts.html file.
 * @author Markus Gylling
 */
public class DocIndexGenerator implements ErrorHandler {

	DocIndexGenerator(EFolder docFolder) throws Exception {
		
		/*
		 * We assume that the name of doc/subfolder and the id
		 * of the wrapper div element in the destination file 
		 * is the same.
		 */
		
		EFile indexFile = new EFile(docFolder, "index.html");		
		Document indexDoc = parse(indexFile);						
		populate(indexDoc, docFolder, "scripts", "Pipeline Task: ");
		populate(indexDoc, docFolder, "transformers", "Transformer documentation: ");		
		serialize(indexDoc, indexFile);
				
		EFile scriptsFile = new EFile(docFolder, "scripts.html");		
		Document scriptsDoc = parse(scriptsFile);								
		populate(scriptsDoc, docFolder, "scripts", "Pipeline Task: ");
		serialize(scriptsDoc, scriptsFile);        
	}
	
	
	private Document parse(EFile file) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder db = dbf.newDocumentBuilder();		
		db.setEntityResolver(CatalogEntityResolver.getInstance());
		db.setErrorHandler(this);
		return  db.parse(file);
	}
	
	private void serialize(Document doc, EFile file) throws IOException {
        OutputFormat outputFormat = new OutputFormat(doc);
        outputFormat.setPreserveSpace(false);
        outputFormat.setIndenting(true);
        outputFormat.setOmitComments(true);
        outputFormat.setIndent(3);        
        OutputStream fout= new FileOutputStream(file);
        XMLSerializer serializer = new XMLSerializer(fout, outputFormat);
        serializer.serialize(doc);		
	}


	private void populate(Document indexDoc, EFolder docFolder, String name, String deleteInLabels) throws IOException, CatalogExceptionNotRecoverable, PoolException, XMLStreamException {
		EFolder subDocFolder = new EFolder(docFolder, name);
		Element subDocListParent = indexDoc.getElementById(name);
		subDocListParent = (Element)deleteChildren(subDocListParent);
		Map<File,String> filesWithTitles = getFilesWithTitles(subDocFolder);
		addRefs(filesWithTitles, subDocListParent, name, deleteInLabels);
	}

	private void addRefs(Map<File, String> filesWithTitles, Element parent, String parentFolder, String deleteInLabels) {
		for (Iterator iter = filesWithTitles.keySet().iterator(); iter.hasNext();) {
			File file = (File) iter.next();
			String title = filesWithTitles.get(file);			
			Element p = parent.getOwnerDocument().createElement("p");			
			Element anchor = parent.getOwnerDocument().createElement("a");
			anchor.setAttribute("href", "./"+parentFolder+"/"+file.getName());
			anchor.setTextContent(title.replace(deleteInLabels, ""));			
			p.appendChild(anchor);
			parent.appendChild(p);			
		}							
	}

	
	private Map<File, String> getFilesWithTitles(EFolder transformersDocFolder) throws PoolException, FileNotFoundException, XMLStreamException, CatalogExceptionNotRecoverable {
		Map<File, String> map = new HashMap<File, String>();
		Collection<File> files = transformersDocFolder.getFiles(false, ".+\\.[Xx]?[Hh][Tt][Mm][Ll]?$");
		for (File file : files) {
			System.err.println("parsing " + file.getName());
			map.put(file, getTitle(new EFile(file)));						
		}		
		return map;		
	}

	
	private String getTitle(EFile file) throws PoolException, FileNotFoundException, XMLStreamException, CatalogExceptionNotRecoverable {
		Map props = new HashMap();
		props.put(XMLInputFactory.RESOLVER, new StaxEntityResolver(CatalogEntityResolver.getInstance()));
		
		XMLInputFactory xif = StAXInputFactoryPool.getInstance().acquire(props);
		XMLEventReader reader = xif.createXMLEventReader(file.asInputStream());
		boolean active = false;
		String ret = null;
		while(reader.hasNext()) {
			XMLEvent ev = (XMLEvent)reader.next();
			if(ev.isStartElement() && ev.asStartElement().getName().getLocalPart().equals("title")) {
				active = true;
				continue;
			}else if (ev.isCharacters() && active) {
				if(ret==null) {
					ret=ev.asCharacters().getData();
				}else{
					ret += ev.asCharacters().getData();
				}	
			}else if(ev.isEndElement() && active) {
				reader.close();
				return ret;
			}			
		}
		reader.close();
		return "failed - need head title";
	}

	
	private Node deleteChildren(Node parent) {
		
		NodeList nodeList = XPathUtils.selectNodes(parent, "./*");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(!node.getNodeName().equals("h2")){
				if (node.hasChildNodes()) {
					deleteChildren(node);
				}
				Node del = node.getParentNode().removeChild(node);	
			}	
		}
		return parent;
	}

	
	/**
	 * Main class
	 * @param absolute path to the /doc/ folder
	 */
	public static void main(String[] args) {		
		System.err.println("Running DocIndexGenerator.");
		try {
			EFolder docFolder = new EFolder(args[0]);
			if(!docFolder.exists()) {
				throw new IOException(docFolder.toString());
			}
		
			DocIndexGenerator dig = new DocIndexGenerator(docFolder);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("DocIndexGenerator done");
	}





	public void error(SAXParseException arg0) throws SAXException {
		System.err.println(arg0.toString());		
	}

	public void fatalError(SAXParseException arg0) throws SAXException {
		System.err.println(arg0.toString());		
	}

	public void warning(SAXParseException arg0) throws SAXException {
		System.err.println(arg0.toString());		
	}

}
