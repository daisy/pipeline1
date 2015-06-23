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
import org.daisy.util.fileset.AudioFile;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;

/**
 * Abstract base for XML-based playlists
 * @author Markus Gylling
 */
public abstract class AbstractXMLWriter extends AbstractWriter {
	protected List<XMLEvent> mXMLEventList = null;
	protected XMLEventFactory mXMLEventFactory = null;
	
	AbstractXMLWriter(FilesetLabelProvider labelProvider, Collection<AudioFile> audioSpine) throws FilesetFatalException {
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
		Map<String,Object> properties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
		
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
