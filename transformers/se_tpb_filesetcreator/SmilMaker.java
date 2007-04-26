/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package se_tpb_filesetcreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.daisy.dmfc.core.event.BusListener;
import org.daisy.dmfc.core.event.UserAbortEvent;
import org.daisy.dmfc.exception.TransformerAbortException;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.collection.MultiHashMap;
import org.daisy.util.execution.AbortListener;
import org.daisy.util.execution.ProgressObserver;
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.BookmarkedXMLEventReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * A class making <tt>.smil</tt> files given a dtbook document modified in a 
 * certain way. 
 * 
 * Expected input is the dtbook document enriched with smil attributes on those 
 * elements containing audio. The output is not considered "valid" output in any 
 * other sense, than as input the NCXMaker.
 * 
 * The class is only used internaly from the class 
 * <tt>se_tpb_filesetcreator.FileSetCreator</tt>, main class of the transformer
 * File Set Creator
 * 
 * @author Martin Blomberg
 *
 */
public class SmilMaker implements AbortListener, BusListener {
	
	public static String DEBUG_PROPERTY = "org.daisy.debug";	// the system property used to determine if we're in debug mode or not
	
	private BookmarkedXMLEventReader reader;		// reader pointed to the input document
	private XMLEventWriter writer;					// writes the (possibly) modified input document
	private XMLEventFactory eventFactory;			// factory for the modified stax events
	private XMLOutputFactory outputFactory;			// factory building the stax output writer
	private DocumentBuilder documentBuilder;		// parses the smil template over and over again

	private File outputDirectory;					// directory in which to write smil files
	private File smilTemplateFile;					// the location of the smil template file

	private Vector smilTrees = new Vector();		// container of almost (link rumble may still be left) finished smil files represented as DOM.
	private Vector smilFiles = new Vector();		// container of Files, smilFiles.get(i) represents smilTrees.get(i)
	
	private int smilId;								// generator for smil file element ids making use of a simple counter
	private int dtbid;								// generator for dtb file element ids making use of a simple counter
	
	private Set skippable;							// names of skippable elements
	private Set ecapable;							// names of escapable elements
	private Set headings;							// names of heading elements
	private Set forceLink = new HashSet();			// mapping between element names of ref->content (e.g noteref->note)
	private Set otherEncounteredFiles = new HashSet();	// names of encountered files, such as images.
	
	private Set currentlySkipped = new HashSet();	// names of present skippable elements in the current smil
	private Set totalSkipped = new HashSet();		// names of present skippable elements in all smils
	
	private Document currentSmil;					// the current smil	
	private Stack openSeqs = new Stack();			// stack keeping track of which seq in the current smil to append children to
	
	private String smilClipBegin = "clipBegin";		// name of smil attribute
	private String smilClipEnd = "clipEnd";			// name of smil attribute
	private String smilSrc = "src";					// name of smil attribute
	private String tempLinkId = "tempLinkId";		// name of temporary attribute for force link elements
	
	private String currentAudioFilename;			// name of the current audio file
	private String finalDTBookFilename;				// name of the final dtbook file
	private boolean newSmilIncoming = false;		// whether next sync point's corresponding audio is in a file other than currentAudioFilename 
	private long totalTime;							// total duration of this book.
	
	private Vector generatedFiles = new Vector();	// names of all generated (smil) files
	private File manuscriptOutputFile;				// the location to which the modified input document should be written
	private String uid;								// the id of this book
	
	private String smilDoctypePublic = "-//NISO//DTD dtbsmil 2005-1//EN";					// doctype public of output smil
	private String smilDoctypeSystem = "http://www.daisy.org/z3986/2005/dtbsmil-2005-1.dtd";// doctype system of output smil
	private String smilNamespaceURI = "http://www.w3.org/2001/SMIL20/";						// namespace for the smil elements
	
	private Map smilFileMapping = new HashMap();		// used for mapping element id to file
	private MultiHashMap forceLinkMap = new MultiHashMap(false);
	private ProgressObserver obs;						// ProgressObserver: a component to report progress to, used for ui.
	private int numSmilFiles;							// the expected number of generated smil files, used for progress reporting
	private double noteRumbleTimeProportion = 0.15;		// the proprtion of this program's time used for inserting extra force link targets
	private double domFinalizeTimeProportion = 0.15;	// the proprtion of this program's time used for finalizing the doms (time calcs.) before output to file
	
	private FileSetCreator checkAbortCallBack;			// component throwing an exception if the user has aborted the run
	
	
	/**
	 * @param inputManuscript the input file to read
	 * @param outputDir	the dir in which to place generated smil files
	 * @param smilTemplate	the smil template file
	 * @param skippable	set of names of skippable elements
	 * @param escapable	set of names of escapable elements
	 * @param forceLink	set of names of elements supposed to be link to be their referrers
	 * @param obs	a progress observer
	 * @param fsc	a file set creator, used for polling the ui: did user interrupt?
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 * @throws ParserConfigurationException
	 * @throws CatalogExceptionNotRecoverable
	 */
	SmilMaker(
			File inputManuscript, 
			File outputDir, 
			File smilTemplate, 
			Set skippable, 
			Set escapable, 
			Set forceLink,
			ProgressObserver obs, 
			FileSetCreator fsc) 
			throws 
			FileNotFoundException, 
			XMLStreamException, 
			ParserConfigurationException, 
			CatalogExceptionNotRecoverable {	
		
		// prepare for progress reports:
		reader = getBookmarkedXMLEventReader(inputManuscript);
		numSmilFiles = countAudioFiles(reader);
		reader.close();
		
		reader = getBookmarkedXMLEventReader(inputManuscript);
		outputFactory = XMLOutputFactory.newInstance();
		outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.FALSE);

		outputDirectory = outputDir;
		if (!outputDirectory.isDirectory()) {
			if (!outputDirectory.exists()) {
				outputDirectory.mkdirs();
			} else {
				throw new IllegalArgumentException("Output path is not a directory; \"" 
					+ outputDirectory.getAbsolutePath()
					+ "\"");
			}
		}
		
		this.smilTemplateFile = smilTemplate;
		this.skippable = skippable;
		this.ecapable = escapable;
		
		// TODO
		// until we have a context aware bookmarked xml event reader, 
		// narrator will treat h1-h6 as valid headings. Not hd.
		this.headings = new HashSet();
		headings.add("h1");
		headings.add("h2");
		headings.add("h3");
		headings.add("h4");
		headings.add("h5");
		headings.add("h6");
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		documentBuilder = dbf.newDocumentBuilder();
		documentBuilder.setEntityResolver(CatalogEntityResolver.getInstance());
		
		eventFactory = XMLEventFactory.newInstance();
		this.forceLink.addAll(forceLink);
		this.obs = obs;
		this.checkAbortCallBack = fsc;
		
	}


	
	
	
	/**
	 * Returns a <code>BookmarkedXMLEventReader</code> for the file <code>inputFile</code>.
	 * @param inputFile the file to be read.
	 * @return a <code>BookmarkedXMLEventReader</code> for the file <code>inputFile</code>.
	 * @throws FileNotFoundException if <code>inputFile</code> can't be found.
	 * @throws XMLStreamException
	 */
	private BookmarkedXMLEventReader getBookmarkedXMLEventReader(File inputFile) throws FileNotFoundException, XMLStreamException {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
		FileInputStream fis = new FileInputStream(inputFile);
		XMLEventReader plainReader = inputFactory.createXMLEventReader(fis);
		return new BookmarkedXMLEventReader(plainReader);
	}
	
	
	/**
	 * Returns the number of distinct audio file names in the manuscript file.
	 * @param reader a <code>BookmarkedXMLEventReader</code> for the manuscript file.
	 * @return the number of distinct audio file names in the manuscript file.
	 * @throws XMLStreamException
	 */
	private int countAudioFiles(BookmarkedXMLEventReader reader) throws XMLStreamException {
		Set audioFiles = new HashSet();
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			if (event.isStartElement() && hasSmilAttributes(event.asStartElement())) {
				Map attrs = getSmilContext(event.asStartElement());
				audioFiles.add(attrs.get(smilSrc));
			}
		}
		return audioFiles.size();
	}
	
	
	/**
	 * Returns a <code>Map</code> containing the the <code>smil</code>-context
	 * for this start element, that is the <code>smil</code>-attributes mapped
	 * as follows:
	 * <ol>
	 * <li>clipBegin -> value for clipBegin</li>
	 * <li>clipEnd -> value for clipEnd</li>
	 * <li>src -> value for src</li>
	 * </ol>
	 * or <code>null</code> if no such attributes exist.
	 * @param se
	 * @return a <code>Map</code> containing the smil-attributes of the
	 * start element <code>se</code> or null of no such attribute exist.
	 * @throws XMLStreamException
	 */
	private Map getSmilContext(StartElement se) throws XMLStreamException {
		if (!hasSmilAttributes(se)) {
			return null;
		}
		
		Map attributes = new HashMap();
		
		//DEBUG(se);
		for (Iterator it = se.getAttributes(); it.hasNext(); ) {
			Attribute at = (Attribute) it.next();
			attributes.put(at.getName().getLocalPart(), at.getValue());
		}
		
		return attributes;
	}
	
	
	/**
	 * Returns a <code>Set</code> containing the names of all custom test 
	 * performed in the document processed.
	 * @return a <code>Set</code> containing the names of all custom test 
	 * performed in the document processed.
	 */
	public Set getAllCustomTests() {
		return totalSkipped;
	}
	
	
	/**
	 * Returns a <code>Vector</code> containing the names as
	 * <code>java.lang.String</code>-objects of the generated files.
	 * @return a <code>Vector</code> containing the names as
	 * <code>java.lang.String</code>-objects of the generated files.
	 */
	public Vector getAllGeneratedSmilFiles() {
		return generatedFiles;
	}
	
	
	/**
	 * Returns a <code>File</code> representing the modified manuscript 
	 * that is output from this process.
	 * @return a <code>File</code> representing the modified manuscript 
	 * that is output from this process.
	 */
	public File getModifiedManuscriptFile() {
		return manuscriptOutputFile;
	}
	
	
	/**
	 * Starting point for generating smil files.
	 * 
	 * @throws TransformerException
	 * @throws TransformerRunException
	 * @throws IOException
	 */
	public void makeSmils() throws TransformerException, TransformerRunException, IOException {
		
		try {
			uid = getUid(reader);
			startNewSmil(reader);
			
			while (reader.hasNext()) {
				XMLEvent event = reader.nextEvent();
				
				if (event.isStartElement()) {
					
					if (isEscapable(event)) {
						XMLEvent modifiedStartElement = newSeq(reader, event.asStartElement());
						event = modifiedStartElement;
					}
					
					else if (isPar(event.asStartElement())) {
						XMLEvent modifiedStartElement = newPar(event.asStartElement());
						event = modifiedStartElement;
					} 
					else if (isLinkSource(event.asStartElement())) {
						handleLinkSource(event.asStartElement());
					} 
					else if (isLinkTarget(event.asStartElement())) {
						handleLinkTarget(event.asStartElement());
					}
					
				} else if (event.isStartDocument()) { 
					StartDocument sd = (StartDocument)event;
					if (sd.encodingSet()) {
						manuscriptOutputFile = File.createTempFile("dtbook.temp.", null);
						writer = outputFactory.createXMLEventWriter(
								new FileOutputStream(manuscriptOutputFile), 
								sd.getCharacterEncodingScheme());					
					} else {
						manuscriptOutputFile = File.createTempFile("dtbook.temp.", null);
						writer = outputFactory.createXMLEventWriter(
								new FileOutputStream(manuscriptOutputFile), 
								"utf-8");
						
						event = eventFactory.createStartDocument("utf-8", "1.0");                
					}
				} else if (event.isEndElement()) {
					if (isEscapable(event)) {
						closeSeq();
					}
					
					if (isLink(event.asEndElement())) {
						boolean escapable = false;
						closeSeq(escapable);
					}
					
					// will we pass a heading before next par?
					if (!newSmilIncoming && nextParIsNewSmil(reader)) {
						newSmilIncoming  = true;
					}
					
					if (newSmilIncoming && openSeqs.size() == 1) {
						startNewSmil(reader);
						newSmilIncoming = false;
					}
				}
				writeEvent(event);
			} // end while
					
			closeStreams();
			
		} catch (Exception e) {
			DEBUG("makeSmils(): e.getMessage: " + e);
			e.printStackTrace();
			throw new TransformerRunException(e.getMessage(), e);
		}
		
		rearrangeForceLinkElems();
		insertLinks();
		outputResult();
	}


	/**
	 * Closes the open streams.
	 * 
	 * @throws XMLStreamException
	 */
	private void closeStreams() throws XMLStreamException {
		writer.flush();
		writer.close();
		reader.close();
	}


	
	/**
	 * Prints the generated smil-files to disk.
	 * @throws TransformerException
	 * @throws IOException
	 */
	private void outputResult() throws TransformerException, IOException {
		long millis = 0;
		int i = 0;
		int size = smilTrees.size();
		for (; i < size; i++) {
			
			Document doc = (Document) smilTrees.get(i);
			File file = (File) smilFiles.get(i);
			
			millis = finishSmil(millis, doc);
			outputDocument(doc, file);
			this.obs.reportProgress(1 - domFinalizeTimeProportion * ((size - (i+1)) / size));
		}
	}
	
	
	/**
	 * Implements the links found in the content document in the smil-files.
	 *
	 */
	private void insertLinks() {
		DEBUG("insertLinks");
		for (Iterator it = smilFileMapping.keySet().iterator(); it.hasNext(); ) {
			String linkTargetId = (String) it.next();
			String linkTargetFilename = (String) smilFileMapping.get(linkTargetId);
			
			for (int i = 0; i < smilTrees.size(); i++) {
				
				Document dom = (Document) smilTrees.get(i);
				File file = (File) smilFiles.get(i);
				
				String  filenamePart = "";
				if (!file.getName().equals(linkTargetFilename)) {
					filenamePart = linkTargetFilename;
				}
				
				String xPath = "//a[@href='" + linkTargetId + "']";
				NodeList linkSources = XPathUtils.selectNodes(dom.getDocumentElement(), xPath);
				
				for (int j = 0; j < linkSources.getLength(); j++) {
					Element linkSource = (Element) linkSources.item(j);
					linkSource.setAttribute("href", filenamePart + "#" + linkTargetId);
				}
			}
		}
	}


	/**
	 * Some pair of elements, such as [noteref, note] and
	 * [annoref, annotation], are supposed to be linked together.
	 * This link is implemented, apart from a-elements, by copying the link target's corresponding
	 * seq-element (e.g. &lt;seq class="note"...) and inserting it right 
	 * after the referring element. In this way, the player will continue
	 * right after the reference after playing the target element instead
	 * of countinuing after the target. 
	 * 
	 * @throws TransformerAbortException 
	 *
	 */
	private void rearrangeForceLinkElems() throws TransformerAbortException {
		
		// exec time
		long forEachGeneratedDom = 0;
		long forEachLinkTarget = 0;
		long forEachReference = 0;
		long tmpFer = 0;
		
		
		// fetch all link targets. note elements, and such
		Map forceLinks = new HashMap();
		// for each genereated smil DOM
		forEachGeneratedDom = System.currentTimeMillis();
		for (Iterator it = smilTrees.iterator(); it.hasNext(); ) {
			Document curr = (Document) it.next();
			
			// select every node with attribute tempLinkId
			// store them in a hashmap using the id as key
			NodeList elementList = XPathUtils.selectNodes(curr.getDocumentElement(), "//*[@" + tempLinkId + "]");
			for (int i = 0; i < elementList.getLength(); i++) {
				Element elem = (Element) elementList.item(i);
				forceLinks.put(elem.getAttribute(tempLinkId), elem);
			}
		}
		forEachGeneratedDom = System.currentTimeMillis() - forEachGeneratedDom;
		
		// for each link target
		int numLaps = forceLinks.keySet().size();
		int counter = 0;
		forEachLinkTarget = System.currentTimeMillis();
		for (Iterator elemIt = forceLinks.keySet().iterator(); elemIt.hasNext(); ) {
			
			String elemId = (String) elemIt.next();
			Node linkTarget = (Node) forceLinks.get(elemId);
			((Element) linkTarget).removeAttribute(tempLinkId);			
			Collection srcs = forceLinkMap.getCollection(elemId);
			if (null == srcs) {
				// note (or something) without any reference
				continue;
			}
		
			tmpFer = System.currentTimeMillis();
			for (Iterator srcIt = srcs.iterator(); srcIt.hasNext(); ) {
				
				Element linkSrc = (Element) srcIt.next();
				linkSrc.removeAttribute("idref");
				Element nextSibling = (Element) linkSrc.getNextSibling();
				Element parent = (Element) linkSrc.getParentNode();
				
				Node newLinkTarget = parent.getOwnerDocument().importNode(linkTarget, true);
				updateSubtreeIds(newLinkTarget);
				
				boolean isEqualNode = (nextSibling != null) && (nextSibling.isEqualNode(linkTarget));
				if (null == nextSibling) {
					parent.appendChild(newLinkTarget);
					updateDTBUserEscape(parent);
				} else if (!isEqualNode) {
					parent.insertBefore(newLinkTarget, nextSibling);
				} else {
					newLinkTarget = linkTarget;
				}
								
				addLink(getAudioChild(linkSrc), ((Element) newLinkTarget).getAttribute("id"));
			}
			
			
			/*
			// for each reference to "linkTarget"
			for (Iterator smilIt = smilTrees.iterator(); smilIt.hasNext(); ) {
				Document curr = (Document) smilIt.next();
				// Martin Blomberg 2006-09-11:
				// handling of 2005-1 and 2005-2 different idref types.
				String xPath = "//par[@idref=translate('" + elemId + "', '#', '')]";
				NodeList linkSrcs = XPathUtils.selectNodes(curr.getDocumentElement(), xPath);
				
				tmpFeri = System.currentTimeMillis();
				for (int i = 0; i < linkSrcs.getLength(); i++) {
					Element linkSrc = (Element) linkSrcs.item(i);
					linkSrc.removeAttribute("idref");
					Element nextSibling = (Element) linkSrc.getNextSibling();
					Element parent = (Element) linkSrc.getParentNode();
					
					Node newLinkTarget = parent.getOwnerDocument().importNode(linkTarget, true);
					updateSubtreeIds(newLinkTarget);
					
					boolean isEqualNode = (nextSibling != null) && (nextSibling.isEqualNode(linkTarget));
					if (null == nextSibling) {
						parent.appendChild(newLinkTarget);
						updateDTBUserEscape(parent);
					} else if (!isEqualNode) {
						parent.insertBefore(newLinkTarget, nextSibling);
					} else {
						newLinkTarget = linkTarget;
					}
									
					addLink(getAudioChild(linkSrc), ((Element) newLinkTarget).getAttribute("id"));
				}
				forEachReferenceInner += System.currentTimeMillis() - tmpFeri;
			}
			*/
			
			forEachReference += System.currentTimeMillis() - tmpFer;
			counter++;
			checkAbortCallBack.checkTransformerAborted();
			obs.reportProgress((1 - noteRumbleTimeProportion - domFinalizeTimeProportion) + noteRumbleTimeProportion * counter / numLaps);
		}
		forEachLinkTarget = System.currentTimeMillis() - forEachLinkTarget;
		
//		System.err.println("foreach generated dom: " + formatTime(forEachGeneratedDom));
//		System.err.println("foreach link target:   " + formatTime(forEachLinkTarget));
//		System.err.println("foreach reference: " + formatTime(forEachReference));
	}
	
	
	private String formatTime(long timestamp) {
		String val = "";
		
		long ms = timestamp % 1000;
		timestamp /= 1000;
		long s = timestamp % 60;
		timestamp /= 60;
		long m = timestamp % 60;
		timestamp /= 60;
		long h = timestamp % 60;
		
		val = h + ":" +
			(m > 9 ? "" : "0") + m + ":" +
			(s > 9 ? "" : "0") + s + "." + ms;
		
		return val;
	}
	
	
	/**
	 * Returns the child audio element of <code>noteref</code>,
	 * or <code>null</code> if no such element exsits.
	 * @param noteref an element having a child audio element.
	 * @return the child audio element of <code>noteref</code>,
	 * or <code>null</code> if no such element exsits.
	 */
	private Element getAudioChild(Element noteref) {
		NodeList children = noteref.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Element elem = (Element) children.item(i);
			if (elem.getTagName().equals("audio")) {
				return elem;
			}
		}
		return null;
	}
	
	
	/**
	 * Inserts a <code>a<code>-element that wraps <code>linkFrom</code> with 
	 * a link to <code>linkToId<code> in <code>filename<code>. 
	 * @param linkFrom the element to wrap with an <code>a</code>-element.
	 * @param linkToId the id to which the link points.
	 * @param filename the name of the file containing <code>linkToId<code>.
	 */
	private void addLink(Element linkFrom, String linkToId, String filename) {		
		Node parent = linkFrom.getParentNode();
		linkFrom = (Element) parent.removeChild(linkFrom);		
		
		Document owner = linkFrom.getOwnerDocument();
		Element link = owner.createElement("a");
		link.setAttribute("href", filename + "#" + linkToId);

		link.appendChild(linkFrom);		
		parent.appendChild(link);
	}
	
	
	/**
	 * Inserts a <code>a<code>-element that wraps <code>linkFrom</code> with 
	 * a link to <code>linkToId<code>. Those elements are in the same file.
	 * @param linkFrom the element to wrap with an <code>a</code>-element.
	 * @param linkToId the id to which the link points.
	 */
	private void addLink(Element linkFrom, String linkToId) {		
		addLink(linkFrom, linkToId, "");
	}
	
	
	/**
	 * Updates id values in the tree <code>subtree</code>. This 
	 * might have to be done since some elements are duplicated.
	 * @param subtree the subtree in which ids should be refreshed.
	 * @see se_tpb_filesetcreator.SmilMaker#rearrangeForceLinkElems()
	 */
	private void updateSubtreeIds(Node subtree) {
		Element elem = (Element) subtree;
		if (elem.hasAttribute("id")) {
			elem.setAttribute("id", getNextSmilId("forcelinkstruct"));
		}
		
		NodeList children = elem.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			updateSubtreeIds(children.item(i));
		}
		
		if (elem.hasAttribute("end")) {
			updateDTBUserEscape(elem);
		}
		
		if (elem.hasAttribute(tempLinkId)) {
			elem.removeAttribute(tempLinkId);
		}
	}
	
	
	/**
	 * Returns the id of <code>elem</code>'s last child time container
	 *  or null if no such child exists.
	 * @param elem an element
	 * @return the id of <code>elem</code>'s last child time container
	 *  or null if no such child exists.
	 */
	private String getLastChildTCId(Element elem) {
		Element lastChild = (Element) elem.getLastChild();
		if (null == lastChild) {
			return null;
		}
		return lastChild.getAttribute("id");
	}
	
	
	/**
	 * Updates DTBuserEscape;-values, good to do when e.g. note
	 * bodies have been inserted in new places.
	 * @param elem the <code>seq</code> containing
	 * a, e.g. note body.
	 */
	private void updateDTBUserEscape(Element elem) {
		if (elem.getAttribute("end").length() > 0) {
			String id = getLastChildTCId(elem);
			elem.setAttribute("end", "DTBuserEscape;" + id + ".end");
		}
	}
	
	
	/**
	 * Returns <code>true</code> if <code>se</code> represents
	 * an element supposed to be linked together with its referring
	 * element, such as note-noteref, annotation-annoref.
	 * @param se the start element to check
	 * @return <code>true</code> if <code>se</code> represents
	 * an element supposed to be linked together with its referring
	 * element, such as note (-noteref) or annotation (-annoref), 
	 * <code>false</code> otherwise.
	 */
	private boolean isForceLink(StartElement se) {
		return forceLink.contains(se.getName().getLocalPart());
	}
	
	
	/**
	 * Writes the <code>XMLEvent e</code> to the output file.
	 * @param e the current element.
	 * @throws XMLStreamException
	 */
	private void writeEvent(XMLEvent e) throws XMLStreamException {
		writer.add(e);
	}
	
	 /* Returns the c-number, found in a <code>&lt;meta name="dc:Identifier" content="c-number" /&gt;</code> tag,
	 * or <code>null</code> if no such element is found. 
	 */
	/**
	 * Returns the <code>dtb:uid</code> <code>meta</code>-attribute from the 
	 * input document.
	 * @param reader a reader for the input document.
	 * @return the <code>dtb:uid</code> <code>meta</code>-attribute from the 
	 * input document.
	 * @throws XMLStreamException
	 */
	private String getUid(BookmarkedXMLEventReader reader) throws XMLStreamException {
		String bookmark = "uid-bokmärket";
		reader.setBookmark(bookmark);
		
		String uid = null;
		XMLEvent event = null;
		StartElement se = null;
		while (reader.hasNext()) {
			event = reader.nextEvent();
			if (!event.isStartElement()) {
				continue;
			} else {
				se = event.asStartElement();
			}
			
			Attribute at = se.getAttributeByName(new QName("name"));
			if (null == at) {
				continue;
			} else if (at.getValue().equals("dtb:uid")) {
				at = se.getAttributeByName(new QName("content"));
				if (at != null) {
					uid = at.getValue();
				}
				break;
			}			
		}
		
		reader.gotoAndRemoveBookmark(bookmark);
		return uid;
	}
	
	/**
	 * Opens a new <code>par</code>-element in the currently constructed smil-dom.
	 * Returns the substitute <code>XMLEvent</code> that is to be written to the 
	 * output file instead of the input <code>StartElement</code>. The substitute
	 * element will have a smilref attribute added, and the smil:clipBegin etc removed.
	 * @param se the element representing a spoken phrase.
	 * @return the substitute <code>XMLEvent</code> that is to be written to the 
	 * output file.
	 */
	private XMLEvent newPar(StartElement se) {
		String currentSmilClipBegin = null;
		String currentSmilClipEnd = null;
		String currentSmilClipSrc = null;
		String dtbId = null;
		String idRef = null;
		
		try {
			Map tmp = this.getSmilContext(se);
			currentSmilClipBegin = (String) tmp.get(smilClipBegin);
			currentSmilClipEnd = (String) tmp.get(smilClipEnd);
			currentSmilClipSrc = (String) tmp.get(smilSrc);
			dtbId = (String) tmp.get("id");
			idRef = (String) tmp.get("idref");
		} catch (XMLStreamException e) {
			for (Iterator it = se.getAttributes(); it.hasNext(); ) {
				Attribute a = (Attribute) it.next();
				if (a.getName().getLocalPart().equals(smilClipBegin)) {
					currentSmilClipBegin = a.getValue();
				} else if (a.getName().getLocalPart().equals(smilClipEnd)) {
					currentSmilClipEnd = a.getValue();
				} else if (a.getName().getLocalPart().equals(smilSrc)) {
					currentSmilClipSrc = a.getValue();
				} else if (a.getName().getLocalPart().equals("id")) {
					dtbId = a.getValue();
				} else if (a.getName().getLocalPart().equals("idref")) {
					idRef = a.getValue();
				}
			}
		}
		
		
		
		// Create a new par-element, add it to the seq on the stack.
		Element parentSeqElement = (Element) openSeqs.peek();
		Element parElement = parentSeqElement.getOwnerDocument().createElement("par");
		
		String parId = getNextSmilId("tcp");
		if (null == dtbId) {
			dtbId = getNextDTBId();
		}
		
		if (isSkippable(se)) {
			makeSkippable(parElement, se);
		}
		
		parElement.setAttribute("id", parId);
		// noteref -> note, annoref -> annotation
		if (null != idRef) {
			idRef = idRef.replaceAll("#", "");
			parElement.setAttribute("idref", idRef);
			forceLinkMap.put(idRef, parElement);
		}
		parentSeqElement.appendChild(parElement);
		
		// create the right <text> element and a ref to the dtbook
		Element textElement = parentSeqElement.getOwnerDocument().createElement("text");
		textElement.setAttribute("src", finalDTBookFilename + "#" + dtbId);
		parElement.appendChild(textElement);
		
		
		// .. and the corresponding <audio>
		Element audioElement = parentSeqElement.getOwnerDocument().createElement("audio");
		audioElement.setAttribute(smilClipBegin, currentSmilClipBegin);
		audioElement.setAttribute(smilClipEnd, currentSmilClipEnd);
		audioElement.setAttribute(smilSrc, currentSmilClipSrc);
		parElement.appendChild(audioElement);
		
		//--------------------------------------------------------------------
		// Create a new StartElement to return. That element will be
		// written to file instead of the one we received as parameter
		
		Collection namespaces = new HashSet();
		Collection attributes = new HashSet();
		
		// smilref-attr & id-attr
		attributes.add(eventFactory.createAttribute(new QName("smilref"), getCurrentSmilFilename() + "#" + parId));
		attributes.add(eventFactory.createAttribute(new QName("id"), dtbId));
			
		for (Iterator it = se.getAttributes(); it.hasNext(); ) {
			attributes.add(it.next());
		}

		for (Iterator ns = se.getNamespaces(); ns.hasNext(); ) {
			namespaces.add(ns.next());
		}
		
		StartElement newStartElement = eventFactory.createStartElement(se.getName(), attributes.iterator(), namespaces.iterator());
		return newStartElement;
	}
	
	
	/**
	 * Returns the name of the currently constructed smil file.
	 * @return the name of the currently constructed smil file.
	 */
	private String getCurrentSmilFilename() {
		return currentAudioFilename.substring(0, currentAudioFilename.indexOf('.')) + ".smil";
	}

	/**
	 * Opens a seq in the current smil dom. The new seq will be a child
	 * of the last opened, but not yet closed, seq.
	 * @param reader a reader to the input file.
	 * @param se the start element of the structure the seq will represent.
	 * @return the substitute <code>XMLEvent</code> that is to be written to the 
	 * output file instead of the one we received as parameter.
	 * @throws XMLStreamException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private StartElement newSeq(BookmarkedXMLEventReader reader, StartElement se) throws XMLStreamException, ParserConfigurationException, SAXException, IOException {		
		String tcsId = getNextSmilId("tcs");
		Attribute dtbIdAtt = se.getAttributeByName(new QName("id")); 
		String dtbId = null == dtbIdAtt ? this.getNextDTBId() : dtbIdAtt.getValue();
		
		Element parentSeqElement = (Element) openSeqs.peek();
		Element newSeq = parentSeqElement.getOwnerDocument().createElement("seq");
		newSeq.setAttribute("class", se.getName().getLocalPart());
		newSeq.setAttribute("id", tcsId);
		
		if (isForceLink(se)) {
			Attribute at = se.getAttributeByName(new QName("id"));
			if (at != null) {
				newSeq.setAttribute(tempLinkId, at.getValue());
			}
		}
		
		// skippable structure?
		if (isSkippable(se)) {
			makeSkippable(newSeq, se);
		}
		
		// attach the newly created seq element to the tree
		parentSeqElement.appendChild(newSeq);
		
		// make the seq available to others
		openSeqs.push(newSeq);
		
		// Create what's supposed to be in the dtbook:		
		// attributes:
		if (scopeContainsSmil(reader, se)) {
			Set attributes = new HashSet();
			attributes.add(eventFactory.createAttribute(new QName("smilref"), getCurrentSmilFilename() + "#" + tcsId));
			attributes.add(eventFactory.createAttribute(new QName("id"), dtbId));
			for (Iterator it = se.getAttributes(); it.hasNext(); ) {
				attributes.add(it.next());
			}
			return eventFactory.createStartElement(se.getName(), attributes.iterator(), se.getNamespaces());
		} else {
			return se;
		}
	}
	
	/**
	 * Closes the current seq-element.
	 * @param escapable Are we supposed to calculate DTBuserEscape?
	 */
	private void closeSeq(boolean escapable) {
		Element seq = (Element) openSeqs.pop();
		if (seq.getChildNodes().getLength() == 0) {
			seq.getParentNode().removeChild(seq);
		} else if (escapable) {
			Element lastChild = (Element) seq.getLastChild();
			seq.setAttribute("end", "DTBuserEscape;" + lastChild.getAttribute("id") + ".end");
		}
	}
	
	
	/**
	 * Closes the current seq-element, calculates DTBuserEscape.
	 * This call is the same as <code>closeSeq(true)</code>.
	 */
	private void closeSeq() {
		closeSeq(true);
	}
	
	/**
	 * Finish a smil dom.
	 * @param elapsedMillis the time elapsed before this smil file is used, i e. the sum
	 * of the time in all earlier smil files in this book as milliseconds.
	 * @param smilDom the smil DOM to operate on.
	 * @return the time elapsed so far, including this file, as milliseconds.
	 */
	private long finishSmil(long elapsedMillis, Document smilDom) {
		//--------------------------------------------------------------------------------------------------
		// which customTest are made?
		//
		NodeList customTestNodes = XPathUtils.selectNodes(smilDom.getDocumentElement(), "//*[@customTest]");
		Element customAttributes = 
			(Element) XPathUtils.selectSingleNode(smilDom.getDocumentElement(), "//customAttributes");
		
		if (customTestNodes.getLength() > 0) {	
			Set customTestNames = new HashSet();
			for (int i = 0; i < customTestNodes.getLength(); i++) {
				Element elem = (Element) customTestNodes.item(i);
				customTestNames.add(elem.getAttribute("customTest"));
			}
		
			for (Iterator it = customTestNames.iterator(); it.hasNext(); ) {
				Element customTest = smilDom.createElement("customTest");
				customTest.setAttribute("id", (String) it.next());
				customTest.setAttribute("override", "visible");
				customAttributes.appendChild(customTest);
			}
		} else {
			customAttributes.getParentNode().removeChild(customAttributes);
		}
		
		//---------------------------------------------------------------------------------------------------
		// what is the duration?
		//
		NodeList timeContainers = XPathUtils.selectNodes(smilDom.getDocumentElement(), "//audio");
		long currentDuration = 0;
		for (int i = 0; i < timeContainers.getLength(); i++) {
			Element tc = (Element) timeContainers.item(i);
			NamedNodeMap nnm = tc.getAttributes();
			currentDuration -= new SmilClock(nnm.getNamedItem("clipBegin").getNodeValue()).millisecondsValue();
			currentDuration += new SmilClock(nnm.getNamedItem("clipEnd").getNodeValue()).millisecondsValue();
		}
		
		// put the sum of the time in the first seq, attribute "dur"
		String elapsedTime = new SmilClock(currentDuration).toString(SmilClock.FULL);
		Element duration = (Element) XPathUtils.selectSingleNode(smilDom.getDocumentElement(), "//*[@dur]");
		duration.setAttribute("dur", elapsedTime);
		
		// add the time with the time value found in 'dtb:totalElapsedTime', this sum will be returned.
		Element elapsed = (Element) XPathUtils.selectSingleNode(smilDom.getDocumentElement(), "//meta[@name='dtb:totalElapsedTime']");
		SmilClock smilClockElapsed = new SmilClock(elapsedMillis);
		elapsed.setAttribute("content", smilClockElapsed.toString(SmilClock.FULL));
		
		totalTime += currentDuration;
		return elapsedMillis + currentDuration;
	}
		
	/**
	 * Opens a new smil DOM by reading a template file.. If there is an open smil DOM already, it is placed
	 * on hold until it's time to (possibly) rearrange the notes, etc.
	 * @param reader a reader pointed to the input document.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerRunException
	 * @throws TransformerException
	 * @throws XMLStreamException
	 */
	private void startNewSmil(BookmarkedXMLEventReader reader) throws ParserConfigurationException, SAXException, IOException, TransformerRunException, TransformerException, XMLStreamException {
		// reporting to UI
		obs.reportProgress(((double) smilFiles.size() / numSmilFiles) * (1 - noteRumbleTimeProportion - domFinalizeTimeProportion));
		// have we been aborted?
		checkAbortCallBack.checkTransformerAborted();
		
		if (null == currentAudioFilename) {
			String src = getNextSmilSrc(reader);
			setCurrentAudioFile(src);
		}
		
		// create a new smil DOM by reading the template
		currentSmil = documentBuilder.parse(smilTemplateFile);
		Element metaUid = (Element) XPathUtils.selectSingleNode(currentSmil.getDocumentElement(), "//meta[@name='dtb:uid']");
		metaUid.setAttribute("content", uid);
		
		// put the first seq on the stack, which will be empty
		Element rootSeq = (Element) XPathUtils.selectSingleNode(currentSmil.getDocumentElement(), "//*[@id='mseq']");
		openSeqs.clear();
		openSeqs.push(rootSeq);
		
		// store the DOM and its future filename, not yet on disk, though.
		smilTrees.add(currentSmil);
		smilFiles.add(getSmilFile());
		generatedFiles.add(getSmilFile().getName());
	}

	
	/**
	 * Returns the current smil-file.
	 * @return the current smil-file.
	 */
	private File getSmilFile() {
		File currentSmilFile = new File(outputDirectory, getCurrentSmilFilename());
		return currentSmilFile;
	}
	
	
	/**
	 * Returns the next smil src, i.e the name of the next audio file
	 * in the input document.
	 * @param reader a reader for the input document.
	 * @return the name of the next audio file, or <code>null</code>
	 * if no such exists.
	 * @throws XMLStreamException
	 */
	private String getNextSmilSrc(BookmarkedXMLEventReader reader, Vector levelChange) throws XMLStreamException {
		String bookmark = "getNextSmilSrc";
		reader.setBookmark(bookmark);
		String src = null;
		boolean marked = false;
		if (null == levelChange) {
			levelChange = new Vector();
		}
		levelChange.add(Boolean.FALSE);
		
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			if (!event.isStartElement()) {
				continue;
			}
			
			String localPart = event.asStartElement().getName().getLocalPart();
			// a heading?
			if (!marked && headings.contains(localPart)) {
				marked = true;
				levelChange.clear();
				levelChange.add(Boolean.TRUE);	
			}
			
			
			if (hasSmilAttributes(event.asStartElement())) {
				for (Iterator it = event.asStartElement().getAttributes(); it.hasNext(); ) {
					Attribute at = (Attribute) it.next();
					if (at.getName().getNamespaceURI().equals(this.smilNamespaceURI) && at.getName().getLocalPart().equals(smilSrc)) {
						src = at.getValue().trim();
						reader.gotoAndRemoveBookmark(bookmark);
						return src;
					}
				}
			}
		}
		
		reader.gotoAndRemoveBookmark(bookmark);
		return src;
	}
	
	private String getNextSmilSrc(BookmarkedXMLEventReader reader) throws XMLStreamException {
		return getNextSmilSrc(reader, null);
	}
	
	
	/**
	 * Returns <code>true</code> if the next sync point encountered in the input
	 * document is in a different level from the one last read (and hence should be 
	 * in a different smil file), <code>false</code> otherwise.
	 * If there is a change of audio file, the vaiable <code>currentAudioFilename</code>
	 * will be changed to the new filename.
	 * @param reader a for the input document
	 * @return <code>true</code> if the next sync point encountered in the input
	 * document is in a different level from the one last read (and hence should be 
	 * in a different smil file), <code>false</code> otherwise.
	 * @throws XMLStreamException
	 */
	private boolean nextParIsNewSmil(BookmarkedXMLEventReader reader) throws XMLStreamException {
		
		boolean isNewLevel = false;
		Vector levelChangeHolder = new Vector();
		String smilSrc = getNextSmilSrc(reader, levelChangeHolder);
		
		Boolean levelChange = (Boolean) levelChangeHolder.get(0);
		if (!levelChange.booleanValue()) {
			if (null != smilSrc) {
				otherEncounteredFiles.add(smilSrc);
			}
			isNewLevel = false;
		} else {		
			if (null != currentAudioFilename && null != smilSrc) {
				isNewLevel = !smilSrc.equals(currentAudioFilename);
			}
			
			if (null != smilSrc) {
				setCurrentAudioFile(smilSrc);
			}
		}
		return isNewLevel;
	}
	
	/**
	 * Outputs <code>document</code> to <code>location</code>.
	 * @param document the document to be save on file.
	 * @param location the file to store the document in.
	 * @throws TransformerException
	 * @throws IOException
	 */
	private void outputDocument(Document document, File location) throws TransformerException, IOException {
		TransformerFactory xformFactory = TransformerFactory.newInstance();  
		javax.xml.transform.Transformer idTransform = xformFactory.newTransformer();
		idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
		idTransform.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, smilDoctypePublic);
		idTransform.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, smilDoctypeSystem);
		Source input = new DOMSource(document);
		FileOutputStream fos = new FileOutputStream(location);
		Result output = new StreamResult(fos);
		idTransform.transform(input, output);
		fos.close();
		DEBUG("Filen \"" + location + "\" genererad.");
	}
	
	
	/**
	 * Sets the name of the current audio file.
	 * @param filename the new current file.
	 */
	private void setCurrentAudioFile(String filename) {
		if (null != filename) {
			otherEncounteredFiles.add(filename);
		}
		currentAudioFilename  = filename;
	}
	
	
	/**
	 * Returns <code>true</code> if <code>ee</code> represents
	 * a link, i.e "a", <code>false</code> otherwise.
	 * @param ee the end element
	 * @return <code>true</code> if <code>ee</code> represents
	 * a link, i.e "a", <code>false</code> otherwise.
	 */
	private boolean isLink(EndElement ee) {
		return ee.getName().getLocalPart().equals("a");
	}
	
	
	/**
	 * Returns <code>true</code> if <code>se</code> represents
	 * a link source, i.e "a" with a "href"-attribute, <code>false</code> otherwise.
	 * @param se
	 * @return <code>true</code> if <code>se</code> represents
	 * a link source, i.e "a" with a "href"-attribute, <code>false</code> otherwise.
	 */
	private boolean isLinkSource(StartElement se) {
		if (se.getName().getLocalPart().equals("a")) {
			return se.getAttributeByName(new QName("href")) != null;
		}
		return false;
	}
	
	/**
	 * Returns <code>true</code> if <code>se</code> represents
	 * a link target, i.e "a" without "href"-attribute, <code>false</code> otherwise.
	 * @param se
	 * @return <code>true</code> if <code>se</code> represents
	 * a link target, i.e "a" without "href"-attribute, <code>false</code> otherwise.
	 */
	private boolean isLinkTarget(StartElement se) {
		if (se.getName().getLocalPart().equals("a")) {
			return se.getAttributeByName(new QName("href")) == null;
		}
		return false;
	}
	
	
	/**
	 * Inserts a link source element into the smil DOM.
	 * @param link the link source element from the input document.
	 */
	private void handleLinkSource(StartElement link) {
		Attribute at = link.getAttributeByName(new QName("href"));  
		String href = at.getValue();
		while (href.startsWith("#")) {
			href = href.substring(1);
		}
		
		Element parentSeqElement = (Element) openSeqs.peek();
		Element linkElement = parentSeqElement.getOwnerDocument().createElement("a"); 
		parentSeqElement.appendChild(linkElement);
		openSeqs.push(linkElement);
		
		linkElement.setAttribute("href", href);
	}
	
	
	/**
	 * Inserts a link target element into the smil DOM.
	 * @param se the link target element from the input document.
	 */
	private void handleLinkTarget(StartElement se) {
		String linkTargetId;
		Attribute at = se.getAttributeByName(new QName("id"));
		if (at != null) {
			linkTargetId = at.getValue();
		} else {
			linkTargetId = getNextSmilId("tcs");
		}
		
		Element parentSeq = (Element) openSeqs.peek();
		Element linkTarget = parentSeq.getOwnerDocument().createElement("seq");
		linkTarget.setAttribute("class", "a");
		parentSeq.appendChild(linkTarget);
		openSeqs.push(linkTarget);
		linkTarget.setAttribute("id", linkTargetId);
		smilFileMapping.put(linkTargetId, getCurrentSmilFilename());
	}
	
	
	/**
	 * Returns <code>true</code> if <code>e</code> have be 
	 * configured as escapable, <code>false</code> otherwise.
	 * @param e the <code>XMLEvent</code>.
	 * @return <code>true</code> if <code>e</code> have be 
	 * configured as escapable, <code>false</code> otherwise.
	 */
	private boolean isEscapable(XMLEvent e) {
		String tagName = "";
		if (e.isEndElement()) {
			tagName = e.asEndElement().getName().getLocalPart();
		} else if (e.isStartElement()) {
			tagName = e.asStartElement().getName().getLocalPart();
		}
		
		return ecapable.contains(tagName);
	}
	
	
	/**
	 * Returns <code>true</code> if <code>se</code> have be 
	 * configured as skippable, <code>false</code> otherwise.
	 * @param se the <code>StartElement</code>.
	 * @return <code>true</code> if <code>se</code> have be 
	 * configured as skippable, <code>false</code> otherwise.
	 */
	private boolean isSkippable(StartElement se) {
		for (Iterator it = se.getAttributes(); it.hasNext(); ) {
			Attribute at = (Attribute) it.next();
			QName name = at.getName();
			if (name.getLocalPart().equals("render") && at.getValue().equals("required")) {
				return false;
			}
		}
		return skippable.contains(se.getName().getLocalPart());
	}
	
	
	/**
	 * Makes the representation of <code>se</code> in the
	 * smil DOM, <code>e</code>, skippable by adding a 
	 * custom test to it.
	 * @param e the smil DOM element.
	 * @param se the StartElement from the input document.
	 */
	private void makeSkippable(Element e, StartElement se) {
		String elemName = se.getName().getLocalPart();
		e.setAttribute("customTest", elemName);
		currentlySkipped.add(elemName);
		totalSkipped.add(elemName);
	}
	
	
	/**
	 * Returns the next generated id using a counter, more formally:
	 * <code>prefix + (++smilId);</code> where smilId is the counter.
	 * @param prefix the id prefix
	 * @return the next generated id using a counter.
	 */
	private String getNextSmilId(String prefix) {
		return prefix + (++smilId);
	}
	
	
	
	/**
	 * Returns the next generated id on the form "dtbx" using a counter
	 * to represent x.
	 * @return the next generated id on the form "dtbx" using a counter
	 * to represent x.
	 */
	private String getNextDTBId() {
		return "dtb" + (++dtbid);
	}
	
	
	/**
	 * Returns <code>true</code> if <code>se</code> should
	 * represent a par element, i.e if <code>se</code> has
	 * smil attributes, <code>false</code> otherwise.
	 * @param se the start element.
	 * @return <code>true</code> if <code>se</code> should
	 * represent a par element, i.e if <code>se</code> has
	 * smil attributes, <code>false</code> otherwise.
	 */
	private boolean isPar(StartElement se) {
		return hasSmilAttributes(se);
	}
	
	
	/**
	 * Returns <code>true</code> if and only if <code>e</code>
	 * has attributes from the smil namespace with the namespace URI "http://www.w3.org/2001/SMIL20/",
	 * <code>false</code> otherwise.
	 * 
	 * @param se The element to be examined.
	 * @return <code>true</code> if and only if <code>e</code>
	 * has attributes from the smil namespace with the namespace URI "http://www.w3.org/2001/SMIL20/",
	 * <code>false</code> otherwise.
	 */
	private boolean hasSmilAttributes(StartElement se) {
		for (Iterator it = se.asStartElement().getAttributes(); it.hasNext(); ) {
			Attribute at = (Attribute) it.next();
			if (at.getName().getNamespaceURI().equals(smilNamespaceURI )) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Returns <code>true</code> if the current xml scope, where <code>se</code> is the 
	 * top start element, contains smil attributes, <code>false</code> otherwise.
	 * @param reader a reader for the input document
	 * @param se the start element of the scope.
	 * @return <code>true</code> if the current xml scope, where <code>se</code> is the 
	 * top start element, contains smil attributes, <code>false</code> otherwise.
	 * @throws XMLStreamException
	 */
	private boolean scopeContainsSmil(BookmarkedXMLEventReader reader, StartElement se) throws XMLStreamException {
		if (hasSmilAttributes(se)) {
			return true;
		}
		
		String bookmark = "SmilMaker.scopeContainsSmil";
		boolean containsSmil = false;
		reader.setBookmark(bookmark);
		int elemCount = 1;
		while (reader.hasNext() && elemCount > 0) {
			XMLEvent e = reader.nextEvent();
			if (e.isStartElement()) {
				elemCount++;
				
				if (hasSmilAttributes(e.asStartElement())) {
					containsSmil = true;
					break;
				}
				
			} else if (e.isEndElement()) {
				elemCount--;
			}
		}
		
		reader.gotoAndRemoveBookmark(bookmark);
		return containsSmil;
	}
	
	
	/**
	 * Prints debug messages on System.out iff the system property 
	 * represented by <tt>SmilMaker.DEBUG_PROPERTY</tt> is defined.
	 * Debug messages are prefixed with "<tt>DEBUG: </tt>".
	 * @param msg the message.
	 */
	private void DEBUG(String msg) {
		if (System.getProperty(DEBUG_PROPERTY) != null) {
			System.out.println("DEBUG: " + msg);
		}
	}
	
	
	/**
	 * Prints debug messages on System.err if <code>DEBUG == true</code>
	 * @param e the <code>XMLEvent</code> to output.
	 */
	private void DEBUG(XMLEvent e) {
		if (e.isStartElement()) {
			DEBUG(e.asStartElement().getName().getLocalPart());
			return;
		}
		
		if (e.isEndElement()) {
			DEBUG(e.asEndElement().getName().getLocalPart());
			return;
		}
	}
	

	
	/**
	 * Returns the total time for all smils as a string in smilClock format.
	 * @return the total time for all smils as a string in smilClock format.
	 */
	public String getStrTotoalTime() {
		return new SmilClock(totalTime).toString(SmilClock.FULL);
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	
	/**
	 * Returns a <code>Set</code> containing .. objects representing the
	 * encountered files in the input document, typically audio files.
	 */
	public Set getAdditionalFiles() {
		return otherEncounteredFiles;
	}

	
	/**
	 * Sets the name of the final dtbook document, used for making links etc.
	 * @param finalDTBookFilename the name of the future dtbook file.
	 */
	public void setFinalDTBookFilename(String finalDTBookFilename) {
		this.finalDTBookFilename = finalDTBookFilename;
	}


	public void abortEvent() {
		try {
			this.closeStreams();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}




	/*
	 * (non-Javadoc)
	 * @see org.daisy.dmfc.core.event.BusListener#received(java.util.EventObject)
	 */
	public void received(EventObject event) {
		/*
		 * mg20070327:
		 * we are registered to listen to UserAbortEvent
		 */ 
		
		if(event instanceof UserAbortEvent) {
			abortEvent();
		}		
	}
	
}
