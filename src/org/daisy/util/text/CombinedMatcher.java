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

/**
 * A <code>TextMatcher</code> that combines two <code>TextMatcher</code>s.
 * @author Linus Ericson
 */
public class CombinedMatcher implements TextMatcher {

    private TextMatcher tm1;
    private int tm1start = 0;
    private int tm1end = 0;
    private String tm1match;
    private boolean tm1result = false;
    
    private TextMatcher tm2;
    private int tm2start = 0;
    private int tm2end = 0;
    private String tm2match;
    private boolean tm2result = false;
    
    private int start = 0;
    private int end = 0;
    private String match;
    
    /**
     * Creates a new <code>TextMatcher</code> from two existing
     * <code>TextMatcher</code>s.
     * @param one a <code>TextMatcher</code> to be combined
     * @param two a <code>TextMatcher</code> to be combined
     */
    public CombinedMatcher(TextMatcher one, TextMatcher two) {
        if (one == null) {
            throw new IllegalArgumentException("TextMatcher one may not be null");
        }
        if (two == null) {
            throw new IllegalArgumentException("TextMatcher two may not be null");
        }
        tm1 = one;
        tm2 = two;
        if (!tm1.getText().equals(tm2.getText())) {
            throw new IllegalStateException("The TextMatchers must be applied on the same string!");
        }
    }
    
    public boolean find() {
        boolean result = false;
        if (!tm1result) {
            tm1result = tm1.find();
            if (tm1result) {
                tm1start = tm1.getStart();
                tm1end = tm1.getEnd();
                tm1match = tm1.getMatch();
            }
        }
        if (!tm2result) {
            tm2result = tm2.find();
            if (tm2result) {
                tm2start = tm2.getStart();
                tm2end = tm2.getEnd();
                tm2match = tm2.getMatch();
            }
        }
        
        if (tm1result && !tm2result) {
            start = tm1start;
            end = tm1end;
            match = tm1match;
            tm1result = false;
            result = true;
        } else if (!tm1result && tm2result) {
            start = tm2start;
            end = tm2end;
            match = tm2match;
            tm2result = false;
            result = true;
        } else if (tm1result && tm2result) {
            if (tm1start < tm2start) {
                start = tm1start;
                end = tm1end;
                match = tm1match;
                tm1result = false;
            } else if (tm1start == tm2start){
                if (tm1end >= tm2end) {
                    start = tm1start;
                    end = tm1end;
                    match = tm1match;
                    tm1result = false;
                } else {
                    start = tm2start;
                    end = tm2end;
                    match = tm2match;
                    tm2result = false;
                }
            } else /*tm1start > tm2start*/ {
                start = tm2start;
                end = tm2end;
                match = tm2match;
                tm2result = false;
            }
            result = true;
        }        
        return result;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getMatch() {
        return match;
    }

    public String getText() {
        return tm1.getText();
    }

}
