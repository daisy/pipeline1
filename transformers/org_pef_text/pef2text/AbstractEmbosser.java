package org_pef_text.pef2text;

import java.io.Closeable;
import java.io.IOException;

/**
 * Provides an embosser communication interface. Communication is 
 * flat. For example, only one of these should be called when
 * starting a new page:
 * <ul>
 * <li>newPage</li>
 * <li>newSectionAndPage</li>
 * <li>newVolumeSectionAndPage</li>
 * </ul>
 * 
 * @author  Joel Hakansson, TPB
 * @version 10 okt 2008
 */
public interface AbstractEmbosser extends EmbosserProperties, Closeable {

	// TODO: naming convention: class is not abstract, it is an interface
	
	/**
	 * Write a string of braille to the embosser.
	 * Values must be between 0x2800 and 0x28FF
	 * @param braille
	 * @throws IOException
	 */
	public void write(String braille) throws IOException;
	/**
	 * Start a new line
	 * @throws IOException
	 */
	public void newLine() throws IOException;
	/**
	 * Starts a new page
	 * @throws IOException
	 */
	public void newPage() throws IOException;
	/** 
	 * Starts a new page on a blank sheet of paper 
	 * with the specified duplex settings.
	 * @param duplex
	 * @throws IOException
	 */
	public void newSectionAndPage(boolean duplex) throws IOException;
	/**
	 * Starts a new page on a blank sheet of paper in a new volume
	 * with the specified duplex settings.
	 * @param duplex
	 * @throws IOException
	 */
	public void newVolumeSectionAndPage(boolean duplex) throws IOException;
	/**
	 * Opens for writing
	 * @throws IOException
	 */
	public void open(boolean duplex) throws IOException;
	/**
	 * Returns true if embosser is open
	 * @return returns true if embosser is open, false otherwise
	 */
	public boolean isOpen();

	/**
	 * Test if embosser has been closed
	 * @return returns true if the embosser has been open, but is now closed, false otherwise
	 */
	public boolean isClosed();
	/**
	 * Set the row gap for following calls to newLine
	 * to the specified value, measured as an 
	 * integer multiple of the dot-to-dot height.
	 * @param value
	 */
	public void setRowGap(int value);
	/**
	 * Get the current row gap, measured as an integer
	 * multiple of the dot-to-dot height.
	 * @return
	 */
	public int getRowGap();

}
