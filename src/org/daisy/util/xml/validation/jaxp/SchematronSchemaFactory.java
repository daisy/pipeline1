package org.daisy.util.xml.validation.jaxp;

import org.daisy.util.xml.validation.SchemaLanguageConstants;

public class SchematronSchemaFactory extends AbstractSchemaFactory{
	/* 
	 * This is for the reflection instantiation that the SchemaFactoryFinder performs.
	 */
 	public SchematronSchemaFactory() {
 		super();
 		this.schemaLanguage = SchemaLanguageConstants.SCHEMATRON_NS_URI;
 	}
 	
	public boolean isSchemaLanguageSupported(String schemaLanguage) {
        if (schemaLanguage == null) throw new NullPointerException();        
        return (schemaLanguage.equals(SchemaLanguageConstants.SCHEMATRON_NS_URI));        
	}
}
