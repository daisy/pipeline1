package org_pef_text.pef2text;

public class Element {
	private String uri;
	private String localName;
	
	/**
	 * 
	 * @param uri
	 * @param localName
	 */
	public Element(String uri, String localName) {
		this.uri = uri;
		this.localName = localName;
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
}