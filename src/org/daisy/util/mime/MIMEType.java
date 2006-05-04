package org.daisy.util.mime;

import java.util.Collection;
import java.util.Map;

/**
 * Represents an atomic MIME type.
 * @author Markus Gylling
 */
public interface MIMEType {
	//width statics
	public static final int WIDTH_LOCAL = 0;
	public static final int WIDTH_LOCAL_PLUS_ALIASES = 2;
	public static final int WIDTH_LOCAL_PLUS_ALIASES_PLUS_DESCENDANTS = 4;
	public static final int WIDTH_LOCAL_PLUS_ALIASES_PLUS_ANCESTORS = 6;
	public static final int WIDTH_LOCAL_PLUS_ALIASES_PLUS_DESCENDANTS_PLUS_ANCESTORS = 8;
	
	public static final int FILENAME_PATTERN_GLOB = 0;
	public static final int FILENAME_PATTERN_REGEX = 1;

	/**
	 * @return A Map&lt;ID,MimeType&gt; of hierarchical ancestor MimeTypes if such exist. 
	 * If no ancestors exist, the return is an empty map; not null.
	 */
	public Map getAncestors() throws MIMETypeException;
	
	/**
	 * @param mime a MimeType that may or may not be a ancestor to this MimeType
	 * @return True if the inparam MimeType is an ancestor to this MimeType, false otherwise. 
	 */
	public boolean isAncestor(MIMEType mime) throws MIMETypeException;
	
	/**
	 * @return True if at least one hierarchical parent MimeType exists, 
	 * false if no parent exists.
	 */
	public boolean hasAncestors() throws MIMETypeException;
	
	/**
	 * @return A Map&lt;ID,MimeType&gt; of
	 * aliases to this MimeType if such exist. 
	 * If no aliases exist, the return is an empty map; not null.
	 */
	public Map getAliases() throws MIMETypeException;
	
	/**
	 * Asserts equalness and aliashood between this MimeType and another.
	 * Any charset field in the this or the compared MimeType is ignored
	 * when doing the comparison.
	 * @param
	 *   mime a MimeType object to test aliasness for
	 * @return 
	 * 	true if inparam MimeType is an alias to this MimeType,
	 * 	false otherwise
	 */
	public boolean isEqualOrAlias(MIMEType mime)throws MIMETypeException;
			
	/**
	 * @return True if this MimeType has
	 * aliases registered in MimeRegistry, 
	 * false otherwise.
	 */
	public boolean hasAliases() throws MIMETypeException;
			
	/**
	 * @param a MimeType that may or may not be a descendant of this MimeType.
	 * @return True if the inparam MimeType is a descendant of this MimeType, false otherwise. 
	 */
	public boolean isDescendant(MIMEType mime) throws MIMETypeException;
	
	/**
	 * @return A Map&lt;MimeType&gt; of
	 * hierarchical descendants to this MimeType if such exist. 
	 * If no descendants exist, the return is an empty map; not null.
	 */
	public Map getDescendants() throws MIMETypeException;

	/**
	 * @return True if
	 * hierarchical descendants to this MimeType exist, 
	 * false otherwise.
	 */
	public boolean hasDescendants() throws MIMETypeException;
	
	/**
 	 * @param mime A MimeType that may or may not be an ancestor or descendant of this MimeType.
	 * @return True if a
	 * hierarchical relationship to this MimeType exists, 
	 * false otherwise
	 */
	public boolean isRelative(MIMEType mime) throws MIMETypeException;
	
	/**
	 * @return the full string name identifier
	 * of this MimeType. This string - minus the optionally present charset part - is
	 * always present in the Mime Registry.
	 * @see #getContentType()
	 * @see #getSubType()
	 * @see #getCharset()
	 * @see #dropParametersPart()
	 */
	public String getString();
	
	/**
	 * @return the ID identifier of this MimeType, as specified in the MimeTypeRegistry.xml document. 
	 * @see #getContentType()
	 * @see #getSubType()
	 * @see #getParametersPart()
	 * @see #dropParametersPart()
	 * @see #getString()
	 */
	public String getId();
	
	/**
	 * @return a collection&lt;String&gt; of known filename patterns for this MimeType and its aliases. 
	 * The returned collection excludes ancestor and descendant filename patterns.
	 * The filename patterns use MS Glob format ("*.htm" etc).
	 * @see #getFilenamePatterns(int) 
	 */
	public Collection getFilenamePatterns() throws MIMETypeException ;
			
	/**
	 * @param 
	 * 	width A static int (available in MimeType.java) defining the scope of the returned colletion 
	 * (=whether to include alias, ancestor and descendant filename patterns.)
	 * @return a collection&lt;String&gt; of known filename patterns for this MimeType.
	 * The filename patterns use MS Glob format ("*.htm" etc).
	 */
	public Collection getFilenamePatterns(int width) throws MIMETypeException ;
			
	/**
	 * @param 
	 * 	width A static int (available in MimeType.java) defining the scope of the returned colletion 
	 * (=whether to include alias, ancestor and descendant filename patterns.)
	 * @param patternType
	 *  A static int (available in MimeType.java) defining the type of pattern used in the returned collection 
	 * (=ms glob or regex). 
	 * @return a collection&lt;String&gt; of known filename patterns for this MimeType.
	 * The filename patterns use MS Glob format ("*.htm" etc) or regex.
	 */
	public Collection getFilenamePatterns(int width, int patternType) throws MIMETypeException ;
	
	/**
	 * @return the content type ('top level') substring of the string identifier
	 * of this MimeType. Example: 'text' from the MIME type "text/xml".
	 * @see #getString()
	 * @see #getSubType()
	 * @see #getCharset()
	 */
	public String getContentTypePart();

	/**
	 * @return the subtype substring of the string identifier
	 * of this MimeType. Example: 'xml' from the MIME type "text/xml".
	 * @see #getString()
	 * @see #getContentType()
	 * @see #getCharset()
	 */
	public String getSubTypePart();
	
	/**
	 * @return the parameters substring of the string identifier
	 * of this MimeType, or null if no paremeter part is specified.
	 * Example: 'us-ascii' from the MIME type "text/plain;charset=us-ascii".
	 * @see #getString()
	 * @see #getSubType()
	 * @see #getContentType()
	 */
	public String getParametersPart();
	
	/**
	 * @return the ContentType and SubType substring of the string identifier
	 * of this MimeType. If no parameter part was specified during instantiation, 
	 * this method returns a string identical to the getString() method.
	 * Example: 'text/plain' from the MIME type "text/plain;charset=us-ascii".
	 * @see #getString()
	 * @see #getSubType()
	 * @see #getContentType()
	 */
	public String dropParametersPart();
	
}
