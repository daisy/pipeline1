package org.daisy.util.i18n;

/**
 * Miscellaneous unicode realm character and string utilities 
 * @author Markus Gylling
 */
public final class CharUtils {

	/**
	 * @return true if all characters in the string are within the range of 7bit
	 *         ascii, false otherwise
	 */
	public static final boolean isAscii(
			String string) {
		return isAscii(string.toCharArray());
	}

	/**
	 * @return true if all characters in the char array are within the range of
	 *         7bit ascii, false otherwise
	 */
	public static final boolean isAscii(char[] ch) {
		for (int i = 0; i < ch.length; i++) {
			if (!isAscii(ch[i])) {
				return false;
			}
		}// ch.length
		return true;
	}

	/**
	 * @return true if the character is within the range of 7bit ascii, false
	 *         otherwise
	 */
	public static boolean isAscii(char ch) {
		return (ch < 128);
	}

	/**
	 * @return true if all characters in the string are within the printable
	 *         range of 7bit ascii, false otherwise
	 */
	public static final boolean isAsciiPrintable(
			String string) {
		return isAsciiPrintable(string.toCharArray());
	}

	/**
	 * @return true if all characters in the char array are within the printable
	 *         range of 7bit ascii, false otherwise
	 */
	public static final boolean isAsciiPrintable(
			char[] ch) {
		for (int i = 0; i < ch.length; i++) {
			if (!isAsciiPrintable(ch[i])) {
				return false;
			}
		}// ch.length
		return true;
	}

	/**
	 * @return true if the character is within the printable range of 7bit
	 *         ascii, false otherwise
	 */
	public static boolean isAsciiPrintable(char ch) {
		return (ch < 127 && ch > 31);
	}

	/**
	 * @return true if the char is within the control character (non
	 *         printable) range of 7bit ascii, false otherwise
	 */
	public static boolean isAsciiControl(char ch) {
		return (ch == 127 || ch < 32);
	}

	/**
	 * @return true if the char is defined (by unicode) as whitespace, false otherwise
	 */
	public static boolean isWhitespace(char ch) {
		return isWhitespace(Character.valueOf(ch));
	}

	/**
	 * @return true if the char is defined (by unicode) as whitespace, false otherwise
	 */
	public static boolean isWhitespace(
			Character ch) {
		return Character.isSpaceChar(ch.charValue());
	}

	/**
	 * Converts each whitespace char in the incoming char array with 
	 * the given replacement char
	 * If the given replacement char is whitespace too, fallback to the underscore char
	 * @param ch the input char array to replace whitespace in
	 * @param replace the char to replace any found whitespace with
	 */
	public static char[] toNonWhitespace(char[] ch, char replace) {
		String result = "";
		char replacement = replace;		
		if(isWhitespace(replace)) replacement ='_';
				
		for (int i = 0; i < ch.length; i++) {
			if(isWhitespace(ch[i])) {
				result = result + replacement;
			}else{
				result = result + ch[i];
			}
		}
		return result.toCharArray();
	}

	/**
	 * Converts each whitespace char in the incoming string with 
	 * the given replacement char
	 * If the given replacement char is whitespace too, fallback to the underscore char
	 * @param string the string to replace whitespace in
	 * @param replace the char to replace any found whitespace with
	 */
	public static String toNonWhitespace(String string, char replace) {
		return String.valueOf(toNonWhitespace(string.toCharArray(),replace));	
	}
	
	/**
	 * Converts each whitespace char in the incoming string with the replacement char underscore
	 * @param string the string to replace whitespace in
	 * @see #toNonWhitespace(String, char)
	 */
	public static String toNonWhitespace(String string) {
		return String.valueOf(toNonWhitespace(string.toCharArray(),'_'));	
	}

	/**
	 * Converts each whitespace char in the incoming string with the replacement char underscore
	 * @param string the string to replace whitespace in
	 * @see #toNonWhitespace(String, char)
	 */
	public static char[] toNonWhitespace(char[] ch) {
		return toNonWhitespace(ch,'_');	
	}
	
	/**
	 * Converts each non-ascii-printable character in the incoming string to a
	 * printable asccii character
	 * 
	 * @return a string containing printable 7bit ascii only
	 */
	public static final String toPrintableAscii(
			String string) {
		String result = "";
		for (int i = 0; i < string.length(); i++) {
			result = result
					+ toPrintableAscii(string.charAt(i));
		}// ch.length
		return result;
	}

	/**
	 * Converts each non-ascii-printable character in the incoming char array to
	 * a printable asccii character
	 * 
	 * @return a string containing printable 7bit ascii only
	 */
	public static final char[] toPrintableAscii(
			char[] ch) {
		return toPrintableAscii(new String(ch)).toCharArray();
	}

	/**
	 * Converts a non-ascii-printable character to a printable asccii character
	 * 
	 * @return a string containing only chars within the range of printable 7bit
	 *         ascii
	 */
	public static final String toPrintableAscii(
			char c) {

		if (isAsciiPrintable(c)) {
			return String.valueOf(c);
		}

		if (c == 'À') {
			return "A";
		} else if (c == 'à') {
			return "a";
		} else if (c == 'Á') {
			return "A";
		} else if (c == 'á') {
			return "a";
		} else if (c == 'Â') {
			return "A";
		} else if (c == 'â') {
			return "a";
		} else if (c == 'Ã') {
			return "A";
		} else if (c == 'ã') {
			return "a";
		} else if (c == 'Ä') {
			return "AE";
		} else if (c == 'ä') {
			return "ae";
		} else if (c == 'Å') {
			return "AA";
		} else if (c == 'å') {
			return "aa";
		} else if (c == 'Æ') {
			return "AE";
		} else if (c == 'æ') {
			return "ae";
		} else if (c == 'Ç') {
			return "C";
		} else if (c == 'ç') {
			return "c";
		} else if (c == 'È') {
			return "E";
		} else if (c == 'è') {
			return "e";
		} else if (c == 'É') {
			return "E";
		} else if (c == 'é') {
			return "e";
		} else if (c == 'Ê') {
			return "E";
		} else if (c == 'ê') {
			return "e";
		} else if (c == 'Ë') {
			return "E";
		} else if (c == 'ë') {
			return "e";
		} else if (c == 'Ì') {
			return "I";
		} else if (c == 'ì') {
			return "i";
		} else if (c == 'Í') {
			return "I";
		} else if (c == 'í') {
			return "i";
		} else if (c == 'Î') {
			return "I";
		} else if (c == 'î') {
			return "i";
		} else if (c == 'Ï') {
			return "I";
		} else if (c == 'ï') {
			return "i";
		} else if (c == 'Ð') {
			return "D";
		} else if (c == 'ð') {
			return "d";
		} else if (c == 'Ñ') {
			return "N";
		} else if (c == 'ñ') {
			return "n";
		} else if (c == 'Ò') {
			return "O";
		} else if (c == 'ò') {
			return "o";
		} else if (c == 'Ó') {
			return "O";
		} else if (c == 'ó') {
			return "o";
		} else if (c == 'Ô') {
			return "O";
		} else if (c == 'ô') {
			return "o";
		} else if (c == 'Õ') {
			return "O";
		} else if (c == 'õ') {
			return "o";
		} else if (c == 'Ö') {
			return "OE";
		} else if (c == 'ö') {
			return "oe";
		} else if (c == 'Ø') {
			return "OE";
		} else if (c == 'ø') {
			return "oe";
		} else if (c == 'Š') {
			return "S";
		} else if (c == 'š') {
			return "s";
		} else if (c == 'Ù') {
			return "U";
		} else if (c == 'ù') {
			return "u";
		} else if (c == 'Ú') {
			return "U";
		} else if (c == 'ú') {
			return "u";
		} else if (c == 'Û') {
			return "U";
		} else if (c == 'û') {
			return "u";
		} else if (c == 'Ü') {
			return "U";
		} else if (c == 'ü') {
			return "u";
		} else if (c == 'Ý') {
			return "Y";
		} else if (c == 'ý') {
			return "y";
		} else if (c == 'Þ') {
			return "th";
		} else if (c == 'þ') {
			return "TH";
		} else if (c == 'Ÿ') {
			return "Y";
		} else if (c == 'ÿ') {
			return "y";
		}

		return "_";

	}

	/**
	 * converts a char to an xml character entity
	 * 
	 * @return an xml character entity representation of the incoming char
	 */
	public static final String xmlEscape(char ch) {
		String ret = String.valueOf(ch);
		char[] a = ret.toCharArray();
		ret = "&#x"
				+ Integer.toHexString(Character.codePointAt(a, 0))
				+ ";";
		return ret;
	}

	/**
	 * converts a Character to an xml character entity
	 * 
	 * @return an xml character entity representation of the incoming Character
	 */
	public static final String xmlEscape(
			Character ch) {
		return xmlEscape(ch.charValue());
	}

	/**
	 * converts each character of a string to xml character entities
	 * @return a string of xml character entities
	 */
	public static final String xmlEscape(
			String string) {
		String ret = "";
		char[] a = string.toCharArray();
		for (int i = 0; i < a.length; i++) {
			ret = ret + xmlEscape(a[i]);
		}
		return ret;
	}

	/**
	 * converts a char to a java-style unicode escaped string
	 */
	public static final String unicodeEscape(
			char ch) {
		if (ch < 0x10) {
			return "\\u000"
					+ Integer.toHexString(ch);
		} else if (ch < 0x100) {
			return "\\u00"
					+ Integer.toHexString(ch);
		} else if (ch < 0x1000) {
			return "\\u0"
					+ Integer.toHexString(ch);
		}
		return "\\u" + Integer.toHexString(ch);
	}

	/**
	 * converts a Character to a java-style unicode escaped string
	 */
	public static final String unicodeEscape(
			Character ch) {
		return unicodeEscape(ch.charValue());
	}

	/**
	 * converts each char of a string to java-style unicode escaped strings
	 */
	public static final String unicodeEscape(
			String string) {
		String ret = "";
		char[] a = string.toCharArray();
		for (int i = 0; i < a.length; i++) {			
			ret = ret + unicodeEscape(a[i]);;
		}
		return ret;
	}

}
