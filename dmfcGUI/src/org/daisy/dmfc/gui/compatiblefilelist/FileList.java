/**
 *DAISY Multi-Format Converter, or DAISY Pipeline Graphical User Interface.
Copyright (C) 2006 DAISY Consortium

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.daisy.dmfc.gui.compatiblefilelist;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.daisy.dmfc.gui.Window;
import org.daisy.dmfc.gui.transformerlist.ITransformerListViewer;




/**
 * Class that plays the role of the domain model 
 * This can be used with a persistent data store, i.e. database
 * to place items into a Vector, Array, whatever.
 * In this case, the compatible file list is generated
 * after a user chooses a directory in the gui.
 * JFace implementation
 * @author Laurie Sherve
 */

public class FileList {
	
	//List of all transformers handlers for each script/job
	Window window;
	private ArrayList alCompatibleFiles;
	private Set changeListeners = new HashSet();
	

	
	/**
	 * Constructor
	 */
	public FileList() {
		super();
		this.initData();
	}
	
	/*
	 * Initialize the table data.
	 */
	private void initData() {
		
		//each job, get the scriptHandler and list of transformers (transformerhandlers) for each
		 window = window.getInstance();
		 alCompatibleFiles =window.getConvertMultipleFiles().getArrayListTableContents();
		
	}

	
	/**
	 * Return an ArrayList of compatible files
	 */
	public ArrayList getFiles() {
		return alCompatibleFiles;
	}
	

	
	
	/**
	 * If want to add to the list...
	 */
	public void addFile() {
		
	}

	/**
	 *Transformer is never removed in this app...
	 * @param file
	 */
	public void removeFile(File file) {
		alCompatibleFiles.remove(file);
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IFileListViewer) iterator.next()).removeFile(file);
	}

	/**
	 * Called when ???
	 * the table is updated
	 * @param File
	 */
	public void fileComplete(File file) {
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IFileListViewer) iterator.next()).updateFile(file);
	}

	/**
	 * @param viewer
	 */
	public void removeChangeListener(IFileListViewer viewer) {
		changeListeners.remove(viewer);
	}

	/**
	 * @param viewer
	 */
	public void addChangeListener(IFileListViewer viewer) {
		changeListeners.add(viewer);
	}

}
