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
 * Interface for text matchers.
 * @author Linus Ericson
 */
public interface TextMatcher {

    /**
     * Finds the next match. This method starts att the beginning of the text to
     * search, or at the first character not matched by the previous invocation
     * of this method.
     * @return <code>true</code> if a match was found, <code>false</code> otherwise.
     */
    public boolean find();
    
    /**
     * Gets the start index of the current match.
     * @return the start index of the current match.
     * @throws IllegalStateException if the last <code>find()</code> or if <code>find()</code> has not been called yet. 
     */
    public int getStart();
    
    /**
     * Gets the end index of the current match.
     * @return the end index of the current match.
     * @throws IllegalStateException if the last <code>find()</code> or if <code>find()</code> has not been called yet.
     */
    public int getEnd();
    
    /**
     * Gets the current match.
     * @return the current match.
     * @throws IllegalStateException if the last <code>find()</code> or if <code>find()</code> has not been called yet.
     */
    public String getMatch();
    
    /**
     * Gets the text on which this <code>TextMatcher</code> was applied.
     * @return the text.
     */
    public String getText();
    
}
