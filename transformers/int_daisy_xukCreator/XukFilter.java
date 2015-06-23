/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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
package int_daisy_xukCreator;

import java.util.Map;

import org.daisy.util.file.Directory;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.util.FilesetFileFilter;

/**
 * Abstract base for all XUK creators. <p>Any implementation 
 * must provide a zero-argument constructor as its default constructor.</p>
 * @author Markus Gylling
 */
public abstract class XukFilter {

	/**
	 * Method called during factory implementation discovery.
	 */
	public abstract boolean supports(Fileset inputFileset, Map<String,Object> parameters);
	
	/**
	 * Perform the conversion between input and XUK.
	 */
	public abstract void createXuk(Fileset inputFileset, Map<String,Object> parameters, Directory destination) throws XukFilterException;
	
	/**
	 * Provide a FilesetFileFilter to the owner transformer to decide which members of
	 * the input fileset to move to destination after the XUK has been created.
	 */	
	public abstract FilesetFileFilter getCopyFilter();

}
