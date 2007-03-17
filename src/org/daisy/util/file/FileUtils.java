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
package org.daisy.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Basic file functions.
 * 
 * @author Linus Ericson
 * @author Markus Gylling
 */
public class FileUtils {

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

	public static File writeInputStreamToFile(InputStream input, File output) throws IOException{

        // Obtain a channel
        WritableByteChannel channel = new FileOutputStream(output).getChannel();
    
        // Create a direct ByteBuffer;
        ByteBuffer buf = ByteBuffer.allocateDirect(10);
    
        byte[] bytes = new byte[1024];
        int count = 0;
        int index = 0;
    
        // Continue writing bytes until there are no more
        while (count >= 0) {
            if (index == count) {
                count = input.read(bytes);
                index = 0;
            }
            // Fill ByteBuffer
            while (index < count && buf.hasRemaining()) {
                buf.put(bytes[index++]);
            }
    
            // Set the limit to the current position and the position to 0
            // making the new bytes visible for write()
            buf.flip();
    
            // Write the bytes to the channel
            int numWritten = channel.write(buf);
    
            // Check if all bytes were written
            if (buf.hasRemaining()) {
                // If not all bytes were written, move the unwritten bytes
                // to the beginning and set position just after the last
                // unwritten byte; also set limit to the capacity
                buf.compact();
            } else {
                // Set the position to 0 and the limit to capacity
                buf.clear();
            }
        }
    
        // Close the file
        channel.close();
        
        return output;

}
	
}