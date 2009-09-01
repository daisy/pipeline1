package org_pef_dtbook2pef.system.tasks.layout.page;

import java.util.ArrayList;
import java.util.Collection;

import org_pef_dtbook2pef.system.tasks.textnode.filters.StringFilterHandler;

public class BaseLayoutMaster extends AbstractLayoutMaster {
	protected final StringFilterHandler filters;

	public BaseLayoutMaster(int pageWidth, int pageHeight, int headerHeight, int footerHeight, StringFilterHandler filters) {
		super(pageWidth, pageHeight, headerHeight, footerHeight);
		this.filters = filters;
	}

	public Collection<Row> getHeader(Page p) {
		ArrayList<Row> ret = new ArrayList<Row>();
		for (int i = 0; i<getHeaderHeight(); i++) {
			ret.add(new Row(""));
		}
		return ret;
	}
	
	public Collection<Row> getFooter(Page p) {
		ArrayList<Row> ret = new ArrayList<Row>();
		for (int i = 0; i<getFooterHeight(); i++) {
			ret.add(new Row(""));
		}
		return ret;
	}

}
