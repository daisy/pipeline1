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
package int_daisy_opsCreator;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


import org.daisy.util.dtb.meta.MetadataList;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;

/**
 * Abstract base for NcxBuilder and OpfBuilder.
 * @author Markus Gylling
 */
public abstract class Builder {
	protected List<XMLEvent> mEventList = null;
	protected List<Fileset> mInputFilesets = null;
	
	protected MetadataList mMetaData = null;
	private static Map<String,Object> xofProperties = null;
	
	Builder(List<Fileset> inputFilesets, MetadataList metadata) {
		mEventList = new LinkedList<XMLEvent>();
		mInputFilesets = inputFilesets;
		mMetaData  = metadata;
		
		if(xofProperties==null){
			xofProperties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();	
		}		
	}
	
	abstract void build() throws PoolException, FileNotFoundException, XMLStreamException;
	
	void render(File destination) throws PoolException, XMLStreamException, IOException {
		XMLOutputFactory xof = null;
		try{
			xof = StAXOutputFactoryPool.getInstance().acquire(xofProperties);
			FileOutputStream fos = new FileOutputStream(destination);
			XMLEventWriter xev = xof.createXMLEventWriter(fos);
			for (XMLEvent event : mEventList) {
				xev.add(event);
			}			
			xev.flush();
			xev.close();
			fos.close();
		}finally{
			StAXOutputFactoryPool.getInstance().release(xof,xofProperties);
		}
	}
	
}
