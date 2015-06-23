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
