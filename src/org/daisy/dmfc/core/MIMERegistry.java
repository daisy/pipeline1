/*
 * DMFC - The DAISY Multi Format Converter
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
package org.daisy.dmfc.core;

/**
 * Singleton MIME registry class
 * @author Linus Ericson
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
