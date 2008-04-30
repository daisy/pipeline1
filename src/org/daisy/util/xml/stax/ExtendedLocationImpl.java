/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.util.xml.stax;

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.Location;


/**
 * An extension to javax.xml.stream.Location that allows carrying arbitrary additional String values.
 * @author Markus Gylling
 */
public class ExtendedLocationImpl extends LocationImpl {
	private Map<InformationType,String> mExtendedValues = null;
	
	/**
	 * Type identifiers for different types of extended location information. 
	 * <p>To provide additional types, extend this enum; the rest of the class API is type agnostic.</p>
	 * @author Markus Gylling
	 */
	public enum InformationType {
		PRECEDING_HEADING {
		    public String toString() {
		        return "Current heading";
		    }
		},
		PRECEDING_PAGE{
		    public String toString() {
		        return "Current page";
		    }
		},
		XPATH{
		    public String toString() {
		        return "XML Path";
		    }
		},
		PRECEDING_TEXT{
		    public String toString() {
		        return "Preceding text";
		    }
		}
	}
			
	
	/**
	 * Constructor.
	 * @param loc the Location to extend.
	 */
	public ExtendedLocationImpl(Location loc) {
		super(loc);
		mExtendedValues = new HashMap<InformationType,String>();		
	}
	
	/**
	 * Populate the extended fields.
	 */
	public void setExtendedLocationInfo(InformationType type, String value) {
		mExtendedValues.put(type, value);
	}
	
	/**
	 * Get an extended location field value.
	 * @param type the enum identifier of the field (ExtendedLocation.InformationType).
	 */
	public String getExtendedLocationInfo(InformationType type) {		
		return mExtendedValues.get(type);
	}
}
