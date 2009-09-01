package org_pef_dtbook2pef.setups.sv_SE.definers;

import java.util.ArrayList;
import java.util.Collection;

import org_pef_dtbook2pef.system.tasks.layout.page.BaseLayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.page.Page;
import org_pef_dtbook2pef.system.tasks.layout.page.Row;
import org_pef_dtbook2pef.system.tasks.layout.utils.LayoutTools;
import org_pef_dtbook2pef.system.tasks.textnode.filters.StringFilterHandler;

/**
 * CoverLayoutMaster
 * @author joha
 *
 */
public class CoverLayoutMaster extends BaseLayoutMaster {
	private ArrayList<Row> footer;
	private ArrayList<Row> header;
	
	public CoverLayoutMaster(int pageWidth, int pageHeight, StringFilterHandler filters) {
		super(pageWidth, pageHeight, 0, 1, filters);
		header = new ArrayList<Row>();
		setFooter("");
	}
	
	public void setFooter(String str) {
		footer = new ArrayList<Row>();
		int indent = getPageWidth() / 2;
		footer.add(new Row(filters.filter(LayoutTools.fill(' ', indent) + str)));
	}

	@Override
	public Collection<Row> getFooter(Page p) {
		return footer;
	}

	@Override
	public Collection<Row> getHeader(Page p) {
		return header;
	}

}
