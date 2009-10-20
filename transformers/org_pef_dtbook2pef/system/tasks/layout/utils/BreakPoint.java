package org_pef_dtbook2pef.system.tasks.layout.utils;

public class BreakPoint {
	private final String head;
	private final String tail;
	private final boolean hardBreak;

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
	 * Test if this BreakPoint was achieved by breaking on a character other than hyphen, soft hyphen or space
	 * @return returns true if this BreakPoint was achieved by breaking on a character other than hyphen, soft hyphen or space
	 */
	public boolean isHardBreak() {
		return hardBreak;
	}
}
