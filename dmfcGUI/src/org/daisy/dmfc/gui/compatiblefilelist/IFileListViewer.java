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

/**
 * File List functionality - jface
 * @author Laurie Sherve
 *
 */

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
