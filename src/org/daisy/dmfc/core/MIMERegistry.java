/*
 * Created on 2005-mar-22
 */
package org.daisy.dmfc.core;

/**
 * Singleton MIME registry class
 * @author LINUSE
 */
public class MIMERegistry {
	private static MIMERegistry registry = null;
	
	/**
	 * Private constructor
	 */
	private MIMERegistry() {
		// Nothing
	}
	
	/**
	 * Get an instance of the MIME registry 
	 * @return a MIMERegistry instance
	 */
	public static MIMERegistry instance() {
		if (registry == null) {
			registry = new MIMERegistry();
		}
		return registry;
	}
	
	/**
	 * Checks if two MIME types match.
	 * This function would typically return true for (pseudo code) matches("XHTML", "XML"),
	 * but not for matches("XML", "XHTML")
	 * @param a_subType a sub type
	 * @param a_superType a super type
	 * @return <code>true</code> if <code>a_subType</code> is a type of <code>a_superType</code>
	 */
	public boolean matches(String a_subType, String a_superType) {
		// FIXME something better here
		return a_subType.equals(a_superType);
	}
}
