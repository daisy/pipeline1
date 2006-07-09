package org.daisy.util.xml.validation.jaxp;

import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.xml.sax.SAXException;

import com.thaiopensource.validate.ValidationDriver;

/**
 * This implementation uses Jing.
 * @author Markus Gylling
 */
public class RelaxNGValidator extends AbstractValidator {

	/*package*/ ValidationDriver driver = null;
	/*package*/ RelaxNGSchema schema = null;

	/*package*/RelaxNGValidator(RelaxNGSchema schema) {
		this.schema = schema;
	}
	
	/*package*/ boolean initialize() {		
		try {
			driver = JingUtils.configDriver(this,driver);
		} catch (Exception e) {
			return false;
		}				 		
		return JingUtils.loadSchemas(this, driver, schema);
	}

	public void validate(Source source, Result result) throws SAXException, IOException {
		//no PSVI in these whereabouts.
		this.validate(source);
	}

	public void validate(Source source) throws SAXException, IOException {
		JingUtils.validate(driver,source);
	}
}
