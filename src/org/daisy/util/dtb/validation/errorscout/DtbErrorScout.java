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

package org.daisy.util.dtb.validation.errorscout;

import java.net.URI;
import java.util.Iterator;

import org.daisy.util.fileset.interfaces.Fileset;
/**
 * @author Markus Gylling
 */
public interface DtbErrorScout {
	
	/**
	 * Execute the scouting procedure
	 * @param manifest absolute URI of the manifest file (ncc, opf, x) being input port for the DTB fileset
	 * @return true if errors were encountered, false otherwise
	 * @throws DtbErrorScoutException
	 */
	public boolean scout(URI manifest) throws DtbErrorScoutException;
		
	/**
	 * @return an iterator over the &lt;Exception&gt; errors HashSet populated during the last execution of {@link #scout(URI)}
	 */
	public Iterator getErrorsIterator();
					
	/**
	 * @return the Fileset object built during the last execution of {@link #scout(URI)}
	 */
	public Fileset getFileset();
	
}
