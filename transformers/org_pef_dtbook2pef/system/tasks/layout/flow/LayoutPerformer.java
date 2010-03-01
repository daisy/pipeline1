package org_pef_dtbook2pef.system.tasks.layout.flow;

import java.io.Closeable;
import java.util.ArrayList;

import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.page.PagedMediaWriter;

/**
 * <p>The LayoutPerformer breaks a stream of {@link Row} into pages.</p>
 * 
 * <p>The LayoutPerformer implementation is responsible for breaking
 * pages when required by the properties of the {@link LayoutMaster}. It
 * is also responsible for placing page dependent items such
 * as headers, footers and footnotes.</p>
 * 
 * <p>The final result is passed on to the {@link PagedMediaWriter}.</p>
 * 
 * @author Joel Håkansson, TPB
 *
 */
public interface LayoutPerformer extends Closeable {
	
	/**
	 * Open the LayoutPerformer for writing to the supplied writer 
	 * @param writer the PagedMediaWriter to use
	 */
	public void open(PagedMediaWriter writer);

	/**
	 * Add a new sequence of pages
	 * @param master the {@link LayoutMaster} to use for this sequence
	 * @param pagesOffset page offset
	 */
	public void newSequence(LayoutMaster master, int pagesOffset);
	
	/**
	 * Add a new sequence of pages. Continue page numbering from preceding sequence or zero if there is no preceding section
	 * @param master the {@link LayoutMaster} to use for this sequence
	 */
	public void newSequence(LayoutMaster master);
	
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
