package org_pef_dtbook2pef.system.tasks.layout.utils;


public class SplitResult {
	private final String text;
	private final boolean match;
	
	/**
	 * Create a new SplitResult
	 * @param text
	 * @param match
	 */
	public SplitResult (String text, boolean match) {
		this.text = text;
		this.match = match;
	}

	public String getText() {
		return text;
	}

	public boolean isMatch() {
		return match;
	}

}
