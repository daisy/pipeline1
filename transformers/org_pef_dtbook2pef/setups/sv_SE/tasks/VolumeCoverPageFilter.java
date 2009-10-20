package org_pef_dtbook2pef.setups.sv_SE.tasks;

import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import org_pef_dtbook2pef.system.tasks.layout.impl.Row;
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
	private int vols;

	public VolumeCoverPageFilter(XMLEventReader xer,
			OutputStream outStream,
			StringFilter filters,
			String title,
			ArrayList<String> creator,
			TextBorder tb,
			int height, int vols)
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
		this.vols = vols;
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
    	boolean hasCreator = false;
    	for (String c : creator) {
    		if (c!=null && c.length()>0) {
	    		for (String s : tb.addBorderToParagraph(filters.filter(c))) {
	    			ret.add(new Row(s));
	    		}
	    		hasCreator = true;
    		}
    	}
    	if (title!=null && title.length()>0) {
    		if (hasCreator) {
    			ret.add(new Row(tb.addBorderToRow("")));
    		}
	    	for (String s : tb.addBorderToParagraph(filters.filter(title))) {
	    		ret.add(new Row(s));
	    	}
    	}
    	while (ret.size()<height-2) {
    		ret.add(new Row(tb.addBorderToRow("")));
    	}
    	if (vols==1) {
    		ret.add(new Row(tb.addBorderToRow(filters.filter("En volym"))));
    	} else {
    		ret.add(new Row(tb.addBorderToRow(filters.filter("Volym "+loc(volumeNo)+" av "+loc(vols)))));
    	}
    	ret.add(new Row(tb.getBottomBorder()));
    	if (ret.size()>height) {
    		throw new RuntimeException("Unable to perform layout. Title page contains too many rows.");
    	}
    	return ret;
    }
    
    private String loc(int value) {
    	switch (value) {
    		case 0: return "noll"; 
    		case 1: return "ett";
    		case 2: return "två";
    		case 3: return "tre";
    		case 4: return "fyra";
    		case 5: return "fem";
    		case 6: return "sex";
    		case 7: return "sju";
    		case 8: return "åtta";
    		case 9: return "nio";
    		case 10: return "tio";
    		case 11: return "elva";
    		case 12: return "tolv";
    	}
    	return ""+value;
    }

}
