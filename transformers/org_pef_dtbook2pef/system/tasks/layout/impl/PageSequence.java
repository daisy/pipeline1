package org_pef_dtbook2pef.system.tasks.layout.impl;

import java.util.Stack;

import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMaster;


public class PageSequence {
	private Stack<Page> pages;
	private LayoutMaster master;
	
	public PageSequence(LayoutMaster master) {
		this.pages = new Stack<Page>();
		this.master = master;
	}
	
	public Stack<Page> getPages() {
		return pages;
	}

	public int rowsOnCurrentPage() {
		return currentPage().rowsOnPage();
	}
	
	public void newPage() {
		pages.push(new Page(this, pages.size()));
	}
	
	public Page currentPage() {
		return pages.peek();
	}
	
	public void newRow(Row row) {
		if (currentPage().rowsOnPage()>=master.getFlowHeight()) {
			newPage();
		}
		currentPage().newRow(row);
	}
	
	public LayoutMaster getLayoutMaster() {
		return master;
	}

}
