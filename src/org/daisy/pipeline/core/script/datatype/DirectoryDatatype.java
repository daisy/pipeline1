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
package org.daisy.pipeline.core.script.datatype;

import java.io.File;

/**
 * A datatype for directories 
 * @author Linus Ericson
 */
public class DirectoryDatatype extends Datatype {

	private String mType;
	
	/**
	 * Constructor. Allowed values for the type attribute are "input" and "output".
	 * @param type
	 */
	public DirectoryDatatype(String type) {
		super(Type.DIRECTORY);
		mType = type;
	}

	/**
	 * @return true if this is an input directory datatype, false otherwise
	 */
	public boolean isInput() {
		return "input".equals(mType);
	}
	
	/**
	 * @return true if this is an output directory datatype, false otherwise
	 */
	public boolean isOutput() {
		return !isInput();
	}
	
	@Override
	public void validate(String value) throws DatatypeException {
		if (isInput()) {
			File file = new File(value);
			if (!file.isDirectory()) {
				throw new DatatypeException("Input directory '" + file.toString() + "' is not a directory.");
			}
			if (!file.exists()) {
				throw new DatatypeException("Directory '" + file.toString() + "' does not exist.");
			}			
		}	
	}

}
