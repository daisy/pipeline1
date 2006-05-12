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
		//table.setFont(FontChoices.fontLabel);
		
		tcFiles = new TableColumn (table, SWT.LEFT);
		tcFiles.setText("File Name");
		tcFiles.setWidth(500);
		tcFiles.setResizable(true);
		
	}
	
	public void setDirSelected(File dirSelect){
		this.dirSelected=dirSelect;
		
	}
	
	public ArrayList setTableContents(ArrayList alPatterns){
		
		ArrayList alCompatibleFiles = new ArrayList();
		
		EFolder eFolder=null;
		try {
			 eFolder = new EFolder(this.dirSelected.getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Collection recursiveFolders = eFolder.getFolders(true, ".+", false);
		Iterator itFolders = recursiveFolders.iterator();
		String strExtension="";
		while (itFolders.hasNext()){
			File folderFile = (File)itFolders.next();
			System.out.println("Names of all folders : " + folderFile.getPath());
			
			//get all files in this folder
			File [] arFiles = folderFile.listFiles();
		
			for (int j=0; j<arFiles.length; j++){
				//for each file, create an EFile
				File compareFile = arFiles[j];
				EFile eFile = new EFile(compareFile.getPath());
				strExtension = eFile.getExtension();
				//System.out.println("the filename is " + compareFile.getName());
				System.out.println("    the string extension is: " + strExtension);
				
//				compare them to the mimetype pattern(s) and 
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
		/*
		Iterator itCompatible = alCompatibleFiles.iterator();
		while (itCompatible.hasNext()){
			TableItem ti = new TableItem(table ,SWT.NONE,0);
			ti.setText((String)itCompatible.next());
		}
		*/	
		int size = alCompatibleFiles.size();
		System.out.println("How many compatible files in the array?" + size);
		return alCompatibleFiles;
	}
	
	
}
