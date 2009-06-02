/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
package org.daisy.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URI;
import java.nio.channels.FileChannel;

/**
 * Basic file functions.
 * 
 * @author Linus Ericson
 * @author Markus Gylling
 */
public final class FileUtils {
	
	private FileUtils() {}

	/**
	 * Copy a file.
	 * 
	 * @param inFile
	 *            source file
	 * @param outFile
	 *            destination file
	 * @throws IOException
	 *             if anything bad happens.
	 */
	public static void copy(File inFile, File outFile) throws IOException {
		if (inFile.equals(outFile)) {
			return;
		}
		createDirectory(outFile.getParentFile());
		FileInputStream fis = new FileInputStream(inFile);
		FileOutputStream fos = new FileOutputStream(outFile);
		byte[] buf = new byte[1024];
		int i = 0;
		while ((i = fis.read(buf)) != -1) {
			fos.write(buf, 0, i);
		}
		fis.close();
		fos.close();
	}

	/**
	 * Copy a file using java.nio
	 * 
	 * @param inFile
	 *            source file
	 * @param outFile
	 *            destination file
	 * @throws IOException
	 *             if anything bad happens.
	 */

	public static void copyFile(File inFile, File outFile) throws IOException {		
		if (inFile.equals(outFile)) return;		
		if (!inFile.isFile()) throw new IOException(inFile.getAbsolutePath() + " is not a file");				
		if (outFile.exists()) {
			if (!outFile.isFile()) {
				throw new IOException(outFile.getAbsolutePath() + " is not a file");
			}	
		} else {
			createDirectory(outFile.getParentFile());
			outFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(inFile).getChannel();
			destination = new FileOutputStream(outFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}


	/**
	 * Copy a file to a directory. If a base directory is specified, the file is
	 * first relativized to this base directory and the relative path is
	 * copied to the destination directory, creating sub directories if they do
	 * not exist.
	 * 
	 * @param file
	 *            the file to copy
	 * @param toDir
	 *            the destination directory
	 * @param baseDir
	 *            the base directory to relativize the file to copy
	 * @throws IOException
	 *             if anything bad happens
	 */
	public static void copyChild(File file, File toDir, File baseDir)
			throws IOException {
		if (file == null || toDir == null) {
			throw new IllegalArgumentException();
		}
		Directory toDirectory = new Directory(toDir);
		if (baseDir == null) {
			toDirectory.addFile(file, true);
		} else if (!file.getCanonicalPath().equals(
				baseDir.getParentFile().getCanonicalPath())) {
			if (file.getParentFile().getCanonicalPath().equals(
					baseDir.getParentFile().getCanonicalPath())) {
				// file is in the base directory
				toDirectory.addFile(file, true);
			} else {
				// file is in a sub-directory
				URI relative = baseDir.toURI().relativize(
						file.getParentFile().toURI());
				if (!relative.toString().startsWith("..")) {
					Directory subdir = new Directory(toDirectory, relative
							.getPath());
					FileUtils.createDirectory(subdir);
					subdir.addFile(file, true);
				}
			}
		}
	}
	
	/**
	 * Deletes the given file or directory. If the argument is a directory, then
	 * the directory is deleted recursively.
	 * 
	 * @param file
	 *            the file to delete
	 * @return <code>true</code> if and only if the file or directory is
	 *         successfully deleted; <code>false</code> otherwise
	 * @throws IOException
	 *             if the given file does not exist
	 */
	public static boolean delete(File file) throws IOException {

		if (!file.exists())
			throw new IOException(file.getName() + " does not exist");

		if (isSymlink(file)) {
			return true;
		}

		boolean result = true;

		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null) {
				for (File child : children) {
					result &= delete(child);
				}
			}
		}
		result &= file.delete();
		return result;
	}

	/**
	 * Make sure a directory exists.
	 * @param dir
	 * @throws IOException
	 *             if the dir could not be created or if a regular file with the
	 *             specified name exists.
	 */
	public static File createDirectory(File dir)
			throws IOException {
		if (!dir.exists()) {
			boolean result = dir.mkdirs();
			if (!result) {
				throw new IOException("Could not create directory "
						+ dir);
			}
		} else if (!dir.isDirectory()) {
			throw new IOException(dir
					+ " already exists and is not a directory");
		}
		return dir;
	}

	public static File writeStringToFile(
			File file, String string,
			String encoding)
			throws IOException {

		FileOutputStream out = new FileOutputStream(file);
		PrintStream p = new PrintStream(out, true, encoding);
		p.print(string);
		p.flush();
		p.close();
		out.close();
		return file;
	}
		
	public static File writeBytesToFile(File file, byte[] bytes)throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		out.write(bytes);
		out.close();
		return file;
	}

	public static File writeInputStreamToFile(InputStream input, File outFile) throws IOException {		
		createDirectory(outFile.getParentFile());
		FileOutputStream fos = new FileOutputStream(outFile);
		byte[] buf = new byte[4096];
		int i = 0;
		while ((i = input.read(buf)) != -1) {
			fos.write(buf, 0, i);
		}
		fos.close();
		return outFile;
	}
	
	/**
	 * Moves a file.
	 * @param inFile the file to move
	 * @param outFile the destination file
	 * @throws IOException
	 */
	public static void moveFile(File inFile, File outFile) throws IOException {
	    if (inFile.equals(outFile)) {
	        return;        
	    }
        if (!inFile.isFile()) {
            throw new IOException(inFile.getAbsolutePath() + " is not a file");               
        }
        if (outFile.exists()) {
            if (!outFile.isFile()) {
                throw new IOException(outFile.getAbsolutePath() + " is not a file");
            }   
        } else {
            createDirectory(outFile.getParentFile());
        }
        
        // First try to use File.renameTo()
        boolean renameSuccess = inFile.renameTo(outFile);
        
        // If that fails, do copy + delete instead
        if (!renameSuccess) {
            copyFile(inFile, outFile);
            boolean deleteSuccess = inFile.delete();
            if (!deleteSuccess) {
                throw new IOException("Failed to delete source file " + inFile.getAbsolutePath());
            }
        }
	}
	
	/**
	 * Checks whether a file is a symbolic link.
	 * @param file the file to test
	 * @return <code>true</code> if the file is a symbolic link, <code>false</code> otherwise
	 * @throws IOException
	 */
	public static boolean isSymlink(File file) throws IOException {
	    File parent = file.getParentFile();
	    File test = new File(parent.getCanonicalFile(), file.getName());
	    return !test.getAbsolutePath().equals(test.getCanonicalPath());
	}
	

	

	
	/**
	 * Returns the total length file, if file is a directory the directory is
	 * scanned recursively and the total length of all files are returned.
	 * 
	 * @param file
	 *            a file or directory.
	 * @return the size of file.
	 */
	public static long getSize(File file) {
		if (!file.exists()) {
			return 0;
		}
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			long sum = 0;
			for (File child : children) {
				sum += getSize(child);
			}
			return sum;
		} else {
			return file.length();
		}
	}
}
