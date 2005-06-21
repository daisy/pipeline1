package org.daisy.util.fileset;

/**
 * Represents the ncc.html file in a Daisy 2.02 fileset
 * @author Markus Gylling
 */
public interface D202NccFile extends Xhtml10File, ManifestFile{
	public SmilClock getStatedDuration();
}
