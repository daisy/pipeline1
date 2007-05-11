package int_daisy_opsCreator.metadata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventConsumer;

import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;

/**
 * Holds an ordered list of abstract XML metadata items.
 * @author Markus Gylling
 */
public class MetadataList extends LinkedList<MetadataItem> {
			
	/**
	 * Constructor.
	 */
	public MetadataList() {
		super();	
	}
	
	/**
	 * Add a metadata item to this list.
	 */
	public boolean add(QName name, String value) {		
		return this.add(new MetadataItem(name,value));
		
	}

	/**
	 * Add a metadata item to this list.
	 */
	public boolean add(String name, String value) {				
		return this.add(new MetadataItem(new QName(name),value));
	}
	
	/**
	 * Get the first metadata item in the list that matches on inparam QName.
	 * If not match, return null.
	 */
	public MetadataItem get(QName name) {
		for(MetadataItem item : this) {
			if(item.getQName().equals(name)) {
				return item;
			}
		}
		return null;
	}
	
	public List<XMLEvent> asXMLEvents() {
		List<XMLEvent> list = new LinkedList<XMLEvent>();
		for(MetadataItem item : this) {
			list.addAll(item.asXMLEvents());
		}		
		return list;
	}
			
	public void asXMLEvents(XMLEventConsumer consumer) throws XMLStreamException {								
		for (MetadataItem m : this) {
			m.asXMLEvents(consumer);			
		}				
	}
	
	/**
	 * Serialize this list as an XML document.
	 */
	public void serialize(File destination) throws XMLStreamException, IOException {
		Map xofProperties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
		XMLOutputFactory xof = null;
		XMLEventFactory xef = null;
		try{
			xof = StAXOutputFactoryPool.getInstance().acquire(xofProperties);
			xef = StAXEventFactoryPool.getInstance().acquire();
			QName root = new QName("MetadataList");
			XMLEventWriter xew = xof.createXMLEventWriter(new FileWriter(destination));
			xew.add(xef.createStartDocument("utf-8"));
			xew.add(xef.createStartElement(root,null,null));			
			this.asXMLEvents(xew);
			xew.add(xef.createEndElement(root,null));			
			xew.add(xef.createEndDocument());
		}finally{
			StAXOutputFactoryPool.getInstance().release(xof, xofProperties);
			StAXEventFactoryPool.getInstance().release(xef);
		}
	}

	/**
	 * Create an instance of this class from an XML serialization.
	 */

	public static MetadataList deserialize(URL source) throws XMLStreamException, IOException {
		Map xifProperties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(Boolean.FALSE);
		XMLInputFactory xif = null;
		XMLEventFactory xef = null;
		MetadataList ret = new MetadataList();
		try{
			xif = StAXInputFactoryPool.getInstance().acquire(xifProperties);
			xef = StAXEventFactoryPool.getInstance().acquire();
			XMLEventReader xer = xif.createXMLEventReader(source.openStream());
			
			boolean isRootElement = true;
			boolean isMetaElementOpen = false;
			QName rootElem = null;
			MetadataItem m = null;
			
			while(xer.hasNext()) {
				XMLEvent event = xer.nextEvent();
				if(event.isStartElement()) {
					StartElement se = event.asStartElement();
					if(!isRootElement) {
						isMetaElementOpen = true;
						m = new MetadataItem(se.getName());
						for (Iterator iter = se.getAttributes(); iter.hasNext();) {
							m.addAttribute((Attribute) iter.next());							
						}
					}else {
						rootElem = se.getName();
					}
					isRootElement = false;
				}else if(event.isCharacters()) {
					if(m!=null && isMetaElementOpen) m.setValue(event.asCharacters().getData());					
				}else if(event.isEndElement()) {
					isMetaElementOpen = false;
					if(!event.asEndElement().getName().equals(rootElem)) {
						if(m!=null)ret.add(m);	
					}					
				}
			}					
		}finally{
			StAXInputFactoryPool.getInstance().release(xif, xifProperties);
			StAXEventFactoryPool.getInstance().release(xef);
		}
		return ret;
	}
	
	private static final long serialVersionUID = 5154760082579948477L;
}
