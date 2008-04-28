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

/**
 * Information about an initialism, acronym, abbreviation or fix. 
 * @author Linus Ericson
 */
/*package*/ class Abbr {

    /** Initialism constant */     
    public static final int INITIALISM = 1;
    /** Acronym constant */
    public static final int ACRONYM = 2;
    /** Abbreviation constant */
    public static final int ABBREVIATION = 4;
    /** Fix constant */
    public static final int FIX = 8;
    
    private int start;
    private int end;
    private int type;
    private String key;
    private String expansion;
    private String expAttr;
    
    /**
     * Creates a new <code>Abbr</code> object.
     * @param k unexpanded acronym/abbreviation
     * @param exp expanded acronym/abbreviation
     * @param t type (one of Abbr.INITIALISM, Abbr.ACRONYM, Abbr.ABBREVIATION and Abbr.FIX)
     * @param s start index
     * @param e end index
     */
    public Abbr(String k, String exp, String expAt, int t, int s, int e) {
        key = k;
        expansion = exp;
        type = t;
        start = s;
        end = e;
        expAttr = expAt;
    }
    
    public int getEnd() {
        return end;
    }
    
    public String getExpansion() {
        return expansion;
    }
    
    public String getExpAttr() {
        return expAttr;
    }
    
    public String getKey() {
        return key;
    }
    
    public int getStart() {
        return start;
    }
    
    public int getType() {
        return type;
    }    
    
    public String toString() {
        String result = null;
        switch (type) {
        case INITIALISM:
            result = "initialism\t";
            break;
        case ACRONYM:
            result = "acronym\t";
            break;
        case ABBREVIATION:
            result = "abbr\t";
            break;
        case FIX:
            result = "fix\t";
            break;
        }        
        result += key;
        result += "\t" + expansion;
        //result += "\t" + start;
        //result += "\t" + end;
        return result;// + ")";
    }
}
