package org_pef_dtbook2pef.system.tasks.layout.page;


/**
 * LayoutMaster is an interface that specifies the layout of a paged media
 * @author Joel Håkansson, TPB
 */
public interface LayoutMaster extends SectionProperties {

	public Template getTemplate(int pagenum);

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
