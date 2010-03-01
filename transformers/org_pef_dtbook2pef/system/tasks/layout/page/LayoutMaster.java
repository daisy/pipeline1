package org_pef_dtbook2pef.system.tasks.layout.page;


/**
 * LayoutMaster is an interface that specifies the layout of a paged media
 * @author Joel Håkansson, TPB
 */
public interface LayoutMaster extends SectionProperties {

	/**
	 * Get the template for the specified page number
	 * @param pagenum the page number to get the template for
	 * @return returns the template
	 */
	public Template getTemplate(int pagenum);

}
