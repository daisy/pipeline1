package int_daisy_mixedContentNormalizer.dom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

import org.daisy.util.i18n.CharUtils;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Markus Gylling
 */
public class DOMConfig {

	private Set<StartElement> mWrappers = null;
	private Set<StartElement> mIgnorables = null;
	private Set<String> mSupportedNamespaces = null;
	private Map<String, QName> mSyncPointScopes = null;
	private Map<String, Attribute> mSyncPointAttributes = null;
	private Map<String, char[]> mExtraWhitespaceCharacters = null;
	private Set<String> mWrapperScrubbingNamespaces = null; //namespaces for which wrapper scrubbing is activated
	
	
	public DOMConfig() {
		mIgnorables = new HashSet<StartElement>(); 
		mWrappers = new HashSet<StartElement>();
		mSupportedNamespaces = new HashSet<String>();
		mSyncPointScopes = new HashMap<String, QName>();
		mSyncPointAttributes = new HashMap<String, Attribute>();
		mExtraWhitespaceCharacters = new HashMap<String, char[]>();
		mWrapperScrubbingNamespaces = new HashSet<String>(); 
	}
	
	/*package*/ void addIgnorable(StartElement se) {
		mIgnorables.add(se);
	}
	
	/*package*/ void addSupportedNamespace(String ns) {
		mSupportedNamespaces.add(ns);
	}
	
	/*package*/ void addWrapper(StartElement se) {
		mWrappers.add(se);
	}

	/*package*/ void addScope(String namespaceuri, QName scope) {
		mSyncPointScopes.put(namespaceuri, scope);
	}

	/*package*/ void addWhitespaceCharacters(String namespaceuri, String characters) throws IllegalArgumentException {
		//support only bmp so 1-char representation all the time
		String[] tmp = characters.split(" ");
		char[] ret = new char[tmp.length];
		
		for (int i = 0; i < tmp.length; i++) {
			if(tmp[i].length()>1) throw new IllegalArgumentException(tmp[i]);
			ret[i] = tmp[i].charAt(0);
		}
		mExtraWhitespaceCharacters.put(namespaceuri, ret);
	}
	
	/*package*/ void addSyncPointAttribute(String namespaceuri, QName name, String value) {
		XMLEventFactory xef = null;
		try{
			xef = StAXEventFactoryPool.getInstance().acquire();
			mSyncPointAttributes.put(namespaceuri, xef.createAttribute(name, value));
		}finally{	
			StAXEventFactoryPool.getInstance().release(xef);
		}
	}
	
	/**
	 * @return the QName of an element outside of which syncpoint location will be deactivated
	 */
	public QName getSyncPointScope(String namespaceuri) {
		return mSyncPointScopes.get(namespaceuri);
	}
	
	/**
	 * @return the Attribute to be used as a sync point marker within the given namespace context.
	 */
	public Attribute getSyncPointAttribute(String namespaceuri) {
		return mSyncPointAttributes.get(namespaceuri);
	}

	/**
	 * @return true of this config has explicit support for the given namespace
	 */
	public boolean supportsNamespace(String namespaceuri) {
		return mSupportedNamespaces.contains(namespaceuri);
	}
	
	/**
	 * @return true if inparam element is in the list of elements marked as ignorable in the config.
	 */
	public boolean isIgnorable(Element e) {				
		for(StartElement se : mIgnorables) {
			if(se.getName().getLocalPart().equals(e.getNodeName())) {
				if(se.getName().getNamespaceURI().equals(e.getNamespaceURI())) {
					//TODO compare attributes
					return true;
				}	
			}			
		}				
		return false;
	}
	
	
	/**
	 * Retrieve the wrapper element to use in a particular namespace context.
	 */
	public StartElement getWrapperElement(String namespaceuri) {
		for(StartElement se : mWrappers) {
			if(se.getName().getNamespaceURI().equals(namespaceuri)) {
				return se;								
			}
		}
		//fallback //TODO separate method
		XMLEventFactory xef = null;
		try{
			xef = StAXEventFactoryPool.getInstance().acquire();
			return xef.createStartElement("", namespaceuri, "span");
		}finally{	
			StAXEventFactoryPool.getInstance().release(xef);
		}
	}

	/**
	 * @return true if inparam sibling list is only ignorable elements and/or XML whitespace
	 */
	public boolean isIgnorableElementsAndWhitespaceOnly(NodeList childNodes) {
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node n = childNodes.item(i);
			if(n.getNodeType()==Node.TEXT_NODE) {
//				if(!CharUtils.isXMLWhiteSpace(n.getNodeValue())) {
//					return false;
//				}
//				alt:
				TextNodeType tnt = getTextNodeType(n.getNodeValue(), n.getParentNode().getNamespaceURI());
				if(tnt==TextNodeType.TEXT) return false;
//				end alt:				
			}else if(n.getNodeType()==Node.ELEMENT_NODE) {
				Element e = (Element)n;
				if(!isIgnorable(e)) return false;
			}
		}		
		return true;
	}
	
		
	public static enum TextNodeType {
		XML_WHITESPACE,			//ignorable
		NAMESPACE_WHITESPACE,   //ignorable; xml ws + any chars added in config 
		TEXT;					//non-ignorable
	}
	
	public TextNodeType getTextNodeType(String text, String namespaceuri) {
		//System.err.println("input: " + text );
		
		char[] chars = text.toCharArray();
		boolean hasXMLWhitespace = false;
		boolean hasNamespaceWhitespace = false;
		
		for (int i = 0; i < chars.length; i++) {			
			char c = chars[i];		
			boolean isXMLWhitespace = CharUtils.isXMLWhiteSpace(c); 
			boolean isNamespaceWhitespace = isNamespaceWhitespace(c, namespaceuri);
			
			if(!isXMLWhitespace && !isNamespaceWhitespace) {		
				return TextNodeType.TEXT;
			}
			
			if(isXMLWhitespace) hasXMLWhitespace = true;
			if(isNamespaceWhitespace) hasNamespaceWhitespace = true;
		}
		
		if (hasNamespaceWhitespace) return TextNodeType.NAMESPACE_WHITESPACE;
				
		return TextNodeType.XML_WHITESPACE;
	}
	
	/**
	 * Check only for namespace-specific addons, not the XML whitespace set.
	 */
	private boolean isNamespaceWhitespace(char c, String nsURI) {
		//TODO support non BMP
		char[] extraWS = mExtraWhitespaceCharacters.get(nsURI);
		if(extraWS!=null) {
			for (int i = 0; i < extraWS.length; i++) {
				if(extraWS[i]==c) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Determine whether inparam char is XML or Config whitespace.
	 */
	public boolean isWhitespace(char c, String nsURI) {
		//TODO support non BMP
		if(CharUtils.isXMLWhiteSpace(c)) return true;
		if(isNamespaceWhitespace(c, nsURI)) return true;
		return false;
	}

	/**
	 * @return true if element has a text node child that is not XML whitespace nor namespace whitespace
	 */
	public boolean hasTextChild(Element e) {
		if(e.hasChildNodes()) {
			for (int i = 0; i < e.getChildNodes().getLength(); i++) {
				Node c = e.getChildNodes().item(i);
				if(c.getNodeType() == Node.TEXT_NODE){
					TextNodeType tnt = getTextNodeType(c.getNodeValue(), e.getNamespaceURI());
					if(tnt == TextNodeType.TEXT) return true;
				}
			}
		}
		return false;
	}

	/**
	 * Return true if wrappers added in given namespace should have leading and trailing
	 * whitespace scrubbed (i.e. moved outside the wrapper).
	 */
	public boolean isScrubbingWrappers(String namespaceURI) {
		if(mWrapperScrubbingNamespaces.contains(namespaceURI)) {
			return true;
		}
		return false;
	}

	/*package*/ void setIsScrubbingWrappers(String ns) {
		mWrapperScrubbingNamespaces.add(ns);		
	}
	
}
