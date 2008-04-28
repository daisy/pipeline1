package se_tpb_aligner.subtree;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;

import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;

import se_tpb_aligner.util.XMLResult;
import se_tpb_aligner.util.XMLSource;

/**
 * A tree within the entire tree of an XML document.
 * @author Markus Gylling
 */
public class SubTree {
	private List<XMLEvent> mContent = null;
	
	/**
	 * Constructor
	 * @param content a List of XMLEvent which adhers to XML wellformedness constraints.
	 */
	public SubTree(List<XMLEvent> content) {
		mContent = new LinkedList<XMLEvent>(content);		
	}
	
	/**
	 * Constructor.
	 * @param content a List of XMLEvent which in itself may or may not adher 
	 * 		to XML wellformedness contraints.
	 * @param wf An instance of WellFormer to use to wellform the content
	 */
	public SubTree(List<XMLEvent> content, WellFormer wf) {		
		content = wf.makeWellFormed(content);		
		mContent = new LinkedList<XMLEvent>(content);		
	}

	/**
	 * Constructor.
	 * @param content a reference to a subtree stored as a file.
	 * @throws XMLStreamException 
	 */
	public SubTree(XMLSource content) throws XMLStreamException {		
		//read the input file, scrap any pre-root stuff and the enddocument event.
		Map<String,Object> properties = null;
		XMLInputFactory xif = null;
		mContent = new LinkedList<XMLEvent>();
		try{				
			properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xif = StAXInputFactoryPool.getInstance().acquire(properties);
			StreamSource ss = new StreamSource(content);
			XMLEventReader reader = xif.createXMLEventReader(ss);
			boolean rootElementSeen = false;
			while(reader.hasNext()) {
				XMLEvent xe = reader.nextEvent();				
				if(!rootElementSeen && xe.isStartElement()) {
					rootElementSeen = true;
				}
				if(rootElementSeen && !xe.isEndDocument())mContent.add(xe);
			}
									
		}finally{
			StAXInputFactoryPool.getInstance().release(xif, properties);
		}		
	}
	
	/**
	 * Get the content of this subtree as a list of XMLEvent.
	 */
	public List<XMLEvent> getContent() {
		return mContent;
	}
	
//	/**
//	 * Get the language code of the content in this subtree.
//	 */
//	public String getLanguage() {
//		return null; //TODO
//	}
	
	/**
	 * Render this subtree into a file. The rendered document may be namespace compound, 
	 * and may contain events in the namespace "http://www.tpb.se/ns/ignore#".
	 * @throws XMLStreamException 
	 * @throws IOException 
	 */
	public void render(XMLResult destination) throws XMLStreamException, IOException {
		Map<String,Object> properties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
		XMLOutputFactory xof = null;
		XMLEventFactory xef = StAXEventFactoryPool.getInstance().acquire();
		XMLEvent x2 = null;
		FileOutputStream fos = null;
		int i = 0;
		try{
			fos = new FileOutputStream(destination);
			xof = StAXOutputFactoryPool.getInstance().acquire(properties);
			XMLEventWriter wrt = xof.createXMLEventWriter(fos,"utf-8");			
			wrt.add(xef.createStartDocument("utf-8","1.0"));
			boolean firstElement = true;
			for(XMLEvent x : mContent){
				i++;
				x2=x;
				wrt.add(x);				
				if(firstElement) {
					wrt.add(xef.createNamespace(SubTreeHandler.IGNORE_NSPREFIX, SubTreeHandler.IGNORE_NSURI));
					firstElement = false;
				}
			}
			wrt.add(xef.createEndDocument());
			wrt.flush();
			wrt.close();
		}catch (XMLStreamException e) {
			//debug stuff
			System.out.println(i);
			PrintWriter wrt = new PrintWriter(System.out,true);
			x2.writeAsEncodedUnicode(wrt);
			wrt.flush();
			throw e;
		}finally{
			StAXOutputFactoryPool.getInstance().release(xof,properties);
			StAXEventFactoryPool.getInstance().release(xef);
			fos.close();
		}
	}	
	
	/**
	 * Print this SubTree as (well or mal) formed XML to a stream.
	 * @throws XMLStreamException 
	 */
	public void print(PrintStream to) throws XMLStreamException {
		PrintWriter wrt = new PrintWriter(to,true);
		for(XMLEvent x : mContent){			
			x.writeAsEncodedUnicode(wrt);
			wrt.flush();
		}		
	}
}
