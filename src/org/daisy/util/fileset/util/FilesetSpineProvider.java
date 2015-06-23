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
package org.daisy.util.fileset.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.daisy.util.fileset.AudioFile;
import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.SmilFile;
import org.daisy.util.fileset.Z3986OpfFile;
import org.daisy.util.fileset.exception.FilesetTypeNotSupportedException;

/**
 * A helper class for spinal (main presentational sequence) retrievals from Fileset instances.
 * <p>This implementation does not per se support all Fileset types, but is extended on a need basis.</p>
 * @author Markus Gylling
 */
public class FilesetSpineProvider {
	
	/**
	 * Obtain a presentationally ordered collection of AudioFile members of this fileset.
	 * <p>The collection may or may not contain all AudioFile members of the fileset, since some fileset types
	 * have AudioFile members that do not form a natural part of the main presentation. Such satellite AudioFiles 
	 * are not included in the returned collection.</p>
	 * <p>If the fileset does not contain any AudioFile members that are part of the main presentation, an empty list is returned.</p>
	 * @throws FilesetTypeNotSupportedException  if the currently registered Fileset is not explicitly supported by this method.
	 */
	@SuppressWarnings("unchecked")
	public static Collection getAudioSpine(Fileset fileset) throws FilesetTypeNotSupportedException {
		List list = new ArrayList();
		Collection smilSpine = FilesetSpineProvider.getSmilSpine(fileset);
		for (Iterator iter = smilSpine.iterator(); iter.hasNext();) {
			SmilFile smil = (SmilFile) iter.next();
			Collection smilRefs = smil.getReferencedLocalMembers();
			for (Iterator iterator = smilRefs.iterator(); iterator.hasNext();) {
				FilesetFile file = (FilesetFile) iterator.next();
				if(file instanceof AudioFile) list.add(file);
			}
		}
		return list;
	}

	/**
	 * Obtain a Collection of SmilFile representing the presentation spine
	 * @throws FilesetTypeNotSupportedException 
	 */
	public static Collection<? extends FilesetFile> getSmilSpine(Fileset fileset) throws FilesetTypeNotSupportedException{
		if(fileset.getFilesetType() == FilesetType.DAISY_202) {
			D202NccFile ncc = (D202NccFile) fileset.getManifestMember();
			return ncc.getSpineItems();
		}else if (fileset.getFilesetType() == FilesetType.Z3986){
			Z3986OpfFile opf = (Z3986OpfFile) fileset.getManifestMember();
			return opf.getSpineItems();
		}
		throw new FilesetTypeNotSupportedException(fileset.getFilesetType().toNiceNameString() + " not supported");
	}
	
}
