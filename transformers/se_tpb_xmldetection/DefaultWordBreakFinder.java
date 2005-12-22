/*
 * DMFC - The DAISY Multi Format Converter
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
package se_tpb_xmldetection;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Vector;

/**
 * @author Linus Ericson
 */
/*package*/ class DefaultWordBreakFinder extends BreakFinder {

    protected BreakIterator iterator = BreakIterator.getWordInstance();
    
    public Vector findBreaks(String text, ArrayList al) {
        // Has the locale changed?
        if ((newLocale != null && !newLocale.equals(current)) ||
                (newLocale == null && current != null)) {
            //System.err.println(newLocale);
            if (newLocale == null) {
                iterator = BreakIterator.getWordInstance();
            } else {
                iterator = BreakIterator.getWordInstance(newLocale);
            }
            current = newLocale;
        }
        
        Vector result = new Vector();
        
        iterator.setText(text);
        //System.err.println("Text: " + text);
        int start = iterator.first();
        int end = 0;
        for (end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            //System.err.println("start: " + start + ", end: " + end + ", word: " + text.substring(start, end));
            
            if (!text.substring(start, end).matches("\\p{javaWhitespace}")) {
                result.add(new Integer(start));
                result.add(new Integer(end));
            }
            
            //result.add(new Integer(start));
        }
        //result.add(new Integer(end));
        //System.err.println("Done");
        return result;        
    }


}
