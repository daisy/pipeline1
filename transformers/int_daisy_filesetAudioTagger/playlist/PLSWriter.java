package int_daisy_filesetAudioTagger.playlist;

import java.nio.charset.Charset;
import java.util.Collection;

import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.interfaces.audio.AudioFile;
import org.daisy.util.fileset.util.FilesetLabelProvider;

/**
 * A writer for PLS playlists
 * <p>For further information, see:</p>
 * <ul>
 * <li>http://www.assistanttools.com/articles/pls_playlist_format.shtml</li>
 * <li>http://forums.winamp.com/showthread.php?threadid=65772</li>
 * <ul>
 * @author Markus Gylling
 */
public class PLSWriter extends AbstractWriter implements PlaylistWriter {

	public PLSWriter(FilesetLabelProvider labelProvider, Collection audioSpine) throws FilesetFatalException {
		super(labelProvider, audioSpine);		
		mOutputCharset = Charset.defaultCharset();		
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetDecorator.playlist.PlaylistWriter#initialize()
	 */
	public void initialize() throws FilesetFatalException {				
		//build the output as a string
		mOutputBuilder.append("[playlist]");
		mOutputBuilder.append(mNewLine);
		mOutputBuilder.append("NumberOfEntries="+Integer.toString(mAudioSpine.size()));
		mOutputBuilder.append(mNewLine);
		
		int i = 0;
		for (Object object : mAudioSpine) {
			try{
				i++;
				AudioFile file = (AudioFile) object;
				mOutputBuilder.append("File");
				mOutputBuilder.append(Integer.toString(i));
				mOutputBuilder.append('=');
				mOutputBuilder.append(getRelativeURL(file,false));
				mOutputBuilder.append(mNewLine);
				
				mOutputBuilder.append("Title");
				mOutputBuilder.append(Integer.toString(i));
				mOutputBuilder.append('=');						
				String label = mLabelProvider.getFilesetFileTitle(file);
				if(label==null) label = "unknown title";				
				mOutputBuilder.append(label);
				mOutputBuilder.append(mNewLine);
				
				mOutputBuilder.append("Length");
				mOutputBuilder.append(Integer.toString(i));
				mOutputBuilder.append('=');
				mOutputBuilder.append(file.getLength().secondsValueRounded());			
				mOutputBuilder.append(mNewLine);
				
			}catch (Exception e) {
				throw new FilesetFatalException(e.getMessage(),e);
			}
		}
		

		mOutputBuilder.append("Version=2");
	}	
}