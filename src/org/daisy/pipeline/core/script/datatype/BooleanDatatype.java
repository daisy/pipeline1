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

/**
 * A datatype for boolean values.
 * @author Linus Ericson
 */
public class BooleanDatatype extends Datatype {
	
	private static final long serialVersionUID = 1L;
	
	private String trueValue = null;
	private String falseValue = null;
	
	/**
	 * Constructor. If the <code>trueVal</code> or <code>falseVal</code> is
	 * <code>null</code>, the default values are 'true' and 'false', respectively. 
	 * @param trueVal the value representing 'true'
	 * @param falseVal the value representing 'false'
	 */
	public BooleanDatatype(String trueVal, String falseVal) {
		super(Type.BOOLEAN);
		if (trueVal == null) {
			this.trueValue = "true";
		} else {
			this.trueValue = trueVal;
		}
		if (falseVal == null) {
			this.falseValue = "false";
		} else {
			this.falseValue = falseVal;
		}
	}

	/**
	 * Gets the value representing 'false'
	 * @return the value reporesenting false
	 */
	public String getFalseValue() {
		return falseValue;
	}

	/**
	 * Gets the value representing 'true'
	 * @return the value representing true
	 */
	public String getTrueValue() {
		return trueValue;
	}

	@Override
	public void validate(String value) throws DatatypeException {
		if (!trueValue.equals(value) && !falseValue.equals(value)) {
			throw new DatatypeException("Boolean value must be '" + trueValue + "' or '" + falseValue + "'.");
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
        builder.append("boolean (").append(getTrueValue());
        builder.append(", ").append(getFalseValue()).append(")");
		return builder.toString();
	}

	
}
