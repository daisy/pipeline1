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

package org.daisy.dmfc.gui.widgetproperties;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


//Checked table
public class CompatibleFilesTableProperties {

	Table table;
	TableColumn tcFiles;
	File dirSelected;
	
	public CompatibleFilesTableProperties(Table _table){
		
		this.table=_table;
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(0,0,270,160);
		
		
		tcFiles = new TableColumn (table, SWT.LEFT);
		tcFiles.setText("Compatible Sources");
		tcFiles.setWidth(500);
		tcFiles.setResizable(true);
		
	}
	
	public void setDirSelected(File dirSelect){
		this.dirSelected=dirSelect;
		
	}
	
	
	/**
	 * Gets a list of all files from the directory and
	 * all recursive directories.
	 * 
	 * @deprecated
	 * @param alPatterns
	 * @return
	 */
	public ArrayList setTableContents(ArrayList alPatterns){
		
		//mg: optimized usage of EFolder -
		//1) build a regex by summing alPatterns (simplified by getting regex version instead of glob version from MIMETypeImpl.getFilenamePatterns())
		//2) invoke EFolder.getFiles() with the regex and recursive to true: eFolder.getFiles(true, summedRegexString, false):
		//3) done. Replaces all the code in this method (if return value can be changed to a Collection)
						
		System.out.println("The directory selected is " + dirSelected);
		ArrayList alCompatibleFiles = new ArrayList();
		
		File fileDirSelected = this.dirSelected;
		EFolder eFolder=null;
		try {
			
			 eFolder = new EFolder(this.dirSelected.getPath());
			 System.out.println("eFolder has folder children " + eFolder.hasFolderChildren());
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
				//get all files in this folder
				File [] arBaseFiles = fileDirSelected.listFiles();
				String strEnd= "";
				
				for (int j=0; j<arBaseFiles.length; j++){
					//for each file, create an EFile
					File compareFile = arBaseFiles[j];
					EFile eFile = new EFile(compareFile.getPath());
					strEnd = eFile.getExtension();
					//System.out.println("the filename is " + compareFile.getName());
				//	System.out.println("    the string extension is: " + strEnd);
					
//					compare them to the mimetype pattern(s) and 
					//only place compatible types in the array
					
					Iterator itMimeTypes = alPatterns.iterator();
					
					while (itMimeTypes.hasNext()){
						String type = (String)itMimeTypes.next();
						//System.out.println("    names in the patterns " + type);
						
						if (strEnd!=null ){
							boolean match = strEnd.equalsIgnoreCase(type);
							if (match){
								alCompatibleFiles.add(compareFile);
							}
						}
					}	
				}
			
		
		
		if (eFolder.hasFolderChildren()){
			
			Collection recursiveFolders = eFolder.getFolders(true, ".+", false);
			Iterator itFolders = recursiveFolders.iterator();
			String strExtension="";
			while (itFolders.hasNext()){
				File folderFile = (File)itFolders.next();
				//System.out.println("Names of all folders : " + folderFile.getPath());
				
				//get all files in this folder
				File [] arFiles = folderFile.listFiles();
				
				for (int j=0; j<arFiles.length; j++){
					//for each file, create an EFile
					File compareFile = arFiles[j];
					EFile eFile = new EFile(compareFile.getPath());
					strExtension = eFile.getExtension();
					//System.out.println("the filename is " + compareFile.getName());
				//	System.out.println("    the string extension is: " + strExtension);
					
//					compare them to the mimetype pattern(s) and 
					//only place compatible types in the array
					
					Iterator itMimeTypes = alPatterns.iterator();
					
					
					while (itMimeTypes.hasNext()){
						String type = (String)itMimeTypes.next();
						//System.out.println("    names in the patterns " + type);
						
						if (strExtension!=null ){
							boolean match = strExtension.equalsIgnoreCase(type);
							if (match){
								alCompatibleFiles.add(compareFile);
							}
						}
						
					}
					
				}
			}
		}
		int size = alCompatibleFiles.size();
		System.out.println("How many compatible files in the array?" + size);
		return alCompatibleFiles;
	}
	
	
}
