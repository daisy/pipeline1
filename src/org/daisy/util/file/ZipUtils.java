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
package org.daisy.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author Martin Blomberg
 * @author Romain Deltour
 * 
 */
public final class ZipUtils {

	private ZipUtils() {}

	/**
	 * Zips sourceFile and stores the result as outputFile. If sourceFile is a
	 * directory, all files in the directory (recursive) will be included in the
	 * zip file.
	 * 
	 * @param sourceFile
	 *            the file to zip.
	 * @param outputFile
	 *            the resulting zip file.
	 * @throws IOException
	 */
	public static void zip(File sourceFile, File outputFile) throws IOException {
		OutputStream os = new FileOutputStream(outputFile);
		ZipOutputStream zos = new ZipOutputStream(os);

		Map<String, File> files = getZipEntryNames(sourceFile);
		Iterator<String> it = files.keySet().iterator();
		byte[] buf = new byte[4096];

		while (it.hasNext()) {
			String zipEntryName = it.next();
			ZipEntry entry = new ZipEntry(zipEntryName);
			FileInputStream in = new FileInputStream(files.get(zipEntryName));

			zos.putNextEntry(entry);
			int len;
			while ((len = in.read(buf)) >= 0) {
				zos.write(buf, 0, len);
			}

			in.close();
			zos.closeEntry();
		}

		zos.close();
	}

	/**
	 * Zips the source file and writes the result to the ZipOutpurStream. If
	 * sourceFile is a directory, all files in the directory (recursive) will be
	 * included in the zip.
	 * 
	 * @param sourceFile
	 *            the file or directory to zip
	 * @param zos
	 *            the ZipOutputStream
	 * @throws IOException
	 */
	public static void zip(File sourceFile, ZipOutputStream zos)
			throws IOException {
		Map<String, File> files = getZipEntryNames(sourceFile);
		Iterator<String> it = files.keySet().iterator();
		byte[] buf = new byte[4096];

		while (it.hasNext()) {
			String zipEntryName = it.next();
			ZipEntry entry = new ZipEntry(zipEntryName);
			FileInputStream in = new FileInputStream(files.get(zipEntryName));

			zos.putNextEntry(entry);
			int len;
			while ((len = in.read(buf)) >= 0) {
				zos.write(buf, 0, len);
			}

			in.close();
			zos.closeEntry();
		}

		zos.close();
	}

	/**
	 * Unzips the file input and places its content in outputDirectory.
	 * 
	 * @param input
	 *            a zip file.
	 * @param outputDirectory
	 *            a directory.
	 * @throws ZipException
	 * @throws IOException
	 */
	public static void unzip(File input, File outputDirectory)
			throws ZipException, IOException {
		ZipFile zip = new ZipFile(input);
		for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries
				.hasMoreElements();) {
			ZipEntry entry = entries.nextElement();

			if (entry.isDirectory()) {
				File dir = new File(outputDirectory, entry.getName());
				dir.mkdirs();
			} else {
				File out = new File(outputDirectory, entry.getName());
				out.getParentFile().mkdirs();

				FileOutputStream fos = new FileOutputStream(out);
				copy(zip.getInputStream(entry), fos);
			}
		}
	}

	private static Map<String, File> getZipEntryNames(File file) {
		Map<String, File> files = new HashMap<String, File>();
		if (!file.isDirectory()) {
			files.put(file.getName(), file);

		} else {
			URI base = file.toURI();
			RecursiveFileListIterator rfi = new RecursiveFileListIterator(file);
			while (rfi.hasNext()) {
				File f = rfi.next();
				String relName = base.relativize(f.toURI()).getRawPath();
				files.put(relName, f);
			}
		}
		return files;
	}

	/**
	 * Writes the contents of in to out.
	 * 
	 * @param in
	 *            the InputStream.
	 * @param out
	 *            the OutputStream.
	 * @throws IOException
	 */
	private static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
		}

		in.close();
		out.close();
	}
}
