package org.daisy.util.fileset;

/**
 * Interface that any FilesetFile that can carry a UID must implement.
 * @author Markus Gylling
 */
public interface UIDCarrier {

	/**
	 * @return the UID as stated in this file, or null if no UID is present.
	 */
	public String getUID();
	
}
