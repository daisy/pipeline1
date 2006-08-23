/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.daisy.util.i18n;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.Normalizer;
import com.ibm.icu.text.UCharacterIterator;

/**
 * Substitute unicode characters with replacement strings.
 * <p>Usage example:</p>
 * <code><pre>
 * 	UCharReplacer ucr = new UCharReplacer();
 *	ucr.addTranslationTable(fileURL,"us-ascii");
 *	ucr.setFallbackToUCD(true);		
 *	String ret = ucr.toReplacementString(unicodeCodepoint);
 * </pre></code>
 * 
 * The translation table file is using the same xml format as that of
 * java.util.Properties [1][2], using the HEX representation (without 
 * the characteristic 0x-prefix!) of a unicode character as the <tt>key</tt> 
 * attribute and the replacement string as value of the <tt>entry</tt> 
 * element.
 * 
 * [1] http://java.sun.com/j2se/1.5.0/docs/api/java/util/Properties.html
 * [2] http://java.sun.com/dtd/properties.dtd
 * @author Markus Gylling
 */
public class UCharReplacer  {	
	private ArrayList translationTables = new ArrayList(); //ArrayList<HashMap:<int codepoint>,<replaceString>>
	private boolean fallbackToUCD = false;
	String replacementText = null;
	Integer integer = null;
	HashMap curTable = null;
	StringBuilder sb = new StringBuilder();
	//collects UCD replacements (no match in user provided table) 	
	private HashMap warnings = new HashMap(); //int, string

	
	public UCharReplacer() {
		
	}

	/**
	 * Translate a single Unicode codepoint to a replacement String.
	 * <p>Note - there is a significant difference between a unicode codepoint (32 bit int)
	 * and a UTF16 codeunit (=char) - a codepoint consists of one or two codeunits.</p>  
	 * <p>To make sure the inparam int represents a codepoint and not a codeunit, use for example
	 * <code>com.ibm.icu.text.Normalizer</code> to NFC compose, followed by 
	 * <code>com.ibm.icu.text.UCharacterIterator</code> to retrieve possibly non-BMP codepoints 
	 * from a string.</p>
	 * @param codePoint Unicode codepoint to transform
	 * @return The replacement text if a replacement text was found, 
	 * null if a replacement text was not found
	 * @see #addTranslationTable
	 */
	public String toReplacementString(int codePoint) {
		return getReplacementString(codePoint);
	}

	/**
	 * Replace codepoints in a string with substitute strings.
	 * <p>Iterates over all codepoints in the inparam string, searches all added translationtables, and 
	 * (if setFallbackToUCD is true) ultimately the UCD data file, for a replacement string.</p>
	 * @param string the string to perform codepoint-to-replacementstring substitution on
	 * @return a string that is guaranteed to be NFC composed, and where certain codepoints may or may not
	 * have been replaced by replacement strings, depending on which replacement tables are loaded.
	 * @see #getReplacementString(int)
	 * @see #addTranslationTable
	 */
	public String toReplacementString(String string) {
		int codePoint;
		String s;
		
		sb.delete(0,sb.length());
		Normalizer.normalize(string,Normalizer.NFC);
		UCharacterIterator uci = UCharacterIterator.getInstance(string);					
		while((codePoint=uci.nextCodePoint())!=UCharacterIterator.DONE){									
			replacementText = getReplacementString(codePoint);
			if(null!=replacementText) {
				sb.append(replacementText);
			}else{				
				s = String.copyValueOf(UCharacter.toChars(codePoint));
				Normalizer.normalize(s,Normalizer.NFC);
				sb.append(s);
			}
		}//while	
		return sb.toString();
	}
	
	/**
	 * @return an NFC normalized replacement text if a replacement 
	 * text was found in loaded tables, 
	 * null if a replacement text was not found
	 */
	private String getReplacementString(int codePoint) {
		replacementText = null;		
		integer = Integer.valueOf(codePoint);		
		for (Iterator iter = translationTables.iterator(); iter.hasNext();) {
			curTable = (HashMap) iter.next();
			replacementText = (String)curTable.get(integer);
			if(replacementText!=null) {
				break;
			}
		}
						
		if(replacementText==null && fallbackToUCD) {
			replacementText = UCharacter.getName(codePoint);
			this.addWarning(codePoint);
		}		
		
		if(replacementText!=null) {
			Normalizer.normalize(replacementText,Normalizer.NFC);
		}	
		
		return replacementText;
	}
	
	/**
	 * Add a table for use as source for translation strings.
	 * <p>The table must use the syntax of the (two first fields of) UCD UnicodeData.txt (unicodeHexValue;replacementString):</p>
	 * <code><pre>
	 * 05DE;HEBREW LETTER MEM
	 * </pre></code>
	 * <p>This method can be called multiple times prior to a getReplacementString() call to add several tables. The 
	 * table preference order is determined by add order (first added, highest preference).</p>
	 * @param table URL of the table to add
	 * @param encoding character set encoding of the table textfile. If null, default locale encoding is used.
	 * @see #fallbackToUCD
	 */
	public void addTranslationTable(URL table, String encoding) throws IOException {
		translationTables.add(loadTable(table, encoding));		
	}
	
	/**
	 * <p>Determines whether the translation should fallback to the UCD table 
	 * if a replacement text was not found in the table(s) set in #addTranslationTable().</p>
	 * <p>Except for the case when zero user tables have been added through #addTranslationTable(),
	 * the default behavior is false, that is, if no replacement text is found in the table(s) 
	 * set in #addTranslationTable(), fallback to UCD will not be made.</p>
	 * @see #addTranslationTable(URL, String)
	 */
	public void setFallbackToUCD(boolean fallback) {
		fallbackToUCD = fallback;
	}

	/**
	 * @return true if this instance is configured to fall back to the UCD table 
	 * if a replacement text is not found in the table(s) set in #addTranslationTable()
	 */
	public boolean getFallbackToUCD() {
		return fallbackToUCD; 
	}
		
	/**
	 * @return true if this instance has successfully loaded one or more
	 * user replacement tables. (ie successful completion of one or several
	 * calls to #addTranslationTable()
	 */
	public boolean hasUserTables() {
		return !this.translationTables.isEmpty(); 
	}
	
	private HashMap loadTable(URL tableURL, String encoding) throws IOException {
		HashMap map = new HashMap();
		
		// Maring Blomberg 2006-08-15:
		Properties props = new Properties();
		props.loadFromXML(tableURL.openStream());
		Set keys = props.keySet();
		for (Iterator it = keys.iterator(); it.hasNext(); ) {
			String key = (String) it.next();
			try {
				map.put(Integer.decode("0x" + key), props.getProperty(key));
			} catch (NumberFormatException e) {
				System.err.println("error in translation table " 
								+ tableURL.toString() + ": attribute key=\"" + key + "\" is not a hex number.");
			}
		}
	
		return map;
	}
	
	private void addWarning(int codePoint) {
		StringBuilder sb = new StringBuilder(40);
		sb.append("No user provided replacement text found for ");
		sb.append(CharUtils.unicodeHexEscape(codePoint).toUpperCase());
		sb.append("[");
		sb.append(String.copyValueOf(UCharacter.toChars(codePoint)));
		sb.append("]");
		
		warnings.put(Integer.valueOf(codePoint), sb.toString());
	}
	
	/**
	 * Retrieve a list of warnings issued each time
	 * a character subtitution is made based on the UCD table
	 * instead of user added tables.
	 * @return a list that may or may not be empty
	 */
	public List getWarnings() {		
		ArrayList list = new ArrayList(warnings.size());
		list.addAll(warnings.values());
		return list;
	}
		
}