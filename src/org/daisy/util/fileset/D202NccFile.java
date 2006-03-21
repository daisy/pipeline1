package org.daisy.util.fileset;

import org.daisy.util.xml.SmilClock;

/**
 * Represents the ncc.html file in a Daisy 2.02 fileset
 * @author Markus Gylling
 */
public interface D202NccFile extends Xhtml10File, ManifestFile{
	public SmilClock getStatedDuration();
	/**
	 *returns the value of meta dc:identifier if set, null otherwise
	 */
	public String getDcIdentifier();
	/**
	 *returns the value of meta dc:title if set, null otherwise
	 */
	public String getDcTitle();
	
	/**
	 *returns true if this ncc contains indications that it is
	 *a part of a multivolume DTB. The two indicators 
	 *that must present to return true are:
	 *<ul>
	 * <li>meta ncc:setInfo with a value other than '1 of 1'</li>
	 * <li>any "rel" attribute on any child of body</li>
	 *</ul>
	 */
	public boolean hasMultiVolumeIndicators();
}
