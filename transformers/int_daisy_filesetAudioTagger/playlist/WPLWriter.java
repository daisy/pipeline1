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

import java.util.Collection;

import javax.xml.namespace.QName;

import org.daisy.util.fileset.AudioFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.util.FilesetLabelProvider;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXEventFactoryPool;

/**
 * A writer for WPL playlists. For further information see:
 * http://support.microsoft.com/kb/316992
 * 
 * @author Markus Gylling
 */
public class WPLWriter extends AbstractXMLWriter {
			
	public WPLWriter(FilesetLabelProvider labelProvider,Collection<AudioFile> audioSpine) throws FilesetFatalException {
		super(labelProvider, audioSpine);
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetDecorator.playlist.PlaylistWriter#initialize()
	 */
	public void initialize() throws FilesetFatalException {
		
		try {			
			final QName qSmil = new QName("smil");
			final QName qHead = new QName("head");
			final QName qBody = new QName("body");
			final QName qSeq = new QName("seq");
			final QName qMedia = new QName("media");
			final QName qSrc = new QName("src");
			final QName qTitle = new QName("title");
			final QName qMeta = new QName("meta");
			final QName qName = new QName("name");
			final QName qContent = new QName("content");
			
			mXMLEventFactory = StAXEventFactoryPool.getInstance().acquire();
						
			mXMLEventList.add(mXMLEventFactory.createStartDocument("utf-8"));
			mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			mXMLEventList.add(mXMLEventFactory.createStartElement(qSmil,null,null));			
			mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			
			mXMLEventList.add(mXMLEventFactory.createStartElement(qHead,null,null));
			mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			
			createMetaElement("Generator", "Daisy Pipeline WPLWriter",qMeta, qName, qContent);
			createMetaElement("AverageRating", "0",qMeta, qName, qContent);
			createMetaElement("TotalDuration", getTotalDuration(),qMeta, qName, qContent);
			createMetaElement("ItemCount", Integer.toString(mAudioSpine.size()),qMeta, qName, qContent);
			
			mXMLEventList.add(mXMLEventFactory.createStartElement(qTitle, null, null));
			mXMLEventList.add(mXMLEventFactory.createCharacters(mLabelProvider.getFilesetTitle()));			
			mXMLEventList.add(mXMLEventFactory.createEndElement(qTitle, null));
			mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			mXMLEventList.add(mXMLEventFactory.createEndElement(qHead, null));
			mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			
			mXMLEventList.add(mXMLEventFactory.createStartElement(qBody,null,null));
			mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));

			mXMLEventList.add(mXMLEventFactory.createStartElement(qSeq,null,null));
			mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			
			int i = 0;
			for (Object object : mAudioSpine) {
					i++;
					AudioFile file = (AudioFile) object;										
					mXMLEventList.add(mXMLEventFactory.createStartElement(qMedia, null,null));
					mXMLEventList.add(mXMLEventFactory.createAttribute(qSrc, (this.getRelativeURL(file, true)).toString()));
					mXMLEventList.add(mXMLEventFactory.createEndElement(qMedia, null));					
					mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));		
			}		
			
			mXMLEventList.add(mXMLEventFactory.createEndElement(qSeq, null));
			mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			
			mXMLEventList.add(mXMLEventFactory.createEndElement(qBody, null));
			mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			
			mXMLEventList.add(mXMLEventFactory.createEndElement(qSmil, null));
			mXMLEventList.add(mXMLEventFactory.createEndDocument());
			
		} catch (Exception e) {
			throw new FilesetFatalException(e.getMessage(), e);
		} finally {
			try {
				StAXEventFactoryPool.getInstance().release(mXMLEventFactory);
			} catch (PoolException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return duration in seconds
	 */
	private String getTotalDuration() {
		long length = 0;
		try{
			for (Object o : mAudioSpine) {
				AudioFile af = (AudioFile) o;
				length += af.getLength().secondsValueRounded();
			}
			return Long.toString(length);
		}catch (Exception e) {
			return "0";
		}
	}

	private void createMetaElement(String name, String content, QName qMeta, QName qName, QName qContent) {
		mXMLEventList.add(mXMLEventFactory.createStartElement(qMeta, null,null));
		mXMLEventList.add(mXMLEventFactory.createAttribute(qName, name));
		mXMLEventList.add(mXMLEventFactory.createAttribute(qContent, content));		
		mXMLEventList.add(mXMLEventFactory.createEndElement(qMeta, null));
		mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
	}

}
