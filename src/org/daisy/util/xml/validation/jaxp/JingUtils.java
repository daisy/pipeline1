package org.daisy.util.xml.validation.jaxp;

import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.daisy.util.xml.validation.XmlReaderCreatorImpl;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;

/**
 * A set of code-duplication-minimization utilities for AbstractValidator subclasses that use Jing
 * @author Markus Gylling
 */
final class JingUtils {
	
	/**
	 * Performs a basic configuration of the com.thaiopensource.validate.ValidationDriver
	 */
	/*package*/ static ValidationDriver configDriver(AbstractValidator val, ValidationDriver driver) throws Exception {
		PropertyMapBuilder builder = new PropertyMapBuilder();

		/* Set the ErrorHandler.
		 * AbstractValidator instantiator sets self as default,
		 * so no need to check for null.
		 */
		builder.put(ValidateProperty.ERROR_HANDLER, val.getErrorHandler());

		/* Implement XmlReaderCreator in order to hook up an EntityResolver.
		 * ValidateProperty.ENTITY_RESOLVER does not seem to work.
		 * AbstractValidator instantiator sets CatalogEntityResolver as default,
		 * so no need to check for null.
		 */
		builder.put(ValidateProperty.XML_READER_CREATOR, new XmlReaderCreatorImpl(false, val.getEntityResolver()));

		/*
		 * initialize the driver
		 */
		return new ValidationDriver(builder.toPropertyMap());	
	}
	
	/**
	 * Loads the schema(s) carried in AbstractSchema
	 * @return true if all schemas were loaded successfully, false if at least one schema was not loaded sucessfully.
	 */
	/*package*/ static boolean loadSchemas(AbstractValidator validator, ValidationDriver driver, AbstractSchema schema) {
		boolean loadSuccess = true;
		try {
			InputSource[] schemaSources = schema.asInputSources();
			for (int i = 0; i < schemaSources.length; i++) {
				if (!driver.loadSchema(schemaSources[i])) {
					loadSuccess = false;
					validator.getErrorHandler().fatalError
						(new SAXParseException("Cannot load schema " + 
								schema.asInputSources()[i].getSystemId(), new LocatorImpl()));
				}
			}						
		} catch (Exception e) {
			try {
				loadSuccess = false;
				validator.getErrorHandler().fatalError(new SAXParseException(e.getMessage(), new LocatorImpl()));				
			} catch (SAXException e1) {

			}
		}		
		return loadSuccess;		
	}
	
	/**
	 * Validates a source using a driver. 
	 * @return true if the source is valid, false if invalid.
	 * @throws IOException if driver is null
	 * 
	 */
	/*package*/ static boolean validate(ValidationDriver driver, Source source) throws SAXException, IOException{
		boolean isValid = true;
		if(driver!=null) {
			if (!driver.validate(SAXSource.sourceToInputSource(source))) {
				isValid = false;
			}
		}else{
			throw new IOException("Validator not configured correctly (driver==null)");
		}
		return isValid;
	}
}
