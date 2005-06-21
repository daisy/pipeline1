package org.daisy.util.fileset;

/**
 * Represents an XHTML 1.0 file
 * @author Markus Gylling 
 */
public interface Xhtml10File extends XmlFile{

	/**
	 * @return true if the xhtml heading sequence is correctly hierarchical, false otherwise
	 */
	public boolean hasCorrectHeadingSequence();
	
}
