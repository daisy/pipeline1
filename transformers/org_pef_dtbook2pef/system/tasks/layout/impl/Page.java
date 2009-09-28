package org_pef_dtbook2pef.system.tasks.layout.impl;

import java.util.ArrayList;

import org_pef_dtbook2pef.system.tasks.layout.flow.Marker;

/**
 * A page object
 * @author joha
 *
 */
public class Page {
	private PageSequence parent;
	private ArrayList<Row> rows;
	private ArrayList<Marker> markers;
	private Integer pageIndex;
	private int rowsInHeader;
	private int rowsInFooter;
	
	public Page(PageSequence parent, int pageIndex) {
		this.rows = new ArrayList<Row>();
		this.markers = new ArrayList<Marker>();
		this.pageIndex = pageIndex;
		this.rowsInHeader = 0;
		this.rowsInFooter = 0;
		this.parent = parent;
	}
	
	public void newRow(Row r) {
		rows.add(r);
		markers.addAll(r.getMarkers());
	}
	
	public int rowsOnPage() {
		return rows.size();
	}
	
	public void addMarkers(ArrayList<Marker> m) {
		markers.addAll(m);
	}
	
	public ArrayList<Marker> getMarkers() {
		return markers;
	}
	
	public ArrayList<Row> getRows() {
		return rows;
	}

	/**
	 * Get the number for the page
	 * @return returns the page index in the sequence (zero based)
	 */
	public Integer getPageIndex() {
		return pageIndex;
	}

	public PageSequence getParent() {
		return parent;
	}
	
	public void setHeader(ArrayList<Row> c) {
		for (int i=0; i<rowsInHeader; i++) {
			rows.remove(0);
		}
		rowsInHeader = c.size();
		rows.addAll(0, c);
	}
	
	public void setFooter(ArrayList<Row> c) {
		for (int i=0; i<rowsInFooter; i++) {
			rows.remove(rows.size()-1);
		}
		rowsInFooter = c.size();
		rows.addAll(c);
	}

}
