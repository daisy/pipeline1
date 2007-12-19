/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.daisy.util.xml.validation.jaxp;

import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.daisy.util.xml.validation.SchemaLanguageConstants;
import org.xml.sax.SAXException;

import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.SchemaReaderFactory;
import com.thaiopensource.validate.ValidationDriver;

/**
 *  This implementation uses Jing. It allows compound schema input (sch embedded in RNG or WXS).
 *  @author Markus Gylling
 */
public class SchematronValidator extends AbstractValidator {
	private ValidationDriver driver = null;
	private SchematronSchema schema = null;
	public static final String JING_SCHEMA_READER_KEY = "org.daisy.util.xml.validation.jaxp.SchematronValidator.SchemaReaderFactory"; 
	
	/*package*/ SchematronValidator(SchematronSchema schema) {
		this.schema = schema;
	}

	/*package*/ boolean initialize() {
		try {
			/*
			 * mg20071212:
			 * Due to various mem leak and deadlock issues, we are
			 * adding a layer here to optionally override Jings discovery
			 * of impls of com.thaiopensource.validate.SchemaReaderFactory.
			 * 
			 * If the given sysprop is not available, or class load fails,
			 * we fall back to Jings default behavior, which is to use
			 * jar:/META-INF/services/com.thaiopensource.validate.SchemaReaderFactory
			 */
									
			String schemaReaderFactoryValue = System.getProperty(JING_SCHEMA_READER_KEY);
			if(schemaReaderFactoryValue !=null && schemaReaderFactoryValue.length()>0) {
				SchemaReaderFactory srf = null;
				try{
					Class c = Class.forName(schemaReaderFactoryValue);
					srf = (SchemaReaderFactory)c.newInstance();			
					SchemaReader rd = srf.createSchemaReader(SchemaLanguageConstants.SCHEMATRON_NS_URI);		
					//System.err.println("DEBUG: SchematronValidator#initialize using explicit SchemaReaderFactory read from system properties: " + srf.getClass().getCanonicalName());
					driver = JingUtils.configDriver(this,driver,rd);
				}catch (Exception e) {
					System.err.println("Error in SchematronValidator#initialize when instantiating schemaReaderFactoryValue: " + e.getMessage());
					driver = null;
				}	
			}
			
			if(driver==null) {
				//fallback to default behavior
				//System.err.println("DEBUG: SchematronValidator#initialize using Jing discovery for SchemaReaderFactory ");
				driver = JingUtils.configDriver(this,driver);
			}
			
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
