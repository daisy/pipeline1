package int_daisy_opsCreator;

import int_daisy_opsCreator.metadata.MetadataItem;
import int_daisy_opsCreator.metadata.MetadataList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.i18n.CharUtils;
import org.daisy.util.xml.IDGenerator;
import org.daisy.util.xml.LocusTransformer;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.BookmarkedXMLEventReader;
import org.daisy.util.xml.stax.ContextStack;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Build the NCX of an OPS 2.0 publication.
 * @author Markus Gylling
 */
class NcxBuilder extends Builder implements ErrorHandler  {
		
	private List<NavPointSource>mNavMapIncludeEvents = null;		
	private List<NavPointSource>mNavListIncludeEvents = null;
	private NcxBuilderConfiguration mNcxConfiguration = null;
	private Transformer mOwner = null;
	private enum relation {PARENT, SIBLING, CHILD, UNKNOWN}
	private IDGenerator mIdGenerator = null;
	private Map<URI, Document> mDocumentCache = null;
	private static XPath mXPathEvaluator = null;
	private static DocumentBuilderFactory mDocumentBuilderFactory = null;
	private static DocumentBuilder mDocumentBuilder = null;
		
	private String mNcxNamespace = null;
	private String mDtbookNamespace = null;
	private QName mQNameNcxRoot = null;
	private QName mQNameNcxNavLabel = null;
	private QName mQNameNcxNavList = null;
	private QName mQNameNcxPageList = null;
	private QName mQNameNcxNavMap = null;
	private QName mQNameNcxNavPoint = null;
	private QName mQNameNcxNavTarget = null;
	private QName mQNameNcxPageTarget = null;
	private QName mQNameNcxContent = null;		
	private QName mQNameNcxText = null;
	private QName mQNameConfigLabel = null;
	private QName mQNameDtbookPagenum = null;
	private QName mQNameDcLanguage = null;
	private String mDcLanguageValue = null;
	
	
	NcxBuilder(List<Fileset> inputFilesets, MetadataList metadata, NcxBuilderConfiguration config, Transformer owner) throws PoolException, XMLStreamException, IOException {
		super(inputFilesets,metadata);		
		mIdGenerator = new IDGenerator("ncx");		
		mDocumentCache = new HashMap<URI,Document>();
		mNcxConfiguration = config;
		mOwner = owner;		
		
		mNcxNamespace = "http://www.daisy.org/z3986/2005/ncx/";						
		mDtbookNamespace = "http://www.daisy.org/z3986/2005/dtbook/";
		mQNameNcxRoot = new QName(mNcxNamespace,"ncx");
		mQNameNcxNavLabel = new QName(mNcxNamespace,"navLabel");
		mQNameNcxNavList = new QName(mNcxNamespace,"navList");
		mQNameNcxPageList = new QName(mNcxNamespace,"pageList");
		mQNameNcxNavMap = new QName(mNcxNamespace,"navMap");
		mQNameNcxNavPoint = new QName(mNcxNamespace,"navPoint");
		mQNameNcxNavTarget = new QName(mNcxNamespace,"navTarget");
		mQNameNcxPageTarget = new QName(mNcxNamespace,"pageTarget");
		mQNameNcxContent = new QName(mNcxNamespace,"content");		
		mQNameNcxText = new QName(mNcxNamespace,"text");
		mQNameConfigLabel = new QName("http://www.daisy.org/pipeline/ncxconfig#","label");
		mQNameDtbookPagenum = new QName(mDtbookNamespace,"pagenum");
		mQNameDcLanguage = new QName(OpsCreator.DC_NS,"language");		
		MetadataItem m = mMetaData.get(mQNameDcLanguage);		
		if(m!=null) mDcLanguageValue = m.getValue();
		
		buildIncludeEvents();		
	}
	
	@Override
	void build() {
		XMLEventFactory mXMLEventFactory = null;		
		try{
			 									
			mXMLEventFactory = StAXEventFactoryPool.getInstance().acquire();
			
			mEventList.add(mXMLEventFactory.createStartDocument("utf-8", "1.0"));
			mEventList.add(mXMLEventFactory.createDTD("<!DOCTYPE ncx PUBLIC \"-//NISO//DTD ncx 2005-1//EN\" \"http://www.daisy.org/z3986/2005/ncx-2005-1.dtd\" >"));
			
			mEventList.add(mXMLEventFactory.createStartElement(mQNameNcxRoot, null, null));
			mEventList.add(mXMLEventFactory.createAttribute("version", "2005-1"));
			if(mDcLanguageValue != null) {
				mEventList.add(mXMLEventFactory.createAttribute("xml:lang", mDcLanguageValue));
			}
			
			buildHead(mXMLEventFactory);					
			buildNavMap(mXMLEventFactory);					
			buildNavLists(mXMLEventFactory);				
						
			mEventList.add(mXMLEventFactory.createEndElement(mQNameNcxRoot, null));
			mEventList.add(mXMLEventFactory.createEndDocument());
			
		}finally{
			StAXEventFactoryPool.getInstance().release(mXMLEventFactory);
		}			
	}

	private void buildHead(XMLEventFactory xef) {
		
		QName dcTitle = new QName(OpsCreator.DC_NS,"title",OpsCreator.DC_PFX);
		QName dcCreator = new QName(OpsCreator.DC_NS,"creator",OpsCreator.DC_PFX);
		
		mEventList.add(xef.createStartElement("", mNcxNamespace, "head"));	
		mEventList.add(xef.createStartElement("", mNcxNamespace, "meta"));	
		mEventList.add(xef.createAttribute("name", "generator"));
		mEventList.add(xef.createAttribute("content", OpsCreator.APP_NAME));
		mEventList.add(xef.createEndElement("", mNcxNamespace, "meta"));
		mEventList.add(xef.createEndElement("", mNcxNamespace, "head"));

		mEventList.add(xef.createStartElement("", mNcxNamespace, "docTitle"));		
		mEventList.add(xef.createStartElement(mQNameNcxText,null,null));
		MetadataItem m = mMetaData.get(dcTitle);
		if(m!=null && m.getValue()!=null)mEventList.add(xef.createCharacters(m.getValue()));				
		mEventList.add(xef.createEndElement(mQNameNcxText,null));
		mEventList.add(xef.createEndElement("", mNcxNamespace, "docTitle"));

		mEventList.add(xef.createStartElement("", mNcxNamespace, "docAuthor"));				
		mEventList.add(xef.createStartElement(mQNameNcxText,null,null));
		m = mMetaData.get(dcCreator);
		if(m!=null && m.getValue()!=null)mEventList.add(xef.createCharacters(m.getValue()));
		mEventList.add(xef.createEndElement(mQNameNcxText,null));
		mEventList.add(xef.createEndElement("", mNcxNamespace, "docAuthor"));
		
	}

	/**
	 * Produce all XMLEvents of the navLists .
	 */
	private void buildNavLists(XMLEventFactory xef) {
		
		if(mNavListIncludeEvents.isEmpty()) return;
		
		//sort the events in QName groups
		Map<QName, List<NavPointSource>> sortedMap = new HashMap<QName, List<NavPointSource>>(); 
		
		for(NavPointSource nps : mNavListIncludeEvents) {
			QName q = nps.mStartElement.getName();
			if(!sortedMap.containsKey(q)) {
				List<NavPointSource> list = new LinkedList<NavPointSource>();
				list.add(nps);
				sortedMap.put(q, list);
			}else{
				sortedMap.get(q).add(nps); 
			}
		}
		
		//then render one navList per group
		//if theres a pagelist, render it first				
		if(sortedMap.containsKey(mQNameDtbookPagenum)) {
			List<NavPointSource> list = sortedMap.get(mQNameDtbookPagenum);
			if(list!=null && !list.isEmpty()) {
				buildList(mQNameDtbookPagenum, list,xef);
			}
			
		}
		
		for(QName q : sortedMap.keySet()) {
			if(!q.equals(mQNameDtbookPagenum)) {
				List<NavPointSource> list = sortedMap.get(q);
				if(list!=null && !list.isEmpty()) {
					buildList(q, list,xef);
				}	
			}			
		}
											
	}
	
	private void buildList(QName q, List<NavPointSource> list, XMLEventFactory xef) {
		/*
		 * We need different names for the events if the incoming qname is dtbook:pagenum
		 */
		QName qList = mQNameNcxNavList;
		QName qTarget = mQNameNcxNavTarget;		
		boolean isPageList = false;		
		if(q.equals(mQNameDtbookPagenum)) {
			isPageList = true;
			qList = mQNameNcxPageList;
			qTarget = mQNameNcxPageTarget;
		}
		
		//open navlist
		mEventList.add(xef.createStartElement(qList, null, null));
		mEventList.add(xef.createAttribute("class", q.getLocalPart()));
		mEventList.add(xef.createAttribute("id", q.getLocalPart()+"-list"));
		mEventList.add(xef.createStartElement(mQNameNcxNavLabel, null, null));
		mEventList.add(xef.createStartElement(mQNameNcxText, null, null));
		
		//label for the navlist
		StartElement first = list.get(0).mStartElement;		
		String lang = null;
		if(mDcLanguageValue!=null) {
			lang = mDcLanguageValue;
		}else{
			lang = Locale.getDefault().getLanguage();
		}
				
		String label = mNcxConfiguration.getNavListLabel(first, lang);
		if(label==null) label = q.getLocalPart();
		mEventList.add(xef.createCharacters(label));
				
		mEventList.add(xef.createEndElement(mQNameNcxText, null));
		mEventList.add(xef.createEndElement(mQNameNcxNavLabel, null));
		
		//navTargets 		
		for(NavPointSource nps : list) {
			createTargetStartElement(qTarget, nps, xef);
			if(isPageList) {
				Attribute page = getAttribute("page", nps.mStartElement);
				String typeValue = "normal";
				if(page!=null) {
					typeValue = page.getValue();
				}
				mEventList.add(xef.createAttribute("type", typeValue));
			}
			createNavLabelElement(nps, xef);
			createContentElement(xef, nps);				
			mEventList.add(xef.createEndElement(qTarget,null));
		}			
		//close navlist
		mEventList.add(xef.createEndElement(qList, null));
		
	}

	/**
	 * Produce all XMLEvents of the navMap.
	 */
	private void buildNavMap(XMLEventFactory xef) {						
		
		Stack<NavPointSource> openPointStack = new Stack<NavPointSource>();

		mEventList.add(xef.createStartElement(mQNameNcxNavMap,null,null));						

		for (int i = 0; i < mNavMapIncludeEvents.size(); i++) {		  
		  NavPointSource nps = mNavMapIncludeEvents.get(i);
		  if(i == mNavMapIncludeEvents.size()-1) {
			  //we are at the last navpoint
			  writeNavPoint(nps, xef);	
			  //close current
			  //System.err.println("close:: " + nps.mStartElement.getName().getLocalPart() + "::" + nps.mLabel);
			  mEventList.add(xef.createEndElement(mQNameNcxNavPoint, null));
			  //close all open in the stack
			  while(!openPointStack.empty()) {
				  NavPointSource nps2 = openPointStack.pop();
				  //System.err.println("close:: " + nps2.mStartElement.getName().getLocalPart() + "::" + nps2.mLabel);
				  mEventList.add(xef.createEndElement(mQNameNcxNavPoint, null));
			  }
		  }else{
			  //we are not at the last navpoint
			  NavPointSource next = mNavMapIncludeEvents.get(i+1);
			  writeNavPoint(nps, xef);		  
			  relation rel = getRelation(nps, next);
			  if(rel == relation.SIBLING||rel == relation.UNKNOWN) {
				  //System.err.println("close:: " + nps.mStartElement.getName().getLocalPart() + "::" + nps.mLabel);
				  mEventList.add(xef.createEndElement(mQNameNcxNavPoint, null));			  
			  }else if(rel == relation.CHILD) {
				  //dont close current			  
				  openPointStack.push(nps);			  
			  }else if(rel == relation.PARENT) {
				  //close current
				  //System.err.println("close:: " + nps.mStartElement.getName().getLocalPart() + "::" + nps.mLabel);
				  mEventList.add(xef.createEndElement(mQNameNcxNavPoint, null));
				  
				  //close events on the stack until
				  //no longer parent relation between next and the stack top
				  relation rel2 = null;
				  do {	  					  
					  if(!openPointStack.empty()){
						  NavPointSource nps2 = openPointStack.pop();
						  //System.err.println("close:: " + nps2.mStartElement.getName().getLocalPart() + "::" + nps2.mLabel);
						  mEventList.add(xef.createEndElement(mQNameNcxNavPoint, null));
						  rel2 = getRelation(next, nps2);
					  }
				  } while (rel2!=null && rel2 != relation.SIBLING);
			  }	
		  }
		}
		
		mEventList.add(xef.createEndElement(mQNameNcxNavMap,null));
			
	}


	/**
	 * Render all events of a navPoint except its closing tag
	 */
	private void writeNavPoint(NavPointSource nps, XMLEventFactory xef) {
		//System.err.println("open:: " + nps.mStartElement.getName().getLocalPart() + "::" + nps.mLabel);
		createTargetStartElement(mQNameNcxNavPoint, nps, xef);		
		createNavLabelElement(nps, xef);
		createContentElement(xef, nps);		
	}

	private void createTargetStartElement(QName name, NavPointSource nps, XMLEventFactory xef) {
		mEventList.add(xef.createStartElement(name, null, null));
		mEventList.add(xef.createAttribute("id", mIdGenerator.generateId()));
		mEventList.add(xef.createAttribute("class", nps.mStartElement.getName().getLocalPart()));
		mEventList.add(xef.createAttribute("playOrder", Integer.toString(nps.mPlayOrder)));
	}
	
	private void createNavLabelElement(NavPointSource nps, XMLEventFactory xef) {
		mEventList.add(xef.createStartElement(mQNameNcxNavLabel, null, null));
		mEventList.add(xef.createStartElement(mQNameNcxText, null, null));
		mEventList.add(xef.createCharacters(nps.mLabel));
		mEventList.add(xef.createEndElement(mQNameNcxText, null));			
		mEventList.add(xef.createEndElement(mQNameNcxNavLabel, null));
	}
	
	private void createContentElement(XMLEventFactory xef, NavPointSource nps) throws NullPointerException {
		mEventList.add(xef.createStartElement(mQNameNcxContent, null, null));	
		String idValue = getIdAttrValue(nps.mStartElement);
		if(idValue == null) {
			String message = "error: no ID on target " + nps.mStartElement.getName().getLocalPart();
			mOwner.sendMessage(message, MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM,null);
			throw new NullPointerException(message);
		}
		mEventList.add(xef.createAttribute("src", nps.mTargetFile.getName()+"#"+idValue));			
		mEventList.add(xef.createEndElement(mQNameNcxContent, null));				
	}
	
	
	/**
	 * Retrieve the relation of next to current.
	 * First, attempt to establish relation based on XHTML and DTBook headings (hx) - element type specific.
	 * If this fails, establish relation based on the nesting context (depth) - element type neutral.
	 */
	private relation getRelation(NavPointSource currentNps, NavPointSource nextNps) {
		String currentNpsLocalName = currentNps.mStartElement.getName().getLocalPart();
		String nextNpsLocalName = nextNps.mStartElement.getName().getLocalPart();
		int currentInt = -1;
		int nextInt = -1;
		//if h1-h6
		if(isHeadingElement(currentNpsLocalName) && isHeadingElement(nextNpsLocalName)) {
			currentInt = Integer.parseInt(currentNpsLocalName.substring(1, 2));
			nextInt = Integer.parseInt(nextNpsLocalName.substring(1, 2));
		}else{
			//xpath context			
			currentInt = countDepth(currentNps.mXPathContext);
			nextInt = countDepth(nextNps.mXPathContext);
		}
		
		if(currentInt!=-1 && nextInt != -1) {
			if(nextInt>currentInt) return relation.CHILD;
			if(nextInt==currentInt) return relation.SIBLING;
			return relation.PARENT;
		}
		
		//else we cannot tell
		return relation.UNKNOWN;
	}
	
	private int countDepth(String path) {
		int count = 0;
		for (int i = 0; i < path.length(); i++) {
			char c = path.charAt(i);
			if(c=='/') count++;
		}
		return count;
	}

	private boolean isHeadingElement(String localName) {
		if(localName.length()==2 && localName.startsWith("h")) {
			return true;
		}
		return false;
	}

	private String getIdAttrValue(StartElement startElement) {		
		Attribute a = getAttribute("id",startElement);
		if(a !=null) {		
		  return a.getValue();
		}					
		return null;
	}

	private Attribute getAttribute(String localPart, StartElement se) {
		for (Iterator iter = se.getAttributes(); iter.hasNext();) {
			Attribute a = (Attribute) iter.next();
			if(a.getName().getLocalPart().equals(localPart)) {
				return a;
			}			
		}
		return null;
	}
	
	/**
	 * Loop through input filesets, populate the mNavMapIncludeEvents and mNavListIncludeEvents lists.
	 * @throws PoolException 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	private void buildIncludeEvents() throws PoolException, FileNotFoundException, XMLStreamException {
		mNavMapIncludeEvents = new LinkedList<NavPointSource>();		
		mNavListIncludeEvents = new LinkedList<NavPointSource>();
		
		XMLInputFactory xif = null;
		Map properties = null;
		try {
			properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(Boolean.FALSE);
			xif = StAXInputFactoryPool.getInstance().acquire(properties);
			int playOrder = 0;
			
			for(Fileset fileset : mInputFilesets) {
				FilesetFile manifest = fileset.getManifestMember();
				ContextStack stack = new ContextStack();
				BookmarkedXMLEventReader bxer = new BookmarkedXMLEventReader(xif.createXMLEventReader(manifest.asInputStream()));
				
				while(bxer.hasNext()) {
					XMLEvent event = bxer.nextEvent();
					stack.addEvent(event);
					if(event.isStartElement()){
						StartElement se = event.asStartElement();
						String xpath = null;
						if(mNcxConfiguration.matchesNavMapFilter(se)) {
							xpath = stack.getContextXPath(ContextStack.XPATH_SELECT_ELEMENTS_ONLY,ContextStack.XPATH_PREDICATES_NONE);
							mNavMapIncludeEvents.add(new NavPointSource(se,getLabel(bxer,se,manifest),manifest.getFile(),++playOrder,xpath));
						}
						else if(mNcxConfiguration.matchesNavListFilter(se)) {
							xpath = stack.getContextXPath(ContextStack.XPATH_SELECT_ELEMENTS_ONLY,ContextStack.XPATH_PREDICATES_NONE);
							mNavListIncludeEvents.add(new NavPointSource(se,getLabel(bxer,se,manifest),manifest.getFile(),++playOrder,xpath));
						}
					}
				}				
			}
		}finally {
			StAXInputFactoryPool.getInstance().release(xif, properties);
		}
	}
	
	/**
	 * Retrieve text for an NCX navLabel. 
	 * <p>If the backing template element has a label attribute, 
	 * attempt to do it via DOM+XPath, else just get the enclosed text from the eventreader.</p>
	 * @throws XMLStreamException 
	 */
	private String getLabel(BookmarkedXMLEventReader bxer, StartElement initialPos, FilesetFile source) throws XMLStreamException {
		StringBuilder label = new StringBuilder();		
		
		try{					
			StartElement template = mNcxConfiguration.getTemplate(initialPos);
			if(template!=null) {
				Attribute configLabel = template.getAttributeByName(mQNameConfigLabel);
				if(configLabel!=null) {
					label.append(getLabelFromDOM(initialPos, configLabel.getValue(), source.getFile()));
				}
			}
		}catch (Exception e) {
			String message = e.getClass().getSimpleName()+": " +e.getMessage();
			mOwner.sendMessage(message, MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM,null);	
			label.delete(0, label.length());
		}
		
		if(label.length() == 0) {							
			//With inparam reader positioned at a StartElement, locate a text node to use as a label for this element
			//collect characters until we get a close event of same QName as initialPos
			bxer.setBookmark("name");
			while(bxer.hasNext()) {
				XMLEvent event = bxer.nextEvent();
				if(event.isEndElement() && event.asEndElement().getName().equals(initialPos.getName())) {				
					if(label.length()==0) {
						//we have an empty element, fallback
						label.append(initialPos.getName().getLocalPart());
					}
					bxer.gotoAndRemoveBookmark("name");
					return label.toString();  //TODO this may be very long - consider truncating, or specializing via config
				}else if(event.isCharacters() && !CharUtils.isXMLWhiteSpace(event.asCharacters().getData())) {
					label.append(event.asCharacters().getData());
				}
			}
		}
		return label.toString();
	}

	
	
	
	/**
	 * Build an xpath statement and apply to a DOM instance; we should return a string 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws XPathExpressionException 
	 */		
	private String getLabelFromDOM(StartElement se, String xpath, File source) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		URI uri = source.toURI();
		
		if(!mDocumentCache.containsKey(uri)) {
			mDocumentCache.put(uri, getDOM(source));
		}
		Document doc = mDocumentCache.get(uri);
		String id = getIdAttrValue(se);
		String elemName = se.getName().getLocalPart();
		StringBuilder xpathBuilder = new StringBuilder();
		xpathBuilder.append('/').append('/').append(elemName);
		xpathBuilder.append("[@id='").append(id).append("']");
		xpathBuilder.append(xpath);
		if(mXPathEvaluator ==null) mXPathEvaluator = XPathFactory.newInstance().newXPath();
		return (String)mXPathEvaluator.evaluate(xpathBuilder.toString(), doc.getDocumentElement(), XPathConstants.STRING);		
	}
	
	private Document getDOM(File source) throws ParserConfigurationException, SAXException, IOException {
		if(mDocumentBuilderFactory == null) {
			mDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
			mDocumentBuilderFactory.setNamespaceAware(false);
			mDocumentBuilderFactory.setValidating(false);
			mDocumentBuilder = mDocumentBuilderFactory.newDocumentBuilder();
		}	
		mDocumentBuilder.reset();	
		mDocumentBuilder.setEntityResolver(CatalogEntityResolver.getInstance());
		mDocumentBuilder.setErrorHandler(this);
		return mDocumentBuilder.parse(source);		
	}
	
	class NavPointSource {
		private StartElement mStartElement = null;
		private String mLabel = null;
		private File mTargetFile = null;
		private int mPlayOrder = -1;
		private String mXPathContext = null;
		
		NavPointSource(StartElement se, String s, File target, int playOrder, String xpathContext) {
			mStartElement = se;
			mLabel = s;
			mTargetFile = target;
			mPlayOrder = playOrder;
			mXPathContext = xpathContext;
		}
	}

	public void error(SAXParseException e) throws SAXException {
		Location loc = LocusTransformer.newLocation(e);
		mOwner.sendMessage(e.getMessage(), MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT, loc);		
	}


	public void fatalError(SAXParseException e) throws SAXException {
		Location loc = LocusTransformer.newLocation(e);
		mOwner.sendMessage(e.getMessage(), MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT, loc);		
	}


	public void warning(SAXParseException e) throws SAXException {
		Location loc = LocusTransformer.newLocation(e);
		mOwner.sendMessage(e.getMessage(), MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT, loc);
	}
}
