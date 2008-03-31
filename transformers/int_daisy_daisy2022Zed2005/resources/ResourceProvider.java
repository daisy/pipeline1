package int_daisy_daisy2022Zed2005.resources;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.daisy.util.file.FileUtils;
import org.daisy.util.file.TempFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.impl.FilesetImpl;

/**
 * Getter for transformer-local resources.
 * @author Markus Gylling
 */
public class ResourceProvider {
	private FilesetErrorHandler mErrorHandler = null;
	private Set<File> mCreatedTempFiles = new HashSet<File>();
	
	public ResourceProvider(FilesetErrorHandler errH) {
		mErrorHandler = errH;
		mCreatedTempFiles = new HashSet<File>();
	}
	
	/**
	 * Retrieve a Fileset handle to default CSS or ResourceFile. 
	 * @throws IOException 
	 * @throws FilesetFatalException 
	 */
	public Fileset getDefaultAuxilliaryFileset(FilesetType type) throws IOException, FilesetFatalException {
		/*
		 * Hardcoded knowledge on names of things.
		 * Since possibly jarred, pipe to temp and return.
		 */
		File tempManifest = TempFile.create();	
		mCreatedTempFiles.add(tempManifest);
		URL manifestSourceURL = null;
		String resourceName = null;
		if(type.equals(FilesetType.CSS)) {
			resourceName = "dtbook.2005.basic.css";
			manifestSourceURL = this.getClass().getResource(resourceName);			 
		}else if(type.equals(FilesetType.Z3986_RESOURCEFILE)) {
			resourceName = "pipeline.res";
			manifestSourceURL = this.getClass().getResource(resourceName);			
		}

		FileUtils.writeInputStreamToFile(manifestSourceURL.openStream(), tempManifest);
		File realFile = new File(tempManifest.getParentFile(),resourceName);
		realFile.deleteOnExit();
		FileUtils.copy(tempManifest, realFile);
		mCreatedTempFiles.add(realFile);
		return new FilesetImpl(realFile.toURI(),mErrorHandler,false,false);
	}
	
	/**
	 * Explicitly delete temporary files
	 */
	public void clean() {
		for(File file : mCreatedTempFiles) {
			try{
				file.delete();
			}catch (Exception e) {
				//yowza
			}
		}
	}
}
