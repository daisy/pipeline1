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
package int_daisy_filesetAudioTagger.playlist;

import java.nio.charset.Charset;
import java.util.Collection;

import org.daisy.util.fileset.AudioFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.util.FilesetLabelProvider;

/**
 * A writer for M3U8 playlists
 * <p>M3U8 is a UTF-8 version of the M3U playlist.</p>
 * <p>For further information, see:</p>
 * <ul>
 * <li>http://www.assistanttools.com/articles/m3u_playlist_format.shtml</li>
 * <li>http://forums.winamp.com/showthread.php?threadid=65772</li>
 * <ul>
 * @author Markus Gylling
 */

public class M3U8Writer extends AbstractWriter {

	
	public M3U8Writer(FilesetLabelProvider labelProvider, Collection<AudioFile> audioSpine) throws FilesetFatalException {
		super(labelProvider, audioSpine);
		mOutputCharset = Charset.forName("utf-8");		
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetDecorator.playlist.PlaylistWriter#initialize()
	 */
	public void initialize() throws FilesetFatalException {
		mOutputBuilder.append("#EXTM3U");
		mOutputBuilder.append(mNewLine);		
		for (Object object : mAudioSpine) {
			try{
				AudioFile file = (AudioFile) object;
				mOutputBuilder.append("#EXTINF:");
				mOutputBuilder.append(file.getLength().secondsValueRounded());
				mOutputBuilder.append(',');
				String label = mLabelProvider.getFilesetFileTitle(file);
				if(label==null) label = "unknown title";				
				mOutputBuilder.append(label);
				mOutputBuilder.append(mNewLine);
				mOutputBuilder.append(getRelativeURL(file,false));
				mOutputBuilder.append(mNewLine);
				
			}catch (Exception e) {
				throw new FilesetFatalException(e.getMessage(),e);
			}
		}	
		
	}
	
}
