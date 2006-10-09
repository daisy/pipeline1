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
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class FileLabelProvider extends LabelProvider
	implements ITableLabelProvider {

	/**
	 * Table consists of a checkbox and the name of the Compatible File 
	 * There is only one column.
	 * 
	 */
	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		File file = (File) element;
		switch (columnIndex) {
			case 0 :
				result = file.getPath();
				break;
			default :
				break; 	
		}
		return result;
	}
	
	/**
	 * Must be implemented, in ITableLabelProvider
	 * Not used in this application.
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		return 	null;
	}

	

}


