package org_pef_dtbook2pef.system.tasks.layout.page;

/**
 * Configuration class for a LayoutMaster
 * @author joha
 *
 */
public class LayoutMasterConfigurator {
	int pageWidth;
	int pageHeight;
	// optional
	int headerHeight = 0; 
	int footerHeight = 0;
	int innerMargin = 0;
	int outerMargin = 0;
	float rowSpacing = 1;
	boolean duplex = true;

	public LayoutMasterConfigurator(int pageWidth, int pageHeight) {
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
	}
	
	public LayoutMasterConfigurator headerHeight(int value) {
		this.headerHeight = value;
		return this;
	}

	public LayoutMasterConfigurator footerHeight(int value) {
		this.footerHeight = value;
		return this;
	}

	public LayoutMasterConfigurator innerMargin(int value) {
		this.innerMargin = value;
		return this;
	}
	
	public LayoutMasterConfigurator outerMargin(int value) {
		this.outerMargin = value;
		return this;
	}
	
	public LayoutMasterConfigurator rowSpacing(float value) {
		this.rowSpacing = value;
		return this;
	}
	
	public LayoutMasterConfigurator duplex(boolean value) {
		this.duplex = value;
		return this;
	}
}
