package org_pef_dtbook2pef.system.tasks.layout.page;


/**
 * AbstractLayoutMaster will ensure that the LayoutMaster measurements adds up. 
 * @author joha
 *
 */
public abstract class AbstractLayoutMaster implements LayoutMaster {
	protected final int headerHeight;
	protected final int footerHeight;
	protected final int flowWidth;
	protected final int flowHeight;
	protected final int innerMargin;
	protected final int outerMargin;
	protected final float rowSpacing;
	protected final boolean duplex;

	public AbstractLayoutMaster(LayoutMasterConfigurator config) {
		// int flowWidth, int flowHeight, int headerHeight, int footerHeight, int innerMargin, int outerMargin, float rowSpacing
		this.headerHeight = config.headerHeight;
		this.footerHeight = config.footerHeight;
		this.flowWidth = config.pageWidth-config.innerMargin-config.outerMargin;
		this.flowHeight = config.pageHeight-config.headerHeight-config.footerHeight;
		this.innerMargin = config.innerMargin;
		this.outerMargin = config.outerMargin;
		this.rowSpacing = config.rowSpacing;
		this.duplex = config.duplex;
	}
	
	public int getPageWidth() {
		return flowWidth+innerMargin+outerMargin;
	}

	public int getPageHeight() {
		return flowHeight+headerHeight+footerHeight;
	}

	public int getFlowWidth() {
		return flowWidth;
	}

	public int getFlowHeight() {
		return flowHeight;
	}

	public int getHeaderHeight() {
		return headerHeight;
	}

	public int getFooterHeight() {
		return footerHeight;
	}

	public int getInnerMargin() {
		return innerMargin;
	}

	public int getOuterMargin() {
		return outerMargin;
	}
	
	public float getRowSpacing() {
		return rowSpacing;
	}
	
	public boolean duplex() {
		return duplex;
	}

}
