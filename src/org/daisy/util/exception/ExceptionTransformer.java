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

package org.daisy.util.exception;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;
/**
 * 
 * @author Markus Gylling
 */
public class ExceptionTransformer {

	public static SAXParseException newSAXParseException(Exception e){
		LocatorImpl loc = new LocatorImpl();		
		
		try{
			if(e instanceof TransformerException) {			
				TransformerException te = (TransformerException) e;		
				loc.setLineNumber(te.getLocator().getLineNumber());
				loc.setColumnNumber(te.getLocator().getColumnNumber());
				loc.setPublicId(te.getLocator().getPublicId());
				loc.setSystemId(te.getLocator().getSystemId());			
			}
		}catch (Exception e2) {
			//silence
		}

		return new SAXParseException(e.getMessage(), loc, e);
	}
	
}
