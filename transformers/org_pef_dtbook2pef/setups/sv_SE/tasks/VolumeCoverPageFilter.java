package org_pef_dtbook2pef.setups.sv_SE.tasks;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import org_pef_dtbook2pef.system.tasks.layout.impl.BreakPointHandler;
import org_pef_dtbook2pef.system.tasks.layout.impl.BreakPointHandler.BreakPoint;
import org_pef_dtbook2pef.system.tasks.layout.page.Row;
import org_pef_dtbook2pef.system.tasks.textnode.filters.StringFilterHandler;

public class VolumeCoverPageFilter extends StaxFilter2 {
	private final static String PEF_NS = "http://www.daisy.org/ns/2008/pef";
	private final QName volume;
	private final QName section;
	private boolean firstSection;
	private int volumeNo;
	private StringFilterHandler filters;
	private String title;
	private ArrayList<String> creator;
	private int rows;
	private int cols;
	private int width;
	private int height;

	public VolumeCoverPageFilter(XMLEventReader xer,
			OutputStream outStream,
			StringFilterHandler filters,
			String title,
			ArrayList<String> creator,
			int width,
			int height)
			throws XMLStreamException {
		super(xer, outStream);
		this.filters = filters;
		volume = new QName(PEF_NS, "volume");
		section = new QName(PEF_NS, "section");
		firstSection = true;
		volumeNo = 0;
		rows = 0;
		cols = 0;
		this.height = height;
		this.width = width;
		this.title = title;
		this.creator = creator;
	}
	
    protected StartElement startElement(StartElement event) {
		try {
	    	if (event.getName().equals(section) && firstSection) {
				getEventWriter().add(getEventFactory().createStartElement("", PEF_NS, "section"));
				getEventWriter().add(getEventFactory().createStartElement("", PEF_NS, "page"));
				for (Row r : buildTitlePage()) {
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
	    		rows = Integer.parseInt(event.getAttributeByName(new QName("rows")).getValue());
	    		cols = Integer.parseInt(event.getAttributeByName(new QName("cols")).getValue());
	    		System.out.println("Rows: " + rows + " Cols: " + cols);
	    	}
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return event;
    }
    
    private ArrayList<Row> buildTitlePage() {
    	ArrayList<Row> ret = new ArrayList<Row>();
    	BreakPointHandler bph;
    	BreakPoint bp;
    	bph = new BreakPointHandler(filters.filter(title));
    	while (bph.hasNext()) {
    		bp = bph.nextRow(20);
    		ret.add(new Row(bp.getHead()));
    	}
    	for (String s : creator) {
        	bph = new BreakPointHandler(filters.filter(s));
        	while (bph.hasNext()) {
        		bp = bph.nextRow(width);
        		ret.add(new Row(bp.getHead()));
        	}
    	}
    	ret.add(new Row(filters.filter("Volym "+volumeNo)));
    	if (ret.size()>height) {
    		throw new RuntimeException("Unable to perform layout. Title page contains too many rows.");
    	}
    	return ret;
    }
}
