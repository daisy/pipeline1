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

/**
 * Basic file functions.
 * @author Linus Ericson
 */
public class FileUtils {
    
    /**
     * Copy a file.
     * @param inFile source file
     * @param outFile destination file
     * @throws IOException if anything bad happens.
     */
    public static void copy(File inFile, File outFile) throws IOException {
        if (inFile.equals(outFile)) {
            return;
        }
        FileInputStream fis  = new FileInputStream(inFile);
        FileOutputStream fos = new FileOutputStream(outFile);
        byte[] buf = new byte[1024];
        int i = 0;
        while ((i=fis.read(buf))!=-1) {
            fos.write(buf, 0, i);
        }
        fis.close();
        fos.close();
    }
    
    public static File createDirectory(File dir) throws IOException {
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            if (!result) {
                throw new IOException("Could not create directory " + dir);
            }
        } else if (!dir.isDirectory()) {
            throw new IOException(dir + " already exists and is not a directory");
        }
        return dir;
    }
    
}
