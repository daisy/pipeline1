/*
 * Daisy Pipeline (C) 2005-2009 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package se_tpb_daisy202splitter;


/**
 * Representation of an item in the NCC
 * @author Linus Ericson
 */
public class NccItem {

	public static enum Type {
		PAGE_FRONT,
		PAGE_NORMAL,
		PAGE_SPECIAL,
		SIDEBAR,
		OPTIONAL_PRODNOTE,
		NOTEREF,
		H1,
		H2,
		H3,
		H4,
		H5,
		H6
	}
	
	private Type type;
	private String idAttr;
	private String uri;
	private String text;
	private String rel;
	private String classAttr;
	
	/**
	 * Creates a new NCC item.
	 * @param type the type of the NCC item
	 * @param idAttr the id attribute value
	 * @param uri the URI
	 * @param text the text content
	 */
	public NccItem(Type type, String idAttr, String uri, String text) {
		this.type = type;
		this.idAttr = idAttr;
		this.uri = uri;
		this.text = text;
		this.rel = null;
		this.classAttr = null;
	}
	
	/**
	 * Copy constructor.
	 * @param other the NCC item to copy
	 */
	public NccItem(NccItem other) {
		this.type = other.type;
		this.idAttr = other.idAttr;
		this.uri = other.uri;
		this.text = other.text;
		this.rel = other.rel;
		this.classAttr = other.classAttr;
	}

	/**
	 * Gets the type of NCC item
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * Gets the URI
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}
	
	/**
	 * Sets the URI
	 * @param uri the URI to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	/**
	 * Gets the id attribute
	 * @return the idAttr
	 */
	public String getIdAttr() {
		return idAttr;
	}

	/**
	 * Gets the text content
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Gets the rel attribute
	 * @return the rel
	 */
	public String getRel() {
		return rel;
	}
	
	/**
	 * Sets the rel attribute
	 * @param rel the rel attribute to set
	 */
	public void setRel(String rel) {
		this.rel = rel;		
	}
	
	/**
	 * Gets the class attribute
	 * @return the classAttr
	 */
	public String getClassAttr() {
		return classAttr;
	}

	/**
	 * Sets the class attribute
	 * @param classAttr the classAttr to set
	 */
	public void setClassAttr(String classAttr) {
		this.classAttr = classAttr;
	}
	
}
