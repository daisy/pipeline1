package org_pef_dtbook2pef.system.tasks.layout.page;

import java.util.ArrayList;


/**
 * @author joha
 */
public interface LayoutMaster {

	/**
	 * Get header rows for a page using this LayoutMaster. Each ArrayList<Object> must 
	 * fit within a single row, i.e. the combined length of all resolved strings in each ArrayList<Object> must
	 * be smaller than the page width. Keep in mind that text filters will be applied to the 
	 * resolved string, which could affect its length.
	 * @param page the page to get the header for
	 * @return returns an ArrayList containing an ArrayList of String
	 */
	public ArrayList<ArrayList<Object>> getHeader(int pagenum);
	
	/**
	 * Get footer rows for a page using this LayoutMaster. Each ArrayList<Object> must 
	 * fit within a single row, i.e. the combined length of all resolved strings in each ArrayList<Object> must
	 * be smaller than the page width. Keep in mind that text filters will be applied to the 
	 * resolved string, which could affect its length.
	 * @param page the page to get the header for
	 * @return returns an ArrayList containing an ArrayList of String
	 */
	public ArrayList<ArrayList<Object>> getFooter(int pagenum);
	
	/**
	 * Get the page width
	 * @return returns the page width
	 */
	public int getPageWidth();
	
	/**
	 * Get the page height
	 * @return returns the page height
	 */
	public int getPageHeight();
	
	/**
	 * Get the flow height
	 * @return returns the flow height, i.e. the height available for the text flow
	 */
	public int getFlowHeight();
	
	/**
	 * Get the header height
	 * @return returns the header height
	 */
	public int getHeaderHeight();
	
	/**
	 * Get the footer height
	 * @return returns the footer height
	 */
	public int getFooterHeight();
	
	/**
	 * Get binding margin
	 * @param pagenum the page to get the binding margin for
	 * @return returns the binding margin
	 */
	public int getInnerMargin();
	
	/**
	 * Get outer margin
	 * @return
	 */
	public int getOuterMargin();

}
