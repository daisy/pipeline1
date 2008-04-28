/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
