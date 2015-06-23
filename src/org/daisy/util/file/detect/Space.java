/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
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
