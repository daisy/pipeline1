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
    
    /**
     * Creates a new <code>StringCollectionMatcher</code>.
     * @param strings a collection of strings to serach for.
     * @param textToSearch the text to search.
     */
    public StringCollectionMatcher(Collection strings, String textToSearch) {
        coll = strings;
        text = textToSearch;
    }
    
    /**
     * Adds more strings to the collection.
     * @param strings a collection of strings
     */
    public void addStrings(Collection strings) {
        coll.addAll(strings);
    }
    
    public boolean find() {
        result = false;
        start = Integer.MAX_VALUE;
        int originalEnd = end;
        for (Iterator it = coll.iterator(); it.hasNext(); ) {
            String current = (String)it.next();
            int currentStart = text.indexOf(current, originalEnd);
            if (currentStart != -1) {
                // A match was found
                
                int currentEnd = currentStart + current.length();
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
