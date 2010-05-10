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

/**
 * Represents a OEB OPF (OpenEbook Package File) file,
 * versions 1.01 or 1.2. For specific subtypes of OPF see:
 * @see org.daisy.util.fileset.Z3986OpfFile
 * @see org.daisy.util.fileset.NimasOpfFile
 * @author Markus Gylling
 */
public interface OpfFile extends XmlFile, ManifestFile {
	static String mimeStringConstant = MIMEConstants.MIME_TEXT_XML;
	
	/**
	 * @return an ordered collection of FilesetFile subclasses listed in spine.
	 * <p>The particular subclass(es) of FilesetFile returned will vary depending on
	 * whether this class has been subclassed (NimasOpfFile, Z3986OpfFile).</p>
	 */
	public Collection<FilesetFile> getSpineItems() throws IllegalStateException;

	/**
	 * @return the value of the Dc:Format metadata item; null if not existing
	 */
	public String getMetaDcFormat();
	
	/**
	 * @return the value of the Dc:Title metadata item; null if not existing
	 */
	public String getMetaDcTitle();

	/**
	 * @return the value of the Dc:Creator metadata item; null if not existing
	 */
	public String getMetaDcCreator();
	
	/**
	 * @return the value of the Dc:Identifier metadata item set as UID; null if not existing
	 */
	public String getUID();
}
