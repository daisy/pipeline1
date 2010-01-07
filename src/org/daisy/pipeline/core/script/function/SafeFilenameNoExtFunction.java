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
package org.daisy.pipeline.core.script.function;

import org.daisy.util.file.EFile;
import org.daisy.util.i18n.CharUtils;

/**
 * Script function for extracting the filename part of a path.
 * 
 * @author Linus Ericson
 */
public class SafeFilenameNoExtFunction extends Function {

	@Override
	public String apply(String value) {
		EFile file = new EFile(value);
		String name = file.getNameMinusExtension();
		return CharUtils.toRestrictedSubset(
				CharUtils.FilenameRestriction.Z3986, name);
	}

}
