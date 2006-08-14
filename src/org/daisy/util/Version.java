package org.daisy.util;

/**
 * Exposes global version information on the whole org.daisy.util package
 * <p>Version information is an ISO8691 date string (YYYY-MM-DD).</p>
 * @author Markus Gylling
 */
public class Version {

	private static final String VERSION = "2006-08-14";
	
	public static String getVersion() {
		return VERSION;
	}
	
}