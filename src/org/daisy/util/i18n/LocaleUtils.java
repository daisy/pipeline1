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

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Some Locale utility functions.
 * @author Linus Ericson
 */
public class LocaleUtils {
    
    private static Pattern localePattern = Pattern.compile("(\\p{Alpha}{2})(?:[-_](\\p{Alpha}{2}))?(?:[-_](\\p{Alnum}{1,8}))*");
    
    private LocaleUtils() { }
    
    /**
     * Creates a Locale object from a String.
     * This method parses the input string and creates an appropriate Locale
     * object.
     * @param lang the string representation of a locale.
     * @return the Locale
     */
    public static Locale string2locale(String lang) {
        Locale locale = null;
        if (lang != null) {
	        Matcher m = localePattern.matcher(lang);
	        if (m.matches()) {
	            locale = new Locale(m.group(1), m.group(2)!=null?m.group(2):"");
	        } 
        }
        return locale;
    }
    
    /**
     * Normalizes a locale string.
     * @param lang the string representation of a locale to normalize.
     * @return the normalized locale string representation.
     */
    public static String normalize(String lang) {        
        return string2locale(lang).toString();
    }
    
}
