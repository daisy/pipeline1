package org.daisy.util.dtb.build;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.Namespaces;

/**
 * Representation of a point within NCX hierarchy
 * 
 * @author jpritchett@rfbd.org
 *
 */
public class NavigationItemPoint extends NavigationItem {

	private ArrayList<NavigationItemPoint> subpoints;

	/**
	 * Get all the children of this point
	 * @return List of child NavigationItemPoints
	 */
	public ArrayList<NavigationItemPoint> getSubpoints() {
		return subpoints;
	}
	
	/**
	 * Set the list of children of this point
	 * @param subpoints List of NavigationItemPoints that are children of this point
	 */
	public void setSubpoints(ArrayList<NavigationItemPoint> subpoints) {
		this.subpoints = subpoints;
	}

	@Override
	/**
	 * Serializes the navPoint as XML via Stax
	 * @return Collection of XMLEvents
	 */
	public Collection<XMLEvent> asEvents() {
//	    Renders as:
//	        <navPoint id="" class="" playOrder="">
//	            [navLabel rendering]
//	            [content rendering]
//	            [rendering of all subpoints]
//	        </navPoint>

		ArrayList<XMLEvent> events = new ArrayList<XMLEvent>();
		Collection<Attribute> attrs = new ArrayList<Attribute>();
		
		// Gather up the attributes for this navPoint
		attrs.add(xef.createAttribute("id", this.id));
		attrs.add(xef.createAttribute("playOrder", String.valueOf(this.playOrder)));
		if (this.itemClass != null && !this.itemClass.equals("")) {
			attrs.add(xef.createAttribute("class", this.itemClass));
		}
		
		// Start the navPoint
		events.add(xef.createStartElement("", Namespaces.Z2005_NCX_NS_URI, "navPoint", attrs.iterator(), null));
		
		// Render the label
		events.addAll(this.labelAsEvents());
		
		// Render the content
		events.addAll(this.contentAsEvents());
		
		// Render any subpoints
		if (this.subpoints != null && this.subpoints.size() > 0) {
			for (NavigationItemPoint navPoint : this.subpoints) {
				events.addAll(navPoint.asEvents());
			}
		}
		
		// Now just close it all up
		events.add(xef.createEndElement("", Namespaces.Z2005_NCX_NS_URI, "navPoint"));
		
		return events;
	}

}
