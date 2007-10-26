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

import org.daisy.util.xml.validation.XmlReaderCreatorImpl;
import org.xml.sax.SAXException;

import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;

/**
 * This implementation uses oNVDL, which uses Jing.
 * @author Markus Gylling
 */
public class NVDLValidator extends AbstractValidator {

	/*package*/ ValidationDriver driver = null;
	/*package*/ NVDLSchema schema = null;

	/*package*/NVDLValidator(NVDLSchema schema) {
		this.schema = schema;
	}
	
	/*package*/ boolean initialize() {		
		try {
			driver = JingUtils.configDriver(this,driver);
		} catch (Exception e) {
			return false;
		}				 		
		return JingUtils.loadSchemas(this, driver, schema);
//		PropertyMapBuilder builder = new PropertyMapBuilder();
//		builder.put(ValidateProperty.ERROR_HANDLER, this.getErrorHandler());
//		builder.put(ValidateProperty.XML_READER_CREATOR, new XmlReaderCreatorImpl(false, this.getEntityResolver()));		
//		builder.put(ValidateProperty.ENTITY_RESOLVER, this.getEntityResolver());
		
	}

	public void validate(Source source, Result result) throws SAXException, IOException {
		//no PSVI in these whereabouts.
		this.validate(source);
	}

	public void validate(Source source) throws SAXException, IOException {
		JingUtils.validate(driver,source);
	}
}
