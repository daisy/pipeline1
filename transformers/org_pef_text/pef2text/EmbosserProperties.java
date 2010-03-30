package org_pef_text.pef2text;

/**
 * Provides information about the embosser.
 * @author Joel Håkansson, TPB
 */
public interface EmbosserProperties {

	/**
	 * Returns true if this embosser has some method for volume handling
	 * @return returns true if this embosser supports volumes
	 */
	public boolean supportsVolumes();

	/**
	 * Returns true if this embosser supports 8 dot braille
	 * @return returns true if this embosser supports 8 dot braille
	 */
	public boolean supports8dot();
	
	/**
	 * Returns true if this embosser supports duplex printing
	 * @return returns true if this embosser supports duplex printing
	 */
	public boolean supportsDuplex();
	
	/**
	 * Returns true if this embosser supports aligning. This indicates
	 * that rows can be padded with whitespace to move the text block
	 * horizontally using the value returned by <code>getMaxWidth</code>. 
	 * Should return true for all physical embossers, since they all have
	 * a finite row length.
	 * @return returns true if this embosser supports aligning, false otherwise.
	 */
	public boolean supportsAligning();

	/**
	 * Gets the maximum row width in the current configuration  
	 * @return returns the maximum row width, in characters
	 */
	public int getMaxWidth();
	
	/**
	 * Gets the maximum page height in the current configuration
	 * @return returns the maximum page height, in rows
	 */
	public int getMaxHeight();
	
}
