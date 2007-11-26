package org.daisy.pipeline.gui.update;

import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;

import org.daisy.pipeline.gui.util.Filter;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Filters out some files from a ZIP update patch. This is used to ignore the
 * patch and OS-specific metadata files in the update operation.
 * 
 * @author Romain Deltour
 * 
 */
public class ZipUpdateFilter implements Filter<ZipEntry> {

	/** The set of ignored file and directory names */
	public final static Set<String> ignoredNames;;
	static {
		ignoredNames = new HashSet<String>();
		ignoredNames.add("update.properties");//$NON-NLS-1$
		ignoredNames.add("__MACOSX");//$NON-NLS-1$
		ignoredNames.add(".DS_Store");//$NON-NLS-1$
	}

	public boolean accept(ZipEntry entry) {
		if (entry == null) {
			return false;
		}
		IPath pathname = new Path(entry.getName());
		String simplename = pathname.lastSegment();
		return !ignoredNames.contains(simplename);
	}

}
