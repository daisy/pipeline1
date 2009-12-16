package org_pef_dtbook2pef.system.tasks.layout.page;


/**
 * SectionProperites specifies the configuration properties for a section of pages
 * in a paged media.
 * @author Joel Håkansson, TPB
 *
 */
public interface SectionProperties {

	/**
	 * Get the page width.
	 * An implementation must ensure that getPageWidth()=getFlowWidth()+getInnerMargin()+getOuterMargin()
	 * @return returns the page width
	 */
	public int getPageWidth();

	/**
	 * Get the page height.
	 * An implementation must ensure that getPageHeight()=getHeaderHeight()+getFlowHeight()+getFooterHeight()
	 * @return returns the page height
	 */
	public int getPageHeight();

	/**
	 * Get row spacing, in row heights. For example, use 2.0 for double row spacing and 1.0 for normal row spacing.
	 * @return returns row spacing
	 */
	public float getRowSpacing();
	
	/**
	 * 
	 * @return returns true if output is intended on both sides of the sheets
	 */
	public boolean duplex();
	

	/**
	 * Get the flow width
	 * @return returns the flow width
	 */
	public int getFlowWidth();

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
