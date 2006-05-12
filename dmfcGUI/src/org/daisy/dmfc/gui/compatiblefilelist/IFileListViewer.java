package org.daisy.dmfc.gui.compatiblefilelist;

import java.io.File;

public interface IFileListViewer {

		/**
		 * Update the view to show that a file was added 
		 * to the list.  
		 * 
		 * @param TransformerHandler
		 */
		public void addFile(File file);
		
		/**
		 * Update the view to show that a file was removed 
		 * from the list. 
		 * 
		 * @param transformerHandler
		 */
		public void removeFile(File file);
		
		/**
		 * Update the view 
		 * @param task
		 */
		public void updateFile(File file);
	}
