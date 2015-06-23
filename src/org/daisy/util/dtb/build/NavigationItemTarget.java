package org.daisy.util.dtb.build;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.Namespaces;

/**
 * Representation of a navigation target in NCX
 * @author jpritchett@rfbd.org
 *
 */
public class NavigationItemTarget extends NavigationItem {

	private long value;
	
	/**
	 * Get the numeric value of this target (if any)
	 * @return The numeric value or zero if none
	 */
	public long getValue() {
		return value;
	}

	/**
	 * Set the numeric value of this target
	 * @param value
	 */
	public void setValue(long value) {
		this.value = value;
	}

	@Override
	/**
	 * Serializes the navTarget as XML via Stax
	 * @return Collection of XMLEvents
	 */
	public Collection<XMLEvent> asEvents() {
		ArrayList<XMLEvent> events = new ArrayList<XMLEvent>();
		//	    Renders as:
		//	        <navTarget id="" value="" class="" playOrder="">
		//	            [navLabel rendering]
		//	            [content rendering]
		//	        </navTarget>
		
		// Gather up the attributes for the navTarget
		Collection<Attribute> attrs = new ArrayList<Attribute>();
		attrs.add(xef.createAttribute("id", this.id));
		attrs.add(xef.createAttribute("playOrder", String.valueOf(this.playOrder)));
		if (this.value > 0) {
			attrs.add(xef.createAttribute("value", String.valueOf(this.value)));
		}
		if (this.itemClass != null) {
			attrs.add(xef.createAttribute("class", this.itemClass));
		}
		
		// Do the rendering
		events.add(xef.createStartElement("", Namespaces.Z2005_NCX_NS_URI, "navTarget", attrs.iterator(), null));
		events.addAll(this.labelAsEvents());
		events.addAll(this.contentAsEvents());
		events.add(xef.createEndElement("", Namespaces.Z2005_NCX_NS_URI, "navTarget"));
		
		return events;
	}

}
