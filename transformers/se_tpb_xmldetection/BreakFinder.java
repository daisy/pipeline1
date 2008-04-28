/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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
package se_tpb_xmldetection;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

/**
 * Base class for break finders.
 * @author Linus Ericson
 */
public abstract class BreakFinder {

    protected Locale newLocale = null;
    protected Locale current = null;
    
    /**
     * Find breaks in a specified string of text.
     * @param text the text to search for breaks in.
     * @param al
     * @return a Vector of breaks
     */
    public abstract Vector<?> findBreaks(String text, ArrayList<?> al);
    
    /**
     * Sets a new Locale
     * @param locale the locale to set
     */
    public void setLocale(Locale locale) {
        newLocale = locale;
    }    
    
}
