package org_pef_dtbook2pef.system.tasks.layout.page;

import java.util.Collection;


/**
 * @author joha
 *
 */
public interface LayoutMaster {

	public Collection<Row> getHeader(Page p);
	public Collection<Row> getFooter(Page p);
	
	public int getPageWidth();
	public int getPageHeight();
	public int getFlowHeight();
	public int getHeaderHeight();
	public int getFooterHeight();

}
