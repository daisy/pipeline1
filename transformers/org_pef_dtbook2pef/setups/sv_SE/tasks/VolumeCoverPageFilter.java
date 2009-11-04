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
    	String voltext;
    	if (vols==1) {
    		voltext = "En volym";
    	} else {
    		voltext = "Volym "+intToText(volumeNo)+" av "+intToText(vols);
    	}
    	ArrayList<String> vol = tb.addBorderToParagraph(filters.filter(voltext));
    	while (ret.size()<height-vol.size()-1) {
    		ret.add(new Row(tb.addBorderToRow("")));
    	}
    	for (String s : vol) {
    		ret.add(new Row(s));
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
    		case 13: return "tretton";
    		case 14: return "fjorton";
    		case 15: return "femton";
    		case 16: return "sexton";
    		case 17: return "sjutton";
    		case 18: return "arton";
    		case 19: return "nitton";
    		case 20: return "tjugo"; 
    	}
    	return ""+value;
    }

    private static String intToText(int value) {
    	if (value<0) return "minus " + intToText(-value);
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
			case 13: return "tretton";
			case 14: return "fjorton";
			case 15: return "femton";
			case 16: return "sexton";
			case 17: return "sjutton";
			case 18: return "arton";
			case 19: return "nitton";
    		case 20: return "tjugo";
    		case 30: return "trettio";
    		case 40: return "fyrtio";
    		case 50: return "femtio";
    		case 60: return "sextio";
    		case 70: return "sjuttio";
    		case 80: return "åttio";
    		case 90: return "nittio";
    	}
    	String pre = "";
    	if (value>=1000) {
    		pre = intToText(value / 1000) + "tusen";
    		value = value % 1000;
    	}
    	if (value>=100) {
    		pre = pre + (value>=200?intToText(value / 100):"") + "hundra";
    		value = value % 100;
    	}
    	if (value==0) return pre;
    	if (value<20) {
    		return pre + intToText(value);
    	} else {
        	int t = value % 10;
        	int r = (value / 10) * 10;
    		return pre + intToText(r) + (t>0?intToText(t):"");
    	}
    }
    
    public static void main (String[] args) {
    	for (int i = 0; i<100; i++) {
    		System.out.println(i + " " + intToText(i));
    	}
    }
}
