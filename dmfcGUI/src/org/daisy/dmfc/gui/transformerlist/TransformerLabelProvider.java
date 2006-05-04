package org.daisy.dmfc.gui.transformerlist;



import org.daisy.dmfc.core.transformer.TransformerHandler;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class TransformerLabelProvider extends LabelProvider
	implements ITableLabelProvider {

	/**
	 * Table consists of a checkbox and the name of the transformer (Handler)
	 * There is only one column.
	 * 
	 */
	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		TransformerHandler th = (TransformerHandler) element;
		switch (columnIndex) {
			case 0 :
				result = th.getName();
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


