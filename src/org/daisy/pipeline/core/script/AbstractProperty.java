/*
 * Daisy Pipeline
 * Copyright (C) 2007  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.pipeline.core.script;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.daisy.pipeline.core.script.function.Function;
import org.daisy.pipeline.core.script.function.FunctionRegistry;

/**
 * Abstract base class for different property types. 
 * @author Linus Ericson
 */
public abstract class AbstractProperty {
	
	// The regex used to find properties to expand
	protected static Pattern sPropertyPattern = Pattern.compile("\\$(\\w*)\\{(\\w+)\\}");
		
	protected String mName;	
	protected String mValue;	
	protected Map<String,AbstractProperty> mProperties;	
	
	/**
	 * Creates a new property.
	 * @param name the name of the property
	 * @param value the value of the property
	 * @param properties a set of known propertis in this script
	 */
	public AbstractProperty(String name, String value, Map<String,AbstractProperty> properties) throws ScriptValidationException {
		this.mName = name;
		this.mValue = value;
		this.mProperties = properties;
		validate();
	}
	
	/**
	 * Gets the name of this property
	 * @return the property name
	 */
	public String getName() {
		return mName;
	}
	
	/**
	 * Gets the value of this property. Any inline property references on
	 * the form ${propName} will be automatically expanded.
	 * @return the expanded property value
	 */
	public String getValue() {
		return getValue(null);
	}
	
	/** 
	 * Gets the value of this property. Any inline property references on
	 * the form ${propName} will be automatically expanded.
	 * @param runnerProperties an extra set of properties to be used in the expansion
	 * @return the expanded property value
	 */
	public String getValue(Map<String, ? extends AbstractProperty> runnerProperties) {
		assert(mValue != null);
		assert(mProperties != null);				        
	    // Expand properties in the value string     
        Matcher matcher = sPropertyPattern.matcher(mValue);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String propName = matcher.group(2);
            String propValue = null;  
            if (runnerProperties != null && runnerProperties.containsKey(propName)) {
            	propValue = runnerProperties.get(propName).getValue(runnerProperties);
            } else {
            	propValue = mProperties.get(propName).getValue(runnerProperties);  
            }
            
            String funcName = matcher.group(1);
            if (funcName != null && !"".equals(funcName)) {
            	Function func = FunctionRegistry.lookup(funcName);
            	propValue = func.apply(propValue);
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(propValue));
        }
        matcher.appendTail(sb);
        return sb.toString();
	}
	
	/**
	 * Validate the value string of the property.
	 * Make sure all referenced properties and functions exist.
	 * @throws ScriptValidationException
	 */
	protected void validate() throws ScriptValidationException {
		assert(mValue != null);
		assert(mProperties != null);
		Matcher matcher = sPropertyPattern.matcher(mValue);
		// Loop through value string
		while (matcher.find()) {
			// Make sure property exists
			String propName = matcher.group(2);
			if (!mProperties.containsKey(propName)) {
				throw new ScriptValidationException("Property '" + propName + "' is undefined in '" + mValue + "'");
			}
			// Make sure function exists
			String funcName = matcher.group(1);
			if (funcName != null && !"".equals(funcName)) {
				Function func = FunctionRegistry.lookup(funcName);
				if (func == null) {
					throw new ScriptValidationException("Undefined function '" + funcName + "' in '" + mValue + "'");
				}
			}
		}		
	}
	
}
