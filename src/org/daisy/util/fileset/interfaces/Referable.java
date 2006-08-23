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

package org.daisy.util.fileset.interfaces;

import java.net.URI;
import java.util.Collection;

/**
 * Interface for FilesetFiles that can be 
 * referred to by other FilesetFiles
 * @author Markus Gylling
 */
public interface Referable extends Descendant {
	/**
	 * @param uri absolute URI of a FilesetFile Fileset member that may or may not refer to this member
	 * @return the corresponding member (@link FilesetFile) object if referring to this member, null otherwise
	 * @throws FilesetFatalException if this collection has not been populated
	 */
	public FilesetFile getReferringLocalMember(URI uri) throws NullPointerException;
		
	/**
	 * @return the referring members collection; not ordered
	 * @throws FilesetFatalException if this collection has not been populated
	 */
	public Collection getReferringLocalMembers() throws NullPointerException;
		
}