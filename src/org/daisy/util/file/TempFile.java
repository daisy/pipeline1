/*
 * Created on 2005-mar-14
 */
package org.daisy.util.file;

import java.io.File;
import java.io.IOException;

/**
 * Creates a temporary file. The file will be automatically deleted when
 * the virtual machine exists.
 * @author LINUSE
 */
public class TempFile {
	
	private static File tempDir = null;
	
	private File file;
	
	/**
	 * Sets the directory where the temporary files will be stored.
	 * If the directory is not set, the temporary directory of the system
	 * will be used.
	 * @param a_directory a directory
	 */
	public static void setTempDir(File a_directory) {
		if (a_directory.isDirectory()) {
			tempDir = a_directory;
		}
	}
	
	/**
	 * Creates a new TempFile.
	 * @throws IOException
	 */
	public TempFile() throws IOException {
		file = File.createTempFile("temp", ".tmp", tempDir);
		file.deleteOnExit();
	}
	
	/**
	 * Creates a new <code>TempFile</code> and returns the <code>File</code> reference to it.
	 * @return the <code>File</code> reference to to the <code>TempFile</code>
	 * @throws IOException
	 */
	public static File create() throws IOException {
		TempFile _temp = new TempFile();
		return _temp.getFile();
	}
	
	/**
	 * @return the <code>File</code> reference to to the <code>TempFile</code> 
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Deletes the temporary file. The file will automatically deleted on exit, but
	 * this function can be used to delete the file manually earlier.
	 * @return <code>true</code> if the deletion was successful, <code>false</code> otherwise
	 */
	public boolean delete() {
		return file.delete();
	}
}
