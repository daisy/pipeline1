package org_pef_dtbook2pef.system.tasks.layout.flow;

/**
 * Get information about the current page
 * @author joha
 *
 */
public interface CurrentPageInfo {

	/**
	 * Get the flow height for the current page
	 * @return return the current page flow height
	 */
	public int getFlowHeight();
	
	/**
	 * Get the number of rows currently on the current page
	 * @return
	 */
	public int countRows();

}
