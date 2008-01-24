package org.daisy.util.file.detect;

/**
 * 
 * @author Markus Gylling
 */
/*package*/ class Space {

	/**
	 * @return true if the char is defined (by unicode) as whitespace, false
	 *         otherwise
	 */
	/*package*/ static boolean isUnicodeWhitespace(char ch) {
		return isUnicodeWhitespace(Character.valueOf(ch));
	}

	/**
	 * @return true if each char in the array is defined (by unicode) as
	 *         whitespace, false otherwise
	 */
	/*package*/ static boolean isUnicodeWhitespace(char[] ch) {
		for (int i = 0; i < ch.length; i++) {
			if (!isUnicodeWhitespace(ch[i])) {
				return false;
			}
		}// ch.length
		return true;
	}

	/**
	 * @return true if each char in the String is defined (by unicode) as
	 *         whitespace, false otherwise
	 */
	/*package*/ static boolean isUnicodeWhitespace(String string) {
		return isUnicodeWhitespace(string.toCharArray());
	}

	/**
	 * @return true if the char is defined (by unicode) as whitespace, false
	 *         otherwise
	 */
	/*package*/ static boolean isUnicodeWhitespace(Character ch) {
		return Character.isSpaceChar(ch.charValue());
	}

	/**
	 * @return true if the char is defined by XML as whitespace, false
	 *         otherwise. see: http://www.w3.org/TR/1998/REC-xml-19980210#NT-S
	 */
	/*package*/ static boolean isXMLWhiteSpace(char ch) {
		return (ch == 0x20) || (ch == 0x09)
				|| (ch == 0xD) || (ch == 0xA);
	}

	/**
	 * @return true if each char in the array is defined by XML as whitespace,
	 *         false otherwise. see:
	 *         http://www.w3.org/TR/1998/REC-xml-19980210#NT-S
	 */
	/*package*/ static boolean isXMLWhiteSpace(
			char[] ch) {
		for (int i = 0; i < ch.length; i++) {
			if (!isXMLWhiteSpace(ch[i])) {
				return false;
			}
		}// ch.length
		return true;
	}

	/**
	 * @return true if each char in the String is defined by XML as whitespace,
	 *         false otherwise. see:
	 *         http://www.w3.org/TR/1998/REC-xml-19980210#NT-S
	 */
	/*package*/ static boolean isXMLWhiteSpace(String string) {
		return isXMLWhiteSpace(string.toCharArray());
	}

}
