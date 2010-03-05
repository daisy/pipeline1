package org_pef_dtbook2pef.system.tasks.layout.utils;

/**
 * <p>A BreakPoint is a data object to keep the information about a break point result.
 * Since this implementation uses two Strings, rather than the original String and
 * an integer for the break point position, it can be used with non standard hyphenation 
 * algorithms.</p>
 * @author Joel Håkansson, TPB
 */
public class BreakPoint {
	private final String head;
	private final String tail;
	private final boolean hardBreak;

	/**
	 * Create a new BreakPoint.
	 * @param head the part of the original String that fits within the target break point 
	 * @param tail the part of the original String that is left
	 * @param hardBreak set to true if a break point could not be achieved with respect for break point boundaries 
	 */
	public BreakPoint(String head, String tail, boolean hardBreak) {
		this.head = head;
		this.tail = tail;
		this.hardBreak = hardBreak;
	}
	
	/**
	 * Get the head part of the BreakPoint String
	 * @return returns the head part of the BreakPoint String
	 */
	public String getHead() {
		return head;
	}

	/**
	 * Get the tail part of the BreakPoint String
	 * @return returns the tail part of the BreakPoint String
	 */
	public String getTail() {
		return tail;
	}
	
	/**
	 * Test if this BreakPoint was achieved by breaking on a character other 
	 * than a valid break point character (typically hyphen, soft hyphen or space).
	 * @return returns true if this BreakPoint was achieved by breaking on a character other than hyphen, soft hyphen or space
	 */
	public boolean isHardBreak() {
		return hardBreak;
	}
}
