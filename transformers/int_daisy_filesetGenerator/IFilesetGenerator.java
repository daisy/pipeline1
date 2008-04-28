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
package int_daisy_filesetGenerator;

import int_daisy_filesetGenerator.FilesetGeneratorFactory.OutputType;

import java.util.List;
import java.util.Map;

import org.daisy.util.file.Directory;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.util.FilesetFileFilter;

/**
 * Base interface for fileset generators.
 * @author Markus Gylling
 */

public interface IFilesetGenerator extends FilesetFileFilter {

	/**
	 * Combined Factory and Instantiation method. Does this implementation support the given input type, output type and parameters?
	 * @param input A list of Fileset instances that serve as input to the Fileset generation process. May not be null.
	 * @param output The type of output wanted. May not be null.
	 * @param parameters Optional configuration settings. May be null.
	 * @throws FilesetGeneratorException if the given parameters are not supported.
	 */
	void configure(List<Fileset> input, OutputType output, Map<String,Object> parameters) throws FilesetGeneratorException;
		
	/**
	 * Execute fileset generation.
	 */
	void execute(Directory destination) throws FilesetGeneratorException;
	
	
	
}
