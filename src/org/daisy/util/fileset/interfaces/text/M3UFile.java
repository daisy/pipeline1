package org.daisy.util.fileset.interfaces.text;

import org.daisy.util.mime.MIMEConstants;

/**
 * interface for the m3u playlist format
 * @author Markus Gylling
 */

public interface M3UFile extends PlayList {
	static String mimeStringConstant = MIMEConstants.MIME_AUDIO_X_MPEGURL;
	
}
