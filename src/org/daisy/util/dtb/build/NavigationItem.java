package org.daisy.util.dtb.build;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.IDGenerator;
import org.daisy.util.xml.Namespaces;

/**
 * Abstract superclass for all navigation items in an NCX
 * @author jpritchett@rfbd.org
 *
 */
public abstract class NavigationItem {
	
	// Stuff we need to generate XML
	protected static XMLEventFactory xef = XMLEventFactory.newInstance();
	private static IDGenerator idg = new IDGenerator("navItem");
	
	// Properties that are common to all NCX items
	protected String smilContent;
	protected NavigationLabel label;
	protected long playOrder;
	protected String itemClass;
	protected String id;

	// PROPERTIES
	
	/**
	 * Get the SMIL structure that is the content of this item
	 * @return URI into a SMIL file
	 */
	public String getSmilContent() {
		return smilContent;
	}
	
	/**
	 * Set the SMIL structure that is the content of this item
	 * @param smilContent URI into a SMIL file
	 */
	public void setSmilContent(String smilContent) {
		this.smilContent = smilContent;
	}
	
	/**
	 * Get the multimedia label for this item
	 */
	public NavigationLabel getLabel() {
		return label;
	}
	
	/**
	 * Set the multimedia label for this item
	 * @param label A NavigationLabel object that describes this item
	 */
	public void setLabel(NavigationLabel label) {
		this.label = label;
	}
	
	/**
	 * Get the sequential play order of this item
	 * @return The place of this item in the book sequence3
	 */
	public long getPlayOrder() {
		return playOrder;
	}
	
	/**
	 * Set the sequential play order of this item
	 * @param playOrder
	 */
	public void setPlayOrder(long playOrder) {
		this.playOrder = playOrder;
	}
	
	/**
	 * Get the class attribute value for this item
	 */
	public String getItemClass() {
		return itemClass;
	}
	
	/**
	 * Set the class attribute value for this item
	 */
	public void setItemClass(String itemClass) {
		this.itemClass = itemClass;
	}
	
	/**
	 * Get the XML id of this item
	 */
	public String getId() {
		return id;
	}
	
	// CONSTRUCTOR
	
	public NavigationItem() {
		this.id = idg.generateId();
		this.label = new NavigationLabel("");
	}
	
	// ABSTRACT METHODS
	
	/**
	 * Get an XML rendering of this item
	 * @return Collection of XMLEvents rendering this item
	 */
	public abstract Collection<XMLEvent> asEvents();
	
	
	// PROTECTED METHODS FOR USE BY SUBCLASSES
	/**
	 * Serializes the navLabel as XML via Stax
	 * @return Collection of XMLEvents
	 */
	protected Collection<XMLEvent> labelAsEvents() {
		ArrayList<XMLEvent> events = new ArrayList<XMLEvent>();
		//	Label renders as:
		//            <navLabel>
		//                [label rendering]
		//            </navLabel>
		
		if (this.label != null) {
			events.add(xef.createStartElement("", Namespaces.Z2005_NCX_NS_URI, "navLabel"));
			events.addAll(this.label.asEvents());
			events.add(xef.createEndElement("", Namespaces.Z2005_NCX_NS_URI, "navLabel"));
		}	
		
		return events;
	}
	
	/**
	 * Serializes the SMIL content pointer as XML via Stax
	 * @return Collection of XMLEvents
	 */
	protected Collection<XMLEvent> contentAsEvents() {
		ArrayList<XMLEvent> events = new ArrayList<XMLEvent>();
		//	    Renders as:
		//            <content src="smilURI" />
		
		if (this.smilContent != null) {
			Collection<Attribute> attrs = new ArrayList<Attribute>();
			attrs.add(xef.createAttribute("src", this.smilContent));
			events.add(xef.createStartElement("", Namespaces.Z2005_NCX_NS_URI, "content", attrs.iterator(), null));
			events.add(xef.createEndElement("", Namespaces.Z2005_NCX_NS_URI, "content"));
		}
		return events;
	}
}
