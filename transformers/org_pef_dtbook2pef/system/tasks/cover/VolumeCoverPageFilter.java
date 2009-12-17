package org_pef_dtbook2pef.system.tasks.cover;

import java.io.OutputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import org_pef_dtbook2pef.system.tasks.layout.flow.Row;

public class VolumeCoverPageFilter extends StaxFilter2 {
	private final static String PEF_NS = "http://www.daisy.org/ns/2008/pef";
	private final QName volume;
	private final QName section;
	private final VolumeCoverPage cover;
	private final int vols;
	private boolean firstSection;
	private int volumeNo;


	public VolumeCoverPageFilter(XMLEventReader xer, OutputStream outStream, VolumeCoverPage cover, int vols)
			throws XMLStreamException {
		super(xer, outStream);
		this.volume = new QName(PEF_NS, "volume");
		this.section = new QName(PEF_NS, "section");
		this.cover = cover;
		this.vols = vols;
		firstSection = true;
		volumeNo = 0;
	}

    protected StartElement startElement(StartElement event) {
		try {
	    	if (event.getName().equals(section) && firstSection) {
				getEventWriter().add(getEventFactory().createStartElement("", PEF_NS, "section"));
				getEventWriter().add(getEventFactory().createStartElement("", PEF_NS, "page"));
				for (Row r : cover.buildPage(volumeNo, vols)) {
					getEventWriter().add(getEventFactory().createStartElement("", PEF_NS, "row"));
					getEventWriter().add(getEventFactory().createCharacters(r.getChars().toString()));
					getEventWriter().add(getEventFactory().createEndElement("", PEF_NS, "row"));
				}
				getEventWriter().add(getEventFactory().createEndElement("", PEF_NS, "page"));
				getEventWriter().add(getEventFactory().createEndElement("", PEF_NS, "section"));
				firstSection = false;
	    	} else if (event.getName().equals(volume)) {
	    		volumeNo++;
	    		firstSection = true;
	    		//rows = Integer.parseInt(event.getAttributeByName(new QName("rows")).getValue());
	    		//cols = Integer.parseInt(event.getAttributeByName(new QName("cols")).getValue());
	    	}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
        return event;
    }

}
