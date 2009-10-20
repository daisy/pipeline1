package org_pef_dtbook2pef.system.tasks.layout.page;

import java.util.ArrayList;

public class BaseLayoutMaster extends AbstractLayoutMaster {
	
	public BaseLayoutMaster(LayoutMasterConfigurator config) {
		//int pageWidth, int pageHeight, int headerHeight, int footerHeight, int innerMargin, int outerMargin, float rowSpacing
		
		//super(pageWidth-innerMargin-outerMargin, pageHeight-headerHeight-footerHeight, headerHeight, footerHeight, innerMargin, outerMargin, rowSpacing);
		super(config);
	}

	public ArrayList<ArrayList<Object>> getHeader(int pagenum) {
		ArrayList<ArrayList<Object>> ret = new ArrayList<ArrayList<Object>>();
		ArrayList<Object> row = new ArrayList<Object>();
		for (int i = 0; i<getHeaderHeight(); i++) {
			ret.add(row);
		}
		return ret;
	}
	
	public ArrayList<ArrayList<Object>> getFooter(int pagenum) {
		ArrayList<ArrayList<Object>> ret = new ArrayList<ArrayList<Object>>();
		ArrayList<Object> row = new ArrayList<Object>();
		for (int i = 0; i<getFooterHeight(); i++) {
			ret.add(row);
		}
		return ret;
	}

}
