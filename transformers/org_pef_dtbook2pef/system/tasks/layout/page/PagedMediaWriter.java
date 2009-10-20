package org_pef_dtbook2pef.system.tasks.layout.page;

import java.io.OutputStream;

/**
 * PagedMediaWriter is an interface for writing output to a paged media.
 * @author Joel Håkansson, TPB
 */
public interface PagedMediaWriter {

	/**
	 * Insert a new section
	 * @param p the SectionProperties for this section
	 */
	public void newSection(SectionProperties p);

	/**
	 *  Insert a new page
	 */
	public void newPage();
	
	/**
	 * Add a new row to the current page
	 * @param row the characters to put on the row
	 */
	public void newRow(CharSequence row);
	
	/**
	 * Add a new empty row to the current page  
	 */
	public void newRow();
	
	/**
	 * Open the PagedMediaWriter for writing
	 * @param os The underlying OutputStream for the PagedMedia
	 * @throws PagedMediaWriterException throws an PagedMediaWriterException if the PagedMediaWriter could not be opened
	 */
	public void open(OutputStream os) throws PagedMediaWriterException;
	
	/**
	 * Close the PagedMediaWriter After a call to close(), the PagedMediaWriter should not be reused.
	 */
	public void close();

}
