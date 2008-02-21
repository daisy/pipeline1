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
