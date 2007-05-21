/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2006  Daisy Consortium
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.daisy.util.execution.ProgressObserver;
import org.daisy.util.fileset.interfaces.Fileset;

/**
 * Utilities to copy different collections of files.
 * @see org.daisy.util.file.EFolder#addFileset(Fileset, boolean)
 * @see org.daisy.util.file.EFolder#addFiles(Collection, boolean) 
 * @author Martin Blomberg
 * @author Linus Ericson
 */
public class FileBunchCopy {

	private static File tempFile;
	
	/**
	 * Copy a collection of files.
	 * @param fileset the fileset to copy the files from
	 * @param outputDir a destination directory
	 * @param uris the collection of URIs to copy
	 * @param observer a progress observer (or null)
	 * @param createDummies create dummy files for missing input files
	 * @throws IOException
	 */
	public static void copyFiles(Fileset fileset, File outputDir, Collection uris, ProgressObserver observer, boolean createDummies) throws IOException {
        int fileNum = 1;
        URI manifestDirURI = fileset.getManifestMember().getFile().getParentFile().toURI();
        for (Iterator it = uris.iterator(); it.hasNext(); fileNum++) {
            URI uri = (URI)it.next();
            URI relativeURI = manifestDirURI.relativize(uri);
            File in = new File(uri);
            if (!in.exists()) {
                if (!createDummies) {
                    throw new FileNotFoundException(in + " not found");
                }
                in = DummyFile.create(in.getName());
            }
            File out = new File(outputDir.toURI().resolve(relativeURI));
            FileUtils.copy(in, out);
            if (observer != null) {
                observer.reportProgress((double)fileNum/uris.size());
            }
        }
    }
	
	/**
	 * Copy a collection of files from some place to some other place. Input 
	 * to this method is basically a "from base directory" and a "to base directory",
	 * and to complement those also a set<String> of paths relative to both
	 * base directories.
	 * 
	 * @param inputBaseDir the input base directory
	 * @param outputBaseDir the output base directory
	 * @param paths set of paths relative the base directories
	 * @param observer a progress observer used to report the copying progress. May be <tt>null</tt>.
	 * @param createDummies create dummy files for missing input files
	 * @throws IOException
	 */
	public static void copyFiles(File inputBaseDir, File outputBaseDir, Set<String> paths, ProgressObserver observer, boolean createDummies) throws IOException {
		int i = 0;
		for (Iterator<String> it = paths.iterator(); it.hasNext(); ) {
			String pathFragment = it.next();
			File source = new File(inputBaseDir, pathFragment);
			File target = new File(outputBaseDir, pathFragment);
			if (!source.exists()) {
				if (!createDummies) {
					throw new IOException(source + " not found");
				}
				
				if (null == tempFile) {
					tempFile = File.createTempFile("foo", "bar");
					FileOutputStream fos = new FileOutputStream(tempFile);
					fos.write(1);
					fos.flush();
					fos.close();
				}
				// create dummies somehow
				source = tempFile;
			} 
			
			File targetParent = target.getParentFile();
			if (!targetParent.exists()) {
				targetParent.mkdirs();
			}
			
			FileUtils.copy(source, target);
			if (observer != null) {
				observer.reportProgress( (double) ++i / paths.size());
			}
		}
	}
	
	public static void copyFiles(File inputBaseDir, File outputBaseDir, Set<String> paths) throws IOException {
		copyFiles(inputBaseDir, outputBaseDir, paths, null, false);
	}
	
	
}
