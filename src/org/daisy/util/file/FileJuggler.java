package org.daisy.util.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Given an initial input file and a final output file, this class can be 
 * used to limit the code needed to handle temporary files in a 
 * transformer. After each step (file written) the temporary files are 
 * swapped by calling swap() and you are ready to use the files again. 
 * Very convenient together with optional steps.
 * 
 * Note: FileJuggler does not work on zero byte files. The output file
 * must contain data when a call to swap() is made.
 * 
 * @author  Joel Hakansson, TPB
 * @version 13 december 2007
 * @since 1.0
 */
public class FileJuggler {
	private File t1;
	private File t2;
	private File output;
	private boolean toggle;
	
	/**
	 * Constructs a new FileJuggler object
	 * 
	 * @param input An existing input file
	 * @param output An output file
	 * @throws IOException  An IOException is thrown if the input does not exist
	 * or if the input or output is a directory or if the temporary 
	 * files could not be created.
	 */
	public FileJuggler(File input, File output) throws IOException {
		if (!input.exists()) throw new FileNotFoundException();
		if (!input.isFile() || (output.exists() && !output.isFile())) {
			throw new IOException("Cannot perform this operation on directories.");
		}
		this.toggle = true;
		this.output = output;
		this.t1 = TempFile.create();
		this.t2 = TempFile.create();
		FileUtils.copy(input, this.t1);
	}

	/**
	 * Get the current input file
	 * @return Returns the current input file or null if FileJuggler has been closed
	 */
	public File getInput() {
		return toggle ? t1 : t2;
	}
	
	/**
	 * Get the current output file
	 * @return Returns the current output file or null if FileJuggler has been closed
	 */
	public File getOutput() {
		return toggle ? t2 : t1;
	}
	
	/**
	 * Swap the input and output file before writing to the output again
	 * @throws IOException  An IOException is thrown if FileJuggler has been
	 * closed or if the output file is open or empty.
	 */
	public void swap() throws IOException {
		if (t1==null || t2==null) {
			throw new IllegalStateException("Cannot swap after close.");
		}
		if (getOutput().length()>0) {
			toggle = !toggle;
			// reset the new output to length()=0
			new FileOutputStream(getOutput()).close();
		} else {
			throw new IOException("Cannot swap to an empty file.");
		}
	}
	
	/**
	 * Closes the temporary files and copies the result to the output file.
	 * Closing the FileJuggler is a mandatory last step after which no other
	 * calls to the object should be made.
	 * @throws IOException  An IOException is thrown e.g. if FileJuggler is 
	 * closed or if the temporary files have been deleted.  
	 */
	public void close() throws IOException {
		if (t1==null || t2==null) {
			throw new IllegalStateException("Already closed.");
		}
		try {
			if (getOutput().length()>0) FileUtils.copy(getOutput(), output);
			else if (getInput().length()>0) FileUtils.copy(getInput(), output);
			else {
				throw new IOException("Temporary files corrupted.");
			}
		} finally {
			t1.delete();
			t2.delete();
			t1 = null;
			t2 = null;
		}
	}

}
