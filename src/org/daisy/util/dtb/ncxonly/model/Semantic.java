package org.daisy.util.dtb.ncxonly.model;

/**
 * The structural semantic of a model item.
 * @author Markus Gylling
 */
public enum Semantic {
	
	HEADING1 {
	    public String toString() {
	        return "h1";
	    }	    
	},
	HEADING2 {
	    public String toString() {
	        return "h2";
	    }
	},
	HEADING3 {
	    public String toString() {
	        return "h3";
	    }
	},
	HEADING4 {
	    public String toString() {
	        return "h4";
	    }
	},
	HEADING5 {
	    public String toString() {
	        return "h5";
	    }
	},
	HEADING6 {
	    public String toString() {
	        return "h6";
	    }
	},
	PAGE_NORMAL {
	    public String toString() {
	    	return "pagenum";
	    }
	},
	PAGE_SPECIAL {
	    public String toString() {
	    	return "pagenum";
	    }
	},
	PAGE_FRONT {
	    public String toString() {
	        return "pagenum";
	    }
	},
	UNKNOWN {
	    public String toString() {
	        return "unknown";
	    }
	};

}
