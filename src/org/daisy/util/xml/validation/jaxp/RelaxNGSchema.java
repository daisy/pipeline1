package org.daisy.util.xml.validation.jaxp;

import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

/**
 * 
 * @author Markus Gylling
 */
public class RelaxNGSchema extends AbstractSchema {
	
	RelaxNGSchema(URL schema, SchemaFactory originator) {
		super(schema,originator);
	}
	
	RelaxNGSchema(Source[] sources, SchemaFactory originator){
		super(sources,originator);
	}
	
	public Validator newValidator()  {
		RelaxNGValidator validator = new RelaxNGValidator(this);
		validator.propagateHandlers(this.originator);	
		validator.initialize();
		return validator;
	}

	public ValidatorHandler newValidatorHandler() {
		return null; //TODO
	}

}
