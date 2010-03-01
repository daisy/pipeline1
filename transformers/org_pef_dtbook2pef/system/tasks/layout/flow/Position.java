package org_pef_dtbook2pef.system.tasks.layout.flow;

/**
 * Position is a data object for an integer position.
 * @author Joel Håkansson, TPB
 *
 */
public class Position {

	boolean isRelative;
	double value;
	
	/**
	 * Create a new Position with the supplied value
	 * @param value the position
	 * @param isRelative if true, the value is a percentage 
	 * @throws IllegalArgumentException if the value is less than zero.
	 */
	public Position(double value, boolean isRelative) {
		if (value<0) {
			throw new IllegalArgumentException("Value must be positive " + value);
		}
		this.isRelative = isRelative;
		this.value = value;
	}
	
	public static Position parsePosition(String pos) {
		pos = pos.trim();
		if (pos.endsWith("%")) {
			// remove %
			pos = pos.substring(0, pos.length()-1).trim();
			return new Position(Double.parseDouble(pos)/100, true);
		} else {
			return new Position(Double.parseDouble(pos), false);
		}
	}
	
	public boolean isRelative() {
		return isRelative;
	}

	public double getValue() {
		return value;
	}
	
	public int makeAbsolute(int width) {
		double ret;
		if (isRelative()) {
			ret = width * getValue();
		} else {
			ret = getValue();
		}
		return (int)Math.round(ret);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isRelative ? 1231 : 1237);
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (isRelative != other.isRelative)
			return false;
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value))
			return false;
		return true;
	}
}
