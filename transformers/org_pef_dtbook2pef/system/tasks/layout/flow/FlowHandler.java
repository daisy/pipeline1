package org_pef_dtbook2pef.system.tasks.layout.flow;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * FlowHandler reads a nested "flow" file containing blocks or text within
 * other blocks to an arbitrary depth. The input semantics resembles that of xsl-fo, 
 * but is greatly simplified. The FlowHandler reads the input flow and 
 * breaks it down into rows using the Flow interface.
 * @author Joel Håkansson, TPB
 *
 */
public class FlowHandler extends DefaultHandler {
	private Flow flow;
	private StringBuffer sb;
	
	/**
	 * Create a new FlowReader
	 * @param flow
	 */
	public FlowHandler(Flow flow) {
		this.flow = flow;
		sb = new StringBuffer();
	}
	
	public void startElement (String uri, String localName, String qName, Attributes atts) throws SAXException {
		flushChars();
		if (localName.equals("sequence")) {
			String masterName = atts.getValue("master");
			SequenceProperties.Builder builder = new SequenceProperties.Builder(masterName); 
			for (int i=0; i<atts.getLength(); i++) {
				String name = atts.getLocalName(i);
				if (name.equals("initial-page-number")) {
					builder.initialPageNumber(Integer.parseInt(atts.getValue(i)));
				}
			}
			flow.newSequence(builder.build());
		} else if (localName.equals("block")) {
			BlockProperties.Builder builder = new BlockProperties.Builder();
			for (int i=0; i<atts.getLength(); i++) {
				String name = atts.getLocalName(i);
				if (name.equals("margin-left")) {
					builder.leftMargin(Integer.parseInt(atts.getValue(i)));
				} else if (name.equals("margin-right")) {
					builder.rightMargin(Integer.parseInt(atts.getValue(i)));
				} else if (name.equals("margin-top")) {
					builder.topMargin(Integer.parseInt(atts.getValue(i)));
				} else if (name.equals("margin-bottom")) {
					builder.bottomMargin(Integer.parseInt(atts.getValue(i)));
				} else if (name.equals("text-indent")) {
					builder.textIndent(Integer.parseInt(atts.getValue(i)));
				} else if (name.equals("first-line-indent")) {
					builder.firstLineIndent(Integer.parseInt(atts.getValue(i)));
				} else if (name.equals("list-type")) {
					builder.listType(BlockProperties.ListType.valueOf(atts.getValue(i).toUpperCase()));
				} else if (name.equals("break-before")) {
					builder.breakBefore(BlockProperties.BreakBeforeType.valueOf(atts.getValue(i).toUpperCase()));
				} else if (name.equals("keep")) {
					builder.keep(BlockProperties.KeepType.valueOf(atts.getValue(i).toUpperCase()));
				} else if (name.equals("keep-with-next")) {
					builder.keepWithNext(Integer.parseInt(atts.getValue(i)));
				} else if (name.equals("block-indent")) {
					builder.blockIndent(Integer.parseInt(atts.getValue(i)));
				}
			}
			flow.startBlock(builder.build());
		} else if (localName.equals("marker")) {
			String markerName = "";
			String markerValue = "";
			for (int i=0; i<atts.getLength(); i++) {
				String name = atts.getLocalName(i);
				if (name.equals("class")) {
					markerName = atts.getValue(i);
				} else if (name.equals("value")) {
					markerValue = atts.getValue(i);
				}
			}
			flow.insertMarker(new Marker(markerName, markerValue));
		} else if (localName.equals("br")) {
			flow.newLine();
		} else if (localName.equals("leader")) {
			Leader.Builder builder = new Leader.Builder();
			for (int i=0; i<atts.getLength(); i++) {
				String name = atts.getLocalName(i);
				if (name.equals("align")) {
					builder.align(Leader.Alignment.valueOf(atts.getValue(i).toUpperCase()));
				} else if (name.equals("position")) {
					builder.position(Position.parsePosition(atts.getValue(i)));
				} else if (name.equals("pattern")) {
					builder.pattern(atts.getValue(i));
				}
			}
			flow.insertLeader(builder.build());
		}
	}

	public void endElement (String uri, String localName, String qName) throws SAXException {
		flushChars();
		if (localName.equals("block")) {
			flow.endBlock();
		}
	}
	
	public void characters (char ch[], int start, int length) throws SAXException {
		sb.append(new String(ch, start, length));
	}
	
	// Coalescing feature
	private void flushChars() {
		if (sb.length()>0) {
			flow.addChars(sb);
			sb = new StringBuffer();
		}
	}

}
