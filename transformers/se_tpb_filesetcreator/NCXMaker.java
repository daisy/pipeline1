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
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.collection.MultiHashMap;
import org.daisy.util.execution.AbortListener;
import org.daisy.util.execution.ProgressObserver;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.stax.BookmarkedXMLEventReader;
import org.daisy.util.xml.stax.ContextStack;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * A class constructing the NCX file of a z39.86 fileset, given appropriate
 * input, such as the output of SmilMaker.
 * 
 * @author Martin Blomberg
 *
 */
public class NCXMaker implements AbortListener {

	public static String DEBUG_PROPERTY = "org.daisy.debug";	// the system property used to determine if we're in debug mode or not
	
	private Set navListHeadings;							// heading element names
	private Set levels;										// level element names
	private Set customNavList;								// names of elements which should be given a cusom navlist
	private Map usedCustomNavLists = new HashMap();			// names of elements with a populated custom navlist
	private Set customTests;								// names of the elements with custom test

	private HashMap smilPlayorder = new HashMap();			// a mapping smilref->playorder
	private Map dcElements = new MultiHashMap(false); 		// mapping dc:elementname->(collection of values)
	
	private Document ncxTemplate;							// the ncx template file
	private Stack openLevels = new Stack();					// stack keeping track of the open levels, add new ones to the one on top.
	private ContextStack contextStack = new ContextStack();	// keeping track of xml content. Mainly to recognize level/hd instead of sidebar/hd
	
	private BookmarkedXMLEventReader reader;				// a reader pointed to the input document - a modified dtbook
	private XMLEventFactory eventFactory;					// creates stax events for the otuput of the dtbook document
	private XMLEventWriter writer;							// writes the dtbook document to file
	private File dtbookOutputFile;							// the output location of the dtbook
	private File ncxOutputFile;								// the output location of the ncx file
	
	private int playorder;									// the playorder counter
	private int ncxId;										// ncx id making use of a simple counter
	private int depth;										// keeps track of the deepest (xml-wise) structure in this book
	private int pageCount;									// the number of pages
	private String strPageMax = "0";						// the value of the greatest page number seen so far
	private String uid;										// the dtbook uid
	private Map bookStructs;								// mapping between element names and book structs.
	
	private String smilClipBegin = "clipBegin";				// smil attribute
	private String smilClipEnd = "clipEnd";					// smil attribute
	private String smilSrc = "src";							// smil attribute
	private String smilRef = "smilref";						// dtbook smil reference
	
	private String dtbookVersion;							// the dtbook version, i.e. 2005-1 or 2005-2.
	private String dtbookDoctypeStr_2005_1 = 				// dtbook 2005-1 version doctype
		"<!DOCTYPE dtbook PUBLIC \"-//NISO//DTD dtbook 2005-1//EN\" \"http://www.daisy.org/z3986/2005/dtbook-2005-1.dtd\">";
	private String dtbookDoctypeStr_2005_2 = 				// dtbook 2005-2 version doctype
		"<!DOCTYPE dtbook PUBLIC \"-//NISO//DTD dtbook 2005-2//EN\" \"http://www.daisy.org/z3986/2005/dtbook-2005-2.dtd\">";
	private String dtbookNamespaceURI = "http://www.daisy.org/z3986/2005/dtbook/";		// dtbook namespace
	private String smilNamespaceURI = "http://www.w3.org/2001/SMIL20/";					// smil namespace
	private String ncxDoctypePublic = "-//NISO//DTD ncx 2005-1//EN";					// ncx doctype public
	private String ncxDoctypeSystem = "http://www.daisy.org/z3986/2005/ncx-2005-1.dtd";	// ncx doctype system

	private int numElements;								// number of elements to process, used for detailed progress reports
	private ProgressObserver progressObserver;				// a component to report progress to
	private FileSetCreator checkAbortCallBack;				// FileSetCreator, lets one know if user has aborted the run
	
	
	/**
	 * @param inputFile the input document.
	 * @param levels names of the level elements.
	 * @param navListHeadings names of the heading elements.
	 * @param customNavList names of the custom navlist elements.
	 * @param dtbookOutputFile output file.
	 * @param ncxTemplateFile ncx-file template.
	 * @param obs a progress observer to report progress to.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public NCXMaker(
			File inputFile, 
			Set levels,
			Set navListHeadings,
			Set customNavList,
			File dtbookOutputFile, 
			File ncxTemplateFile,
			ProgressObserver obs,
			FileSetCreator checkAbortCallBack) throws ParserConfigurationException, SAXException, IOException, XMLStreamException {
				
		
		eventFactory = XMLEventFactory.newInstance();
		
		this.navListHeadings = navListHeadings;
		this.dtbookOutputFile = dtbookOutputFile;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
		documentBuilder.setEntityResolver(CatalogEntityResolver.getInstance());
		
		ncxTemplate = documentBuilder.parse(ncxTemplateFile);		
		
		this.levels = levels;
		this.customNavList = customNavList;	
		
		prepareCustomNavLists();
		
		progressObserver = obs;
		this.checkAbortCallBack = checkAbortCallBack;
		
		reader = getBookmarkedXMLEventReader(inputFile);
		numElements = countElements(reader);
		reader.close();
		reader = getBookmarkedXMLEventReader(inputFile);
		
		// get the dtbook version (-1/-2) to be able to output the correct doctype.
		dtbookVersion = checkAbortCallBack.getDTBookVersion(inputFile);
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
	 * Returns an XMLEventWriter pointed to the file <tt>outputFile</tt>. The 
	 * writer has the property <tt>XMLOutputFactory.IS_REPAIRING_NAMESPACES</tt>
	 * set to the value <tt>Boolean.FALSE</tt>.
	 * 
	 * @param outputFile the file to which content will be written.
	 * @param encoding the preferred output encoding.
	 * @return an XMLEventWriter pointed to the file <tt>outputFile</tt>.
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	private XMLEventWriter getXMLEventWriter(File outputFile, String encoding) throws FileNotFoundException, XMLStreamException {
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.FALSE);
		XMLEventWriter xew = outputFactory.createXMLEventWriter(new FileOutputStream(outputFile), encoding);
		return xew;
	}
	
	
	/**
	 * Returns the number of <code>XMLEvents</code> raised reading the complete file.
	 * @param reader a reader to read from.
	 * @return the number of <code>XMLEvents</code> raised reading the complete file.
	 * @throws XMLStreamException
	 */
	private int countElements(BookmarkedXMLEventReader reader) throws XMLStreamException {
		int elemCounter = 0;
		while (reader.hasNext()) {
			reader.nextEvent();
			elemCounter++;
		}
		return elemCounter;
	}
	
	
	/**
	 * Creates <code>navList</code>s in the ncx for the elements pointed out as custom navlist elements.
	 * A list which is empty will be removed before the result is output to file.
	 */
	private void prepareCustomNavLists() {
		Element root = (Element) ncxTemplate.getDocumentElement();
		for (Iterator it = customNavList.iterator(); it.hasNext(); ) {
			String elemName = (String) it.next();
			Element listHead = ncxTemplate.createElement("navList");
			listHead.setAttribute("class", elemName);
			listHead.setAttribute("id", navListName(elemName));
			Element navLabel = ncxTemplate.createElement("navLabel");
			Element text = ncxTemplate.createElement("text");
			text.appendChild(ncxTemplate.createTextNode(elemName));
			navLabel.appendChild(text);
			listHead.appendChild(navLabel);
			root.appendChild(listHead);
		}
	}
	
	
	/**
	 * Returns a name for the element name <code>elemName</code> to be used as
	 * the name of the corresponding <code>navList</code>. That is to avoid 
	 * id collisions with possible <code>customAttributes</code>-elements.
	 *  
	 * @param elemName the element name for which to generate a navList-id for.
	 * @param recordName tells wether or not you are using the list this name
	 * corresponds to, or if you are just wondering what the name would be. This
	 * information is used when empty lists are removed from the document.
	 * @return a name for the element name <code>elemName</code> to be used as
	 * the name of the corresponding <code>navList</code>.
	 */
	private String navListName(String elemName, boolean recordName) {
		if (recordName) {
			usedCustomNavLists.put(elemName, new Object());
		}
		return elemName + "-navList";
	}
	
	
	/**
	 * Returns a name for the element name <code>elemName</code> to be used as
	 * the name of the corresponding <code>navList</code>. That is to avoid 
	 * id collisions with possible <code>customAttributes</code>-elements.
	 * This call is eqvivalent to <code>navListName(elemName, false)</code>.
	 * 
	 * @param elemName the element name for which to generate a navList-id for.
	 * @return a name for the element name <code>elemName</code> to be used as
	 * the name of the corresponding <code>navList</code>.
	 */
	private String navListName(String elemName) {
		return navListName(elemName, false);
	}
	
	
	/**
	 * The main loop. Reads the manuscript file and takes different actions depending on what element is read.
	 * 
	 * @throws XMLStreamException
	 * @throws TransformerRunException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 */
	public void makeNCX() throws XMLStreamException, TransformerRunException, ParserConfigurationException, SAXException, IOException, TransformerException {
		
		DEBUG(ncxTemplate);
		DEBUG("NCXMaker#makeNCX: docElem: " + ncxTemplate.getDocumentElement());
		Element navMap = (Element) XPathUtils.selectSingleNode(ncxTemplate.getDocumentElement(), "//navMap[@id='navMap']");
		DEBUG("NCXMaker#makeNCX: navMap:  " + navMap);
		DEBUG("NCXMaker#makeNCX: docElem: " + ncxTemplate.getDocumentElement());
		openLevels.push(navMap);
		recordMaxDepth();
		int elemCounter = 0;
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			contextStack.addEvent(event);
			
			if (event.isStartElement()) {
				
				/* <meta name="dc:Language".../> is required in TPB's books, no need to do this.
				if (isDTBookRoot(event.asStartElement())) {
					handleBookRoot(event.asStartElement());
				}
				*/
				
				if (isFrontMatter(event.asStartElement())) {
					handleFrontMatter(reader, event.asStartElement());
				}
				
				if (isHead(event.asStartElement())) {
					handleHead(reader, event.asStartElement());
				}
				
				
				if (isPageNum(event.asStartElement())) {
					handlePageNum(reader, event.asStartElement());
				}
				
				if (isCustomNavListElement(event.asStartElement())) {
					handleCustomNavListElement(reader, event.asStartElement());
				}
				
				if (isLevelChange(event.asStartElement())) {
					handlePushLevel("navPoint");
				}
				
				if (isNavMapElement(event.asStartElement())) {
					handleNavMapElement(reader, event.asStartElement());
				}
				
			} else if (event.isEndElement()) {
				
				if (isLevelChange(event.asEndElement())) {
					openLevels.pop();
				}
				
			} else if (event.isStartDocument()) {
				StartDocument sd = (StartDocument)event;
				DEBUG("NCXMaker#makeNCX: outputFile: " + dtbookOutputFile);
				if (sd.encodingSet()) {
					writer = getXMLEventWriter(dtbookOutputFile, sd.getCharacterEncodingScheme());
					
				} else {
					writer = getXMLEventWriter(dtbookOutputFile, "utf-8");
					event = eventFactory.createStartDocument("utf-8", "1.0");             
				}
				writeEvent(event);
				if ("2005-2".equals(dtbookVersion)) {
					event = eventFactory.createDTD(dtbookDoctypeStr_2005_2);
				} else {
					event = eventFactory.createDTD(dtbookDoctypeStr_2005_1);
				}
				
				
			} else if (event.isProcessingInstruction()) {
				continue;
			}
			
			checkAbortCallBack.checkTransformerAborted();
			progressObserver.reportProgress((double) ++elemCounter / numElements);
			writeEvent(event);
		}
		
		// no author found? Remove the docauthor-template element.
		Node authorNode = XPathUtils.selectSingleNode(ncxTemplate.getDocumentElement(), "//*[@id='author']");
		if (authorNode != null) {
			authorNode.getParentNode().removeChild(authorNode);
		}
		
		// no title found? Retreat by using the dc:Title as an only child (text)
		Node titleNode = XPathUtils.selectSingleNode(ncxTemplate.getDocumentElement(), "//*[@id='title']");
		if (titleNode != null) {
			Element titleElement = 
				(Element) XPathUtils.selectSingleNode(ncxTemplate.getDocumentElement(), "//*[@id='title']");
			titleElement.removeAttribute("id");
			Element text = ncxTemplate.createElement("text");
			titleElement.appendChild(text);
			Collection titles = (Collection) dcElements.get("dc:Title");
			String strText = "";
			for (Iterator tit = titles.iterator(); tit.hasNext();) {
				strText += " " + tit.next();
			}
			text.appendChild(ncxTemplate.createTextNode(strText));
		}

		closeStreams();
		
		removeEmptyNavPoints();
		removeEmptyLists();
		makeNCXHead();
		
		printToFile();	
	}

	
	/* (non-Javadoc)
	 * @see org.daisy.util.execution.AbortListener#abortEvent()
	 */
	public void abortEvent() {
		try {
			closeStreams();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}		
	}
	
	
	/**
	 * Closes the streams.
	 * @throws XMLStreamException
	 */
	public void closeStreams() throws XMLStreamException {
		writer.flush();
		writer.close();
		reader.close();
	}
	
	
	/**
	 * Removes empty <code>navPoint</code>-elements, that is 
	 * navPoint-elements referring to levels that do not have
	 * a heading. 
	 */
	private void removeEmptyNavPoints() {
		NodeList emptyNodes = XPathUtils.selectNodes(ncxTemplate.getDocumentElement(), "//navPoint[@class='empty']");
		for (int i = 0; i < emptyNodes.getLength(); i++) {
			Vector navPointChildren = getNavPointChildren((Element) emptyNodes.item(i));
			Node reference = emptyNodes.item(i);
			for (Iterator it = navPointChildren.iterator(); it.hasNext(); ) {
				Node navPoint = (Node) it.next();
				reference.getParentNode().insertBefore(navPoint, reference);
			}
			reference.getParentNode().removeChild(reference);
		}
		
		// remove pageList if empty
		if (0 == pageCount) {
			Element pageList = (Element) XPathUtils.selectSingleNode(ncxTemplate.getDocumentElement(), "//pageList");
			pageList.getParentNode().removeChild(pageList);
		}
	}
	
	/**
	 * Returns the child <code>navPoint</code>-elements of the element
	 * <code>navPoint</code>.
	 * 
	 * @param navPoint a <code>navPoint</code>-element.
	 * @return the child <code>navPoint</code>-elements of the element
	 * <code>navPoint</code>.
	 * 
	 */
	private Vector getNavPointChildren(Element navPoint) {
		Vector children = new Vector();
		NodeList nodes = navPoint.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Element elem = (Element) nodes.item(i);
			if (elem.getTagName().equals("navPoint")) {
				children.add(elem);
			}
		}
		return children;
	}
	
	
	/**
	 * Removes all custom navLists that were requested in the
	 * cofiguration file but didn't come to use during this run.
	 *
	 */
	private void removeEmptyLists() {
		for (Iterator it = customNavList.iterator(); it.hasNext(); ) {
			String elemName = (String) it.next();
			if (usedCustomNavLists.get(elemName) == null) {
				Node customNavList =
					XPathUtils.selectSingleNode(ncxTemplate.getDocumentElement(), "//navList[@id='" + navListName(elemName, true) + "']");
				customNavList.getParentNode().removeChild(customNavList);
			}
		}
	}

	/**
	 * Constructs the head of the ncx-file.
	 */
	private void makeNCXHead() {
		Element meta;
		
		meta = (Element) XPathUtils.selectSingleNode(ncxTemplate.getDocumentElement(), "//meta[@name='dtb:totalPageCount']");
		meta.setAttribute("content", String.valueOf(pageCount));
		
		meta = (Element) XPathUtils.selectSingleNode(ncxTemplate.getDocumentElement(), "//meta[@name='dtb:maxPageNumber']");
		meta.setAttribute("content", strPageMax);
	
		meta = (Element) XPathUtils.selectSingleNode(ncxTemplate.getDocumentElement(), "//meta[@name='dtb:uid']");
		meta.setAttribute("content", getUid());

		meta = (Element) XPathUtils.selectSingleNode(ncxTemplate.getDocumentElement(), "//meta[@name='dtb:depth']");
		meta.setAttribute("content", String.valueOf(getDepth()));
	
		if (customTests != null) {
			Element head = 
				(Element) XPathUtils.selectSingleNode(ncxTemplate.getDocumentElement(), "//head");
			for (Iterator it = customTests.iterator(); it.hasNext(); ) {
				Element customTest = ncxTemplate.createElement("smilCustomTest");
				customTest.setAttribute("override", "visible");
				String elemName = (String) it.next();
				String bookStruct = (String) bookStructs.get(elemName);
				if (null != bookStruct) {
					customTest.setAttribute("bookStruct", bookStruct);
				}
				customTest.setAttribute("id", elemName);
				head.appendChild(customTest);
			}
		}
	}
	

	/**
	 * Returns <code>true</code> if <code>se</code> should be represented
	 * as a custom navlist, <code>false</code> otherwise.
	 * @param se the start element
	 * @return <code>true</code> if <code>se</code> should be represented
	 * as a custom navlist, <code>false</code> otherwise.
	 */
	private boolean isCustomNavListElement(StartElement se) {
		return customNavList.contains(se.getName().getLocalPart());
	}
	
	
	/**
	 * Appends an element to one of the custom navLists.
	 * 
	 * @param reader reads the manuscript document.
	 * @param se the start element of the structure that is supposed 
	 * to result in a custom navlist element.
	 * @throws XMLStreamException
	 */
	private void handleCustomNavListElement(BookmarkedXMLEventReader reader, StartElement se) throws XMLStreamException {
		String eventName = se.getName().getLocalPart();
		Element customElem = createNCXNode(reader, se, "navTarget", eventName);
		Element customList = (Element) XPathUtils.selectSingleNode(ncxTemplate.getDocumentElement(), "//navList[@id='" + navListName(eventName, true) + "']");
		if (customElem != null) {
			customList.appendChild(customElem);
		} else {
			DEBUG("NCXMaker#handleCustomNavListElement ATT: Check to see if there is a " + eventName + " without contents.");
		}
	}
	
	
	/**
	 * Returns a <code>Map</code> containing the the <code>smil</code>-context
	 * for this start element, that is the <code>smil</code>-attributes mapped
	 * as follows:
	 * <ol>
	 * <li>clipBegin -> value for clipBegin</li>
	 * <li>clipEnd -> value for clipEnd</li>
	 * <li>src -> value for src</li>
	 * <li>smilref -> value for smilref</li>
	 * </ol>
	 * or <code>null</code> if no such attributes exist.
	 * @param se
	 * @return a <code>Map</code> containing the smil-attributes of the
	 * start element <code>se</code> or null of no such attribute exist.
	 * @throws XMLStreamException
	 */
	private Map getSmilContext(StartElement se) throws XMLStreamException {
		return getSmilContext(se, null);
	}
	
	
	/**
	 * Populates the <code>Map attributes</code> with the <code>smil</code>-context
	 * for this start element, that is the <code>smil</code>-attributes mapped
	 * as follows:
	 * <ol>
	 * <li>clipBegin -> value for clipBegin</li>
	 * <li>clipEnd -> value for clipEnd</li>
	 * <li>src -> value for src</li>
	 * <li>smilref -> value for smilref</li>
	 * </ol>
	 * or <code>null</code> if no such attributes exist.
	 * If <code>attributes</code> is <code>null</code>, a new instance
	 * of a <code>Map</code> is created instead.
	 * @param se
	 * @return the <code>Map attributes</code> containing the smil-attributes of the
	 * start element <code>se</code> or null of no such attribute exist.
	 * @throws XMLStreamException
	 */
	private Map getSmilContext(StartElement se, Map attributes) throws XMLStreamException {
		DEBUG("NCXMaker#getSmilContext");
		if (!hasSmilAttributes(se)) {
			return null;
		}
		
		if (null == attributes) {
			attributes = new HashMap();
		}
		
		DEBUG(se);
		for (Iterator it = se.getAttributes(); it.hasNext(); ) {
			Attribute at = (Attribute) it.next();
			QName name = at.getName();
			if (name.getLocalPart().equals(smilClipBegin)) {
				attributes.put(smilClipBegin, at.getValue());
			} else if (name.getLocalPart().equals(smilClipEnd)) {
				attributes.put(smilClipEnd, at.getValue());
			} if (name.getLocalPart().equals(smilSrc)) {
				attributes.put(smilSrc, at.getValue());
			} if (name.getLocalPart().equals(smilRef)) {
				attributes.put(smilRef, at.getValue());
			}  
		}
		
		return attributes;
	}
	
	/**
	 * Reads a part of the docuement and returns the text content of
	 * of that part. The method reads as long as the number of end
	 * elements seen doesn't exceed the number of start elements seen.
	 * 
	 * Assume a start element has already been read when this method is called.
	 * The main loop of this method is:
	 * <code>
	 * int elemCount = 1; // since we have already seen the start element somewhere
		while (reader.hasNext() && elemCount > 0) {
			XMLEvent event = reader.nextEvent();
			
			if (event.isStartElement()) {
				elemCount++;
			}
			
			if (event.isEndElement()) {
				elemCount--;
			}
			
			if (event.getEventType() == XMLEvent.CHARACTERS) {
				content += event.asCharacters().getData();
			}
		}
	 * </code>
	 * 
	 * @param reader reads the manuscript file.
	 * @return the text content of this part of the document.
	 * @throws XMLStreamException
	 */
	private String getTextContent(BookmarkedXMLEventReader reader) throws XMLStreamException {
		String content = "";
		String bookmark = "TPB Narrator.NCXMaker.getTextContent";
		reader.setBookmark(bookmark);
		
		int elemCount = 1; // since we have already seen the start element somewhere
		while (reader.hasNext() && elemCount > 0) {
			XMLEvent event = reader.nextEvent();
			
			if (event.isStartElement()) {
				elemCount++;
			}
			
			if (event.isEndElement()) {
				elemCount--;
			}
			
			if (event.getEventType() == XMLEvent.CHARACTERS) {
				content += event.asCharacters().getData();
			}
		}
		
		reader.gotoAndRemoveBookmark(bookmark);
		return content;
	}
	
	
	/*
	 * Fyller Map:en attributes med smil-attributen
	 * Returnerar det textinnehåll som står inom elementet med smil-attribut.
	 * 
	 */
	/**
	 * Finds the next element with smil-attributes (if such exists). The <code>Map attributes</code>
	 * is populated such that the keys are "clipBegin", "clipEnd", "src" and "smilref" and the values
	 * are the corresponding attribute values from the modified (by the SmilMaker) manuscript file.
	 * The method returns the textual content of the examined element by using the method 
	 * <code>getTextContent(BookmarkedXMLEventReader)</code>.
	 * 
	 * @param reader reads the (modified) manuscript
	 * @param attributes a map where to put the attribute names and values.
	 * @return the text content of the element with smil attributes.
	 * @throws XMLStreamException
	 */
	private String getNextSmilContext(BookmarkedXMLEventReader reader, Map attributes) throws XMLStreamException {
		DEBUG("NCXMaker#getNextSmilContext(reader, Map)");
		String bookmark = "TPB Narrator.NCXMaker.getFirstSmilAttrs";
		reader.setBookmark(bookmark);
		
		String textContent = "";
		XMLEvent e = null;
		int elemCount = 1;
		while (elemCount > 0 && !hasSmilAttributes((e = reader.nextEvent()))) {			
			if (e.isStartElement()) {
				elemCount++;
			}
			
			if (e.isEndElement()) {
				elemCount--;
			}
		}
		
		if (elemCount == 0) {
			reader.gotoAndRemoveBookmark(bookmark);
			return null;
		}
		
		getSmilContext(e.asStartElement(), attributes);
		
		textContent = getTextContent(reader);
		reader.gotoAndRemoveBookmark(bookmark);
		return textContent;
	}
	
	
	/**
	 * Creates an "empty" navigation element. The element(s)
	 * has the structure:
	 * 
	 *  <pre>
<[navNodeName] class="empty">
	<navLabel>
		<text/>
		<audio/>
	</navLabel>
	<content/>
</[navNodeName]>	 
	 *  </pre>
	 *  where [navNodeName] is the supplied parameter <code>navNodeName</code>.
	 * @param navNodeName the name of the generated top-level element.
	 * @return the created navigation element.
	 */
	private Element createNCXNode(String navNodeName) {
		Document owner = ncxTemplate;
	
		Element navNode = owner.createElement(navNodeName);
		navNode.setAttribute("class", "empty");
		Element navLabel = owner.createElement("navLabel");
		Element text = owner.createElement("text");
		Element audio = owner.createElement("audio");
		Element content = owner.createElement("content");
		
		navNode.appendChild(navLabel);
		navNode.appendChild(content);
		navLabel.appendChild(text);
		navLabel.appendChild(audio);
		
		return navNode;
	}
	
	
	/**
	 * Returns an element two levels below the supplied <code>navNode</code>.
	 * 
	 * This is typically used as getting the <code>text</code> or <code>audio</code>-element from
	 * a <code>navPoint</code> or <code>navTarget</code>.
	 * 
	 * @param navNode a navigation element.
	 * @param tagName the name of the searched element.
	 * @return the element with the name <code>tagName</code>.
	 */
	private Element getNavNodeGrandChild(Element navNode, String tagName) {
		NodeList children = navNode.getChildNodes();
		
		for (int i = 0; i < children.getLength(); i++) {
			Element child = (Element) children.item(i);
			
			if (child.getTagName().equals("navLabel")) {
				NodeList grandChildren = child.getChildNodes();
				for (int j = 0; j < grandChildren.getLength(); j++) {
					Element grandChild = (Element) grandChildren.item(j);
					
					if (grandChild.getTagName().equals(tagName)) {
						return grandChild;
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the <code>text</code> element given a navigation element, i e 
	 * a <code>navPoint</code> or <code>navTarget</code>.
	 * @param navNode the navigation element.
	 * @return the <code>text</code> element.
	 */
	private Element getNavNodeText(Element navNode) {
		return getNavNodeGrandChild(navNode, "text");
	}

	
	/**
	 * Returns the <code>audio</code> element given a navigation element, i e 
	 * a <code>navPoint</code> or <code>navTarget</code>.
	 * @param navNode the navigation element.
	 * @return the <code>audio</code> element.
	 */
	private Element getNavNodeAudio(Element navNode) {
		return getNavNodeGrandChild(navNode, "audio");
	}
	
	
	/**
	 * Returns the <code>content</code> element given a navigation element, i e 
	 * a <code>navPoint</code> or <code>navTarget</code>.
	 * @param navNode the navigation element.
	 * @return the <code>content</code> element.
	 */
	private Element getNavNodeContent(Element navNode) {
		NodeList children = navNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Element child = (Element) children.item(i);
			if (child.getTagName().equals("content")) {
				return child;
			}
		}
		return null;
	}
	
	private Element createNCXNode(BookmarkedXMLEventReader reader, StartElement se, String navNodeName, String classAttribute) throws XMLStreamException {		
		DEBUG("NCXMaker#createNCXNode(reader, " + se.getName().getLocalPart() + ", " + navNodeName + ", class=" + classAttribute);
		DEBUG(se);
		String bookmark = "TPB Narrator.NCXMaker.createNCXNode";
		reader.setBookmark(bookmark);
			
		Map smilAttrs = new HashMap();
		String contentBuffer = "";
		if (hasSmilAttributes(se)) {
			smilAttrs = getSmilContext(se);
			contentBuffer = getTextContent(reader);
		} else {
			contentBuffer = getNextSmilContext(reader, smilAttrs);
		}
		
		if (null == contentBuffer || smilAttrs.size() == 0) {
			return null;
		}
			
		String currentSmilRef = (String) smilAttrs.get(smilRef);
		Document owner = ncxTemplate;
		Element navNode = owner.createElement(navNodeName);
		navNode.setAttribute("class", classAttribute);
		navNode.setAttribute("playOrder", getStrPlayorder(currentSmilRef));
		navNode.setAttribute("id", getNextId());
		
		Attribute type = null;
		for (Iterator it = se.getAttributes(); it.hasNext(); ) {
			Attribute at = (Attribute) it.next();
			if ("page".equals(at.getName().getLocalPart())) {
				type = at;
				break;
			}
		}
		if (type != null) {
			navNode.setAttribute("type", type.getValue());
		} else if (se.getName().getLocalPart().equals("pagenum")) {
			navNode.setAttribute("type", "normal");
		}
		
		Element navLabel = owner.createElement("navLabel");

		Element audio = owner.createElement("audio");
		audio.setAttribute(smilSrc, (String) smilAttrs.get(smilSrc));
		audio.setAttribute(smilClipBegin, (String) smilAttrs.get(smilClipBegin));
		audio.setAttribute(smilClipEnd, (String) smilAttrs.get(smilClipEnd));
		
		Element text = owner.createElement("text");
		Node textContent = owner.createTextNode(contentBuffer.toString());
		text.appendChild(textContent);
		
		Element content = owner.createElement("content");
		content.setAttribute("src", currentSmilRef);
		
		navLabel.appendChild(text);
		navLabel.appendChild(audio);
		navNode.appendChild(navLabel);
		navNode.appendChild(content);
		
		reader.gotoAndRemoveBookmark(bookmark);
		return navNode;
	}

	
	/**
	 * Removes attributes from a namespace with a URI other than the
	 * dtb URI or the empty string.
	 * 
	 * @param se the start element of concern.
	 * @return the start element without non dtb attributes. 
	 */
	private XMLEvent keepDTB(StartElement se) {
		Collection attributes = new HashSet();
		for (Iterator it = se.getAttributes(); it.hasNext(); ) {
			Attribute at = (Attribute) it.next();
			QName name = at.getName();
			String nsuri = name.getNamespaceURI();
			if (nsuri.equals(dtbookNamespaceURI) || nsuri.equals("") || name.getPrefix().equals("xml")) {
				attributes.add(at);
			}
		}
		
		Collection namespaces = new HashSet();
		for (Iterator it = se.getNamespaces(); it.hasNext(); ) {
			Namespace ns = (Namespace) it.next();
			
			if (ns.isDefaultNamespaceDeclaration()) {
				namespaces.add(ns);
			}
		}
		
		return eventFactory.createStartElement(se.getName(), attributes.iterator(), namespaces.iterator());
	}
	
	
	/**
	 * Prints the ncx DOM to the file <code>ncxOutputFile</code>.
	 * @throws TransformerException
	 * @throws IOException
	 */
	public void printToFile() throws TransformerException, IOException {
		/*
		String defaultFactory = "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";
		String propertyName = "javax.xml.parsers.DocumentBuilderFactory";
		System.err.println(propertyName + " -> " + System.getProperty(propertyName));
		if (true || null == System.getProperty(propertyName)) {
			System.err.println("Sätter om " + propertyName + " som var null till " + defaultFactory);
			System.setProperty(propertyName, defaultFactory);
		}
		System.err.println("properties:");
		System.err.println(System.getProperties());
		System.err.println("/properties");
		*/
		
		FileOutputStream fos = null;
		String currentFactory = null;
		String defaultFactory = "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";
		String propertyName = "javax.xml.parsers.DocumentBuilderFactory";
		
		try {
			currentFactory = System.getProperty(propertyName);
			System.setProperty(propertyName, defaultFactory);
			
			TransformerFactory xformFactory = TransformerFactory.newInstance();  
			Transformer idTransform = xformFactory.newTransformer();
			idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
			idTransform.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, ncxDoctypePublic);
			idTransform.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, ncxDoctypeSystem);
			
			Source input = new DOMSource(ncxTemplate);
			fos = new FileOutputStream(ncxOutputFile);
			Result output = new StreamResult(fos);
			idTransform.transform(input, output);
		} finally {
			if (currentFactory != null) {
				System.setProperty(propertyName, currentFactory);
			}
			
			if (fos != null) {
				fos.close();
			}
		}
		/*
		try {
			idTransform.transform(input, output);
		} catch (Throwable t) {
			System.err.println("getMessage(): " + t.getMessage());
			t.printStackTrace();
			boolean d = DEBUG;
			DEBUG = true;
			DEBUG(ncxTemplate.getDocumentElement());
			DEBUG = d;
		}
		*/
	}

	
	
	/**
	 * Records the max depth of the ncx level structure.
	 */
	private void recordMaxDepth() {
		depth = Math.max(depth, openLevels.size() - 1);
	}
	
	
	
	/**
	 * Returns the depth of the ncx level structure.
	 * @return the depth of the ncx level structure.
	 */
	private int getDepth() {
		return depth;
	}

	
	/**
	 * Writes <code>e</code> to the output file.
	 * @param e the <code>XMLEvent</code> to write.
	 * @throws XMLStreamException
	 */
	private void writeEvent(XMLEvent e) throws XMLStreamException {
		if (e.isStartElement()) {
			e = keepDTB(e.asStartElement());
		}
		writer.add(e);
	}
	
	
	/**
	 * Returns <code>true</code> if <code>se</code> represents
	 * a pagenum element, <code>false</code> otherwise.
	 * @param se the start element.
	 * @return <code>true</code> if <code>se</code> represents
	 * a pagenum element, <code>false</code> otherwise.
	 */
	private boolean isPageNum(StartElement se) {
		return se.getName().getLocalPart().equals("pagenum");
	}
	
	
	
	/**
	 * Creates a node for the pagenum element and adds it to the "pagelist" navlist.
	 * @param reader for the input document.
	 * @param se the pagenum element from the input document.
	 * @throws XMLStreamException
	 */
	private void handlePageNum(BookmarkedXMLEventReader reader, StartElement se) throws XMLStreamException {
		DEBUG("NCXMaker#handlePageNum: pagenum: " + se.getName().getLocalPart());
		Element pageTarget = createNCXNode(reader, se, "pageTarget", "pagenum");
		String pageNumber = ((Element) XPathUtils.selectSingleNode(pageTarget, "//text")).getTextContent();
		DEBUG("NCXMaker#handlePageNum: text content: " + pageNumber);
		try {
			int pNumber = Integer.parseInt(pageNumber);
			if (pNumber > 0) {
				pageTarget.setAttribute("value", pageNumber);
				strPageMax = pageNumber;
			}
		} catch (NumberFormatException nfe) {
			// nada, pageNumber wasn't numeric
		}
		
		Element pageList = (Element) XPathUtils.selectSingleNode(ncxTemplate.getDocumentElement(), "//pageList");
		pageList.appendChild(pageTarget);
		
		pageCount++;
	}
	
	
	/**
	 * Returns <code>true</code> if <code>se</code> is supposed be one of the
	 * level headings in the ncx navMap, <code>false</code> otherwise. 
	 * 
	 * Note that <code>hd</code> only is a valid heading
	 * as a child of <code>level</code>, i.e not in sidebars, tables etc. Those elements
	 * can be made customNavlists if desired.
	 * @param se the start element.
	 * @return Returns <code>true</code> if <code>se</code> is supposed be one of the
	 * level headings in the ncx navMap, <code>false</code> otherwise. 
	 */
	private boolean isNavMapElement(StartElement se) {
		if (se.getName().getLocalPart().equals("hd")) {
			return contextStack.getContextPath().endsWith("level/hd") && 
				navListHeadings.contains(se.getName().getLocalPart());
		}
		return navListHeadings.contains(se.getName().getLocalPart());
	}
	
	
	/**
	 * Creates a new navPoint element. Connects it to the ncx-DOM and
	 * pushes it on the stack <code>openLevels</code>.
	 * @param navNodeName typically "navPoint".
	 */
	private void handlePushLevel(String navNodeName) {
		Element navNode = createNCXNode(navNodeName);
		Element parent = (Element) openLevels.peek();
		parent.appendChild(navNode);
		openLevels.push(navNode);
		recordMaxDepth();
	}
	
	
	/**
	 * Returns <code>true</code> if <code>se</code> means
	 * a level change, <code>false</code> otherwise.
	 * @param se the start element.
	 * @return <code>true</code> if <code>se</code> means
	 * a level change, <code>false</code> otherwise.
	 */
	private boolean isLevelChange(StartElement se) {
		return levels.contains(se.getName().getLocalPart());
	}
	
	/**
	 * Returns <code>true</code> if <code>ee</code> means
	 * a level change, <code>false</code> otherwise.
	 * @param ee the end element.
	 * @return <code>true</code> if <code>ee</code> means
	 * a level change, <code>false</code> otherwise.
	 */
	private boolean isLevelChange(EndElement ee) {
		return levels.contains(ee.getName().getLocalPart());
	}
	
	/**
	 * Returns <code>true</code> if <code>se</code> is frontmatter,
	 * <code>false</code> otherwise.
	 * @param se the start element
	 * @return <code>true</code> if <code>se</code> is frontmatter,
	 * <code>false</code> otherwise.
	 */
	private boolean isFrontMatter(StartElement se) {	
		return se.getName().getLocalPart().equals("frontmatter");
	}
	
	/**
	 * Returns <code>true</code> if <code>se</code> is the head element,
	 * <code>false</code> otherwise.
	 * @param se the start element.
	 * @return <code>true</code> if <code>se</code> is the head element,
	 * <code>false</code> otherwise.
	 */
	private boolean isHead(StartElement se) {
		return se.getName().getLocalPart().equals("head");
	}

	/**
	 * Populates the latest navigation node with information, audio, text and content child elements, 
	 * playorder etc.
	 * @param reader a reader for the input document
	 * @param se the start element representing the start of a navigation node.
	 * @throws XMLStreamException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerRunException
	 */
	private void handleNavMapElement(BookmarkedXMLEventReader reader, StartElement se) throws XMLStreamException, ParserConfigurationException, SAXException, IOException, TransformerRunException {
		DEBUG("NCXMaker#handleNavMapElement: Creates a heading: (" + se.getName().getLocalPart() + ")");
		Element navNode = (Element) openLevels.peek();
		Map attrs = new HashMap();
		String textContent = getNextSmilContext(reader, attrs);
		
		navNode.setAttribute("class", se.getName().getLocalPart());
		navNode.setAttribute("id", getNextId());
		navNode.setAttribute("playOrder", getStrPlayorder((String) attrs.get(smilRef)));
		
		Element audio = getNavNodeAudio(navNode);
		audio.setAttribute(smilSrc, (String) attrs.get(smilSrc));
		audio.setAttribute(smilClipBegin, (String) attrs.get(smilClipBegin));
		audio.setAttribute(smilClipEnd, (String) attrs.get(smilClipEnd));
		
		Element text = getNavNodeText(navNode);
		text.appendChild(text.getOwnerDocument().createTextNode(textContent));
		
		Element content = getNavNodeContent(navNode);
		content.setAttribute("src", (String) attrs.get(smilRef));
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
	private boolean hasSmilAttributes(XMLEvent e) {
		if (!e.isStartElement() || e.asStartElement() == null) {
			return false;
		}
		
		for (Iterator it = e.asStartElement().getAttributes(); it.hasNext(); ) {
			Attribute at = (Attribute) it.next();
			if (at.getName().getNamespaceURI().equals(smilNamespaceURI)) {
				return true;
			}
		}
		return false;
	}
	
	
	
	/**
	 * Reads the dc:Element meta attributes from the head.
	 * @param reader a reader for the input document.
	 * @param se the start element for the head scope.
	 * @throws XMLStreamException
	 */
	private void handleHead(BookmarkedXMLEventReader reader, StartElement se) throws XMLStreamException {
		String bookmark = "TPB Narrator.NCXMaker.handleHead";
		reader.setBookmark(bookmark);
		
		int elemCount = 1;
		while (elemCount > 0) {
			XMLEvent event = reader.nextEvent();
			if (event.isEndElement()) { 
				elemCount--;
				continue;
			}
			
			if (!event.isStartElement()) {
				continue;
			} else {
				elemCount++;
			}
			
			// only start elements come here...
			String key = null;
			String value = null;
			
			for (Iterator it = event.asStartElement().getAttributes(); it.hasNext(); ) {
				Attribute at = (Attribute) it.next();
					
				if (at.getName().getLocalPart().equals("name") && at.getValue().startsWith("dc:")) {
					key = at.getValue();
				} else if (at.getName().getLocalPart().equals("content")) {
					value = at.getValue();
				}
			}
			
			if (key != null && value != null) {
				dcElements.put(key, value);
			} else {
				Attribute at = event.asStartElement().getAttributeByName(new QName("name"));
				if (at != null && at.getValue().equals("dtb:uid")) {
					at = event.asStartElement().getAttributeByName(new QName("content"));
					if (at != null) {
						uid = at.getValue();
					}
				}
			}
		}
		
		if (null == dcElements.get("dc:Identifiler")) {
			dcElements.put("dc:Identifier", uid);
		}
		reader.gotoAndRemoveBookmark(bookmark);
	}
	
	
	/**
	 * Takes care of (at the moment) docAuthor and docTitle. Those could be several
	 * synchronization points but will here be merged togehter in a way that is 
	 * ok for the ncx document.
	 * 
	 * The reader has just read "docauthor" or "doctitle". In this method, all text content from
	 * the current scope is collected and made into a text-element for the ncx DOM. All elements
	 * in the scope that has smil attributes collaborate to give the text-element a corresponding
	 * audio-element. That is, the first <code>clipBegin</code> and the last <code>clipEnd</code>.
	 * 
	 * The nodes become ancestors to <code>parent</code>.
	 * @param reader a reader for the input document.
	 * @param parent the parent of the generated node.
	 * @throws XMLStreamException
	 */
	private void handleDocMisc(BookmarkedXMLEventReader reader, Element parent) throws XMLStreamException {
		String bookmark = "handleTempNameBookmark";
		reader.setBookmark(bookmark);
		
		int elemCount = 1;
		String textContent = getTextContent(reader);
		Element textElement = parent.getOwnerDocument().createElement("text");
		textElement.appendChild(parent.getOwnerDocument().createTextNode(textContent));
		parent.appendChild(textElement);
		Map attrs = new HashMap();
		String begin = null;
		while (elemCount > 0) {
			XMLEvent event = reader.nextEvent();
			if (event.isEndElement()) {
				elemCount--;
			}
			
			if (!event.isStartElement()) {
				continue;
			}
			
			// start elements only:
			elemCount++;
			if (hasSmilAttributes(event.asStartElement())) {
				attrs.clear();
				getSmilContext(event.asStartElement(), attrs);
				attrs.remove("smilref");
				String tmp = (String) attrs.remove(smilClipBegin);
				if (null == begin) {
					begin = tmp;
				}
			}
		}
		
		Element audio = ncxTemplate.createElement("audio");
		audio.setAttribute(smilClipBegin, begin);
		for (Iterator it = attrs.keySet().iterator(); it.hasNext(); ) {
			String key = (String) it.next();
			audio.setAttribute(key, (String) attrs.get(key));
		}
		parent.appendChild(audio);
		reader.gotoAndRemoveBookmark(bookmark);
	}
	
	
	/**
	 * Handles the frontmatter of the book.
	 * @param reader a reader for the input document
	 * @param se the frontmatter start element.
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 */
	private void handleFrontMatter(BookmarkedXMLEventReader reader, StartElement se) throws XMLStreamException, FileNotFoundException {
		String bookmark = "handleFrontMatter";
		reader.setBookmark(bookmark);
		
		ContextStack frontMatterStack = new ContextStack();
		frontMatterStack.addEvent(se);
		
		boolean authorFound = false;
		boolean titleFound = false;
		
		int elemCount = 1;
		while (elemCount > 0) {
			XMLEvent event = reader.nextEvent();
			frontMatterStack.addEvent(event);
			
			if (event.isStartElement()) {
				elemCount++;
			}
			
			if (event.isEndElement()) {
				elemCount--;
			}
			
			// author
			if (frontMatterStack.getContextPath().endsWith("docauthor") &&	!authorFound) {
				Element authorElement = 
					(Element) XPathUtils.selectSingleNode(ncxTemplate.getDocumentElement(), "//*[@id='author']");
				authorElement.removeAttribute("id");
				handleDocMisc(reader, authorElement);
				authorFound = true;
			}
			
			// title.
			if (frontMatterStack.getContextPath().endsWith("doctitle") && !titleFound) {
				
				Element titleElement = 
					(Element) XPathUtils.selectSingleNode(ncxTemplate.getDocumentElement(), "//*[@id='title']");
				titleElement.removeAttribute("id");
				handleDocMisc(reader, titleElement);				
				titleFound = true;
			}
		}
		
		Element metaUid = (Element) XPathUtils.selectSingleNode(ncxTemplate.getDocumentElement(), "//meta[@name='dtb:uid']");
		DEBUG(ncxTemplate.getDocumentElement());
		metaUid.setAttribute("content", uid);
		
		reader.gotoAndRemoveBookmark(bookmark);
	}
	
	
	/**
	 * Returns the next playorder using a simple counter.
	 * @return the next playorder using a simple counter.
	 */
	private int getNextPlayorder() {
		return ++playorder;
	}
	

	/**
	 * Returns the next playorder as a String. Time containers with 
	 * the same smilref attribute will get the same playorder. 
	 * @param smilref the smilref of the timecontainer requesting a playorder.
	 * @return the next playorder as a String.
	 */
	private String getStrPlayorder(String smilref) {
		String playorder = (String) smilPlayorder.get(smilref);
		if (smilref != null && playorder != null) {
			return playorder;
		}
		
		playorder = String.valueOf(getNextPlayorder());
		smilPlayorder.put(smilref, playorder);
		return playorder;
	}
	
	
	/**
	 * Returns the next ncx-id.
	 * @return the next ncx-id.
	 */
	private String getNextId() {
		return "ncx-" + String.valueOf(++ncxId);
	}
	
	
	/**
	 * Prints debug messages on System.out iff the system property 
	 * represented by <tt>NCXMaker.DEBUG_PROPERTY</tt> is defined.
	 * Debug messages are prefixed with "<tt>DEBUG: </tt>".
	 * @param msg the message.
	 */
	private void DEBUG(String msg) {
		if (System.getProperty(DEBUG_PROPERTY) != null) {
			System.out.println("DEBUG: "  + msg);
		}
	}
	
	
	/**
	 * Prints <code>se</code> on System.err if <code>DEBUG == true</code>.
	 * @param se the start element.
	 */
	private void DEBUG(StartElement se) {
		if (System.getProperty(DEBUG_PROPERTY) != null) {
			System.out.print("debug: <");
			System.out.print(se.getName().getLocalPart());
			for (Iterator it = se.getAttributes(); it.hasNext(); ) {
				Attribute at = (Attribute) it.next();
				System.out.print(" " + at.getName().getLocalPart() + "=\"" + at.getValue() + "\"");
			}
			System.out.println(">");
		}
	}
	
	private void DEBUG(Node node) {
		if (!(System.getProperty(DEBUG_PROPERTY) != null)) {
			return;
		}
		
		if (node instanceof Element) {
			Element elem = (Element) node;
			Document doc = elem.getOwnerDocument();
			if (elem.equals(doc.getDocumentElement())) {
				 System.out.println("<?xml version=\"" + 
						 doc.getXmlVersion() + "\" encoding=\"" + 
						 doc.getXmlEncoding() + "\"?>"); 
					 
				 
			}
			System.out.print("<" + elem.getTagName());
			NamedNodeMap nnm = elem.getAttributes();
			boolean open = true;
			for (int i = 0; i < nnm.getLength(); i++) {
				Attr at = (Attr) nnm.item(i);
				System.out.print(" " + at.getName() + "=\"" + at.getValue() + "\"");
			}
			NodeList kids = elem.getChildNodes();
			if (kids.getLength() > 0) {
				System.out.println(">");
			} else {
				System.out.println("/>");
				open = false;
			}
			
			for (int i = 0; i < kids.getLength(); i++) {
				DEBUG(kids.item(i));
			}
			if (open) {
				System.out.println("</" + elem.getTagName() + ">");				
			}

		} else {
			System.err.print(node.getTextContent());
		}
	}
	
	private void DEBUG(Document d) throws FileNotFoundException {
		if (System.getProperty(DEBUG_PROPERTY) != null) {
			boolean success = false;
			try {
				TransformerFactory xformFactory = TransformerFactory.newInstance();  
				javax.xml.transform.Transformer idTransform = xformFactory.newTransformer();
				idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
				Source input = new DOMSource(d);
				Result output = new StreamResult(System.err);
				idTransform.transform(input, output);
				success = true;
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			} finally {
				if (!success) {
					DEBUG(d.getDocumentElement());
				}
			}
		}
	}
	
	/**
	 * Sets the two sets customTests and bookStructs so that they contain
	 * the names of all customTests made and all bookstructs for those.
	 * @param allCustomTests a Set containing the names of the elements on which
	 * customTest were performed.
	 * @param bookStructs a mapping from elementnames to bookstructs.
	 */
	public void setCustomTests(Set allCustomTests, Map bookStructs) {
		customTests = allCustomTests;
		this.bookStructs = bookStructs;
	}
	
	
	/**
	 * Sets the ncx output file.
	 * @param f the file.
	 */
	public void setNCXOutputFile(File f) {
		ncxOutputFile = f;
	}

	
	/**
	 * Returns the dtb:uid.
	 * @return the dtb:uid.
	 */
	public String getUid() {
		return uid;
	}
	
	
	/**
	 * Returns a Map containing the dc:Elements. Note that this Map
	 * is a MultiHashMap.
	 * @return a Map containing the dc:Elements.
	 */
	public Map getDCElements() {
		return dcElements;
	}
}
