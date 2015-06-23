package org.daisy.util.dtb.build;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.SmilClock;

/**
 * Multimedia label information for labeling a NavigationItem
 * @author jpritchett@rfbd.org
 *
 */
public class NavigationLabel {
	private String text;
	private String audioFile;
	private SmilClock audioClipBegin;
	private SmilClock audioClipEnd;
	private String imageFile;
	
	protected static XMLEventFactory xef = XMLEventFactory.newInstance();
	
	/**
	 * Get the text version of the label
	 */
	public String getText() {
		return text;
	}
	/**
	 * Set the text version of the label
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * Get the image version of the label
	 */
	public String getImageFile() {
		return imageFile;
	}
	/**
	 * Set the image version of the label
	 * @param imageFile
	 */
	public void setImageFile(String imageFile) {
		this.imageFile = imageFile;
	}
	
	/**
	 * Set the audio version of the label
	 * @param audioFile URI of the audio file
	 * @param clipBegin The clip begin time within the audio file for this label
	 * @param clipEnd he clip end time within the audio file for this label
	 */
	public void setAudio(String audioFile, SmilClock clipBegin, SmilClock clipEnd) {
		this.audioFile = audioFile;
		this.audioClipBegin = clipBegin;
		this.audioClipEnd = clipEnd;
	}

	/**
	 * @param text The text of the label
	 */
	public NavigationLabel(String text) {
		this.text = text;
	}
	
	/**
	 * Serializes label information as XML via Stax
	 * @return Collection of XMLEvents
	 */
	public Collection<XMLEvent> asEvents() {
		ArrayList<XMLEvent> events = new ArrayList<XMLEvent>();
//	    Renders as:
//	        <text>Text label</text>
//	        <audio src="" clipBegin="" clipEnd="" />
//	        <img src="" />
		if (this.text != null && !this.text.equals("")) {
			events.add(xef.createStartElement("", Namespaces.Z2005_NCX_NS_URI, "text"));
			events.add(xef.createCharacters(this.text));
			events.add(xef.createEndElement("", Namespaces.Z2005_NCX_NS_URI, "text"));
		}
		if (this.audioFile != null) {
			Collection<Attribute> attrs = new ArrayList<Attribute>();
			attrs.add(xef.createAttribute("src", this.audioFile));
			attrs.add(xef.createAttribute("clipBegin", this.audioClipBegin.toString()));
			attrs.add(xef.createAttribute("clipEnd", this.audioClipEnd.toString()));
			events.add(xef.createStartElement("", Namespaces.Z2005_NCX_NS_URI, "audio", attrs.iterator(), null));
			events.add(xef.createEndElement("", Namespaces.Z2005_NCX_NS_URI, "audio"));
		}
		if (this.imageFile != null) {
			Collection<Attribute> attrs = new ArrayList<Attribute>();
			attrs.add(xef.createAttribute("src", this.imageFile));
			events.add(xef.createStartElement("", Namespaces.Z2005_NCX_NS_URI, "img", attrs.iterator(), null));
			events.add(xef.createEndElement("", Namespaces.Z2005_NCX_NS_URI, "img"));
		}
		return events;
	}
}
