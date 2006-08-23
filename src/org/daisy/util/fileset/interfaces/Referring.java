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
 * Interface for FilesetFiles that can 
 * refer to other entities (local or remote) via URIs
 * @author Markus Gylling
 */
public interface Referring extends Descendant  {
		
	void putReferencedMember(FilesetFile file);
	
	/**
	 * @return a collection of URI strings, unparsed and unresolved, 
	 * as they barenaked appeared in this referrer.
	 */
	public Collection getUriStrings();
	
	/**
	 * @param uri absolute URI that may or may not be referenced from this referrer, 
	 * and may or may not represent the location of a colleague member of the current Fileset instance. 
	 * @return the corresponding member {@link org.daisy.util.fileset.interfaces.FilesetFile} object 
	 * if it is referenced from this member, null otherwise
	 */
	public FilesetFile getReferencedLocalMember(URI uri);
		
	/**
	 * @return a collection&lt;FilesetFile&gt; of the members referenced 
	 * from this member; ordered as appearing in document order 
	 */
	public Collection getReferencedLocalMembers();
	
}