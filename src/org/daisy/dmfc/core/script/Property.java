package org.daisy.dmfc.core.script;

public class Property {
	private String name;
	private String value;
	private String type;
	
	public Property(String propName, String propValue, String propType) {
		name = propName;
		value = propValue;
		type = propType;
	}
	
	public Property(String propName, String propValue) {
		this(propName, propValue, null);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}
	
}
