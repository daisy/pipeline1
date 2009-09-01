package org_pef_dtbook2pef.setups.sv_SE.definers;

import java.util.ArrayList;
import java.util.Collection;

import org_pef_dtbook2pef.system.tasks.layout.page.BaseLayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.page.Page;
import org_pef_dtbook2pef.system.tasks.layout.page.Row;
import org_pef_dtbook2pef.system.tasks.layout.utils.LayoutTools;
import org_pef_dtbook2pef.system.tasks.textnode.filters.StringFilterHandler;

public class BodyLayoutMaster extends BaseLayoutMaster {

	public BodyLayoutMaster(int pageWidth, int pageHeight, StringFilterHandler filters) {
		super(pageWidth, pageHeight, 1, 0, filters);
	}

	public Collection<Row> getHeader(Page p) {
		ArrayList<Row> r = new ArrayList<Row>();
		String chars;
		int pagenum = p.getPageIndex() + 1;
		String originalPage =	filters.filter(
									findMarker(p, "pagenum-turn", MarkerSearchDirection.FORWARD, MarkerSearchScope.PAGE) +
									findMarker(p, "pagenum", MarkerSearchDirection.BACKWARD, MarkerSearchScope.SEQUENCE)
								);
		String pagenumStr = filters.filter("" + pagenum);
		String separator = LayoutTools.fill(' ', getPageWidth() - (originalPage.length()+pagenumStr.length()));
		if (pagenum % 2 == 0) { // EVEN PAGE
			chars = pagenumStr + separator + originalPage;
		} else { // ODD PAGE
			chars =	originalPage + separator + pagenumStr;
		}
		r.add(new Row(chars));
		assert chars.length()==getPageWidth();
		return r;
	}
	
	public Collection<Row> getFooter(Page p) {
		return new ArrayList<Row>();
	}

}
