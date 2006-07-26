package org.daisy.util.xml.validation.jaxp;

import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.xml.sax.SAXException;

import com.thaiopensource.validate.ValidationDriver;

/**
 *  This implementation uses Jing. It allows compound schema input (sch embedded in RNG or WXS).
 *  @author Markus Gylling
 */
public class SchematronValidator extends AbstractValidator {
	private ValidationDriver driver = null;
	private SchematronSchema schema = null;
	
	/*package*/ SchematronValidator(SchematronSchema schema) {
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
