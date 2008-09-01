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
 * A datatype for integer values.
 * @author Linus Ericson
 */
public class IntegerDatatype extends Datatype {

	private int min;
	private int max;
	
	/**
	 * Constructor. If any one of the minimum and maximum parameters is null,
	 * Integer.MIN_VALUE and Integer.MAX_VALUE is used, respectively. 
	 * @param min the minimum allowed integer for this datatype
	 * @param max the maximum allowed integer for this datatype
	 */
	public IntegerDatatype(Integer min, Integer max) {
		super(Type.INTEGER);
		if (min == null) {
			this.min = Integer.MIN_VALUE;
		} else {
			this.min = min.intValue();
		}
		if (max == null) {
			this.max = Integer.MAX_VALUE;
		} else {
			this.max = max.intValue();
		}
	}

	/**
	 * Gets the maximum integer for this datatype
	 * @return the maximum value
	 */
	public int getMax() {
		return max;
	}

	/**
	 * Gets the minimum integer for this datatype
	 * @return the minimum value
	 */
	public int getMin() {
		return min;
	}

	@Override
	public void validate(String value) throws DatatypeException {		
		try {
			int i = Integer.parseInt(value);
			if (i < getMin() || i > getMax()) {
				throw new DatatypeException("'" + value + "' is not in the interval [" + getMin() + "," + getMax() + "].");
			}
		} catch (NumberFormatException e) {
			throw new DatatypeException("'" + value + "' is not a valid number.", e);
		}		
		
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
        builder.append("integer [").append(getMin());
        builder.append(", ").append(getMax()).append("]");
		return builder.toString();
	}
}
