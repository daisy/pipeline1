package org_pef_dtbook2pef.system.tasks.layout.page;

import java.util.ArrayList;

/**
 * LayoutMaster is an interface that specifies the layout of a paged media
 * @author Joel Håkansson, TPB
 */
public interface LayoutMaster extends SectionProperties {

	/**
	 * Get header rows for a page using this LayoutMaster. Each ArrayList must 
	 * fit within a single row, i.e. the combined length of all resolved strings in each ArrayList must
	 * be smaller than the flow width. Keep in mind that text filters will be applied to the 
	 * resolved string, which could affect its length.
	 * @param page the page to get the header for
	 * @return returns an ArrayList containing an ArrayList of String
	 */
	public ArrayList<ArrayList<Object>> getHeader(int pagenum);

	/**
	 * Get footer rows for a page using this LayoutMaster. Each ArrayList must 
	 * fit within a single row, i.e. the combined length of all resolved strings in each ArrayList must
	 * be smaller than the flow width. Keep in mind that text filters will be applied to the 
	 * resolved string, which could affect its length.
	 * @param page the page to get the header for
	 * @return returns an ArrayList containing an ArrayList of String
	 */
	public ArrayList<ArrayList<Object>> getFooter(int pagenum);

	/**
	 * Get the flow width
	 * @return returns the flow width
	 */
	public int getFlowWidth();

	/**
	 * Get the flow height
	 * @return returns the flow height, i.e. the height available for the text flow
	 */
	public int getFlowHeight();

	/**
	 * Get the header height.
	 * An implementation must ensure that getHeaderHeight()=getHeader(pagenum).size() for all pagenum's
	 * @return returns the header height
	 */
	public int getHeaderHeight();

	/**
	 * Get the footer height.
	 * An implementation must ensure that getFooterHeight()=getFooter(pagenum).size() for all pagenum's
	 * @return returns the footer height
	 */
	public int getFooterHeight();

	/**
	 * Get inner margin
	 * @return returns the inner margin
	 */
	public int getInnerMargin();

	/**
	 * Get outer margin
	 * @return returns the outer margin
	 */
	public int getOuterMargin();

}
