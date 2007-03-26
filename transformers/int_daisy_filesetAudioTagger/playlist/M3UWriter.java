package int_daisy_filesetAudioTagger.playlist;

import java.nio.charset.Charset;
import java.util.Collection;

import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.util.FilesetLabelProvider;

/**
 * A writer for M3U playlists
 * <p>For further information, see:</p>
 * <ul>
 * <li>http://www.assistanttools.com/articles/m3u_playlist_format.shtml</li>
 * <li>http://forums.winamp.com/showthread.php?threadid=65772</li>
 * <ul>
 * @author Markus Gylling
 */

public class M3UWriter extends M3U8Writer {
	
	public M3UWriter(FilesetLabelProvider labelProvider, Collection audioSpine) throws FilesetFatalException {
		super(labelProvider,audioSpine);
		mOutputCharset = Charset.defaultCharset();
	}
	
}
