package org_pef_dtbook2pef.system.tasks.layout.flow;

import java.io.Closeable;
import java.util.ArrayList;

import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.page.PagedMediaWriter;

/**
 * The Paginator breaks a flow of Rows into pages.
 * It handles page related items such as headers,
 * footers and footnotes
 * @author Joel Håkansson, TPB
 *
 */
public interface LayoutPerformer extends Closeable {
	
	/**
	 * Open the Paginator for writing to the supplied writer 
	 * @param writer the PagedMediaWriter to use
	 */
	public void open(PagedMediaWriter writer);

	/**
	 * Add a new sequence of pages
	 * @param master the LayoutMaster to use for this sequence
	 * @param pagesOffset page offset
	 */
	public void newSequence(LayoutMaster master, int pagesOffset);
	
	/**
	 * Explicitly break a page
	 */
	public void newPage();
	
	/**
	 * Add a new row of characters
	 * @param row the row to add
	 */
	public void newRow(Row row);
	
	/**
	 * Insert markers that cannot be assigned to a row at the current position
	 * @param m
	 */
	public void insertMarkers(ArrayList<Marker> m);
	
	/**
	 * Get information about the current page
	 */
	public CurrentPageInfo getPageInfo();

}
