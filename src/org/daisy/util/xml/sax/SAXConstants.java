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
package org.daisy.util.xml.sax;

/**
 * Convenience constants exposing SAX properties and features.
 * <p>
 * For the official SAX features and properties list, see
 * http://www.saxproject.org/apidoc/org/xml/sax/package-summary.html#package_description
 * </p>
 * 
 * <p>
 * For Xerces specific properties, see
 * http://xerces.apache.org/xerces2-j/properties.html
 * </p>
 * 
 * <p>
 * For Xerces specific features, see
 * http://xerces.apache.org/xerces2-j/features.html
 * </p>
 * 
 * @author Markus Gylling
 */

public class SAXConstants {

	private SAXConstants() {
	}

	/**
	 * Reports whether this parser processes external general entities; always
	 * true if validating.
	 */
	public static final String SAX_FEATURE_EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";

	/**
	 * Reports whether this parser processes external parameter entities; always
	 * true if validating.
	 */
	public static final String SAX_FEATURE_EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";

		
	/**
	 * May be examined only during a parse, after the startDocument() callback
	 * has been completed; read-only. The value is true if the document
	 * specified standalone="yes" in its XML declaration, and otherwise is
	 * false.
	 */
	public static final String SAX_FEATURE_IS_STANDALONE = "http://xml.org/sax/features/is-standalone";

	/**
	 * A value of "true" indicates that the LexicalHandler will report the
	 * beginning and end of parameter entities.
	 */
	public static final String SAX_FEATURE_LEXICAL_HANDLER_PARAMETER_ENTITIES = "http://xml.org/sax/features/lexical-handler/parameter-entities";

	/**
	 * A value of "true" indicates namespace URIs and unprefixed local names for
	 * element and attribute names will be available.
	 */
	public static final String SAX_FEATURE_NAMESPACES = "http://xml.org/sax/features/namespaces";

	/**
	 * A value of "true" indicates that XML qualified names (with prefixes) and
	 * attributes (including xmlns* attributes) will be available.
	 */
	public static final String SAX_FEATURE_NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";

	/**
	 * A value of "true" indicates that system IDs in declarations will be
	 * absolutized (relative to their base URIs) before reporting.
	 */
	public static final String SAX_FEATURE_RESOLVE_DTD_URIS = "http://xml.org/sax/features/resolve-dtd-uris";

	/**
	 * Has a value of "true" if all XML names (for elements, prefixes,
	 * attributes, entities, notations, and local names), as well as Namespace
	 * URIs, will have been interned using java.lang.String.intern.
	 */
	public static final String SAX_FEATURE_STRING_INTERNING = "http://xml.org/sax/features/string-interning";

	/**
	 * Controls whether the parser reports Unicode normalization errors as
	 * described in section 2.13 and Appendix B of the XML 1.1 Recommendation.
	 */
	public static final String SAX_FEATURE_UNICODE_NORMALIZATION_CHECKING = "http://xml.org/sax/features/unicode-normalization-checking";

	/**
	 * Returns "true" if the Attributes objects passed by this parser in
	 * ContentHandler.startElement() implement the org.xml.sax.ext.Attributes2
	 * interface.
	 */
	public static final String SAX_FEATURE_USE_ATTRIBUTES2 = "http://xml.org/sax/features/use-attributes2";

	/**
	 * Returns "true" if the Locator objects passed by this parser in
	 * ContentHandler.setDocumentLocator() implement the
	 * org.xml.sax.ext.Locator2 interface.
	 */
	public static final String SAX_FEATURE_USE_LOCATOR2 = "http://xml.org/sax/features/use-locator2";

	/**
	 * Returns "true" if, when setEntityResolver is given an object implementing
	 * the org.xml.sax.ext.EntityResolver2 interface, those new methods will be
	 * used. Returns "false" to indicate that those methods will not be used.
	 */
	public static final String SAX_FEATURE_USE_ENTITY_RESOLVER2 = "http://xml.org/sax/features/use-entity-resolver2";

	/**
	 * Controls whether the parser is reporting all validity errors; if true,
	 * all external entities will be read.
	 */
	public static final String SAX_FEATURE_VALIDATION = "http://xml.org/sax/features/validation";

	/**
	 * Controls whether, when the namespace-prefixes feature is set, the parser
	 * treats namespace declaration attributes as being in the
	 * http://www.w3.org/2000/xmlns/ namespace.
	 */
	public static final String SAX_FEATURE_XMLNS_URIS = "http://xml.org/sax/features/xmlns-uris";

	/**
	 * Returns "true" if the parser supports both XML 1.1 and XML 1.0. Returns
	 * "false" if the parser supports only XML 1.0.
	 */
	public static final String SAX_FEATURE_XML_1_1 = "http://xml.org/sax/features/xml-1.1";

	public static final String SAX_PROPERTY_DECLARATION_HANDLER = "http://xml.org/sax/properties/declaration-handler";
	public static final String SAX_PROPERTY_LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";

	public static final String APACHE_FEATURE_ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
	
	
	/**
	 * True: Load the DTD and use it to add default attributes and set attribute types when parsing.  
	 * False: Build the grammar but do not use the default attributes and attribute types information it contains.  
	 */
	public static final String APACHE_FEATURE_LOAD_DTD_GRAMMAR = "http://apache.org/xml/features/nonvalidating/load-dtd-grammar";
	
	/**
	 *True: Load the external DTD. 
	 *False: Ignore the external DTD completely. 
	 *Default: true 
	 *Note: This feature is always on when validation is on. 
	 */
	public static final String APACHE_FEATURE_LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
	
	public static final String APACHE_PROPERTY_GRAMMAR_POOL ="http://apache.org/xml/properties/internal/grammar-pool";
}
