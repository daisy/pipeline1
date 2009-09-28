package org_pef_dtbook2pef.system.tasks.layout.page;


public abstract class AbstractLayoutMaster implements LayoutMaster {
	protected final int headerHeight;
	protected final int footerHeight;
	protected final int pageWidth;
	protected final int pageHeight;

	public AbstractLayoutMaster(int pageWidth, int pageHeight, int headerHeight, int footerHeight) {
		this.headerHeight = headerHeight;
		this.footerHeight = footerHeight;
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
	}

	public int getPageWidth() {
		return pageWidth;
	}
	
	public int getPageHeight() {
		return pageHeight;
	}
	
	public int getFlowHeight() {
		return pageHeight-(getHeaderHeight()+getFooterHeight());
	}

	public int getHeaderHeight() {
		return headerHeight;
	}

	public int getFooterHeight() {
		return footerHeight;
	}

}
