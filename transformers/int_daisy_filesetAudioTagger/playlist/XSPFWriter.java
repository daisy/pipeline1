package int_daisy_filesetAudioTagger.playlist;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.interfaces.audio.AudioFile;
import org.daisy.util.fileset.util.FilesetLabelProvider;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;

/**
 * A writer for XSPF ('spiff') playlists. For further information see:
 * http://xspf.org/
 * 
 * @author Markus Gylling
 */
public class XSPFWriter extends AbstractWriter {
		
	private List<XMLEvent> mXMLEventList = null;

	public XSPFWriter(FilesetLabelProvider labelProvider,Collection audioSpine)throws FilesetFatalException {
		super(labelProvider, audioSpine);
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetDecorator.playlist.PlaylistWriter#initialize()
	 */
	public void initialize() throws FilesetFatalException {
		XMLEventFactory mXMLEventFactory = null;
		
		try {			
			mXMLEventFactory = StAXEventFactoryPool.getInstance().acquire();
			mXMLEventList = new ArrayList<XMLEvent>();
			
			mXMLEventList.add(mXMLEventFactory.createStartDocument("utf-8"));
			mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			mXMLEventList.add(mXMLEventFactory.createStartElement("", "http://xspf.org/ns/0/", "playlist"));
			mXMLEventList.add(mXMLEventFactory.createAttribute("version", "1"));
			mXMLEventList.add(mXMLEventFactory.createNamespace("http://xspf.org/ns/0/"));
			mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			
			mXMLEventList.add(mXMLEventFactory.createStartElement("", "http://xspf.org/ns/0/", "trackList"));
			mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			
			int i = 0;
			for (Object object : mAudioSpine) {
					i++;
					AudioFile file = (AudioFile) object;
					
					mXMLEventList.add(mXMLEventFactory.createStartElement("", "http://xspf.org/ns/0/", "track"));
					mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
		
					mXMLEventList.add(mXMLEventFactory.createStartElement("", "http://xspf.org/ns/0/", "location"));
					mXMLEventList.add(mXMLEventFactory.createCharacters((this.getRelativeURL(file, true)).toString()));		
					mXMLEventList.add(mXMLEventFactory.createEndElement("", "http://xspf.org/ns/0/", "location"));
					
					mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
					mXMLEventList.add(mXMLEventFactory.createStartElement("", "http://xspf.org/ns/0/", "title"));
					mXMLEventList.add(mXMLEventFactory.createCharacters(mLabelProvider.getFilesetFileTitle(file)));		
					mXMLEventList.add(mXMLEventFactory.createEndElement("", "http://xspf.org/ns/0/", "title"));

					mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
					mXMLEventList.add(mXMLEventFactory.createStartElement("", "http://xspf.org/ns/0/", "trackNum"));
					mXMLEventList.add(mXMLEventFactory.createCharacters(Integer.toString(i)));
					mXMLEventList.add(mXMLEventFactory.createEndElement("", "http://xspf.org/ns/0/", "trackNum"));

					mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
					mXMLEventList.add(mXMLEventFactory.createStartElement("", "http://xspf.org/ns/0/", "duration"));
					mXMLEventList.add(mXMLEventFactory.createCharacters(Long.toString(file.getLength().millisecondsValue())));
					mXMLEventList.add(mXMLEventFactory.createEndElement("", "http://xspf.org/ns/0/", "duration"));
										
					mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
					mXMLEventList.add(mXMLEventFactory.createEndElement("", "http://xspf.org/ns/0/", "track"));
					mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			}		
			
			mXMLEventList.add(mXMLEventFactory.createEndElement("", "http://xspf.org/ns/0/", "trackList"));
			mXMLEventList.add(mXMLEventFactory.createCharacters(mNewLine));
			
			mXMLEventList.add(mXMLEventFactory.createEndElement("", "http://xspf.org/ns/0/", "playlist"));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see int_daisy_filesetDecorator.playlist.AbstractWriter#render(java.io.File)
	 */
	public void render(File destination) throws IOException {
		XMLOutputFactory mXMLOutputFactory = null;
		Map properties = null;

		
		try {			
			mXMLOutputFactory =  StAXOutputFactoryPool.getInstance().acquire(properties);
			FileOutputStream fos = new FileOutputStream(destination);
			XMLEventWriter writer = mXMLOutputFactory.createXMLEventWriter(fos, "utf-8");
			
			for (XMLEvent xev : mXMLEventList) {
				writer.add(xev);
			}
			writer.flush();
			writer.close();
			fos.close();
			
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		} finally {
			try {
				StAXOutputFactoryPool.getInstance().release(mXMLOutputFactory, properties);
			} catch (PoolException e) {
				e.printStackTrace();
			}
		}
	}

}
