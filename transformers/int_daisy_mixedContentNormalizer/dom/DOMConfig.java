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
import org.w3c.dom.Attr;
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
	private Set<StartElement> mSyncForce = null;
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
		mSyncForce = new HashSet<StartElement>();
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

	/*package*/ void addSyncForce(StartElement se) {
		//System.out.println("adding to syncforce " + se.getName().toString());
		mSyncForce.add(se);
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
	 * @return true if inparam element is in the list of elements marked as ignorable in the config, or if it is empty.
	 */
	public boolean isIgnorable(Element e) {				
		for(StartElement se : mIgnorables) {
			if(se.getName().getLocalPart().equals(e.getLocalName())) {
				if(se.getName().getNamespaceURI().equals(e.getNamespaceURI())) {
					if(matchesAttributes(e,se)) return true;
				}	
			}			
		}			
		//mg20081027 if empty return true
		if(e.getFirstChild()==null) return true;
		return false;
	}
	
	/**
	 * Return true if all attributes on Element e exist on StartElement se, else false.
	 * When se contains zero attributes, no test is made, and true is returned 
	 * (A StartElement in config with 0 attrs means that attributes are not significant).
	 */
	private boolean matchesAttributes(Element e, StartElement se) {
		if(!se.getAttributes().hasNext()) return true;
		
		for (int i = 0; i < e.getAttributes().getLength(); i++) {
			Attr a = (Attr)e.getAttributes().item(i);
			QName test = new QName(a.getNamespaceURI(),a.getLocalName());
			//TODO should we match on attr values as well?
			if(se.getAttributeByName(test)==null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return true if inparam element is in the list of elements marked to be forced to sync in the config.
	 */
	public boolean isSyncForce(Element e) {				
		for(StartElement se : mSyncForce) {			
			if(se.getName().getLocalPart().equals(e.getLocalName())) {
				if(se.getName().getNamespaceURI().equals(e.getNamespaceURI())) {
					if(matchesAttributes(e,se)) {
						return true;
					}
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
		//fallback 
		XMLEventFactory xef = null;
		try{
			xef = StAXEventFactoryPool.getInstance().acquire();
			return xef.createStartElement("", namespaceuri, "span");
		}finally{	
			StAXEventFactoryPool.getInstance().release(xef);
		}
	}
	
	/**
	 * @return true if inparam sibling list are DOM Text nodes only, false if
	 * other node types than text, or the given NodeList is null or empty
	 */
	public boolean isTextOnly(NodeList childNodes) {
		if(childNodes==null || childNodes.getLength()==0) return false;
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node n = childNodes.item(i);
			if(n.getNodeType()!=Node.TEXT_NODE) {
				return false;
			}
		}	
		return true;
	}
	
	/**
	 * @return true if inparam sibling list and descendants are only ignorable elements and/or XML whitespace
	 * @param deep whether to include element descendants in the weighting
	 */
	public boolean isIgnorableElementsAndWhitespaceOnly(NodeList childNodes, boolean deep) {
		if(childNodes==null || childNodes.getLength()==0) return true;
		
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node n = childNodes.item(i);
			if(n.getNodeType()==Node.TEXT_NODE) {
				TextNodeType tnt = getTextNodeType(n.getNodeValue(), n.getParentNode().getNamespaceURI());
				if(tnt==TextNodeType.TEXT) return false;
			}else if(n.getNodeType()==Node.ELEMENT_NODE) {
				Element e = (Element)n;
				if(!isIgnorable(e)) return false;
				if(deep) {
					if(!isIgnorableElementsAndWhitespaceOnly(e.getChildNodes(),deep)) {
						return false;
					}
				}
			}
		}		
		return true;
	}
	
	/**
	 * @return true if inparam sibling list and descendants are only ignorable elements and/or text and/or XML whitespace
	 * @param deep whether to include element descendants in the weighting
	 */
	public boolean isIgnorableElementsAndTextOnly(NodeList childNodes, boolean deep) {
		if(childNodes==null || childNodes.getLength()==0) return true;
		
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node n = childNodes.item(i);
			if(n.getNodeType()==Node.TEXT_NODE) {
				//this includes both whitespace and text
			}else if(n.getNodeType()==Node.ELEMENT_NODE) {
				Element e = (Element)n;
				if(!isIgnorable(e)) return false;
				if(deep) {
					if(!isIgnorableElementsAndTextOnly(e.getChildNodes(),deep)) {
						return false;
					}
				}
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

		char[] chars = text.toCharArray();

		boolean hasNamespaceWhitespace = false;
		
		for (int i = 0; i < chars.length; i++) {			
			char c = chars[i];		
			boolean isXMLWhitespace = CharUtils.isXMLWhiteSpace(c); 
			boolean isNamespaceWhitespace = isNamespaceWhitespace(c, namespaceuri);
			
			if(!isXMLWhitespace && !isNamespaceWhitespace) {		
				return TextNodeType.TEXT;
			}
			
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
	 * @see #hasTextDescendant(Element)
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
	 * @return true if element has at least one text node descendant 
	 * that is not XML whitespace nor namespace whitespace
	 * @see #hasTextChild(Element)
	 */
	public boolean hasTextDescendant(Element e) {
		if(e.hasChildNodes()) {
			for (int i = 0; i < e.getChildNodes().getLength(); i++) {
				Node c = e.getChildNodes().item(i);
				if(c.getNodeType() == Node.TEXT_NODE){
					TextNodeType tnt = getTextNodeType(c.getNodeValue(), e.getNamespaceURI());
					if(tnt == TextNodeType.TEXT) return true;
				} else if(c.getNodeType() == Node.ELEMENT_NODE){
					boolean desc = hasTextDescendant((Element)c);
					if(desc) return true;
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
