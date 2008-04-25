package int_daisy_filesetGenerator;

import int_daisy_filesetGenerator.FilesetGeneratorFactory.OutputType;

import java.util.List;
import java.util.Map;

import org.daisy.util.file.EFolder;
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
	void execute(EFolder destination) throws FilesetGeneratorException;
	
	
	
}
