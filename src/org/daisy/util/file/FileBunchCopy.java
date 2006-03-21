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
import org.daisy.util.fileset.Fileset;

/**
 * Okej. Vad ska hända här?
 * 
 * Som indata ska komma dels ett Set med strängar.
 * Dessa strängar är värdet från src-attribut.
 * Dels ska det komma ett File-objekt som berättar från 
 * vilken katalog de kommer. Vidare ska det också komma
 * ett File-objekt som visar till vilken katalog dessa filer
 * ska kopieras, eventuellt med sin undermapp som finns i strängen.
 * 
 * Strängen kan alltså vara nåt i stil med: images/fp23c23.jpg
 * Då ska det på något sätt parsas så att katalogen images skapas också 
 * under den katalog som angavs som outputkatalog.
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
        URI manifestURI = fileset.getManifestMember().toURI();
        for (Iterator it = uris.iterator(); it.hasNext(); fileNum++) {
            URI uri = (URI)it.next();
            URI relativeURI = manifestURI.relativize(uri);
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
	
	public static void copyFiles(File inputBaseDir, File outputBaseDir, Set paths, ProgressObserver observer, boolean createDummies) throws IOException {
		int i = 0;
		for (Iterator it = paths.iterator(); it.hasNext(); ) {
			String pathFragment = (String) it.next();
			File source = new File(inputBaseDir, pathFragment);
			File target = new File(outputBaseDir, pathFragment);
			if (!source.exists()) {
				if (!createDummies) {
					//continue; // eller ska man dö här?
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
	
	public static void copyFiles(File inputBaseDir, File outputBaseDir, Set paths) throws IOException {
		copyFiles(inputBaseDir, outputBaseDir, paths, null, false);
	}
}
