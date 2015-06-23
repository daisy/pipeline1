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
package us_rfbd_dtbookMigrator;

/**
 * Recognized versions of the DTBook grammar.
 * <p><strong>Note - add new versions chronologically/at the end of the list to support ordinal sorting.</strong></p>
 * @author Markus Gylling
 */
enum DtbookVersion {
	v110 {
	    public String toString() {
	        return "1.1.0";
	    }
	},
	v2005_1 {
	    public String toString() {
	        return "2005-1";
	    }
	},
	v2005_2 {
	    public String toString() {
	        return "2005-2";
	    }
	},
	v2005_3 {
	    public String toString() {
	        return "2005-3";
	    }
	};
}
