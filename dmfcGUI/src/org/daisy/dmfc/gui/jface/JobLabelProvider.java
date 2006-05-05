package org.daisy.dmfc.gui.jface;

import org.daisy.dmfc.qmanager.Job;
import org.daisy.dmfc.qmanager.Status;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * Label provider for the TableViewerExample
 * 
 * @see org.eclipse.jface.viewers.LabelProvider 
 */
public class JobLabelProvider 
	extends LabelProvider
	implements ITableLabelProvider {



	/**
	 * 
	 */
	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		Job job = (Job) element;
		switch (columnIndex) {
			case 0:  // COMPLETED_COLUMN - checked or not
				result = Status.getStatusString(job.getStatus());
				break;
			case 1 :
				result = job.getScript().getName();
				break;
			case 2 :
				result = job.getInputFile().getName();
				break;
			case 3 :
				result = job.getOutputFile().getPath();
				break;
			default :
				break; 	
		}
		return result;
	}

	/**
	 * 
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

}


