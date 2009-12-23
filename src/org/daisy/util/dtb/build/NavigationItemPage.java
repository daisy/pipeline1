package org.daisy.util.dtb.build;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.Namespaces;

/**
 * Representation of a page target in NCX
 * @author jpritchett@rfbd.org
 *
 */
public class NavigationItemPage extends NavigationItem {
	
	/**
	 * The types of page targets
	 * @author jpritchett@rfbd.org
	 */
	public enum PageType {
		FRONT,
		NORMAL,
		SPECIAL
	}
	private long value;
	private PageType type;
	
	/**
	 * Get the numeric value of this page (if any)
	 * @return Numeric value or zero if none
	 */
	public long getValue() {
		return value;
	}
	
	/**
	 * Set the numeric value of this page
	 * @param value
	 */
	public void setValue(long value) {
		this.value = value;
	}
	
	/**
	 * Get the type of this page
	 * @return The PageType for this page
	 */
	public PageType getType() {
		return type;
	}

	/**
	 * @param type The PageType for the page
	 */
	public NavigationItemPage(PageType type) {
		super();
		this.type = type;
	}
	
	/**
	 * @param type The PageType for the page
	 * @param value The numeric value for the page
	 */
	public NavigationItemPage(PageType type, long value) {
		super();
		this.type = type;
		this.value = value;
	}
	
	@Override
	/**
	 * Serializes the pageTarget as XML via Stax
	 * @return Collection of XMLEvents
	 */
	public Collection<XMLEvent> asEvents() {
		ArrayList<XMLEvent> events = new ArrayList<XMLEvent>();
		//	    Renders as:
		//	        <pageTarget id="" value="" type="" class="" playOrder="">
		//	            [navLabel rendering]
		//	            [content rendering]
		//	        </pageTarget>
		
		// Gather up all the attributes for the pageTarget
		Collection<Attribute> attrs = new ArrayList<Attribute>();
		attrs.add(xef.createAttribute("id", this.id));
		attrs.add(xef.createAttribute("playOrder", String.valueOf(this.playOrder)));
		switch(this.type) {
			case FRONT:
				attrs.add(xef.createAttribute("type", "front"));
				break;
			case NORMAL:
				attrs.add(xef.createAttribute("type", "normal"));
				break;
			case SPECIAL:
				attrs.add(xef.createAttribute("type", "special"));
				break;
		}
		
		if (this.value > 0) {
			attrs.add(xef.createAttribute("value", String.valueOf(this.value)));
		}
		if (this.itemClass != null && !this.itemClass.equals("")) {
			attrs.add(xef.createAttribute("class", this.itemClass));
		}
		
		// Now do the rendering
		events.add(xef.createStartElement("", Namespaces.Z2005_NCX_NS_URI, "pageTarget", attrs.iterator(), null));
		events.addAll(this.labelAsEvents());
		events.addAll(this.contentAsEvents());
		events.add(xef.createEndElement("", Namespaces.Z2005_NCX_NS_URI, "pageTarget"));
		
		return events;
	}
}
