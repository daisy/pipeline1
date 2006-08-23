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
