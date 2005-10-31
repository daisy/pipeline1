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
package org.daisy.util.text;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A <code>TextMatcher</code> that searches for a collection of strings.
 * This class searches for the earliest and longest possible occurrence
 * of any of the strings in the collection.
 * @author Linus Ericson
 */
public class StringCollectionMatcher implements TextMatcher {

    private Collection coll;
    private String text;
    private int start = 0;
    private int end = 0;
    private String match;
    private boolean result = false;
    
    private String suffix;
    private int currentStart = 0;
    private int currentEnd = 0;
    
    /**
     * Creates a new <code>StringCollectionMatcher</code>.
     * When using this constructor, a specified suffix will be allowed in the
     * matches. The getStart() and getEnd() functions will return the indexes
     * for the match with the suffix included, but the getMatch() function
     * will return the match without suffix.
     * @param strings a collection of strings to serach for.
     * @param textToSearch the text to search.
     * @param suffixPattern a suffix regular expression.
     */
    public StringCollectionMatcher(Collection strings, String textToSearch, String suffixPattern) {
        coll = strings;
        text = textToSearch;
        suffix = suffixPattern;
    }
    
    /**
     * Creates a new <code>StringCollectionMatcher</code>.
     * @param strings a collection of strings to serach for.
     * @param textToSearch the text to search.
     */
    public StringCollectionMatcher(Collection strings, String textToSearch) {
        this(strings, textToSearch, null);
    }
    
    /**
     * Adds more strings to the collection.
     * @param strings a collection of strings
     */
    public void addStrings(Collection strings) {
        coll.addAll(strings);
    }
    
    public void indexOf(String textToSearch, String stringToFind, int position, String suffixPattern) {       
        if (suffixPattern != null) {
            Pattern pattern = Pattern.compile(Pattern.quote(stringToFind) + "(?:" + suffixPattern + ")?");
            Matcher matcher = pattern.matcher(textToSearch);
            if (matcher.find(position)) {
                currentStart = matcher.start();
                currentEnd = matcher.end();
            } else {
                currentStart = -1;
            }
        } else {
            currentStart = textToSearch.indexOf(stringToFind, position);
            currentEnd = currentStart + stringToFind.length();
        }        
    }
    
    public boolean find() {
        result = false;
        start = Integer.MAX_VALUE;
        int originalEnd = end;
        for (Iterator it = coll.iterator(); it.hasNext(); ) {
            String current = (String)it.next();
            //int currentStart = text.indexOf(current, originalEnd);
            indexOf(text, current, originalEnd, suffix);
            if (currentStart != -1) {
                // A match was found
                
                if (currentStart < start || (currentStart == start && currentEnd > end)) {
                    // Earlier match or longer match
                    start = currentStart;
                    end = currentEnd;
                    match = current;
                    result = true;
                }
            }
        }
        return result;
    }
    
    public int getStart() {
        if (!result) {
            throw new IllegalStateException("Cannot call start() when find() failed or has not been called");
        }
        return start;
    }
    
    public int getEnd() {
        if (!result) {
            throw new IllegalStateException("Cannot call end() when find() failed or has not been called");
        }
        return end;
    }
    
    public String getMatch() {
        if (!result) {
            throw new IllegalStateException("Cannot call getMatch() when find() failed or has not been called");
        }
        return match;
    }

    public String getText() {
        return text;
    }
}
