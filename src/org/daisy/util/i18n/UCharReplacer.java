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
package org.daisy.util.i18n;

import java.io.IOException;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.Normalizer;
import com.ibm.icu.text.UCharacterIterator;

/**
 * Substitute unicode characters with replacement strings.
 * 
 * <p>The substitution is made using different attempts in a series of preference;
 * each successor is considered a fallback to its predecessor.</p>
 * <ol>
 *   <li>Locate a replacement string in one or several user provided tables;</li>
 *   <li>Optional fallback: attempt to create a replacement using transliteration by nonspacing mark removal;</li>
 *   <li>Optional fallback: attempt to create a replacement using transliteration to Latin characters;</li>
 *   <li>Optional fallback: retrieve a replacement string based on UCD names</li>
 * </ol>
 * 
 * <p>All fallbacks are disabled by default.</p>
 * 
 * <p>By setting an "exclusion reportoire" a set of characters are defined which are considered "allowed": replacement
 * will not be attempted on a character that is a member of an excluded repertoire.</p>

 * <p>The use of this class <em>may</em> result in a change in unicode character composition between input and output. 
 * If you need a certain normalization form, normalize after the use of this class.</p>
 * 
 * <p>Usage example:</p>
 * <code><pre>
 * 	UCharReplacer ucr = new UCharReplacer();
 *	ucr.addTranslationTable(fileURL);
 *  ucr.addTranslationTable(fileURL2);
 *  ucr.setExclusionRepertoire(Charset.forName("ASCII"));
 *	String ret = ucr.toReplacementString(input);
 * </pre></code>
 *  
 * <p>The translation table file is using the same xml format as that of
 * java.util.Properties [1][2], using the HEX representation (without 
 * the characteristic 0x-prefix!) of a unicode character as the <tt>key</tt> 
 * attribute and the replacement string as value of the <tt>entry</tt> 
 * element.</p>
 * 
 * <p>If the <tt>key</tt> attribute contains exactly one unicode codepoint 
 * (one character) it will be treated literally. It will not be interpreted 
 * as a HEX representation of another character, even if theoretically 
 * possible. E.g. if the <tt>key</tt> is "a", it will be
 * treated as 0x0061 rather than as 0x000a</p>
 * 
 * <p>Note - there is a significant difference between a unicode codepoint (32 bit int)
 * and a UTF16 codeunit (=char) - a codepoint consists of one or two codeunits.</p>  
 * <p>To make sure an int represents a codepoint and not a codeunit, use for example
 * <code>com.ibm.icu.text.Normalizer</code> to NFC compose, followed by 
 * <code>com.ibm.icu.text.UCharacterIterator</code> to retrieve possibly non-BMP codepoints 
 * from a string.</p>
 *  
 * [1] http://java.sun.com/j2se/1.5.0/docs/api/java/util/Properties.html
 * [2] http://java.sun.com/dtd/properties.dtd
 *
 * @author Markus Gylling
 */

public class UCharReplacer  {	
	private ArrayList<Map<Integer,String>> mSubstitutionTables = null; 					//ArrayList<HashMap:<int codepoint>,<replaceString>>: all loaded translationtables
	private Map<Integer,String> mSubstitutionTable = null;						//represents the currently selected translationTable
	private Iterator<Map<Integer,String>> mSubstitutionTablesIterator = null;			//recycled tables map iterator
	private int mSubstitutionTableUseCount = 0;						//counter: number of replaces made using any loaded tables.
	private Map<Integer,String> mSubstitutionTableFailures = null; 				//<Integer,String>; one entry per user table char replacement failure
	private boolean mFallbackToLatinTransliteration = false;		//whether to attempt transliteration to Latin when a user table does not provide a replacement for a codepoint
	private boolean 
		mFallbackToNonSpacingMarkRemovalTransliteration = false;	//as above, but nonspacing mark (accent) removal
	private boolean mFallbackToUCD = false;							//whether to apply the ultimate resort fallback (ucd nicenames from ICU4J). 		
	private CharsetEncoder mExclusionRepertoire = null;				//if not null, represents (the encoder of) a set of "allowed" (not to be translated) characters 
	
	public final int FALLBACK_TRANSLITERATE_ANY_TO_LATIN = 1;
	public final int FALLBACK_TRANSLITERATE_REMOVE_NONSPACING_MARKS = 2;
	public final int FALLBACK_USE_UCD_NAMES = 3;
	
	/**
	 * Default constructor.
	 */
	public UCharReplacer() {
		mSubstitutionTables = new ArrayList<Map<Integer,String>>();
		mSubstitutionTableFailures = new HashMap<Integer,String>();		
	}

	
	/**
	 * Set the state of a certain fallback type.
	 * @param fallbackType The type of fallback (static int on this class) to set state of.
	 * @param state Whether the state should be active (true) or disabled (false).
	 * @throws IllegalArgumentException if inparam type is not recognized.
	 */
	public void setFallbackState(int fallbackType, boolean state) throws IllegalArgumentException {
		switch (fallbackType) {
			case FALLBACK_TRANSLITERATE_ANY_TO_LATIN:
				mFallbackToLatinTransliteration = state;
				break;
			case FALLBACK_TRANSLITERATE_REMOVE_NONSPACING_MARKS:
				mFallbackToNonSpacingMarkRemovalTransliteration = state;
				break;
			case FALLBACK_USE_UCD_NAMES:
				mFallbackToUCD = state;
				break;
			default:
				throw new IllegalArgumentException(Integer.toString(fallbackType));
		}
		
	}
	
	/**
	 * Retrieve the state of a certain fallback type.
	 * @param fallbackType The type of fallback (static int on this class) to check state of.
	 * @throws IllegalArgumentException if inparam type is not recognized.
	 */
	public boolean getFallbackState(int fallbackType) throws IllegalArgumentException {
		switch (fallbackType) {
			case FALLBACK_TRANSLITERATE_ANY_TO_LATIN:
				return mFallbackToLatinTransliteration;
			case FALLBACK_TRANSLITERATE_REMOVE_NONSPACING_MARKS:
				return mFallbackToNonSpacingMarkRemovalTransliteration;				
			case FALLBACK_USE_UCD_NAMES:
				return mFallbackToUCD;			
			default:
				throw new IllegalArgumentException(Integer.toString(fallbackType));				
		}		
	}
	
	
	
	/**
	 * Add a table for use as source for character substitution strings.	 
	 * <p>This method can be called multiple times prior to a getReplacementString() 
	 * call to add several tables. The  table preference order is determined by add 
	 * order (first added, highest preference).</p>
	 * 
	 * @param table URL of the table to add	 
	 * @throws IOException if the table was not successfully loaded.
	 */
	public void addSubstitutionTable(URL table) throws IOException {
		try{
			mSubstitutionTables.add(loadTable(table));
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}
	
	/**
	 * @return the list of loaded character substitution tables. This list is never null, but may be empty.
	 */
	public List<Map<Integer,String>> getTranslationTables() {		
		return mSubstitutionTables;		
	}
	
	
	/**
	 * Set a repertoire of "excluded" (not to be substituted) characters.
	 * @param charset The exclusion repertoire
	 * @throws UnsupportedOperationException If inparam charset does not support encoding.
	 */
	public void setExclusionRepertoire(Charset charset) throws UnsupportedOperationException {		
		mExclusionRepertoire = charset.newEncoder();
	}
	
	
	/**
	 * @return the repertoire of "excluded" (not to be substituted) characters,
	 * or null if no exclusion repertoire has been set.
	 */
	public Charset getExclusionRepertoire(){
		return (mExclusionRepertoire!=null) ? mExclusionRepertoire.charset() : null;
	}
	
	/**
	 * Retrieve a count on how many times character substitution has occured using a substitution table entry.
	 * @see #reset()
	 */
	public int getTranslationTableHitCount(){
		return mSubstitutionTableUseCount;
	}	
	
	/**
	 * Retrieve a Map [Integer,String] of substitution table failures (ie substitutions that were cancelled or done using fallbacks.)
	 * <p>The Integer represents the unicode codepoint for which substitution failed, the string (which can be null) the value that
	 * was used instead.
	 * @see #reset() 
	 */
	public Map<Integer,String> getTranslationTableFailures() {
		return mSubstitutionTableFailures;
	}
	
	
	/**
	 * Retrieve a substitution character sequence for a single Unicode codepoint. 
	 * @param codePoint Unicode codepoint to retrieve a replacement for.
	 * @return A replacement character sequence if substitution succeeded, else null.
	 * @see #replace(CharSequence)
	 * @see #getTranslationTableFailures()
	 */
	public CharSequence replace(int codePoint) {
		return getSubstitutionChars(codePoint);
	}

	
	/**
	 * Replace codepoints in a character sequence with substitute characters.	 
	 * @param input the character sequence to perform codepoint-to-replacementchars substitution in.
	 * @return a character sequence whose codepoints may or may not
	 * have been replaced by substitution characters, depending on settings and substitution success.
	 * @see #replace(int)
	 * @see #getTranslationTableFailures()
	 */
	public CharSequence replace(CharSequence input) {
		int codePoint;
		
		StringBuilder sb = new StringBuilder(input.length());
		
		//normalize to eliminate any ambiguities vis-a-vis the user tables
		Normalizer.normalize(input.toString(),Normalizer.NFC);
		
		//iterate over each codepoint in the input string
		UCharacterIterator uci = UCharacterIterator.getInstance(input.toString());							
		while((codePoint=uci.nextCodePoint())!=UCharacterIterator.DONE){									
			CharSequence substitution = getSubstitutionChars(codePoint);
			if(null!=substitution && substitution.length()>0) {
				//a replacement occured
				sb.append(substitution);
			}else{
				//a replacement didnt occur
				sb.appendCodePoint(codePoint);
			}
		}					
		return sb;
	}
	
	/**
	 * Attempt to create a substitution charsequence for a given codepoint.
	 * <p>This is the private performer method that does the job for 
	 * the public accessors.</p>
	 * 
	 * @return a replacement sequence of characters if a replacement string 
	 * was found in loaded tables or any active fallbacks, or null if a replacement 
	 * text was not found in tables or active fallbacks, or null if the codePoint 
	 * was represented in an exclusion repertoire.
	 */
	private CharSequence getSubstitutionChars(int codePoint) {			
		CharBuffer cbCodePoint = CharBuffer.wrap(Character.toChars(codePoint));
				
		//Should we exclude this codepoint from translation attempts?
		if((mExclusionRepertoire != null) && (mExclusionRepertoire.canEncode(cbCodePoint))) {
			//... yes we should.
			return null;
		}
	
		//try to locate a substitute string		
		String substitute = null;		
		
		//do we have a substitute in loaded user tables?
		substitute = retrieveSubstituteFromUserTables(codePoint);		
		if(substitute!=null) {			
			mSubstitutionTableUseCount++;
			return substitute;
		}
										
		/*
		 * the fallbacks below may generate results
		 * that contain chars that the user actually
		 * wants to replace. Because of the infinite loop
		 * risks involved, we do not recurse on these values, 
		 * but simply generate a warning.
		 */
		
		if(mFallbackToNonSpacingMarkRemovalTransliteration) {
			substitute = retrieveSubstituteFromTransliteration(codePoint, FALLBACK_TRANSLITERATE_REMOVE_NONSPACING_MARKS);
//			if(substitute!=null && substitute.length()==0){
//				//we substituted a standalone nonspacing mark with the empty string.
//			}
		}
		
		if(null==substitute && mFallbackToLatinTransliteration) {
			substitute = retrieveSubstituteFromTransliteration(codePoint, FALLBACK_TRANSLITERATE_ANY_TO_LATIN);			
		}
				
		if(null==substitute && mFallbackToUCD) {
			substitute = retrieveSubstituteFromUCDNames(codePoint);
		}
				
		this.addTableFailureWarning(codePoint, substitute);
		return substitute;
	}
	
	/**
	 * @return a substite string if available in tables, or null if not available
	 */
	private String retrieveSubstituteFromUserTables(int codePoint) {
		Integer integer = Integer.valueOf(codePoint);		
		for (mSubstitutionTablesIterator = mSubstitutionTables.iterator(); mSubstitutionTablesIterator.hasNext();) {
			mSubstitutionTable = mSubstitutionTablesIterator.next();
			if(mSubstitutionTable.containsKey(integer)) {
				return mSubstitutionTable.get(integer);
			}
		}
		return null;
	}

	/**
	 * @return a substite string using toascii transliteration, or null if transliteration failed
	 */
	
	private String retrieveSubstituteFromTransliteration(int codePoint, int type) {		
		try{
			String codePointString = UCharacter.toString(codePoint);
			String transliterated = null;
			if(type == FALLBACK_TRANSLITERATE_ANY_TO_LATIN) {
				transliterated = CharUtils.transliterateAnyToLatin(codePointString);
			}else if(type == FALLBACK_TRANSLITERATE_REMOVE_NONSPACING_MARKS) {
				transliterated = CharUtils.transliterateNonSpacingMarkRemoval(codePointString);
			}
			
			if(transliterated != null && (!transliterated.equals(codePointString))) {
				//the transliterator returned a result, check it
				if(mExclusionRepertoire != null){
					if(mExclusionRepertoire.canEncode(transliterated)){
						//translit succeeded
						return transliterated;
					}
					//we know that the translit string contains unallowed chars
					return null;
				}
				//no exclusion repertoire, return even though we
				//dont know whether the chars are ok
				return transliterated;
			}
		}catch (Exception e) {
			
		}		
		return null;
	}
	
	/**
	 * @return a substite string using character names from the unicode database, or null if name retrieval fails
	 */
	private String retrieveSubstituteFromUCDNames(int codePoint) {
		try{
			if(UCharacter.isValidCodePoint(codePoint)){
				return UCharacter.getName(codePoint);
			}	
		}catch (Exception e) {
			
		}		
		return null;
	}
	

	
	/**
	 * Loads a table using the Properties class.
	 */
	private HashMap<Integer,String> loadTable(URL tableURL) throws IOException {
		HashMap<Integer,String> map = new HashMap<Integer,String>();
		
		// Martin Blomberg 2006-08-15:
		Properties props = new Properties();
		props.loadFromXML(tableURL.openStream());
		Set<?> keys = props.keySet();
		for (Iterator<?> it = keys.iterator(); it.hasNext(); ) {
			String key = (String) it.next();
			if (key.codePointCount(0, key.length())==1) {
				map.put(key.codePointAt(0), props.getProperty(key));
			} else {
				try {
					map.put(Integer.decode("0x" + key), props.getProperty(key));
				} catch (NumberFormatException e) {
					System.err.println("error in translation table " 
									+ tableURL.toString() + ": attribute key=\"" + key + "\" is not a hex number.");
				}
			}
		}	
		return map;
	}
	
	
	/**
	 * Add a warning to the warnings list. 
	 * A warning is issued each time a codepoint to be translated had no match in loaded tables.
	 * @param codePoint The codePoint for which table lookup failed.
	 * @param result The string (or null) which was generated instead.
	 */
	private void addTableFailureWarning(int codePoint, String result) {
		mSubstitutionTableFailures.put(Integer.valueOf(codePoint), result);
	}
	
	/**
	 * Resets this object to its initial state.
	 */
	
	public void reset() {
		mSubstitutionTables.clear();
		mSubstitutionTableFailures.clear();
		mSubstitutionTableUseCount = 0;
		mFallbackToLatinTransliteration = false;
		mFallbackToNonSpacingMarkRemovalTransliteration = false;
		mFallbackToUCD = false; 		
		mExclusionRepertoire = null;	
	}
	
	/**
	 * @deprecated use .addSubstitutionTable(URL) as we are XML now.
	 */
	public void addTranslationTable(URL table, @SuppressWarnings("unused")
	String encoding) throws IOException {
		addSubstitutionTable(table);
	}
	
	/**
	 * @deprecated use .replace(CharSequence) instead
	 */
	public String toReplacementString(String s) {
		return this.replace(s).toString();
	}
	
	/**
	 * @deprecated use .replace(int) instead
	 */
	public String toReplacementString(int codePoint) {
		return this.replace(codePoint).toString();
	}
	
}