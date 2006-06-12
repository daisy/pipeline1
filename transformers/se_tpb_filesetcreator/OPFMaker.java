package se_tpb_filesetcreator;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class OPFMaker {
	
	private Map mimeTypes;
	private Map dcElements;
	private Map metaData;
	private Vector smils;
	private Set generatedFiles;
	private Set referredFiles;
	private Set validDCElemNames = new HashSet();
	private File opfTemplateFile;
	private File opfOutputFile;
	private Vector ids = new Vector();
	
	private Document opf;
	private DocumentBuilder documentBuilder;
	private int id;
	private boolean DEBUG = false;
	private String doctypePublic = "+//ISBN 0-9673008-1-9//DTD OEB 1.2 Package//EN";
	private String doctypeSystem = "http://openebook.org/dtds/oeb-1.2/oebpkg12.dtd";
	
	public OPFMaker(
			Map mimeTypes, 
			Map dcElements, 
			Map metaData, 
			Vector smils, 
			Set generatedFiles, 
			Set referredFiles, 
			File opfTemplate, 
			File opfOutputFile, 
			File inputDir) throws ParserConfigurationException, CatalogExceptionNotRecoverable {
		
		this.mimeTypes = mimeTypes;
		this.dcElements = dcElements;
		this.metaData = metaData;
		this.smils = smils;
		this.generatedFiles = generatedFiles;
		this.referredFiles = referredFiles;
		this.opfTemplateFile = opfTemplate;
		this.opfOutputFile = opfOutputFile;
		//this.outputDir = opfOutputFile.getParentFile();
	
		DocumentBuilderFactory dfb = DocumentBuilderFactory.newInstance();
		documentBuilder = dfb.newDocumentBuilder();
		documentBuilder.setEntityResolver(CatalogEntityResolver.getInstance());
		
		for (Iterator it = this.generatedFiles.iterator(); it.hasNext(); ) {
			DEBUG("additionalFile: " + it.next());
		}
		
		// as of the z39.86 spec:
		validDCElemNames.add("dc:Title");
		validDCElemNames.add("dc:Creator");
		validDCElemNames.add("dc:Subject");
		validDCElemNames.add("dc:Description");
		validDCElemNames.add("dc:Publisher");
		validDCElemNames.add("dc:Contributor");
		validDCElemNames.add("dc:Date");
		validDCElemNames.add("dc:Type");
		validDCElemNames.add("dc:Format");
		validDCElemNames.add("dc:Identifier");
		validDCElemNames.add("dc:Source");
		validDCElemNames.add("dc:Language");
		validDCElemNames.add("dc:Relation");
		validDCElemNames.add("dc:Coverage");
		validDCElemNames.add("dc:Rights");
		
	}
	
	public void makeOPF() throws SAXException, IOException, TransformerException {
		DEBUG("makeOPF");
		parseTemplate();
		makeDCElements();
		makeMetaElements(metaData);
		makeManifest();
		makeSpine();
		DEBUG(opf);
		DEBUG("utfilen: " + opfOutputFile);
		outputOpf();
	}
	
	
	/**
	 * Constructs the spine.
	 *
	 */
	private void makeSpine() {
		Element spine = (Element) XPathUtils.selectSingleNode(opf.getDocumentElement(), "/package/spine");
		
		for (Iterator it = ids.iterator(); it.hasNext(); ) {
			Element itemref = opf.createElement("itemref");
			itemref.setAttribute("idref", (String) it.next());
			spine.appendChild(itemref);
		}
	}
	
	
	/**
	 * Constructs the manifest.
	 *
	 */
	private void makeManifest() {
		Element manifest = (Element) XPathUtils.selectSingleNode(opf.getDocumentElement(), "/package/manifest");
		
		// smil files, make sure IDs are saved in proper sequence
		for (Iterator it = smils.iterator(); it.hasNext(); ) {
			Object temp = it.next();
			DEBUG("makeManifest: " + temp);
			File currentFile = new File((String) temp);
			String id = getNextId("smil-");
			ids.add(id);
			
			Element elem = opf.createElement("item");
			elem.setAttribute("href", currentFile.getName());
			elem.setAttribute("id", id);
			elem.setAttribute("media-type", getMimeType(currentFile.getName()));
			manifest.appendChild(elem);
		}
		
		// other files
		Set remainingFiles = new HashSet();
		remainingFiles.addAll(referredFiles);
		remainingFiles.addAll(generatedFiles);
		
		for (Iterator it = remainingFiles.iterator(); it.hasNext(); ) {
			
			String filename = (String) it.next();
			filename = filename.replace('\\', '/');
			
			String id;
			if (filename.endsWith(".ncx")) {
				id  = "ncx";
			} else if (filename.endsWith(".res")) {
				id = "resource";
			} else {
				id = getNextId();
			}
			
			Element elem = (Element) opf.createElement("item");
			elem.setAttribute("href", filename);
			elem.setAttribute("id", id);
			elem.setAttribute("media-type", getMimeType(filename));
			manifest.appendChild(elem);
		}
	}
	
	
	/**
	 * Adds the dc-metadata section.
	 *
	 */
	private void makeDCElements() {		
		for (Iterator collIt = dcElements.keySet().iterator(); collIt.hasNext(); ) {
			String originalStr = (String) collIt.next();
			
			String elemName = originalStr;
			String prefix = "";
			String delim = "";
			int index = elemName.indexOf(':');
			if (index >= 0) {
				prefix = elemName.substring(0, index);
				delim += elemName.charAt(index);
				elemName = elemName.substring(index + 1);
				char initial = elemName.charAt(0);
				if (Character.isLowerCase(initial)) {
					elemName = Character.toUpperCase(initial) + elemName.substring(1);
				}
			}
			
			if (!validDCElemNames.contains(prefix + delim + elemName)) {
				continue;
			}
			
			// user defined default value present, discard defaults
			Node tmp = XPathUtils.selectSingleNode(opf.getDocumentElement(), "/package/metadata/dc-metadata/" + elemName);
			if (null != tmp) {
				tmp.getParentNode().removeChild(tmp);
			}
			
			Collection dcVals = (Collection) dcElements.get(originalStr);
			for (Iterator it = dcVals.iterator(); it.hasNext(); ) { 
				String originalValue = (String) it.next();
				Element elem = (Element) XPathUtils.selectSingleNode(opf.getDocumentElement(), "/package/metadata/dc-metadata/" + elemName);
				DEBUG(prefix + delim + elemName + "\t" + originalValue/*dcElements.get(originalStr)*/);
				if (null == elem || elem.getTextContent().trim().length() != 0) {
					elem = opf.createElement(prefix + delim + elemName);
					Element parent = (Element) XPathUtils.selectSingleNode(opf.getDocumentElement(), "/package/metadata/dc-metadata");
					parent.appendChild(elem);
				}
				
				if (null != originalValue && !elem.getTextContent().trim().equals(originalValue.trim())) {
					elem.appendChild(opf.createTextNode(originalValue));
				}
				
				if ("dc:Identifier".equals(prefix + delim + elemName)) {
					elem.setAttribute("id", "uid");
				}
			}
		}
	}
	
	
	/**
	 * Adds the metadata, such as total time, media content etc.
	 * @param metaData
	 */
	private void makeMetaElements(Map metaData) {
		Element xMeta = (Element) XPathUtils.selectSingleNode(opf.getDocumentElement(), "//x-metadata");
		for (Iterator it = metaData.keySet().iterator(); it.hasNext(); ) {
			
			String metaName = (String) it.next();
			String metaContent = (String) metaData.get(metaName);
			
			Element newMeta = opf.createElement("meta");
			newMeta.setAttribute("name", metaName);
			newMeta.setAttribute("content", metaContent);
		
			xMeta.appendChild(newMeta);
		}
	}
	
	
	/**
	 * Returns the mime type given a filename.
	 * @param filename the file.
	 * @return the mime type of the file <code>filename</code>
	 */
	private String getMimeType(String filename) {
		if (filename.indexOf('.') == -1) {
			throw new IllegalArgumentException("Filnamnen måste ha ett suffix för att det ska gå att avgöra dess mime-typ: " + filename);
		}
		
		String suffix = filename.substring(filename.lastIndexOf('.'));
		String mime = (String) mimeTypes.get(suffix);
		if (null == mime) {
			throw new IllegalArgumentException("Ogiltig filändelse: " + mime);
		}
		
		return mime;
	}
	
	
	/**
	 * Parses the opf-template.
	 * @throws SAXException
	 * @throws IOException
	 */
	private void parseTemplate() throws SAXException, IOException {
		DEBUG("parseTemplate");
		opf = documentBuilder.parse(opfTemplateFile);
	}
	
	
	/**
	 * Returns the next opf-id.
	 * @return the next opf-id.
	 */
	private String getNextId() {
		return getNextId("opf-");
	}
	
	
	/**
	 * Returns the next id with a custom prefix.
	 * @param prefix the custom prefix.
	 * @return the next id with a custom prefix.
	 */
	private String getNextId(String prefix) {
		return prefix + (++id);
	}

	
	/**
	 * Prints the opf-DOM to the output file.
	 * @throws TransformerException
	 * @throws IOException
	 */
	private void outputOpf() throws TransformerException, IOException {
		TransformerFactory xformFactory = TransformerFactory.newInstance();  
		javax.xml.transform.Transformer idTransform = xformFactory.newTransformer();
		
		idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
		idTransform.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctypePublic);
		idTransform.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctypeSystem );
		
		Source input = new DOMSource(opf);
		FileOutputStream fis = new FileOutputStream(opfOutputFile);
		Result output = new StreamResult(fis);
		idTransform.transform(input, output);
		fis.close();
	}

	private void DEBUG(String msg) {
		if (DEBUG) {
			System.err.println("debug: " + msg);
		}
	}
	
	private void DEBUG(Document d) {
		if (DEBUG) {
			try {
				TransformerFactory xformFactory = TransformerFactory.newInstance();  
				javax.xml.transform.Transformer idTransform = xformFactory.newTransformer();
				idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
				Source input = new DOMSource(d);
				Result output = new StreamResult(System.err);
				idTransform.transform(input, output);
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
