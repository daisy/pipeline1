package org_pef_text.pef2text;

import java.util.HashMap;

public class Element {
	private String uri;
	private String localName;
	private HashMap<String, String> atts;
	
	/**
	 * 
	 * @param uri
	 * @param localName
	 */
	public Element(String uri, String localName, HashMap<String, String> attributes) {
		this.uri = uri;
		this.localName = localName;
		this.atts = attributes;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * 
	 * @return
	 */
	public String getLocalName() {
		return localName;
	}
	
	/**
	 * 
	 * @return
	 */
	public HashMap<String, String> getAttributes() {
		return atts;
	}
}