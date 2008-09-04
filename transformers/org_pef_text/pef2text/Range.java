package org_pef_text.pef2text;

/**
 * 
 * @author  Joel Hakansson, TPB
 * @version 3 sep 2008
 * @since 1.0
 */
public class Range {

	private int from;
	private int to;
	
	/**
	 * Create a new range.
	 * @param from first page, inclusive
	 * @param to last page, inclusive
	 */
	public Range(int from, int to) {
		init(from, to);
	}
	
	/**
	 * Create a new range.
	 * @param from first page, inclusive
	 */
	public Range(int from) {
		init(from, Integer.MAX_VALUE);
	}
	
	private void init(int from, int to) {
		if (to<from || from<1 || to<1) {
			throw new IllegalArgumentException("Illegal range: " + from + "-" + to);
		}
		this.from = from;
		this.to = to;
	}
	
	/**
	 * 
	 * @param range
	 * @return
	 */
	public static Range parseRange(String range) {
		String[] str = range.split("-");
		if (str.length==1) {
			if (range.indexOf("-")>0){
				return new Range(Integer.parseInt(str[0]));
			} else {
				return new Range(Integer.parseInt(str[0]), Integer.parseInt(str[0]));
			}
		} else {
			if ("".equals(str[0])) {
				return new Range(1, Integer.parseInt(str[1]));
			} else {
				return new Range(Integer.parseInt(str[0]), Integer.parseInt(str[1]));
			}
		}
	}
	
	/**
	 * Test if a value is in range
	 * @param value
	 * @return returns true if value is in range, false otherwise
	 */
	public boolean inRange(int value) {
		if (value>=from && value<=to) return true;
		return false;
	}

}