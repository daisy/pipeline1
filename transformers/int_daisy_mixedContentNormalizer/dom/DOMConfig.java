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
	
	public DOMConfig() {
		mIgnorables = new HashSet<StartElement>(); 
		mWrappers = new HashSet<StartElement>();
		mSupportedNamespaces = new HashSet<String>();
		mSyncPointScopes = new HashMap<String, QName>();
		mSyncPointAttributes = new HashMap<String, Attribute>();
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
	 * @return true if inparam sibling list is only ignorable elements and/or whitespace
	 */
	public boolean isIgnorableElementsAndWhitespaceOnly(NodeList childNodes) {
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node n = childNodes.item(i);
			if(n.getNodeType()==Node.TEXT_NODE) {
				if(!CharUtils.isXMLWhiteSpace(n.getNodeValue())) {
					return false;
				}
			}else if(n.getNodeType()==Node.ELEMENT_NODE) {
				Element e = (Element)n;
				if(!isIgnorable(e)) return false;
			}
		}		
		return true;
	}
}
