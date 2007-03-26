package int_daisy_filesetAudioTagger.playlist;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collection;

import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.audio.AudioFile;
import org.daisy.util.fileset.util.FilesetLabelProvider;

/**
 * Abstract base for playlist renderers.
 * @author Markus Gylling
 */
public abstract class AbstractWriter implements PlaylistWriter {
	
	protected FilesetLabelProvider mLabelProvider = null;
	protected Collection mAudioSpine = null;
	protected StringBuilder mOutputBuilder = new StringBuilder();
	protected OutputStreamWriter mOutputWriter = null;
	protected Charset mOutputCharset = null;
	protected String mNewLine = System.getProperty("line.separator");
	
	/**
	 * Constructor. Build an in-memory representation of the playlist, throw an exception if fail
	 * @throws FilesetFatalException TODO
	 */
	AbstractWriter (FilesetLabelProvider labelProvider, Collection audioSpine) throws FilesetFatalException {		
		mLabelProvider = labelProvider;
		mAudioSpine = audioSpine;
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetDecorator.playlist.PlaylistWriter#render()
	 */
	public void render(File destination) throws IOException {
		try{ 
			mOutputWriter = new OutputStreamWriter(new FileOutputStream(destination), mOutputCharset);
			if(!mOutputCharset.newEncoder().canEncode(mOutputBuilder)) {				
				System.err.println("playlist.AbstractWriter found unencodable characters when creating " + destination.getName());
			}
			mOutputWriter.append(mOutputBuilder);
			mOutputWriter.flush();
		}finally{
			mOutputWriter.close();
		}
	}
	
	/**
	 * We assume that the destination is going to be the same as the fileset manifest.
	 * Via this, we can determine whether a subdir URL is needed - else the relative URL is just the filename
	 */
	protected CharSequence getRelativeURL(AudioFile file, boolean escape) {
		try {
			if(!mLabelProvider.getFileset().getManifestMember().getFile().getParentFile().getCanonicalPath().equals(file.getFile().getParentFile().getCanonicalPath())) {
				//manifest/playlist and audiofile are not in same dir, a relative URL with path specifier is needed
				FilesetFile manifest = mLabelProvider.getFileset().getManifestMember();
				URI uri = manifest.getRelativeURI(file);
				if(escape) {
					return uri.toASCIIString(); 
				}
				return uri.toString(); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//manifest/playlist and audiofile are in same dir, URL equals filename
		return file.getName();
	}
	
}
