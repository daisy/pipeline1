package org_pef_text;

import java.nio.charset.Charset;

/**
 * 
 * 
 * @author  Joel Hakansson, TPB
 * @version 7 nov 2008
 * @since 1.0
 */
public interface AbstractTable {
	
	/**
	 * Transcode the given text string as braille. This may be a one-to-one mapping or
	 * a many-to-one depending on the table implementation.
	 * @param text
	 * @return
	 */
	public String toBraille(String text);
	
	/**
	 * Transcode the given braille into text.
	 * Values must be between 0x2800 and 0x28FF.
	 * @param braille
	 * @return
	 */
	public String toText(String braille);
	
	/**
	 * Get the preferred charset for this braille format
 	 * @return
	 */
	public Charset getPreferredCharset();

	/**
	 * 
	 * @return returns true if 8-dot braille is supported, false otherwise
	 */
	public boolean supportsEightDot();
}
