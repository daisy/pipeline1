package org_pef_dtbook2pef.system.tasks.layout.page;

/**
 * Get information about the current page
 * @author Joel Håkansson, TPB
 *
 */
public interface CurrentPageInfo {

	/**
	 * Get the flow height for the current page
	 * @return returns the flow height of the current page
	 */
	public int getFlowHeight();
	
	/**
	 * Get the number of rows currently on the current page
	 * @return returns the number of rows in the current page
	 */
	public int countRows();

}
