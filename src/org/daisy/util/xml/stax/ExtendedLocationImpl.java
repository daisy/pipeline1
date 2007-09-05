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
		super();
		super.setCharacterOffset(loc.getCharacterOffset());
		super.setColumnNumber(loc.getColumnNumber());
		super.setLineNumber(loc.getLineNumber());
		super.setPublicId(loc.getPublicId());
		super.setSystemId(loc.getSystemId());
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
