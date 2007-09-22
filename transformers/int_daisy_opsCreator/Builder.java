package int_daisy_opsCreator;

import int_daisy_opsCreator.metadata.MetadataList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.fileset.interfaces.Fileset;
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
	
	void render(File destination) throws PoolException, XMLStreamException, FileNotFoundException {
		XMLOutputFactory xof = null;
		try{
			xof = StAXOutputFactoryPool.getInstance().acquire(xofProperties);
			XMLEventWriter xev = xof.createXMLEventWriter(new FileOutputStream(destination));
			for (XMLEvent event : mEventList) {
				
//				PrintWriter dosOut = new PrintWriter(new OutputStreamWriter(System.out));
//				event.writeAsEncodedUnicode(dosOut);
//				System.err.println("\n");
//				dosOut.flush();
							
				xev.add(event);
			}
			xev.flush();
			xev.close();
		}finally{
			StAXOutputFactoryPool.getInstance().release(xof,xofProperties);
		}
	}
	
}
