package org_pef_dtbook2pef.system.tasks.textnode.filters;

/**
 * StringFilter is in interface used when replacing a string with another string.
 * 
 * @author  Joel Hakansson, TPB
 * @version 4 maj 2009
 * @since 1.0
 */
public interface StringFilter {
	
	/**
	 * Replaces a string with another string based on rules determined by the
	 * implementing class.
	 * @param str the string to replace
	 * @return returns the replacement string
	 */
	public String replace(String str);

}
