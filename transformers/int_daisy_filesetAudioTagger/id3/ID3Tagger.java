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
package int_daisy_filesetAudioTagger.id3;

import java.io.File;

import org.daisy.util.file.FileUtils;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.manipulation.FilesetFileManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulationException;
import org.jaudiotagger.tag.id3.ID3v23Frame;
import org.jaudiotagger.tag.id3.ID3v23Frames;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTALB;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTIT2;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTPE1;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTRCK;
import org.jaudiotagger.tag.id3.valuepair.TextEncoding;

/**
 * An implementation of FilesetFileManipulator that performs ID3 tagging on MP3 members of Filesets.
 * @author Markus Gylling
 */
public class ID3Tagger implements FilesetFileManipulator {

	private String mTitleFrameText;
	private String mArtistFrameText;
	private String mAlbumFrameText;
	private String mTrackNumberFrameText;

	public ID3Tagger(String titleFrame, String artistFrame, String albumFrame, String trackNumberFrame) {
		mTitleFrameText = titleFrame;
		mArtistFrameText = artistFrame;
		mAlbumFrameText = albumFrame;
		mTrackNumberFrameText = trackNumberFrame;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.manipulation.FilesetFileManipulator#manipulate(org.daisy.util.fileset.interfaces.FilesetFile, java.io.File, boolean)
	 */
	public File manipulate(FilesetFile inFile, File destination, @SuppressWarnings("unused")
			boolean allowDestinationOverwrite) throws FilesetManipulationException {
		try {
			org.jaudiotagger.audio.mp3.MP3File taggedFile = new org.jaudiotagger.audio.mp3.MP3File((File)inFile);
			ID3v23Tag tag = new ID3v23Tag();
			ID3v23Frame frame = null;
			
			if(mTitleFrameText!=null){
				//FrameBodyTIT2
				//Title/Songname/Content description Text information frame.
				//The 'Title/Songname/Content description' frame is the actual name of the piece (e.g. "Adagio", "Hurricane Donna"). 
				frame = new ID3v23Frame(ID3v23Frames.FRAME_ID_V3_TITLE);						
				FrameBodyTIT2 fbtit2 = new FrameBodyTIT2();
				fbtit2.setTextEncoding(TextEncoding.UTF_16);
				fbtit2.setText(mTitleFrameText);
				frame.setBody(fbtit2);
				tag.setFrame(frame);
			}
			
			if(mArtistFrameText!=null){
				//FrameBodyTPE1
				//Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group Text information frame.
				//The 'Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group' is used for the main artist(s). They are seperated with the "/" character.			
				frame = new ID3v23Frame(ID3v23Frames.FRAME_ID_V3_ARTIST);			
				FrameBodyTPE1 fbtpe1 = new FrameBodyTPE1();			
				fbtpe1.setTextEncoding(TextEncoding.UTF_16);
				fbtpe1.setText(mArtistFrameText);			
				frame.setBody(fbtpe1);
				tag.setFrame(frame);
			}
			
			if(mAlbumFrameText!=null){
				//FrameBodyTALB
				//Album/Movie/Show title Text information frame.
				//The 'Album/Movie/Show title' frame is intended for the title of the recording(/source of sound) which the audio in the file is taken from. 
				frame = new ID3v23Frame(ID3v23Frames.FRAME_ID_V3_ALBUM);
				FrameBodyTALB fbtalb = new FrameBodyTALB(); 
				fbtalb.setTextEncoding(TextEncoding.UTF_16);
				fbtalb.setText(mAlbumFrameText);			
				frame.setBody(fbtalb);
				tag.setFrame(frame);
			}
			
			if(mTrackNumberFrameText!=null){
				//FrameBodyTRCK
				//Track number/Position in set Text information frame.
				//The 'Track number/Position in set' frame is a numeric string containing the order 
				//number of the audio-file on its original recording. This may be extended with a "/" 
				//character and a numeric string containing the total numer of tracks/elements on the original recording. E.g. "4/9".
				frame = new ID3v23Frame(ID3v23Frames.FRAME_ID_V3_TRACK);
				FrameBodyTRCK fbtrck = new FrameBodyTRCK();
				fbtrck.setTextEncoding(TextEncoding.UTF_16);
				fbtrck.setText(mTrackNumberFrameText);
				frame.setBody(fbtrck);
				tag.setFrame(frame);
			}
			
			FileUtils.copy((File)inFile, destination);
			if(tag.getFrameCount()>0) {
				taggedFile.setID3v2Tag(tag);
				taggedFile.save(destination);
			}
			
			return destination;
		} catch (Exception e) {
			throw new FilesetManipulationException(e.getMessage(),e);
		} 
				
	}

}
