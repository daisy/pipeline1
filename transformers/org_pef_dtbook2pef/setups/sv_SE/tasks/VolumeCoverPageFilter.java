package org_pef_dtbook2pef.setups.sv_SE.tasks;

import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import org_pef_dtbook2pef.system.tasks.layout.impl.Row;
import org_pef_dtbook2pef.system.tasks.layout.text.CombinationFilter;
import org_pef_dtbook2pef.system.tasks.layout.text.StringFilter;
import org_pef_dtbook2pef.system.tasks.layout.utils.TextBorder;

public class VolumeCoverPageFilter extends StaxFilter2 {
	private final static String PEF_NS = "http://www.daisy.org/ns/2008/pef";

	private final QName volume;
	private final QName section;
	private boolean firstSection;
	private int volumeNo;
	private StringFilter filters;
	private String title;
	private ArrayList<String> creator;
	private int rows;
	private int cols;
	private TextBorder tb;
	private int height;

	public VolumeCoverPageFilter(XMLEventReader xer,
			OutputStream outStream,
			StringFilter filters,
			String title,
			ArrayList<String> creator,
			TextBorder tb,
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
		this.tb = tb;
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
	    	}
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return event;
    }
    
    private ArrayList<Row> buildTitlePage() {

    	ArrayList<Row> ret = new ArrayList<Row>();
    	ret.add(new Row(tb.getTopBorder()));
    	for (int i=0; i<3; i++) {
    		ret.add(new Row(tb.addBorderToRow("")));
    	}
    	if (title!=null && title.length()>0) {
	    	for (String s : tb.addBorderToParagraph(filters.filter(title))) {
	    		ret.add(new Row(s));
	    	}
	    	ret.add(new Row(tb.addBorderToRow("")));
    	}
    	for (String c : creator) {
    		if (c!=null && c.length()>0) {
	    		for (String s : tb.addBorderToParagraph(filters.filter(c))) {
	    			ret.add(new Row(s));
	    		}
    		}
    	}
    	ret.add(new Row(tb.addBorderToRow("")));
    	ret.add(new Row(tb.addBorderToRow(filters.filter("Volym "+volumeNo))));
    	while (ret.size()<height-1) {
    		ret.add(new Row(tb.addBorderToRow("")));
    	}
    	ret.add(new Row(tb.getBottomBorder()));
    	if (ret.size()>height) {
    		throw new RuntimeException("Unable to perform layout. Title page contains too many rows.");
    	}
    	return ret;
    }

}
