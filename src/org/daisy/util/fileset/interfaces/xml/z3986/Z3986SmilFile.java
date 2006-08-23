/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.daisy.util.fileset.interfaces.xml.z3986;

import org.daisy.util.fileset.interfaces.xml.SmilFile;
import org.daisy.util.mime.MIMEConstants;
import org.daisy.util.xml.SmilClock;

/**
 * Represents an SMIL file in a Z3986 fileset, irrespective of Z3986 subversion
 * @author Markus Gylling
 */
public interface Z3986SmilFile extends SmilFile {
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_SMIL;
	/**
	 * DTB specific SMIL phenomenon.
	 * @return if given, the stated value for TotalElapsedTime (==time prior to onset of this smil file).
	 */
	public SmilClock getStatedTotalElapsedTime();
}
