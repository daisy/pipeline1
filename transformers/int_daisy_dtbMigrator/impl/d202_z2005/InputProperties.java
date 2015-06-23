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
package int_daisy_dtbMigrator.impl.d202_z2005;

import int_daisy_dtbMigrator.BookStruct;
import int_daisy_dtbMigrator.DtbDescriptor;
import int_daisy_dtbMigrator.DtbType;

import java.util.Map;

import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.util.FilesetLabelProvider;

/**
 * Provider of various properties of input DTB. 
 * @author Markus Gylling
 */
class InputProperties {
	private String identifier;
	private String title;
	private boolean isNccOnly;

	InputProperties(Fileset inputFileset, DtbDescriptor inputDescriptor, Map<String,String> parameters) {
		FilesetLabelProvider labelProvider = new FilesetLabelProvider(inputFileset);
		identifier = labelProvider.getFilesetIdentifier();
		String identifierParam = parameters.get("newIdentifier");
		if(identifierParam!=null && identifierParam.length()>0)identifier=identifierParam; 
		title = labelProvider.getFilesetTitle();
		isNccOnly = setNccOnly(inputFileset, inputDescriptor, parameters);
	}
	

	private boolean setNccOnly(Fileset fileset, DtbDescriptor inputDescriptor, Map<String, String> parameters) {
		String inputType = parameters.get("inputType");
		if(inputType.equals("TEXT")) return false;
		if(inputType.equals("NCC_NCX_ONLY")) return true; 	
		//else value is set to "DETECT"
		if(inputDescriptor.getType() == DtbType.AUDIO) return true;
		return isUntrueNccOnly(fileset);		
	}
	
	/**
	 * Called when needed to find out if a DTB is untrue ncc/ncx only, 
	 * meaning that it does have content docs that are
	 * contentual clones of the ncc/ncx
	 */
	private boolean isUntrueNccOnly(Fileset fileset) {
		//TODO
		return false;
	}


	String getIdentifier() {
		return identifier;
	}

	String getTitle() {
		return title;
	}

	/**
	 * Determine whether input DTB is NCC-only, refining the property in the DtbDescriptor
	 * through letting user inparam override the finding, and through heuristically trying 
	 * to find out whether a DTB is a false NCC-Only (eg content doc is a contentual clone of the NCC).
	 * @return true of input DTB is true or untrue 2.02 NCC-only, else false
	 */
	boolean isNccOnly() {
		return isNccOnly;
	}
	
	/**
	 * Get the default state of a skippable type.
	 */
	Boolean getDefaultState(BookStruct struct) {
		//TODO
		return Boolean.TRUE;
	}
}
