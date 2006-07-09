package org.daisy.util.xml.validation;

public final class SchemaLanguageConstants {

	public static final String W3C_XML_SCHEMA_NS_URI  = "http://www.w3.org/2001/XMLSchema".intern();
	public static final String RELAXNG_NS_URI  = "http://relaxng.org/ns/structure/1.0".intern();
	public static final String SCHEMATRON_NS_URI  = "http://www.ascc.net/xml/schematron".intern();
	public static final String ISO_SCHEMATRON_NS_URI  = "http://purl.oclc.org/dsdl/schematron".intern();
	
	/**
	 * @param string A string that may or may or may not match one of the SchemaLanguage constants
	 * @return true if match, false otherwise.
	 */
	public static boolean hasEntry(String string){
		string = string.intern();
		return (string == W3C_XML_SCHEMA_NS_URI
				|| string == RELAXNG_NS_URI
				|| string == SCHEMATRON_NS_URI
				|| string == ISO_SCHEMATRON_NS_URI
				);
	}
	
}
