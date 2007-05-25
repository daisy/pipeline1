package org.daisy.dmfc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
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
 * Autogenerate content for /doc/ indices.
 * @author Markus Gylling
 */
public class DocIndexGenerator implements ErrorHandler {
	Map<File,String> mFilesWithTitles = null;
	
	DocIndexGenerator(EFolder docFolder) throws Exception {
		
		mFilesWithTitles = new HashMap();
		/*
		 * We assume that the name of doc/subfolder and the id
		 * of the wrapper div element in the destination file 
		 * is the same.
		 */
		
		//the index-all file
		System.err.println("Generating index-all...");
		EFile file = new EFile(docFolder, "index-all.html");		
		Document doc = parse(file);						
		populate(doc, docFolder, "scripts", "Pipeline Script: ");
		populate(doc, docFolder, "transformers", "Transformer documentation: ");		
		populate(doc, docFolder, "developer", "");
		populate(doc, docFolder, "enduser", "");
		createTOC(doc);
		serialize(doc, file);
		
		//the index-developer file
		System.err.println("Generating index-developer...");
		file = new EFile(docFolder, "index-developer.html");			
		doc = parse(file);						
		populate(doc, docFolder, "developer", "");
		populate(doc, docFolder, "scripts", "Pipeline Script: ");
		populate(doc, docFolder, "transformers", "Transformer documentation: ");		
		createTOC(doc);
		serialize(doc, file);
		
		//the index-enduser file
		System.err.println("Generating index-enduser...");
		file = new EFile(docFolder, "index-enduser.html");			
		doc = parse(file);						
		populate(doc, docFolder, "scripts", "Pipeline Script: ");		
		populate(doc, docFolder, "enduser", "");
		createTOC(doc);
		serialize(doc, file);

	}
	
	
	private void createTOC(Document doc) {
		Element tocDiv = doc.getElementById("toc");
		deleteChildren(tocDiv);
		NodeList catList = XPathUtils.selectNodes(doc.getDocumentElement(), "//div[@class='cat']");
	
		for (int i = 0; i < catList.getLength(); i++) {
			Element cat = (Element)catList.item(i);
			String id = cat.getAttribute("id");
			Element hd = (Element)XPathUtils.selectSingleNode(cat, "h2");
			String title = hd.getTextContent();			
			tocDiv = appendAnchorWithHref(tocDiv, "#"+id, title, "");			
		}		
	}


	private Document parse(EFile file) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder db = dbf.newDocumentBuilder();		
		db.setEntityResolver(CatalogEntityResolver.getInstance());
		db.setErrorHandler(this);
		return  db.parse(file);
	}
	
	private void serialize(Document doc, EFile file) throws IOException {
		doc = addMetaData(doc);
        OutputFormat outputFormat = new OutputFormat(doc);
        outputFormat.setPreserveSpace(false);
        outputFormat.setIndenting(true);
        outputFormat.setOmitComments(true);
        outputFormat.setIndent(3);        
        OutputStream fout= new FileOutputStream(file);
        XMLSerializer serializer = new XMLSerializer(fout, outputFormat);
        serializer.serialize(doc);		
	}


	private Document addMetaData(Document doc) {
		Element head = (Element)XPathUtils.selectSingleNode(doc.getDocumentElement(), "head");
		
		Element meta = doc.createElement("meta");
		meta.setAttribute("name", "generator");
		meta.setAttribute("content", this.getClass().getName());
		
		head.appendChild(meta);
		
		meta = doc.createElement("meta");
		meta.setAttribute("name", "date");
		meta.setAttribute("content", getDate());
		head.appendChild(meta);
		
		return doc;
	}


	private String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new java.util.Date());
	}


	private void populate(Document indexDoc, EFolder docFolder, String name, String deleteInLabels) throws IOException, CatalogExceptionNotRecoverable, PoolException, XMLStreamException {
		EFolder subDocFolder = new EFolder(docFolder, name);
		Element subDocListParent = indexDoc.getElementById(name);
		subDocListParent = (Element)deleteChildren(subDocListParent);
		getFilesWithTitles(subDocFolder);
		addRefs(subDocListParent, name, deleteInLabels);
	}

	private void addRefs(Element parent, String parentFolder, String deleteInLabels) {
		for (Iterator iter = mFilesWithTitles.keySet().iterator(); iter.hasNext();) {
			File file = (File) iter.next();
			if(file.getParentFile().getName().equals(parentFolder)){
				String title = mFilesWithTitles.get(file);
				String hrefValue = "./"+parentFolder+"/"+file.getName();
				parent = appendAnchorWithHref(parent, hrefValue, title,deleteInLabels);
			}	
		}							
	}

	private Element appendAnchorWithHref(Element parent, String hrefValue, String text, String deleteInLabels) {
		Element p = parent.getOwnerDocument().createElement("p");
		Element anchor = parent.getOwnerDocument().createElement("a");
		anchor.setAttribute("href", hrefValue);
		anchor.setTextContent(text.replace(deleteInLabels, ""));			
		p.appendChild(anchor);
		parent.appendChild(p);	
		return parent;
	}
	
	private void getFilesWithTitles(EFolder transformersDocFolder) throws PoolException, FileNotFoundException, XMLStreamException, CatalogExceptionNotRecoverable {
		
		Collection<File> files = transformersDocFolder.getFiles(false, ".+\\.[Xx]?[Hh][Tt][Mm][Ll]?$");
		for (File file : files) {
			if(!mFilesWithTitles.containsKey(file)) {
				//System.err.println("parsing " + file.getName());
				mFilesWithTitles.put(file, getTitle(new EFile(file)));
			}
		}				
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
		System.err.println("Running DocIndexGenerator...");
		try {
			EFolder docFolder = new EFolder(args[0]);
			if(!docFolder.exists()) {
				throw new IOException(docFolder.toString());
			}
		
			DocIndexGenerator dig = new DocIndexGenerator(docFolder);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("DocIndexGenerator done.");
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
