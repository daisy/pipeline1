package org_pef_dtbook2pef.system.tasks.layout.flow;

public class Position {
	boolean isRelative;
	double value;
	
	/**
	 * 
	 * @param value
	 * @param isRelative
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

	public static void main(String[] args) {
		Position p = parsePosition("33%");
		System.out.println(p.isRelative + " " + p.getValue() + " " + p.makeAbsolute(30));
	}
}
