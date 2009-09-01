package org_pef_dtbook2pef.setups.sv_SE.definers;

import java.util.ArrayList;
import java.util.Collection;

import org_pef_dtbook2pef.system.tasks.layout.page.BaseLayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.page.Page;
import org_pef_dtbook2pef.system.tasks.layout.page.Row;
import org_pef_dtbook2pef.system.tasks.layout.utils.LayoutTools;
import org_pef_dtbook2pef.system.tasks.layout.utils.RomanNumeral;
import org_pef_dtbook2pef.system.tasks.textnode.filters.StringFilterHandler;

public class FrontLayoutMaster extends BaseLayoutMaster {

	public FrontLayoutMaster(int pageWidth, int pageHeight, StringFilterHandler filters) {
		super(pageWidth, pageHeight, 1, 0, filters);
	}

	public Collection<Row> getHeader(Page p) {
		ArrayList<Row> r = new ArrayList<Row>();
		String chars;
		int pagenum = p.getPageIndex() + 1;
		String pagenumStr = filters.filter("" + RomanNumeral.int2roman(pagenum));
		if (pagenum % 2 == 0) { // EVEN PAGE
			chars = pagenumStr;
		} else { // ODD PAGE
			String fill = LayoutTools.fill(' ', getPageWidth() - (pagenumStr.length()));
			chars =	fill + pagenumStr;
			assert chars.length()==getPageWidth();
		}
		r.add(new Row(chars));
		return r;
	}
	
	public Collection<Row> getFooter(Page p) {
		return new ArrayList<Row>();
	}

}
