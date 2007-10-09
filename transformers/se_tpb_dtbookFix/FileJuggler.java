package se_tpb_dtbookFix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.daisy.util.file.FileUtils;
import org.daisy.util.file.TempFile;

/**
 * Given an initial input file and the final output file, this class can be 
 * used to limit the code needed to handle temporary files in a 
 * transformer. After each step (file written) the temporary files are 
 * swapped by calling swap() and you are ready to use the files again. 
 * Very convenient together with optional steps.
 * 
 * @author  Joel Hakansson, TPB
 * @version 27 sep 2007
 * @since 1.0
 */
public class FileJuggler {
	private TempFile t1;
	private TempFile t2;
	private File input;
	private File output;
	private boolean toggle;
	
	/**
	 * Default constructor
	 * @param input An existing input file
	 * @param output An output file
	 * @throws IOException 
	 */
	public FileJuggler(File input, File output) throws IOException {
		this.t1 = null;
		this.t2 = null;
		this.toggle = true;
		this.input = input;
		this.output = output;
		if (!input.exists()) throw new FileNotFoundException();
		this.t1 = new TempFile();
		this.t2 = new TempFile();
		FileUtils.copy(input, this.t1.getFile());
	}

	/**
	 * Get the current input file
	 * @return Returns the current input file
	 */
	public File getInput() {
		return toggle ? t1.getFile() : t2.getFile();
	}
	
	/**
	 * Get the current output file
	 * @return Returns the current output file
	 */
	public File getOutput() {
		return toggle ? t2.getFile() : t1.getFile();
	}
	
	/**
	 * Swap the input and output file before writing to the output again
	 * @throws FileNotFoundException 
	 */
	public void swap() throws FileNotFoundException {
		// Check that the soon to be input file exists
		if (getOutput().exists()) {
			toggle = !toggle;
			getOutput().delete();
		} else {
			throw new FileNotFoundException("Cannot swap to a non-existing file.");
		}
	}
	
	/**
	 * Must be called when done
	 * Closes the temporary files and copies the result to the output file
	 * @throws IOException 
	 */
	public void close() throws IOException {
		if (getOutput().exists()) FileUtils.copy(getOutput(), output);
		else if (getInput().exists()) FileUtils.copy(getInput(), output);
		else FileUtils.copy(input, output);
		t1.delete();
		t2.delete();
	}

}
