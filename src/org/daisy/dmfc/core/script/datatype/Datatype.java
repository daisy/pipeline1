/*
 * Daisy Pipeline
 * Copyright (C) 2007  Daisy Consortium
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
package org.daisy.dmfc.core.script.datatype;

/**
 * Abstract base class for all script parameter data types.
 * @author Linus Ericson
 */
public abstract class Datatype {	
	public static enum Type {BOOLEAN, ENUM, STRING, INTEGER, FILE, FILES, DIRECTORY};
	
	private Type type;
	
	/**
	 * Constructor.
	 * @param type the type of created datatype
	 */
	public Datatype(Type type) {
		this.type = type;
	}
	
	/**
	 * Gets the type
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * Validate the supplied value against this datatype.
	 * @param value the value to validate
	 * @throws DatatypeException if the supplied value isn't valid
	 */
	public abstract void validate(String value) throws DatatypeException;
}
