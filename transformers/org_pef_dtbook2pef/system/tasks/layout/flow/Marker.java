package org_pef_dtbook2pef.system.tasks.layout.flow;

/**
 * A Marker is a reference data inserted at some point in the flow. It can be used to create 
 * running headers/footers.
 * @author joha
 *
 */
public class Marker {
	private String name;
	private String value;
	
	/**
	 * Create a new Marker with the given name and value
	 * @param name the name of the Marker
	 * @param value the Marker value
	 */
	public Marker(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Get the name of this Marker
	 * @return returns the name of this Marker
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the value of this Marker
	 * @return returns this Marker's value
	 */
	public String getValue() {
		return value;
	}

}
