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


