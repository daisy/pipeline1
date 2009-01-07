/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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
package org.daisy.pipeline.core.script.datatype;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A datatype for string values.
 * @author Linus Ericson
 */
public class StringDatatype extends Datatype {
	
	private static final long serialVersionUID = 1L;

	private Pattern pattern = null;
	
	/**
	 * Constructor.
	 * @param regex a regex Pattern
	 */
	public StringDatatype(Pattern regex) {
		super(Type.STRING);
		pattern = regex;
	}

	@Override
	public void validate(String value) throws DatatypeException {
		if (pattern != null) {
			Matcher matcher = pattern.matcher(value);
			if (!matcher.matches()) {
				throw new DatatypeException("String value '" + value + "' does not match '" + pattern.pattern() + "'");
			}			
		}
	}
	
	/**
	 * Gets the regular expression for this datatype
	 * @return a regex Pattern
	 */
	public Pattern getRegex() {
		return pattern;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
        builder.append("string (matching ").append(getRegex());
        builder.append(")");
		return builder.toString();
	}
}
