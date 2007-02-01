/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2006  Daisy Consortium
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

package se_tpb_dtbSplitterMerger;
/*
 * 
 */


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
/**
 * @author Piotr Kiernicki
 */
public class DtbVolumeSet extends ArrayList {
	
	/**
     * 
     */
    private static final long serialVersionUID = -1800274733980879914L;
    /**
	 * 
	 */
	private String titlePromptHrefValue = null;
	private File titlePromptAudio = null;
	private File titlePromptSmil = null;
/*
 * Returns a colllection with smil sets for each volume.
 */
	public HashMap extractSmilSet() {
		HashMap smilSet = new HashMap();
		/*
		 * Extract volumes from the DtbVolumeSet as an Iterator.
		 * DtbVolumeSet is a List.
		 */
		Iterator volumes = this.iterator();
		while(volumes.hasNext()){
			DtbVolume vol = (DtbVolume)volumes.next();
		
			List volSmilSet = vol.getSmilFiles();
			int volNr = vol.getVolumeNr();
			/*
			 * Put the _volNr as the key volSmilSet as the value
			 */
			smilSet.put(new Integer(volNr), volSmilSet);
		 
		}
	
		return smilSet;
	}


	public String getTitlePromptHrefValue() {
		return this.titlePromptHrefValue;
	}

	public void setTitlePromptHrefValue(String string) {
        this.titlePromptHrefValue = string;
	}


	public File getTitlePromptAudio() {
		return this.titlePromptAudio;
	}

	public void setTitlePromptAudio(File file) {
        this.titlePromptAudio = file;
	}

	public File getTitlePromptSmil() {
		return this.titlePromptSmil;
	}

	public void setTitlePromptSmil(File file) {
        this.titlePromptSmil = file;
	}

}
