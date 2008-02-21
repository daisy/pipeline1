package us_rfbd_dtbookMigrator;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.daisy.util.xml.xslt.stylesheets.Stylesheets;

/**
 * A manager groking Dtbook versions, their historical relation,
 * and their associated upwards migration XSLTs.
 * @author Markus Gylling
 */

class DtbookVersionManager {
	private static List<DtbookVersion> chronology = null;
	
	/**
	 * Get the URL of an XSLT that supports migrating the
	 * input DTBook version to a one notch higher version.
	 * If no migrator XSLT exists for the input version, return null.
	 */
	static URL getMigratorStylesheet(DtbookVersion version) {
		if(version==DtbookVersion.v110) {
			return Stylesheets.get("dtbook110to2005-1.xsl");
		}else
		if(version==DtbookVersion.v2005_1) {
			return Stylesheets.get("dtbook2005-1to2.xsl");
		}else
		if(version==DtbookVersion.v2005_2) {
			return Stylesheets.get("dtbook2005-2to3.xsl");
		}
		return null;
	}	
	
	/**
	 * Get an ordered list containing the Dtbook versions in
	 * the temporal order they were published.
	 */
	static List<DtbookVersion> getChronology() {
		if(chronology==null){
			chronology = new LinkedList<DtbookVersion>();
			DtbookVersion[] versions = DtbookVersion.values();
			for (int i = 0; i < versions.length; i++) {
				chronology.add(versions[i]);
			}
		}
		return chronology;
	}
	
	/**
	 * Get the next version of DTBook as published chronologically.
	 * If no next version exists, return null. 
	 * @param current A DTBook version that may have a registered next version
	 */
	static DtbookVersion getNextVersion(DtbookVersion current) {
		List<DtbookVersion> versions = getChronology();		
		boolean passedCurrent = false;
		for (DtbookVersion version : versions) {
			if(passedCurrent) return version;
			if(version == current) {
				passedCurrent = true;
			}
		}
		return null;
	}
	
	/**
	 * Get the relation of <code>to</code> to <code>compare</code>.
	 */
	static ChronologicalRelation getRelation(DtbookVersion compare, DtbookVersion to) {
		if(compare==to) return ChronologicalRelation.CONCURRENT;
		if(compare.ordinal() < to.ordinal()) return ChronologicalRelation.LATER;
		return ChronologicalRelation.EARLIER;
	}
	
	static enum ChronologicalRelation {
		CONCURRENT,
		EARLIER,
		LATER;		
	}
}
