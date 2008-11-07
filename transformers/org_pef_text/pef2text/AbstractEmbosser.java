package org_pef_text.pef2text;

import java.io.IOException;

/**
 * PEF oriented embosser interface.
 * 
 * Note! Interface is a draft. Contact author 
 * before implementing an embosser based on this
 * interface
 *  
 * @author  Joel Hakansson, TPB
 * @version 10 okt 2008
 * @since 1.0
 */
public interface AbstractEmbosser {

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
	 * Start a new volume
	 * @throws IOException
	 */
	public void newVolume() throws IOException;
	/**
	 * Open for writing
	 * @throws IOException
	 */
	public void open(boolean duplex) throws IOException;
	/**
	 * Test if embosser has been open
	 * @return returns true if 
	 */
	public boolean isOpen();
	/**
	 * Finish up and close the output stream.
	 * @throws IOException
	 */
	public void close() throws IOException;
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
	
	/**
	 * Does this embosser support volumes?
	 * @return
	 */
	public boolean supportsVolumes();

	/**
	 * Does this embosser support 8-dot mode?
	 * @return
	 */
	public boolean supports8dot();
	
	/**
	 * Does this embosser support duplex printing?
	 * @return
	 */
	public boolean supportsDuplex();
	
	/**
	 * Get the maximum row width for this embosser  
	 * @return
	 */
	public int getMaxWidth();
	
	/**
	 * Get the maximum number of rows that the embosser can produce on a page
	 * @return
	 */
	public int getMaxHeight();
	
}
