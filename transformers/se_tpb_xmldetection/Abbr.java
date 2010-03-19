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
 * Information about an initialism, acronym, abbreviation or fix
 * and its position in the text.
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
    
    /**
     * Gets the end index (position)
     * @return the end index
     */
    public int getEnd() {
        return end;
    }
    
    /**
     * Gets the expanded value
     * @return the expanded value
     */
    public String getExpansion() {
        return expansion;
    }
    
    public String getExpAttr() {
        return expAttr;
    }
    
    /**
     * Gets the name (key)
     * @return the name
     */
    public String getKey() {
        return key;
    }
    
    /**
     * Gets the start index (position)
     * @return the start index
     */
    public int getStart() {
        return start;
    }
    
    /**
     * Gets the type (acronym/abbreviation/initialism/fix) 
     * @return the type
     */
    public int getType() {
        return type;
    }    
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
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
