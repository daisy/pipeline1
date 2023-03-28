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

package se_tpb_filesetcreator;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.events.Attribute;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.daisy.util.dtb.meta.MetadataItem;
import org.daisy.util.dtb.meta.MetadataList;
import org.daisy.util.text.URIUtils;
import org.daisy.util.xml.SimpleNamespaceContext;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A class making the <b>.opf</b> file for a z39.86 fileset.
 * 
 * @author Martin Blomberg
 *
 */
public class OPFMaker {
	
	public static String DEBUG_PROPERTY = "org.daisy.debug";	// the system property used to determine if we're in debug mode or not
	
	private Map<String,String> mimeTypes;			// maps filename suffix to mime type for valid file types.
	private Map<?,?> dcElements;							// a MultiHashMap containing dc elements and their values.
	private MetadataList xMetaData;							// x-metadata container, such as total time, media content etc.
	private Vector<?> smils;							// the smil files names in order.
	private Set<String> generatedFiles;						// files generated during file set creation.
	private Set<String> referredFiles;						// files referred in other ways, e.g images.
	private Set<String> validDCElemNames = new HashSet<String>();	// valid dc elements for the opf.
	private File opfTemplateFile;					// the opf template
	private File opfOutputFile;						// output location of the generated opf
	private Vector<String> ids = new Vector<String>();				// opf file ids.
	
	private Document opf;							// the opfd beeing costructed
	private int id;									// id making use of a simple counter
	private String doctypePublic = "+//ISBN 0-9673008-1-9//DTD OEB 1.2 Package//EN";	// doctype public for the generated opf
	private String doctypeSystem = "http://openebook.org/dtds/oeb-1.2/oebpkg12.dtd";	// doctype system for the generated opf
	private static final String opfNamespaceURI = "http://openebook.org/namespaces/oeb-package/1.0/";
	private static final String dcNamespaceURI = "http://purl.org/dc/elements/1.1/";
	private SimpleNamespaceContext mNsc;			// custom namespace contex for xpath queries
	

	// list of metadata with 'dtb' prefix allowed in the x-metadata section
	private Set<String> allowedDtbMetadata = new HashSet<String>(
			Arrays.asList(new String[] { "dtb:sourceDate",
					"dtb:sourceEdition", "dtb:sourcePublisher",
					"dtb:sourceRights", "dtb:sourceTitle",
					"dtb:multimediaType", "dtb:multimediaContent",
					"dtb:narrator", "dtb:producer", "dtb:producedDate",
					"dtb:revision", "dtb:revisionDate",
					"dtb:revisionDescription", "dtb:totalTime",
					"dtb:audioFormat" }));

	/**
	 * 
	 * @param mimeTypes	the mapping between filename suffix and mime type.
	 * @param dcElements a mapping between dc element name and a collection of values.
	 * @param xMeta package file x-metadata.
	 * @param smils the smil file names in proper sequence.
	 * @param generatedFiles names of all files generated during file set creation.
	 * @param referredFiles names of files referred from the dtbook.
	 * @param opfTemplate the opf template file.
	 * @param opfOutputFile the location for the generated opf file.
	 * @param inputDir the directory where input dtbook and files reside.
	 */
	public OPFMaker(
			Map<String,String> mimeTypes, 
			Map<?,?> dcElements, 
			MetadataList xMeta, 
			Vector<?> smils, 
			Set<String> generatedFiles, 
			Set<String> referredFiles, 
			File opfTemplate, 
			File opfOutputFile, 
			File inputDir) {
		
		this.mimeTypes = mimeTypes;
		this.dcElements = dcElements;
		this.xMetaData = xMeta;
		this.smils = smils;
		this.generatedFiles = generatedFiles;
		this.referredFiles = referredFiles;
		this.opfTemplateFile = opfTemplate;
		this.opfOutputFile = opfOutputFile;
	
		
				
		// as of the z39.86 spec:
		validDCElemNames.add("dc:Title");
		validDCElemNames.add("dc:Creator");
		validDCElemNames.add("dc:Subject");
		validDCElemNames.add("dc:Description");
		validDCElemNames.add("dc:Publisher");
		validDCElemNames.add("dc:Contributor");
		validDCElemNames.add("dc:Date");
		validDCElemNames.add("dc:Type");
		// Martin Blomberg: Do not permit dc:Format other than from template.
		// http://sourceforge.net/tracker/index.php?func=detail&aid=1782459&group_id=134191&atid=729299
		//validDCElemNames.add("dc:Format");
		validDCElemNames.add("dc:Identifier");
		validDCElemNames.add("dc:Source");
		validDCElemNames.add("dc:Language");
		validDCElemNames.add("dc:Relation");
		validDCElemNames.add("dc:Coverage");
		validDCElemNames.add("dc:Rights");

		mNsc = new SimpleNamespaceContext();
		mNsc.declareNamespace("opf", opfNamespaceURI);
	}
	
	
	/**
	 * Starting point for making the OPF.
	 * 
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws ParserConfigurationException 
	 */
	public void makeOPF() throws SAXException, IOException, TransformerException, ParserConfigurationException {
		DEBUG("OPFMaker#makeOPF(): starting...");
		parseTemplate();
		makeDCElements();
		makeXMetaElements();
		makeManifest();
		makeSpine();
		DEBUG("OPFMaker#makeOPF() output file: " + opfOutputFile);
		outputOpf();
	}
	
	
	/**
	 * Constructs the spine.
	 *
	 */
	private void makeSpine() {
		Element spine = (Element) XPathUtils.selectSingleNode(opf.getDocumentElement(), "/opf:package/opf:spine", mNsc);
		
		for (Iterator<String> it = ids.iterator(); it.hasNext(); ) {
			Element itemref = opf.createElementNS(opfNamespaceURI, "itemref");
			itemref.setAttribute("idref", it.next());
			spine.appendChild(itemref);
		}
	}
	
	
	/**
	 * Constructs the manifest.
	 *
	 */
	private void makeManifest() {
		Element manifest = (Element) XPathUtils.selectSingleNode(opf.getDocumentElement(), "/opf:package/opf:manifest", mNsc);
		
		// smil files, make sure IDs are saved in proper sequence
		for (Iterator<?> it = smils.iterator(); it.hasNext(); ) {
			Object temp = it.next();
			File currentFile = new File((String) temp);
			String id = getNextId("smil-");
			ids.add(id);
			
			Element elem = opf.createElementNS(opfNamespaceURI, "item");
			elem.setAttribute("href", URIUtils.encodePath(currentFile.getName()));
			elem.setAttribute("id", id);
			elem.setAttribute("media-type", getMimeType(currentFile.getName()));
			manifest.appendChild(elem);
		}
		
		// other files
		Set<String> remainingFiles = new HashSet<String>();
		remainingFiles.addAll(referredFiles);
		remainingFiles.addAll(generatedFiles);
		
		for (Iterator<String> it = remainingFiles.iterator(); it.hasNext(); ) {
			
			String filename = it.next();
			filename = filename.replace('\\', '/');
			
			String id;
			if (filename.endsWith(".ncx")) {
				id  = "ncx";
			} else if (filename.endsWith(".res")) {
				id = "resource";
			} else {
				id = getNextId();
			}
			
			Element elem = opf.createElementNS(opfNamespaceURI, "item");
			elem.setAttribute("href", URIUtils.encodePath(filename));
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
		for (Iterator<?> collIt = dcElements.keySet().iterator(); collIt.hasNext(); ) {
			String originalStr = (String)collIt.next();
			
			// sort out the prefix:elemname, e.g "dc:Creator"
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
			
			
			// get the default value (if any!) for the current dc-element (what's present in the opf template) 
			Node tmp = XPathUtils.selectSingleNode(opf.getDocumentElement(), "/package/metadata/dc-metadata/" + elemName);
			if (null != tmp) {
				// user defined default value present, discard defaults
				tmp.getParentNode().removeChild(tmp);
			}
			
			// for each value for the key "dc:Something", add an element to the opf.
			Collection<String> dcVals = (Collection<String>) dcElements.get(originalStr);
			for (Iterator<String> it = dcVals.iterator(); it.hasNext(); ) { 
				String originalValue = it.next();
				Element elem = (Element) XPathUtils.selectSingleNode(opf.getDocumentElement(), "/opf:package/opf:metadata/opf:dc-metadata/" + elemName, mNsc);
				if (null == elem || elem.getTextContent().trim().length() != 0) {
					elem = opf.createElementNS(dcNamespaceURI, prefix + delim + elemName);
					Element parent = (Element) XPathUtils.selectSingleNode(opf.getDocumentElement(), "/opf:package/opf:metadata/opf:dc-metadata", mNsc);
					parent.appendChild(elem);
				}
				
				if (null != originalValue && !elem.getTextContent().trim().equals(originalValue.trim())) {
					elem.appendChild(opf.createTextNode(originalValue));
				}
				
				/* Martin Blomberg 7/5 2007
				if ("dc:Identifier".equals(prefix + delim + elemName)) {
					elem.setAttribute("id", "uid");
				}
				*/
			}
		}
		
		/* Martin Blomberg 7/5 2007 */
		/* here goes the real id element */
		Collection<String> singleValue = (Collection<String>) dcElements.get("uid");
		if (null != singleValue) {
			if (singleValue.size() == 1) {

				Node node = XPathUtils.selectSingleNode(opf, "/opf:package/opf:metadata/opf:dc-metadata", mNsc);
				Element elem = opf.createElementNS(dcNamespaceURI, "dc:Identifier");
				node.appendChild(elem);
				elem.setAttribute("id", "uid");
				for (Iterator<String> it = singleValue.iterator(); it.hasNext(); ) {
					elem.appendChild(opf.createTextNode(it.next()));	
				}			
			}
		}
	}
	
	
	/**
	 * Adds the metadata, such as total time, media content etc.
	 */
	private void makeXMetaElements() {
		Element xMeta = (Element) XPathUtils.selectSingleNode(opf.getDocumentElement(), "//opf:x-metadata", mNsc);
		// Construct a set of the metadata to ignore (already in the template and non repeatable) 
		Set<String> toIgnore = new HashSet<String>();
		NodeList existingMeta = XPathUtils.selectNodes(opf.getDocumentElement(), "//opf:x-metadata/opf:meta/@name", mNsc);
		for (int i = 0; i < existingMeta.getLength(); i++) {
			String metaName = existingMeta.item(i).getNodeValue();
			if (NCXMaker.nonRepeatableMetadata.contains(metaName)) {
				toIgnore.add(metaName);
			}
		}
		
		for(MetadataItem item : xMetaData) {
			Element newMeta = opf.createElementNS(opfNamespaceURI, "meta");
			String metaName = item.getQName().getLocalPart();
			if (!toIgnore.contains(metaName) && (!metaName.startsWith("dtb:") ||  allowedDtbMetadata.contains(metaName))){
				newMeta.setAttribute("name", metaName);
				newMeta.setAttribute("content", item.getValue());
				Iterator<Attribute> iter = item.getAttributes();
				while(iter.hasNext()) {
					Attribute a = iter.next();
					newMeta.setAttribute(a.getName().getLocalPart(), a.getValue());
				}
				xMeta.appendChild(newMeta);
			}
		}
	}
	
	
	/**
	 * Returns the mime type given a filename.
	 * @param filename the file.
	 * @return the mime type of the file <code>filename</code>
	 */
	private String getMimeType(String filename) {
		if (filename.indexOf('.') == -1) {
			throw new IllegalArgumentException("Filenames must end with a '.' followed by a valid suffix in order to determine mime type: " + filename);
		}
		
		String suffix = filename.substring(filename.lastIndexOf('.'));
		String mime = mimeTypes.get(suffix);
		if (null == mime) {
			throw new IllegalArgumentException("Illegal filename suffix: " + suffix);
		}
		//TODO this should not abort if an unknown type is encountered. 
		//Use SignatureDetector or something else
		
		return mime;
	}
	
	
	/**
	 * Parses the opf-template.
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException 
	 */
	private void parseTemplate() throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dfb = DocumentBuilderFactory.newInstance();
		dfb.setNamespaceAware(true);
		DocumentBuilder documentBuilder = dfb.newDocumentBuilder();
		documentBuilder.setEntityResolver(CatalogEntityResolver.getInstance());
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

	
	/**
	 * Prints debug messages to System.out. The messages are prefixed with 
	 * <b>DEBUG:</b>. Messages are printed out iff the system property
	 * <b>org.daisy.debug</b> is defined.
	 * @param msg The message.
	 */
	private void DEBUG(String msg) {
		if (System.getProperty(DEBUG_PROPERTY) != null) {
			System.out.println("DEBUG: " + msg);
		}
	}
	
	
	
	/**
	 * Prints the xml content of thge document <b>d</b> to System.out iff
	 * the system property <b>org.daisy.debug</b> is defined.
	 * @param d the document.
	 */
	@SuppressWarnings("unused")
	private void DEBUG(Document d) {
		if (System.getProperty(DEBUG_PROPERTY) != null) {
			try {
				TransformerFactory xformFactory = TransformerFactory.newInstance();  
				javax.xml.transform.Transformer idTransform = xformFactory.newTransformer();
				idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
				Source input = new DOMSource(d);
				Result output = new StreamResult(System.out);
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
