package org.daisy.util.xml.validation.jaxp;

import org.daisy.util.xml.validation.SchemaLanguageConstants;

public class RelaxNGSchemaFactory extends AbstractSchemaFactory {
	/* 
	 * This is for the discovery instantiation that the SchemaFactoryFinder performs.
	 */
 	public RelaxNGSchemaFactory() {
 		super();
 		this.schemaLanguage = SchemaLanguageConstants.RELAXNG_NS_URI;
 	}
 	
	public boolean isSchemaLanguageSupported(String schemaLanguage) {
        if (schemaLanguage == null) throw new NullPointerException();        
        return (schemaLanguage.equals(SchemaLanguageConstants.RELAXNG_NS_URI));        
	}
}
