package int_daisy_dtbMigrator.impl.d202_z2005;

import java.net.URI;
import java.util.HashSet;

import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.impl.FilesetFileFactory;
import org.daisy.util.fileset.interfaces.FilesetFile;

/**
 * The set of files that make up the output z2005 DTB.
 * <p>These are exhastively reflected in the output OPF manifest</p>
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
