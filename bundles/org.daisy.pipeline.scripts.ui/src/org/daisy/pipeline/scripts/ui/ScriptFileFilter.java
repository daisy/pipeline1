/*
 * DAISY Pipeline GUI Copyright (C) 2006 Daisy Consortium
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
package org.daisy.pipeline.scripts.ui;

import java.io.File;

import org.daisy.util.file.Directory;
import org.daisy.util.file.EFile;
import org.daisy.util.file.EFileFilter;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;

/**
 * A file filter that selects only Pipeline scripts.
 * 
 * @author Romain Deltour
 * 
 */
public class ScriptFileFilter extends EFileFilter {
	private boolean acceptDir;

	/**
	 * Creates an instance on this filter that accept directories if and only if
	 * <code>acceptDir</code> is <code>true</code>.
	 * 
	 * @param acceptDir
	 */
	public ScriptFileFilter(boolean acceptDir) {
		super();
		this.acceptDir = acceptDir;
	}

	@Override
	protected boolean acceptEFile(EFile file) {
		if (!super.acceptEFile(file)) {
			return false;
		}
		Peeker peeker = null;
		try {
			peeker = PeekerPool.getInstance().acquire();
			PeekResult result = peeker.peek(file);
			return "taskScript".equals(result.getRootElementLocalName()); //$NON-NLS-1$
		} catch (Exception e) {
			//TODO log("Couldn't peek in file " + file.getAbsolutePath(), e); //$NON-NLS-1$
			return false;
		}
	}

	@Override
	protected boolean acceptDir(Directory dir) {
		if (!acceptDir || !super.acceptDir(dir)) {
			return false;
		}
		return containsScript(dir);
	}

	private boolean containsScript(File dir) {
		for (File file : dir.listFiles(new ScriptFileFilter(true))) {
			if (file.isFile()) {
				return true;
			} else {
				return containsScript(file);
			}
		}
		return false;
	}

}
