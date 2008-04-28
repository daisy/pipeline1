/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
package org.daisy.util.xml.peek;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.xml.sax.Attributes;

/**
 * Carries the result of a Peeker peek: what the Peeker saw.
 * <p>Correct representation in name getters is not guaranteed if the peeked 
 * document is not namespace wellformed; QNames as an example are computed, not echoed.</p>
 * @author Markus Gylling
 */
public class PeekResult {

	private String mInputSourceSystemId = null;	    	//the path of the doc being peeked
    private String mPrologPublicId = null;		    	//first encountered public id, may remain null	
    private String mPrologSystemId = null;				//first encountered system id, may remain null
    private String mPrologXmlVersion = null;			//XML version in xml declaration
    private String mPrologEncoding = null;				//encoding pseuadattr in xml declaration, may remain null
    private boolean mPrologIsStandalone = false;		//standalone property
    private String mRootElementNsUri = null;			//ns uri of root element, may remain null    
    private String mRootElementLocalName = null;     	//localname of root element
    private Map<String,String> mRootElementPrefixMappings = null;   	//Map<nsuri, prefix> stores all calls to ContentHandler.startPrefixMapping
    private Attributes mRootElementAttributes = null;	//may remain null if no attrs on root
    private Set<String> mXSISchemaLocationURIs = null;			
    
	
	/*package*/ PeekResult(String systemId) {
		this.mInputSourceSystemId = systemId;
		this.mRootElementPrefixMappings = new HashMap<String,String>();
	}

	void addPrefixMapping(String prefix, String uri) {
		this.mRootElementPrefixMappings.put(uri,prefix);
	}
	
	/**
	 * @return a Map &lt;nsuri, prefix&gt; of all namespace declarations occuring on the root element.
	 * A default namespace will have the prefix \"\", ie the empty string.
	 */
	public Map<String,String> getRootElementPrefixMappings() {
		return this.mRootElementPrefixMappings;
	}
	
	/**
	 * @return value of the encoding pseudoattribute of the XML declaration, 
	 * or null if no such pseudoattribute was encountered. If the SAXParser
	 * underlying the Peeker does not implement <code>org.xml.sax.ext.Locator2</code>,
	 * this property will always be null.
	 */

	public String getPrologEncoding() {
		return mPrologEncoding;
	}

	void setPrologEncoding(String prologEncoding) {
		this.mPrologEncoding = prologEncoding;
	}

	/**
	 * @return the first (document entity) prolog id encountered while parsing the source, 
	 * or null if no prolog id was encountered
	 */
	public String getPrologPublicId() {
		return mPrologPublicId;
	}

	void setPrologPublicId(String prologPublicId) {
		if(null==this.mPrologPublicId) this.mPrologPublicId = prologPublicId;
	}

	/**
	 * @return the first (document entity) system id encountered while parsing the source, 
	 * or null if no system id was encountered
	 */
	public String getPrologSystemId() {
		return mPrologSystemId;
	}

	void setPrologSystemId(String prologSystemId) {
		if(null==this.mPrologSystemId) this.mPrologSystemId = prologSystemId;
	}

	/**
	 * @return value of the version pseudoattribute of the XML declaration, 
	 * or null if no such pseudoattribute was encountered. If the SAXParser
	 * underlying the Peeker does not implement <code>org.xml.sax.ext.Locator2</code>,
	 * this property will always be null.
	 */
	public String getPrologXmlVersion() {
		return mPrologXmlVersion;
	}

	void setPrologXmlVersion(String prologXmlVersion) {
		this.mPrologXmlVersion = prologXmlVersion;
	}

	/**
	 * @return boolean value of the standalone property of the peeked document. 
	 * The value is true if the document specified standalone="yes" in its XML 
	 * declaration, and otherwise is false. If the underlying parser does not 
	 * recognize the SAX feature <code>http://xml.org/sax/features/is-standalone</code>
	 * the return value will be false. 
	 */
	public boolean getIsStandalone() {
		return this.mPrologIsStandalone;
	}

	void setIsStandalone(boolean isStandalone) {
		this.mPrologIsStandalone = isStandalone;
	}

	
	/**
	 * @return the local name of the documents root element
	 */
	public String getRootElementLocalName() {
		return mRootElementLocalName;
	}

	void setRootElementLocalName(String rootElementLocalName) {
		this.mRootElementLocalName = rootElementLocalName;
	}

	/**
	 * @return the namespace URI of the root element, or null if no ns uri was encountered
	 */
	public String getRootElementNsUri() {
		return mRootElementNsUri;
	}

	void setRootElementNsUri(String rootElementNsUri) {
		if(rootElementNsUri.length()>0)this.mRootElementNsUri = rootElementNsUri;
	}
	
	/**
	 * @return the namespace bound prefix of the root element, which is \"\" (ie the empty string)
	 * when the root element is in a default namespace. The return value is null if the root element is 
	 * not namespace bound.
	 */
	public String getRootElementPrefix() {		
		if(null != this.mRootElementNsUri) {
			//return a 0-n length string
			return this.mRootElementPrefixMappings.get(this.mRootElementNsUri);
		}
		return null; //no namespace binding
	}

	/**
	 * @return the system id (url path) of the document being peeked
	 */
	public String getInputSourceSystemId() {
		return mInputSourceSystemId;
	}
	
	/**
	 * @return the root element as a QName
	 */
	public QName getRootElementQName() {
		if(this.getRootElementPrefix()!=null) {
			return new QName(this.mRootElementNsUri,this.mRootElementLocalName,this.getRootElementPrefix());
		}
		return new QName(this.mRootElementNsUri,this.mRootElementLocalName);		
	}
	
	void setRootElementAttributes(Attributes atts) {
		this.mRootElementAttributes = atts;
	}

	/**
	 * @return the attribute array of the root element, or null if root element had no attributes.
	 */
	public Attributes getRootElementAttributes() {
		return this.mRootElementAttributes;
	}

	/**
	 * @return any XSI schema location URIs that were namedropped on root.
	 */
	public Set<String> getXSISchemaLocationURIs() {
		return mXSISchemaLocationURIs;
	}

	void setXSISchemaLocationURIs(Set<String> schemaLocationURIs) {
		mXSISchemaLocationURIs = schemaLocationURIs;
	}
}