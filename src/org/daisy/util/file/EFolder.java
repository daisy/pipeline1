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
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.daisy.util.fileset.interfaces.Fileset;

/**
 * EFolder - where E stands for extended.
 * All you ever wanted to do with directories in java.io.File but never dared to
 * ask. 
 * 
 * @author Markus Gylling
 */

public class EFolder extends File {

	public final static int TYPE_FOLDER = 0;
	public final static int TYPE_FILE = 1;
	public final static int TYPE_FILE_OR_FOLDER = 2;

	public EFolder(URI uri) throws IOException {
		super(uri);
		testDir();		
	}

	public EFolder(String path) throws IOException {
		super(path);
		testDir();	
	}

	public EFolder(File parent, String child) throws IOException {
		super(parent, child);
		testDir();	
	}

	public EFolder (File folder) throws IOException {				
		super(folder.toURI());
		testDir();	
	}

	private void testDir() throws IOException {
		if(this.exists()) {
			if(!this.isDirectory()) {
				throw new IOException(this.getName()
						+ "is not a folder");
			}
		}
	}
	
	public boolean mkdir() {
		return testMkResult(super.mkdir());
	}
	
	public boolean mkdirs() {		
		return testMkResult(super.mkdirs());
	}
	
	private boolean testMkResult(boolean result) {
		if(!result) {
			return result;
		}
		if(!this.isDirectory()) {
			return false;
		}
		return true;
	}
			
	/**
	 * @return true if this folder has zero descendants, including hidden files.
	 */
	public boolean isEmpty() {
		return this.listFiles().length == 0;
	}

	/**
	 * @return true if this folder has file children (excluding folders) as
	 *         direct descendants, including hidden files.
	 */
	public boolean hasFileChildren() {
		return !this.list(TYPE_FILE, true, null, false).isEmpty();
	}

	/**
	 * @return true if this folder has file children (excluding folders) as
	 *         descendants, including hidden files.
	 * @param deep
	 *            If true, then subfolders are traversed, and files in these
	 *            subfolders are reflected in the return value.
	 */
	public boolean hasFileChildren(boolean deep) {
		return !this.list(TYPE_FILE, true, null, deep).isEmpty();
	}

	/**
	 * @return true if this folder has folder children, including hidden
	 *         folders.
	 */
	public boolean hasFolderChildren() {
		return !this.list(TYPE_FOLDER, true, null, false).isEmpty();
	}

	/**
	 * @return A Collection&lt;File&gt; of directly descending files of this
	 *         folder. The collection excludes folders and their descending
	 *         files. The collection includes hidden files.
	 */
	public Collection getFiles() {
		return this.list(TYPE_FILE, true, null, false);
	}

	/**
	 * @return A Collection&lt;Folder&gt; of directly descending folders of this
	 *         folder. The collection excludes files. The collection includes
	 *         hidden folders.
	 */
	public Collection getFolders() {
		return this.list(TYPE_FOLDER, true, null, false);
	}

	/**
	 * @return A Collection of directly descending &lt;Folder&gt;|&lt;File&gt;
	 *         objects of this folder. Equals java.io.File#listFiles() except
	 *         the return type. The collection includes hidden objects.
	 */
	public Collection getFilesAndFolders() {
		return this.list(TYPE_FILE_OR_FOLDER, true, null, false);
	}

	/**
	 * @return A Collection&lt;File&gt; of file descendants of this folder. The
	 *         collection excludes folders. The collection includes hidden
	 *         files.
	 * @param deep
	 *            If true, subdirectories will be traversed recursively.
	 */
	public Collection getFiles(boolean deep) {
		return this.list(TYPE_FILE, true, null, deep);
	}

	/**
	 * @return A Collection&lt;Folder&gt; of folder descendants of this folder.
	 *         The collection excludes files. The collection includes hidden
	 *         folders.
	 * @param deep
	 *            If true, subdirectories will be traversed recursively.
	 */
	public Collection getFolders(boolean deep) {
		return this.list(TYPE_FOLDER, true, null, deep);
	}

	/**
	 * @return A Collection of &lt;Folder&gt;|&lt;File&gt; descendants of this
	 *         folder. The collection includes hidden objects.
	 * @param deep
	 *            If true, subdirectories will be traversed recursively.
	 */
	public Collection getFilesAndFolders(
			boolean deep) {
		return this.list(TYPE_FILE_OR_FOLDER, true, null, deep);
	}

	/**
	 * @return A Collection&lt;File&gt; of file descendants of this folder. The
	 *         collection excludes folders. The collection includes hidden
	 *         files.
	 * @param deep
	 *            If true, subdirectories will be traversed recursively.
	 * @param regex
	 *            A regular expression for name filtering; if null treated as
	 *            '.+'
	 */
	public Collection getFiles(boolean deep,
			String regex) {
		return this.list(TYPE_FILE, true, regex, deep);
	}

	/**
	 * @return A Collection&lt;Folder&gt; of folder descendants of this folder.
	 *         The collection excludes files. The collection includes hidden
	 *         folders.
	 * @param deep
	 *            If true, subdirectories will be traversed recursively.
	 * @param regex
	 *            A regular expression for name filtering; if null treated as
	 *            '.+'
	 */
	public Collection getFolders(boolean deep,
			String regex) {
		return this.list(TYPE_FOLDER, true, regex, deep);
	}

	/**
	 * @return A Collection of &lt;Folder&gt;|&lt;File&gt; descendants of this
	 *         folder. The collection includes hidden objects.
	 * @param deep
	 *            If true, subdirectories will be traversed recursively.
	 * @param regex
	 *            A regular expression for name filtering; if null treated as
	 *            '.+'
	 */
	public Collection getFilesAndFolders(
			boolean deep, String regex) {
		return this.list(TYPE_FILE_OR_FOLDER, true, regex, deep);
	}

	/**
	 * @return A Collection&lt;File&gt; of file descendants of this folder. The
	 *         collection excludes folders.
	 * @param deep
	 *            If true, subdirectories will be traversed recursively.
	 * @param regex
	 *            A regular expression for name filtering; if null treated as
	 *            '.+'
	 * @param includeHidden
	 *            If false, hidden files will be excluded from return
	 *            collection.
	 */
	public Collection getFiles(boolean deep,
			String regex, boolean includeHidden) {
		return this.list(TYPE_FILE, includeHidden, regex, deep);
	}

	/**
	 * @return A Collection&lt;Folder&gt; of folder descendants of this folder.
	 *         The collection excludes files.
	 * @param deep
	 *            If true, subdirectories will be traversed recursively.
	 * @param regex
	 *            A regular expression for name filtering; if null treated as
	 *            '.+'
	 * @param includeHidden
	 *            If false, hidden files will be excluded from return
	 *            collection.
	 */
	public Collection getFolders(boolean deep,
			String regex, boolean includeHidden) {
		return this.list(TYPE_FOLDER, includeHidden, regex, deep);
	}

	/**
	 * @return A Collection of &lt;Folder&gt;|&lt;File&gt; descendants of this
	 *         folder.
	 * @param deep
	 *            If true, subdirectories will be traversed recursively.
	 * @param regex
	 *            A regular expression for name filtering; if null treated as
	 *            '.+'
	 * @param includeHidden
	 *            If false, hidden files will be excluded from return
	 *            collection.
	 */
	public Collection getFilesAndFolders(
			boolean deep, String regex,
			boolean includeHidden) {
		return this.list(TYPE_FILE_OR_FOLDER, includeHidden, regex, deep);
	}

	/**
	 * Copies a file into this folder. If the destination already exists within
	 * this folder, an overwrite will be attempted.
	 * 
	 * @param source
	 *            File to be copied into this folder.
	 * @throws IOException
	 * @return The File resulting from the copy operation
	 */
	public File addFile(File source)throws IOException {
		return addFile(source, true);
	}

	/**
	 * Copies a file into this folder.
	 * @param source
	 *            File to be copied into this folder.
	 * @param overwrite
	 *            If true, will attempt to overwrite a preexisting destination.
	 *            If false and the destination already exists, will not perform
	 *            the add.
	 * @return the resulting File if the add was performed, null if the add was
	 *         not performed
	 * @throws IOException
	 */
	public File addFile(File source, boolean overwrite) throws IOException {
		return addFile(source, overwrite, null);
	}

	/**
	 * Copies a file into this folder.
	 * @param source
	 *            File to be copied into this folder.
	 * @param overwrite
	 *            If true, will attempt to overwrite a preexisting destination.
	 *            If false and the destination already exists, will not perform
	 *            the add.
	 * @param newName
	 * 			  A local (pathless) name to give the added file.
	 *            Null is an allowed value.
	 *            If value is null, the source local name will be maintained.           
	 * @return the resulting File if the add (and optional rename ) was successfully performed, 
	 * 			null if the add was not successfully performed
	 * @throws IOException
	 */
	public File addFile(File source, boolean overwrite, String newName) throws IOException {
		File dest;
		
		if(newName==null) {
			dest = new File(this, source.getName());
		}else{
			dest = new File(this, newName);
		}
		
		if (!overwrite && dest.exists()) {
			return null;
		}
		
		FileUtils.copyFile(source, dest);
		
		return dest;
	}

	
	/**
	 * Copies a collection of files into this folder.
	 * <p>Note that any directory relative relations of input collection will be lost</p> 
	 * @param fileSources
	 *            Files to be copied into this folder.
	 * @param overwrite
	 *            If true, will attempt to overwrite a preexisting destination.
	 *            If false and the destination already exists, will not perform
	 *            the add.
	 * @return true if all adds were successfully performed, false otherwise
	 * @throws IOException
	 * @see #addFiles(Collection, boolean)
	 */
	public boolean addFiles(Collection fileSources, boolean overwrite) throws IOException {
		Iterator i = fileSources.iterator();
		boolean result = true;
		
		while(i.hasNext()) {
			Object value = i.next();
			if(value instanceof File) {
				File source = (File) value;
				File dest = new File(this, source.getName());				
				if (!overwrite && dest.exists()) {
					result = false;
					System.err.println("destination exists in EFolder.addFiles and overwrite disabled: " + dest.getPath());
				}else{
					FileUtils.copyFile(source, dest);
				}
			}else{
				result = false;
				System.err.println("classcast problem in EFolder.addFiles: " + value.getClass().getSimpleName());
			}
		}
		return result;
	}
	
	/**
	 * Copies an org.daisy.util.fileset into this folder
	 * <p>Any directory relative relations of input fileset (in relation to manifest) will be maintained.
	 * <p>If a fileset member lives in a superdirectory of the manifest member, an IOException will be thrown. All members
	 * must be in same folder as or subfolder of the Fileset manifest file.</p>
	 * @param fileset the Fileset instance to copy into this folder
	 * @param overwrite whether to overwrite preexisting specimen (specifiles) in destination 
	 * @return true if all members were copied successfully; false otherwise. If overwrite is false and a copy is skipped, still returns true. 
	 * @throws IOException if something bad happens
	 */
	public boolean addFileset(Fileset fileset, boolean overwrite) throws IOException {
		EFolder inputBaseDir = fileset.getManifestMember().getParentFolder();
		Iterator i = fileset.getLocalMembers().iterator();
		while(i.hasNext()) {
			File file = (File) i.next();
			if(file.getParentFile().getCanonicalPath().equals(inputBaseDir.getCanonicalPath())) {
				//file is in same dir as manifestfile
				this.addFile(file,overwrite);
			}else{
				//file is in subdir
				URI relative = inputBaseDir.toURI().relativize(file.getParentFile().toURI());
				if(relative.toString().startsWith("..")) throw new IOException("fileset member "+file.getName()+" does not live in a sibling or descendant folder of manifest member");
				EFolder subdir = new EFolder(this,relative.getPath());
				FileUtils.createDirectory(subdir);
				subdir.addFile(file,overwrite);
			}						
		}						
		return true;
	}
	
	/**
	 * Deletes the contents of this folder. This folder itself is not deleted.
	 * Only directly descending files and empty directly descending
	 * subfolders are deleted. Hidden files and folders are included in deletion.
	 * @return true if all files were successfully deleted, false otherwise
	 */
	public boolean deleteContents() throws Exception {
		return delete(TYPE_FILE_OR_FOLDER,false,null,true);
	}

	/**
	 * Deletes the contents of this folder. This folder itself is not deleted.
	 * Hidden files and folders are included in deletion.
	 * @param deep
	 * 	          If false, delete only directly descending
	 *            files and empty directly descending subfolders
	 *            If true, recursively delete also non-empty 
	 *            subfolders (and their descendants).             
	 * @return true if all files were successfully deleted, false otherwise
	 */
	public boolean deleteContents(boolean deep) throws Exception {
		return delete(TYPE_FILE_OR_FOLDER,deep,null,true);
	}

	/**
	 * Deletes the contents of this folder. This folder itself is not deleted.
	 * Hidden files and folders are included in deletion.
	 * @param deep
	 * 	          	If false, delete only directly descending
	 *            	files and empty directly descending subfolders.
	 *            	If true, recursively delete also non-empty subfolders (and their descendants).  
	 * @param regex
	 * 			  	Delete only objects whose name matches this regular expression;
	 *				if null treated as '.+'
	 * @return true if all files were successfully deleted, false otherwise
	 */
	public boolean deleteContents(boolean deep, String regex) throws Exception {
		return delete(TYPE_FILE_OR_FOLDER,deep,null,true);
	}
		
	/**
	 * Deletes the contents of this folder. This folder itself is not deleted.
	 * @param deep
	 * 	          	If false, delete only directly descending
	 *            	files and empty directly descending subfolders.
	 *            	If true, recursively delete also non-empty subfolders (and their descendants).  
	 * @param regex
	 * 			  	Delete only objects whose name matches this regular expression;
	 *				if null treated as '.+'
	 * @param deleteHidden
	 * 				If false, hidden files or folders are not deleted.
	 * 				If true, hidden files are included in deletion.
	 * @return true if all files were successfully deleted, false otherwise
	 */
	public boolean deleteContents(boolean deep, String regex, boolean deleteHidden) throws Exception {
		return delete(TYPE_FILE_OR_FOLDER,deep,null,deleteHidden);
	}
	
	/**
	 * Deletes the contents of this folder. This folder itself is not deleted.
	 * @param type 
	 * 				A static int (available in Folder class) declaring which
	 * 				type of object to delete (file, folder, both).
	 * @param deep
	 * 	          	If false, delete only directly descending
	 *            	files and empty directly descending subfolders.
	 *            	If true, recursively delete also non-empty subfolders (and their descendants).  
	 * @param regex
	 * 			  	Delete only objects whose name matches this regular expression;
	 *				if null treated as '.+'
	 * @param deleteHidden
	 * 				If false, hidden files or folders are not deleted.
	 * 				If true, hidden files are included in deletion.
	 * @return true if all files were successfully deleted, false otherwise
	 */
	public boolean deleteContents(int type, boolean deep, String regex, boolean deleteHidden) throws Exception {
		return delete(type,deep,null,deleteHidden);
	}
	
	/**
	 * Copies the contents of this folder to a destination Folder
	 * @param destination 
	 * 		The Folder to which the contents of this folder should be copied
	 * @param overwrite
	 * 		whether a prexisting equal object in the destination folder should be overwritten
	 * @return 
	 * 		True if all objects of this folder were successfully copied to destination, false otherwise
	 */
	public boolean copyChildrenTo(EFolder destination, boolean overwrite) throws IOException {
		boolean result = true;
		boolean cur;
		
		if(!destination.exists()) { 
			cur = destination.mkdirs();
			if(!cur) return false;			
		}
		
		if(!destination.isDirectory()) {
			throw new IOException("destination is not a directory");
		}
		
		if(destination.equals(this)) {
			return true;
		}
		
		File[] children = this.listFiles();
		for (int i = 0; i < children.length; i++) {
			if (children[i].getCanonicalFile().equals(children[i].getAbsoluteFile())){
				//TODO its not a symlink?
				if(children[i].isFile()){
					if(destination.addFile(children[i],overwrite) == null) result = false;			
				}else{ //isDirectory
					EFolder srcDir = new EFolder(children[i].getAbsolutePath());
					EFolder destDir = new EFolder(destination,srcDir.getName()); 
					cur = srcDir.copyChildrenTo(destDir,overwrite); 
					if(!cur) result = cur;
				}
			}
		}
		return result;
	}

	public File writeToFile(String fileLocalName,
			String toWrite, String encoding)
			throws IOException {		
		return FileUtils.writeStringToFile(new File(this, fileLocalName),toWrite,encoding);
	}

//	public File writeToFile(String fileLocalName,
//			ByteBuffer toWrite)
//			throws IOException {		
//		return FileUtils.writeBytesToFile(new File(this, fileLocalName),toWrite.array());
//	}
	
	public File writeToFile(String fileLocalName,
			byte[] toWrite)
			throws IOException {		
		return FileUtils.writeBytesToFile(new File(this, fileLocalName),toWrite);
	}
	
	/**
	 * An extended File.listFiles() method
	 * 
	 * @param type
	 *            type of File (file, directory, both) to return
	 * @param hidden
	 *            include hidden files in return set
	 * @param regex
	 *            for file and foldername matching, null equals '.+'
	 * @param deep
	 *            whether to recurse subdirs
	 * @return a HashSet of File and/or Folder objects
	 */
	private HashSet list(int type,
			boolean hidden, String regex,
			boolean deep) {

		HashSet set = new HashSet();

		File[] files = this.listFiles();
		for (int i = 0; i < files.length; i++) {
			if ((type == TYPE_FILE_OR_FOLDER)
					|| (files[i].isFile() && type == TYPE_FILE)
					|| (files[i].isDirectory() && type == TYPE_FOLDER)) {
				if (!files[i].isHidden()
						|| hidden) {
					if (regex == null
							|| files[i].getName().matches(regex)) {
						if (files[i].isFile()) {
							set.add(files[i]);
						} else {
							EFolder f;
							try {
								f = new EFolder(files[i].getAbsolutePath());
							} catch (IOException e) {
								System.err.println(e.getMessage());
								continue;
							}
							set.add(f);
						}
					}
				}
			}
			if (deep && files[i].isDirectory()) {
				try {
					EFolder fldr = new EFolder(files[i].getAbsolutePath());
					set.addAll(fldr.list(type, hidden, regex, deep));
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		} // for int i files.length
		return set;
	}

	/**
	 * An extended File.delete() method
	 * 
	 * @param type
	 *            type of File (file, directory, both) to delete
	 * @param deleteHidden
	 *            include hidden files in deletion
	 * @param regex
	 *            for file and foldername matching, null equals '.+'
	 * @param deep
	 *            whether to recurse subdirs
	 * @return true if all objects matching inparam criteria were successfully
	 *         deleted
	 */
	private boolean delete(int type, boolean deep, String regex,
			boolean deleteHidden) throws Exception {
		
		if(!this.exists()) throw new IOException(this.getName() + " does not exist");
		
		boolean result = true;
						
		File[] children = this.listFiles();
		for (int i = 0; i < children.length; i++) {
			if (children[i].isDirectory()) {
				if (deep) {
					EFolder fldr = new EFolder(children[i].getAbsolutePath());
					boolean cur = fldr.delete(type, deep, regex, deleteHidden);
					if (!cur) result = cur;
				}
			}//if (children[i].isDirectory()) 
			if ((type == TYPE_FILE_OR_FOLDER)
					|| (children[i].isFile() && type == TYPE_FILE)
					|| (children[i].isDirectory() && type == TYPE_FOLDER)) {
				if (!children[i].isHidden()|| deleteHidden) {
					if (regex == null || children[i].getName().matches(regex)) {
						if (children[i].getCanonicalFile().equals(children[i].getAbsoluteFile())){
							//TODO its not a symlink?
							boolean cur = children[i].delete();
							if (!cur) result = cur;
						}
					}
				}
			}
		}	
		return result;
	}

	private static final long serialVersionUID = 4292170837234819837L;

}
