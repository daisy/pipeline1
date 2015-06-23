package org.daisy.util.dtb.build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.Namespaces;

/**
 * Generic SMIL media structure (audio, text, etc.)
 * @author jpritchett@rfbd.org
 */
public class SmilStructureMedia extends SmilStructure {

	protected String src;
	protected String type;
	protected String region;
	
	/**
	 * @param elementName The element name to use when rendering as XML
	 * @param smilID The XML id to use in the output XML for this structure
	 */
	public SmilStructureMedia(String elementName, String smilID) {
		super(elementName, smilID);
	}
	
	/**
	 * @param elementName The element name to use when rendering as XML
	 * @param smilID The XML id to use in the output XML for this structure
	 * @param className The value for the class attribute in the output XML
	 */
	public SmilStructureMedia(String elementName, String smilID, String className) {
		super(elementName, smilID, className);
	}

	/**
	 * @param elementName The element name to use when rendering as XML
	 * @param smilID The XML id to use in the output XML for this structure
	 * @param className The value for the class attribute in the output XML
	 * @param src The URI of the referenced media object
	 */
	public SmilStructureMedia(String elementName, String smilID, String className, String src) {
		super(elementName, smilID, className);
		this.src = src;
	}

	@Override
	Iterator<XMLEvent> asEventIterator() {
		return this.asEvents().iterator();
	}
	
	@Override
	List<XMLEvent> asEvents() {
		// If this is linked to something, it has to be enclosed inside an anchor
		if (linkTarget != null) {
			myEvents.add(xef.createStartElement("", Namespaces.SMIL_20_NS_URI, "a"));
			myEvents.add(xef.createAttribute("id", smilID));
			smilID += "linked";
			myEvents.add(xef.createAttribute("href", linkTarget));
			if (linkExternal) {
				myEvents.add(xef.createAttribute("external", "true"));
			}
		}

		// Render as:  <elementName id="smilID" src="src" />
		// UNLESS this has a customTest or is escapable, in which case wrap in a seq
		if (customTestName != null || this.isEscapable) {
			myEvents.add(xef.createStartElement("", Namespaces.SMIL_20_NS_URI, "seq"));
			myEvents.add(xef.createAttribute("id", smilID));		// The ID goes on the seq, not the media!
			if (customTestName != null) {
				myEvents.add(xef.createAttribute("customTest", customTestName));
			}
			if (this.isEscapable) {
				myEvents.add(xef.createAttribute("end", "DTBuserEscape;" + smilID + "esc.end"));
				// Short-term, to fix a spec issue, wraps all media children in seq (ugh)
				// (spec says that all escapable structure seqs must contain one or more time containers)
				// TODO Get an erratum to the spec in place and then remove this
				myEvents.add(xef.createStartElement("", Namespaces.SMIL_20_NS_URI, "seq"));
				myEvents.add(xef.createAttribute("id", smilID+"esc"));
			}
			// TODO Use element name instead of "text"?
			smilID += "text";		// The media can then go under an assumed name ...
		}
		
		// Gather up the attributes for the media element
		Collection<Attribute> colAttrs = new ArrayList<Attribute>();
		colAttrs.add(xef.createAttribute("id", smilID));
		if (className != null && !className.equals("")) {
			colAttrs.add(xef.createAttribute("class", className));
		}
		if (src != null && !src.equals("")) {
			colAttrs.add(xef.createAttribute("src", src));
		}
		if (type != null && !type.equals("")) {
			colAttrs.add(xef.createAttribute("type", type));
		}
		if (region != null && !region.equals("")) {
			colAttrs.add(xef.createAttribute("region", region));
		}
		
		// Now you can create the actual element
		myEvents.add(xef.createStartElement("", Namespaces.SMIL_20_NS_URI, elementName, colAttrs.iterator(), null));
		myEvents.add(xef.createEndElement("", Namespaces.SMIL_20_NS_URI, elementName));
		
		// If we had to create a seq (or two) for this, then close 'em
		if (customTestName != null || this.isEscapable) {
			if (this.isEscapable) {
				myEvents.add(xef.createEndElement("", Namespaces.SMIL_20_NS_URI, "seq"));
			}
			myEvents.add(xef.createEndElement("", Namespaces.SMIL_20_NS_URI, "seq"));
		}
		
		// And if it's linked, close the anchor, too
		if (linkTarget != null) {
			myEvents.add(xef.createEndElement("", Namespaces.SMIL_20_NS_URI, "a"));
		}
		return myEvents;
	}
}
