package org.daisy.util.dtb.build;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.Namespaces;

/**
 * Generic SMIL container structure (a structure that has children, such as seq or par)
 * @author jpritchett@rfbd.org
 */
public class SmilStructureContainer extends SmilStructure {

	protected ArrayList<SmilStructure> children;
	
	/**
	 * Get the children of this structure
	 * @return A list of SMIL structures
	 */
	public ArrayList<SmilStructure> getChildren() {
		return children;
	}
	
	/**
	 * @param elementName The element name to use when rendering as XML
	 * @param smilID The XML id to use in the output XML for this structure
	 */
	public SmilStructureContainer(String elementName, String smilID) {
		super(elementName, smilID);
		children = new ArrayList<SmilStructure>();
	}

	/**
	 * @param elementName The element name to use when rendering as XML
	 * @param smilID The XML id to use in the output XML for this structure
	 * @param className The value for the class attribute in the output XML
	 */
	public SmilStructureContainer(String elementName, String smilID, String className) {
		super(elementName, smilID, className);
		children = new ArrayList<SmilStructure>();
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
			myEvents.add(xef.createAttribute("id", smilID));	// We give our ID to the anchor ...
			smilID += "linked";									// And make up a related ID for ourselves
			myEvents.add(xef.createAttribute("href", linkTarget));
			if (linkExternal) {
				myEvents.add(xef.createAttribute("external", "true"));
			}
		}
		
		// Start up the container
		myEvents.add(xef.createStartElement("", Namespaces.SMIL_20_NS_URI, elementName));
		myEvents.add(xef.createAttribute("id", smilID));
		if (className != null && !className.equals("")) {
			myEvents.add(xef.createAttribute("class", className));
		}
		if (customTestName != null && !customTestName.equals("")) {
			myEvents.add(xef.createAttribute("customTest", customTestName));
		}

		// If this is escapable, add @end, too
		if (isEscapable && children.size() > 0) {
			myEvents.add(xef.createAttribute("end", "DTBuserEscape;" + children.get(children.size()-1).smilID + ".end"));
		}
		
		// Add the children
		for (SmilStructure struct : children) {
			// Short-term, to fix a spec issue, wraps all media children in seq (ugh)
			// (spec says that all escapable structure seqs must contain one or more time containers)
			// TODO Get an erratum to the spec in place and then remove this
			if (isEscapable && struct instanceof SmilStructureMedia) {
				myEvents.add(xef.createStartElement("", Namespaces.SMIL_20_NS_URI, "seq"));
				myEvents.add(xef.createAttribute("id", struct.getSmilID()));
				struct.setSmilID(struct.getSmilID()+"text");
			}
			myEvents.addAll(struct.asEvents());
			if (isEscapable && struct instanceof SmilStructureMedia) {
				myEvents.add(xef.createEndElement("", Namespaces.SMIL_20_NS_URI, "seq"));
			}
		}	

		// Close the container
		myEvents.add(xef.createEndElement("", Namespaces.SMIL_20_NS_URI, elementName));
		
		// And if it's linked, close the anchor, too
		if (linkTarget != null) {
			myEvents.add(xef.createEndElement("", Namespaces.SMIL_20_NS_URI, "a"));
		}
		return myEvents;
	}
}
