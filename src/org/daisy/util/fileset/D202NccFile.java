/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.util.fileset;

import java.util.Collection;

import org.daisy.util.mime.MIMEConstants;
import org.daisy.util.xml.SmilClock;

/**
 * Represents the ncc.html file in a Daisy 2.02 fileset
 * @author Markus Gylling
 */
public interface D202NccFile extends Xhtml10File, ManifestFile {
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_X_DTBD202NCC_XML;
	
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
	 *returns the value of meta dc:creator if set, null otherwise
	 */
	public String getDcCreator();
	
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
	
	/**
	 * This method is equal to OpfFile.getSpineItems for a Z3986OpfFile;
	 * ie it returns an ordered list FilesetFiles constituting the play order 
	 * of the DTB.
	 * @return an ordered collection of D202SmilFile instances.
	 */
	public Collection<D202SmilFile> getSpineItems() throws IllegalStateException;
}
