package org.daisy.util.dtb.build;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.XMLEvent;

/**
 * Base abstract type for all kinds of SMIL data structures
 * @author jpritchett@rfbd.org
 *
 */
public abstract class SmilStructure {	
	// Various data items that are common to all SMIL structures
	protected String elementName;		// Must be set by every subclass in constructor!
	protected String smilID;
	protected String className;
	protected String customTestName;
	protected boolean isEscapable;
	protected String linkTarget;
	protected boolean linkExternal;
	
	// Things needed for rendering
	protected ArrayList<XMLEvent> myEvents;
	protected XMLEventFactory xef;
	
	
	// PROPERTIES
	
	/**
	 * Get the name of any SMIL custom test that may be associated with this structure
	 */
	public String getCustomTestName() {
		return customTestName;
	}
	/**
	 * Set the name of a SMIL custom test for this structure
	 * @param customTestName The name of the custom test
	 */
	public void setCustomTestName(String customTestName) {
		this.customTestName = customTestName;
	}
	
	/**
	 * Get the XML id of this structure
	 */
	public String getSmilID() { return smilID; }
	
	/**
	 * Set the XML id of this structure
	 * @param smilID The value for the id attribute
	 */
	public void setSmilID(String smilID) { this.smilID = smilID; }
	
	/**
	 * Get the class attribute value for this structure
	 */
	public String getClassName() {
		return className;
	}
	
	/**
	 * Set the class attribute value for this structure
	 * @param className The value for the class attribute
	 */
	public void setClassName(String className) {
		this.className = className;
	} 
	
	/**
	 * Is this an escapable structure?
	 * @return true if escapable
	 */
	public boolean isEscapable() {
		return isEscapable;
	}
	
	/**
	 * Set this structure as escapable
	 * @param isEscapable true if escapable
	 */
	public void setEscapable(boolean isEscapable) {
		this.isEscapable = isEscapable;
	}

	/**
	 * If this structure links to something else, get the link target
	 * @return The link target or null if this is not a link
	 */
	public String getLinkTarget() {
		return linkTarget;
	}
	
	/**
	 * Set the link target for this structure
	 * @param linkTarget The target
	 */
	public void setLinkTarget(String linkTarget) {
		this.linkTarget = linkTarget;
	}
	
	/**
	 * Is this link an external link?
	 * @return true if external
	 */
	public boolean isLinkExternal() {
		return linkExternal;
	}
	
	/**
	 * Set this link as external
	 * @param linkExternal true if the link is external
	 */
	public void setLinkExternal(boolean linkExternal) {
		this.linkExternal = linkExternal;
	}
	
	// CONSTRUCTORS
	
	/**
	 * @param elementName The element name to use when rendering as XML
	 * @param smilID The XML id to use in the output XML for this structure
	 */
	public SmilStructure(String elementName, String smilID) {
		this.elementName = elementName;
		this.smilID = smilID;
		this.myEvents = new ArrayList<XMLEvent>();
		this.isEscapable = false;
		this.linkTarget = null;
		this.linkExternal = false;
		xef = XMLEventFactory.newInstance();
	}
	
	/**
	 * @param elementName The element name to use when rendering as XML
	 * @param smilID The XML id to use in the output XML for this structure
	 * @param className The value for the class attribute in the output XML
	 */
	public SmilStructure(String elementName, String smilID, String className) {
		this(elementName, smilID);
		this.className = className;
	}
	
	// ABSTRACT METHODS
	
	/**
	 * Express this structure as a series of XMLEvents
	 * @return Iterator of XMLEvents, suitable for output
	 */
	abstract Iterator<XMLEvent> asEventIterator();
	
	/**
	 * Express this structure as a series of XMLEvents
	 * @return List of XMLEvents
	 */
	abstract List<XMLEvent> asEvents();
}
