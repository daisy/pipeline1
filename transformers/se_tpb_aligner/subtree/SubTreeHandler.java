package se_tpb_aligner.subtree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;

import se_tpb_aligner.util.XMLResult;
import se_tpb_aligner.util.XMLSource;

/**
 * Abstract base for subtree handlers. Any extender is responsible to 
 * <ul>
 * <li>divide the input document into wellformed XML subtrees,</li> 
 * <li>expose those subtrees as an iterable ordered list, </li>
 * <li>allow replacing any subtree with another,</li>
 * <li>allow co-rendering all subtrees into one document.</li>
 *  </ul>
 * @author Markus Gylling
 */
public abstract class SubTreeHandler extends LinkedList<SubTree> {
	private XMLSource mInputDoc = null;	
	
	static final String IGNORE_NSURI = "http://www.pipeline.org/ns/ignore#";
	static final String IGNORE_NSPREFIX = "pipeline";	
	static final String IGNORE_LOCALNAME = "ignore";
	
	protected static final String DTBOOK_NSURI = Namespaces.Z2005_DTBOOK_NS_URI;
	protected static final String SMIL_NSURI = Namespaces.SMIL_20_NS_URI;
	protected static final String SSML_NSURI = Namespaces.SSML_10_NS_URI;
	protected static final String ANNON_NSURI = "http://www.example.com";
	static final String SMIL_NSPREFIX = "smil";
	static QName ignoreQName = null;
	static QName smilSrcAttrQName = null;
 
	static Attribute ignoreAttribute = null;
	static StartElement ignoreStartElement = null;
	static EndElement ignoreEndElement = null;
		
	private List<XMLEvent> mProlog = null;
	
	/**
	 * Constructor.
	 * @param doc The document to be split into subtrees.
	 * @throws XMLStreamException 
	 */
	public SubTreeHandler(XMLSource doc, DivisionStrategy divider) {		
		if(!this.supportsDivisionStrategy(divider)) throw new IllegalArgumentException(divider.toString());		
		mInputDoc = doc;		
	}

	public SubTreeHandler() {		
				
	}
	
	public void initialize() throws XMLStreamException {
		XMLEventFactory xef = null;
		Map properties = null;
		XMLInputFactory xif = null;
		try{
			//prep some recurring constructs
			xef=StAXEventFactoryPool.getInstance().acquire();
			
			ignoreQName = new QName(IGNORE_NSURI, IGNORE_LOCALNAME, IGNORE_NSPREFIX);			
			smilSrcAttrQName = new QName(SMIL_NSURI, "src", SMIL_NSPREFIX);
			ignoreAttribute = xef.createAttribute(IGNORE_NSPREFIX, IGNORE_NSURI,IGNORE_LOCALNAME, "true");
			ignoreStartElement = xef.createStartElement(IGNORE_NSPREFIX, IGNORE_NSURI,IGNORE_LOCALNAME);			
			ignoreEndElement = xef.createEndElement(IGNORE_NSPREFIX, IGNORE_NSURI,IGNORE_LOCALNAME);
			
			
			mProlog = new LinkedList<XMLEvent>();
			//set up the reader and call a concrete implementation of #read
			properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xif = StAXInputFactoryPool.getInstance().acquire(properties);
			StreamSource ss = new StreamSource(mInputDoc);
			XMLEventReader reader = xif.createXMLEventReader(ss);
			//gather the prolog and pos the reader before root
			while(reader.hasNext()) {
				XMLEvent p = reader.peek();
				if(p.isStartElement()) break;
				mProlog.add(reader.nextEvent());
			}
			//then call sub
			this.read(reader);			
		}finally{
			StAXEventFactoryPool.getInstance().release(xef);
			StAXInputFactoryPool.getInstance().release(xif, properties);
		}
	}
	
	/**
	 * Render all current subtrees into one XML document.
	 * <p>When rendering, any XML start element with an Attribute 'pipeline:ignore' 
	 * occurring in the namespace "http://www.tpb.se/ns/ignore#" will be excluded, 
	 * as will the first occurence of a close tag after an empty pipeline:ignore element. </p>
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public void render (XMLResult destination) throws XMLStreamException, FileNotFoundException {
		/*
		 * <elem pipeline:ignore='true'>
		 * 
		 * <pipeline:ignore/>
		 * </elem>
		 * 
		 */
		Map properties = null;		
		XMLOutputFactory xof = null;
		XMLEventFactory xef = null;
		XMLEventWriter wrt = null;
		FileOutputStream fos = null;
		StreamResult sr = null;
		try{
			xof = StAXOutputFactoryPool.getInstance().acquire(properties);
			xef = StAXEventFactoryPool.getInstance().acquire();
			fos = new FileOutputStream(destination);
			sr = new StreamResult(fos);			
			wrt = xof.createXMLEventWriter(sr);
			//re-add prolog
			for(XMLEvent x : mProlog) {
				wrt.add(x);
			}	
			boolean firstSubTree = true;
			boolean firstSubTreeFirstElement = true;
			for(SubTree subtree : this) {
				EndElement previousEndElement = null;
				List<XMLEvent> list = subtree.getContent();
				for(XMLEvent e : list){
					/*
					 * Watch for start tags with the ignore attribute,
					 * start or end tags in the ignore namespace,
					 * Or an empty element ignore, which indicates that the next end tag (in any namespace) should be dropped.
					 */
					if(e.isStartElement()) {
						if((!e.asStartElement().getName().equals(ignoreQName)) 
								&& (getAttributeByName(e.asStartElement(),ignoreQName)==null)) {

								StartElement se = e.asStartElement();								
								
								Set<Namespace> namespaces = new HashSet<Namespace>();
								Set<Attribute> attributes = new HashSet<Attribute>();
								
								for (Iterator<Namespace> it = namespaces.iterator(); it.hasNext();) {
									namespaces.add(it.next());									
								}
								
								if(firstSubTree && firstSubTreeFirstElement){									
									namespaces.add(xef.createNamespace("smil",SMIL_NSURI));
									namespaces.add(xef.createNamespace("annon",ANNON_NSURI));
									namespaces.add(xef.createNamespace("ssml",SSML_NSURI));
									firstSubTreeFirstElement = false;
								}
								
								for (Iterator<Attribute> it = se.getAttributes(); it.hasNext();) {
									Attribute a =  it.next();
									if(a.getName().equals(smilSrcAttrQName)) {
										//rewrite the smil:src value from absolute to relative.
										//later all audio will be copied to same dir as manifest
										String relative = new File(a.getValue()).getName();
										attributes.add(xef.createAttribute(smilSrcAttrQName, relative));
									}else{
										attributes.add(a);
									}
								
								}															
								wrt.add(xef.createStartElement(se.getName(), attributes.iterator(), namespaces.iterator()));
						} //if(!e.asStartElement().getName().equals(ignoreQName))

					}else if (e.isEndElement()) {
						if(previousEndElement!=null) {
							if(!previousEndElement.getName().equals(ignoreQName)) {						
								if(!e.asEndElement().getName().equals(ignoreQName)) {
									wrt.add(e);
								}
							}
						}else{
							if(!e.asEndElement().getName().equals(ignoreQName)) {
								wrt.add(e);
							}
						}
						previousEndElement = e.asEndElement();
					}else if(e.getEventType() == XMLEvent.NAMESPACE) {
						//strip redundant smil ns decls
						//(we declare them at root to get a cleaner output)
						Namespace ns = (Namespace) e;
						if(!ns.getNamespaceURI().equals(SMIL_NSURI) 
								&& !ns.getNamespaceURI().equals(SSML_NSURI) 
								&& !ns.getNamespaceURI().equals(ANNON_NSURI)) {
							wrt.add(e);
						}
						wrt.add(e);
					}else{
						wrt.add(e);
					}										
				}	
				firstSubTree = false;
			}					
		}finally{
			wrt.flush();
			wrt.close();			
			StAXOutputFactoryPool.getInstance().release(xof,properties);
			StAXEventFactoryPool.getInstance().release(xef);
		}
	}

	
	/**
	 * Get the XML document that was the source for the subtrees in this SubTreeHandler.
	 */
	public XMLSource getInputDoc() {
		return mInputDoc;
	}
			
	public abstract boolean supportsDocumentType(PeekResult peek);
	
	public abstract boolean supportsDivisionStrategy(DivisionStrategy divider);
	
	/**
	 * Read the input document and split it into subtrees. The inparam reader is positioned before the root element.
	 * @throws XMLStreamException 
	 */
	public abstract void read(XMLEventReader reader) throws XMLStreamException;
			
	@Override
    public boolean add(SubTree subtree) {		
		XMLEventFactory xef = null;		
		try{
			xef=StAXEventFactoryPool.getInstance().acquire();
			//add a counter attribute to the root element
			//for use when reassembling
			Attribute counter = xef.createAttribute
				(IGNORE_NSPREFIX, IGNORE_NSURI,"seq", Integer.toString(this.size()+1));
			subtree.getContent().add(1, counter);
			
			//add the smil namespace to the root
			Namespace ns = xef.createNamespace("smil", SMIL_NSURI);
			subtree.getContent().add(1, ns);
		}finally{
			StAXEventFactoryPool.getInstance().release(xef);
		}		
    	return super.add(subtree);
    }

	/**
	 * Override the dreaded wstx indexoutofboundsexception
	 */
	private Attribute getAttributeByName(StartElement se, QName qname) {
		for (Iterator<Attribute> iterator = se.getAttributes(); iterator.hasNext();) {
			Attribute a = iterator.next();
			if(a.getName().equals(qname)) return a;
		}
		return null;
	}
}
