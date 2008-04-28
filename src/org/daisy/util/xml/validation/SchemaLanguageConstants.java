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

package org.daisy.util.xml.validation;

public final class SchemaLanguageConstants {

	public static final String W3C_XML_SCHEMA_NS_URI  = "http://www.w3.org/2001/XMLSchema".intern();
	public static final String RELAXNG_NS_URI  = "http://relaxng.org/ns/structure/1.0".intern();
	public static final String SCHEMATRON_NS_URI  = "http://www.ascc.net/xml/schematron".intern();
	public static final String ISO_SCHEMATRON_NS_URI  = "http://purl.oclc.org/dsdl/schematron".intern();
	public static final String NVDL_NS_URI  = "http://purl.oclc.org/dsdl/nvdl/ns/structure/1.0".intern();
	
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
				|| string == NVDL_NS_URI
				);
	}
	
	/**
	 * @return a nice name string representation of the schema language constants.
	 */
	public static String toNiceNameString(String schemaLanguageConstant) {
		if (schemaLanguageConstant == W3C_XML_SCHEMA_NS_URI) {
			return "W3C XML Schema";
		}else if (schemaLanguageConstant == RELAXNG_NS_URI){
			return "RelaxNG Schema";
		}else if (schemaLanguageConstant == SCHEMATRON_NS_URI){
			return "Schematron Schema";
		}else if (schemaLanguageConstant == ISO_SCHEMATRON_NS_URI){
			return "ISO Schematron Schema";
		}else if (schemaLanguageConstant == NVDL_NS_URI){
			return "NVDL Schema";
		}
		return null;
	}
	
}
