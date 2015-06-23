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

import java.net.URI;
import java.util.HashSet;

import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.impl.FilesetFileFactory;

/**
 * The set of files that make up the output z2005 DTB.
 * <p>These are exhaustively reflected in the output OPF manifest</p>
 * @author Markus Gylling
 */
public class ManifestItems extends HashSet<FilesetFile> {
	
	private FilesetFileFactory mFilesetFileFactory = null;
	
	public ManifestItems() {
		mFilesetFileFactory = FilesetFileFactory.newInstance();
	}
	
	public boolean add(URI resource, Class<? extends FilesetFile> type) {
		try {
			this.add(mFilesetFileFactory.newFilesetFile(type.getSimpleName(), resource));
			return true;
		} catch (FilesetFatalException e) {
			return false;
		}
	}
		
	private static final long serialVersionUID = -2353456626719351899L;

}
