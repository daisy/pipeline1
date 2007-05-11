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
import org.daisy.util.fileset.util.FilesetLabelProvider;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;

/**
 * Abstract base for XML-based playlists
 * @author Markus Gylling
 */
public abstract class AbstractXMLWriter extends AbstractWriter {
	protected List<XMLEvent> mXMLEventList = null;
	protected XMLEventFactory mXMLEventFactory = null;
	
	AbstractXMLWriter(FilesetLabelProvider labelProvider, Collection audioSpine) throws FilesetFatalException {
		super(labelProvider, audioSpine);
		mXMLEventList = new ArrayList<XMLEvent>();
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetDecorator.playlist.AbstractWriter#render(java.io.File)
	 */
	@Override
	public void render(File destination) throws IOException {
		XMLOutputFactory xmlOutputFactory = null;
		Map properties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
		
		try {			
			xmlOutputFactory =  StAXOutputFactoryPool.getInstance().acquire(properties);
			FileOutputStream fos = new FileOutputStream(destination);
			XMLEventWriter writer = xmlOutputFactory.createXMLEventWriter(fos, "utf-8");
			
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
				StAXOutputFactoryPool.getInstance().release(xmlOutputFactory, properties);
			} catch (PoolException e) {
				e.printStackTrace();
			}
		}
	}
}
