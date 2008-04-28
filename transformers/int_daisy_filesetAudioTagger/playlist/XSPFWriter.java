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

import org.daisy.util.fileset.AudioFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.util.FilesetLabelProvider;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXEventFactoryPool;

/**
 * A writer for XSPF ('spiff') playlists. For further information see:
 * http://xspf.org/
 * 
 * @author Markus Gylling
 */
public class XSPFWriter extends AbstractXMLWriter {
		
	private static final String NS_XSPF = "http://xspf.org/ns/0/";

	public XSPFWriter(FilesetLabelProvider labelProvider,Collection<AudioFile> audioSpine)throws FilesetFatalException {
		super(labelProvider, audioSpine);
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetDecorator.playlist.PlaylistWriter#initialize()
	 */
	public void initialize() throws FilesetFatalException {
				
		try {			
			mXMLEventFactory = StAXEventFactoryPool.getInstance().acquire();						
			mXMLEventList.add(mXMLEventFactory.createStartDocument("utf-8"));
			mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			mXMLEventList.add(mXMLEventFactory.createStartElement("", NS_XSPF, "playlist"));
			mXMLEventList.add(mXMLEventFactory.createAttribute("version", "1"));
			mXMLEventList.add(mXMLEventFactory.createNamespace(NS_XSPF));
			mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			
			mXMLEventList.add(mXMLEventFactory.createStartElement("", NS_XSPF, "trackList"));
			mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			
			int i = 0;
			for (Object object : mAudioSpine) {
					i++;
					AudioFile file = (AudioFile) object;
					
					mXMLEventList.add(mXMLEventFactory.createStartElement("", NS_XSPF, "track"));
					mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
		
					mXMLEventList.add(mXMLEventFactory.createStartElement("", NS_XSPF, "location"));
					mXMLEventList.add(mXMLEventFactory.createCharacters((this.getRelativeURL(file, true)).toString()));		
					mXMLEventList.add(mXMLEventFactory.createEndElement("", NS_XSPF, "location"));
					
					mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
					mXMLEventList.add(mXMLEventFactory.createStartElement("", NS_XSPF, "title"));
					mXMLEventList.add(mXMLEventFactory.createCharacters(mLabelProvider.getFilesetFileTitle(file)));		
					mXMLEventList.add(mXMLEventFactory.createEndElement("", NS_XSPF, "title"));

					mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
					mXMLEventList.add(mXMLEventFactory.createStartElement("", NS_XSPF, "trackNum"));
					mXMLEventList.add(mXMLEventFactory.createCharacters(Integer.toString(i)));
					mXMLEventList.add(mXMLEventFactory.createEndElement("", NS_XSPF, "trackNum"));

					mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
					mXMLEventList.add(mXMLEventFactory.createStartElement("", NS_XSPF, "duration"));
					mXMLEventList.add(mXMLEventFactory.createCharacters(Long.toString(file.getLength().millisecondsValue())));
					mXMLEventList.add(mXMLEventFactory.createEndElement("", NS_XSPF, "duration"));
										
					mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
					mXMLEventList.add(mXMLEventFactory.createEndElement("", NS_XSPF, "track"));
					mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			}		
			
			mXMLEventList.add(mXMLEventFactory.createEndElement("", NS_XSPF, "trackList"));
			mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			
			mXMLEventList.add(mXMLEventFactory.createEndElement("", NS_XSPF, "playlist"));
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
}
