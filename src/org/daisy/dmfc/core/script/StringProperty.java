package org.daisy.dmfc.core.script;

import java.util.Map;

/**
 * A property type that will not do any further property value expansion.
 * @author Linus Ericson
 */
public class StringProperty extends AbstractProperty {

	/**
	 * Constructor.
	 * @param name the name of the property
	 * @param value the value of the property
	 */
	public StringProperty(String name, String value) {
		super(name, value, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.dmfc.core.script.AbstractProperty#getValue()
	 */
	@Override
	public String getValue() {
		return mValue;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.dmfc.core.script.AbstractProperty#getValue(java.util.Map)
	 */
	@Override
	public String getValue(Map<String, AbstractProperty> runnerProperties) {		
		return mValue;
	}

}
