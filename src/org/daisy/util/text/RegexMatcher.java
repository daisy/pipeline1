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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A <code>TextMatcher</code> that searches using a regular expression.
 * This class is just a wrapper class for the Java <code>Matcher</code>
 * class.
 * @author Linus Ericson
 */
public class RegexMatcher implements TextMatcher {

    private Matcher matcher;
    private String t;
    private int g;
    
    /**
     * Creates a new <code>RegexMatcher</code>.
     * When using this constructor, the getStart() and getEnd() functions will
     * return the indexes for the entire match, but getMatch() will only return
     * the '<code>group</code>'th matching group.
     * @param regexPattern the regular expression to use while searching.
     * @param text the text to search in.
     * @param group the regex group for getMatch() to return.
     */
    public RegexMatcher(Pattern regexPattern, String text, int group) {
        matcher = regexPattern.matcher(text);
        t = text;
        g = group;
    }
    
    /**
     * Creates a new <code>RegexMatcher</code>.
     * @param regexPattern the regular expression to use while searching.
     * @param text the text to search in.
     */
    public RegexMatcher(Pattern regexPattern, String text) {
        this(regexPattern, text, 0);
    }
    
    public boolean find() {        
        return matcher.find();
    }

    public int getStart() {        
        return matcher.start();
    }

    public int getEnd() {        
        return matcher.end();
    }

    public String getMatch() {        
        return matcher.group(g);
    }

    public String getText() {        
        return t;
    }
}
