package org.daisy.util.fileset.interfaces.text;

import java.io.File;

import org.daisy.util.fileset.interfaces.ManifestFile;
import org.daisy.util.mime.MIMEConstants;

/**
 * interface for the pls playlist format
 * @author Markus Gylling
 */

public interface PlsFile extends ManifestFile {
	static String mimeStringConstant = MIMEConstants.MIME_AUDIO_X_SCPLS;
	public int getStatedNumberOfEntries();
	public int getStatedVersion();
	public String getHeadingForFile(File mp3File);
}
