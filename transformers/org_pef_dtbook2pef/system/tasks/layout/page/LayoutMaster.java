package org_pef_dtbook2pef.system.tasks.layout.page;


/**
 * LayoutMaster is an interface that specifies the layout of a paged media
 * @author Joel Håkansson, TPB
 */
public interface LayoutMaster extends SectionProperties {

	public Template getTemplate(int pagenum);

}
