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

/**
 * Gathers constants that represent those types of filesets that this package
 * claims to support
 * 
 * @author Markus Gylling
 * @author Romain Deltour
 */
public enum FilesetType {

	DAISY_202("DAISY 2.02 DTB"),
	Z3986("Z3986 DTB"),
	NIMAS("NIMAS fileset"),
	OPS_20("OPS 2.0 fileset"), 
	OPS_EPUB("OPS 2.0 EPUB File"),
	Z3986_RESOURCEFILE("Z3986 Resourcefile"),
	XHTML_DOCUMENT("XHTML document"),
	HTML_DOCUMENT("HTML document"),
	DTBOOK_DOCUMENT("Dtbook document"),
	CSS("CSS document"),
	PLAYLIST_M3U("M3U playlist"),
	PLAYLIST_PLS("PLS playlist"),
	UNKNOWN("unknown");

	private String nicename;

	private FilesetType(String nicename) {
		this.nicename = nicename;
	}

	public String toNiceNameString() {
		return nicename;
	}

}
