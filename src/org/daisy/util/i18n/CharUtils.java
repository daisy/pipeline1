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
	 * @return true if the character is within the range of alphabetic 7bit ascii (=[A-Za-z]), 
	 * false otherwise
	 */
	public static boolean isAsciiAlpha(char ch) {
		return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
	}

	/**
	 * @return true if the character is within the range of numeric 7bit ascii (=[0-9]), 
	 * false otherwise
	 */

	public static boolean isAsciiNumeric(char ch) {
		return (ch >= '0' && ch <= '9');
	}

	/**
	 * @return true if the character is within the range of alphanumeric 7bit ascii (=[A-Za-z0-9]), 
	 * false otherwise
	 */

	public static boolean isAsciiAlphanumeric(char ch) {
		return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9');
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
	public static final boolean isAsciiPrintable(char[] ch) {
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
	public static boolean isUnicodeWhitespace(char ch) {
		return isUnicodeWhitespace(Character.valueOf(ch));
	}

	/**
	 * @return true if each char in the array is defined (by unicode) as whitespace, false otherwise
	 */
	public static boolean isUnicodeWhitespace(char[] ch) {
		for (int i = 0; i < ch.length; i++) {
			if (!isUnicodeWhitespace(ch[i])) {
				return false;
			}
		}// ch.length
		return true;		
	}
	
	/**
	 * @return true if each char in the String is defined (by unicode) as whitespace, false otherwise
	 */
	public static boolean isUnicodeWhitespace(String string) {
		return isUnicodeWhitespace(string.toCharArray()); 
	}
	
	/**
	 * @return true if the char is defined (by unicode) as whitespace, false otherwise
	 */
	public static boolean isUnicodeWhitespace(
			Character ch) {
		return Character.isSpaceChar(ch.charValue());
	}

	/**
	 * @return true if the char is defined by XML as whitespace, false otherwise.
	 * see: http://www.w3.org/TR/1998/REC-xml-19980210#NT-S
	 */
	public static boolean isXMLWhiteSpace(char ch) {
	  return (ch == 0x20) || (ch == 0x09) || (ch == 0xD) || (ch == 0xA);
	}
	
	/**
	 * @return true if each char in the array is defined by XML as whitespace, false otherwise.
	 * see: http://www.w3.org/TR/1998/REC-xml-19980210#NT-S
	 */
	public static boolean isXMLWhiteSpace(char[] ch) {
		for (int i = 0; i < ch.length; i++) {
			if (!isXMLWhiteSpace(ch[i])) {
				return false;
			}
		}// ch.length
		return true;
	}
	
	/**
	 * @return true if each char in the String is defined by XML as whitespace, false otherwise.
	 * see: http://www.w3.org/TR/1998/REC-xml-19980210#NT-S
	 */
	public static boolean isXMLWhiteSpace(String string) {
		return isXMLWhiteSpace(string.toCharArray()); 
	}
	
	/**
	 * Converts each unicode whitespace char in the incoming char array with 
	 * the given replacement char
	 * If the given replacement char is whitespace too, fallback to the underscore char
	 * @param ch the input char array to replace whitespace in
	 * @param replace the char to replace any found whitespace with
	 */
	public static char[] toNonWhitespace(char[] ch, char replace) {
		String result = "";
		char replacement = replace;		
		if(isUnicodeWhitespace(replace)) replacement ='_';
				
		for (int i = 0; i < ch.length; i++) {
			if(isUnicodeWhitespace(ch[i])) {
				result = result + replacement;
			}else{
				result = result + ch[i];
			}
		}
		return result.toCharArray();
	}

	/**
	 * Converts each unicode whitespace char in the incoming string with 
	 * the given replacement char
	 * If the given replacement char is whitespace too, fallback to the underscore char
	 * @param string the string to replace whitespace in
	 * @param replace the char to replace any found whitespace with
	 */
	public static String toNonWhitespace(String string, char replace) {
		return String.valueOf(toNonWhitespace(string.toCharArray(),replace));	
	}
	
	/**
	 * Converts each unicode whitespace char in the incoming string with the replacement char underscore
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

    /**
     * Tests whether the given character is usable as the
     * first character of an XML name.
     */
    public static boolean isXMLNameFirstCharacter(char c) {
    	return (NAME_FIRST_CHARACTER[c / 32] & (1 << (c % 32))) != 0;
    }
    
    /**
     * Tests whether the given character is a valid XML name character.
     */
    public static boolean isXMLNameCharacter(char c) {
    	return (NAME_CHARACTER[c / 32] & (1 << (c % 32))) != 0;
    }
    
    /**
     * Tests whether each character in the array is a valid XML name character.
     */
    public static boolean isXMLNameCharacters(char[] ch) {
		for (int i = 0; i < ch.length; i++) {
			if (!isXMLNameCharacter(ch[i])) {
				return false;
			}
		}// ch.length
		return true;    	
    }

    /**
     * Tests whether each character in the String is a valid XML name character.
     */
    public static boolean isXMLNameCharacters(String string) {
    	return isXMLNameCharacters(string.toCharArray());
    }

    
    /**
     * The bit array representing the first character of an XML name.
     * From: org.apache.batik.xml.XMLCharacters
     */
     public final static int[] NAME_FIRST_CHARACTER = {
         0,67108864,-2013265922,134217726,0,0,-8388609,-8388609,-1,2146697215,
         -514,2147483647,-1,-1,-8177,-63832065,16777215,0,-65536,-1,-1,
         -134217217,3,0,0,0,0,0,-10432,-5,1417641983,1048573,-8194,-1,
         -536936449,-1,-65533,-1,-58977,54513663,0,-131072,41943039,-2,127,0,
         -65536,460799,0,134217726,2046,-131072,-1,2097151999,3112959,96,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,-32,603979775,-16777216,3,-417824,
         63307263,-1342177280,196611,-423968,57540095,1577058304,1835008,
         -282656,602799615,0,1,-417824,600702463,-1342177280,3,-700594208,
         62899992,0,0,-139296,66059775,0,3,-139296,66059775,1073741824,3,
         -139296,67108351,0,3,0,0,0,0,-2,884735,63,0,-17816170,537750702,31,0,
         0,0,-257,1023,0,0,0,0,0,0,0,0,0,-1,-65473,8388607,514797,1342177280,
         -2110697471,2908843,1073741824,-176109312,7,33622016,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,-1,268435455,-1,-1,
         67108863,1061158911,-1,-1426112705,1073741823,-1,1608515583,
         265232348,534519807,0,0,0,0,0,0,0,0,0,19520,0,0,7,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,128,1022,-2,-1,2097151,-2,-1,134217727,-32,8191,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         63,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
         -1,-1,15,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         };

     /**
      * The bit array representing a character compositing an XML name.
      * From: org.apache.batik.xml.XMLCharacters
      */
      public final static int[] NAME_CHARACTER = {
          0,134176768,-2013265922,134217726,0,8388608,-8388609,-8388609,-1,
          2146697215,-514,2147483647,-1,-1,-8177,-63832065,16777215,0,-65536,
          -1,-1,-134217217,196611,0,-1,-1,63,3,-10304,-5,1417641983,1048573,
          -8194,-1,-536936449,-1,-65413,-1,-58977,54513663,0,-131072,41943039,
          -2,-130945,-1140850693,-65514,460799,0,134217726,524287,-64513,-1,
          2097151999,-1081345,67059199,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-18,
          -201326593,-14794753,65487,-417810,-741999105,-1333773921,262095,
          -423964,-747766273,1577073031,2097088,-282642,-202506753,15295,65473,
          -417810,-204603905,-1329579633,65475,-700594196,-1010841832,8404423,
          65408,-139282,-1007682049,6307295,65475,-139284,-1007682049,
          1080049119,65475,-139284,-1006633473,8404431,65475,0,0,0,0,-2,
          134184959,67076095,0,-17816170,1006595246,67059551,0,50331648,
          -1029700609,-257,-130049,-21032993,50216959,0,0,0,0,0,0,0,-1,-65473,
          8388607,514797,1342177280,-2110697471,2908843,1073741824,-176109312,
          7,33622016,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,
          -1,-1,-1,268435455,-1,-1,67108863,1061158911,-1,-1426112705,
          1073741823,-1,1608515583,265232348,534519807,0,0,0,0,0,0,536805376,2,
          0,19520,0,0,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,160,4128766,-2,-1,1713373183,
          -2,-1,2013265919,-32,8191,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,63,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
          -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,15,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
          0,0,0,0,0,0,0,
          };

}
