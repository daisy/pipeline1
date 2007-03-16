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
package org.daisy.dmfc.core.script;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract base class for different property types. 
 * @author Linus Ericson
 */
public abstract class AbstractProperty {
	
	// The regex used to find properties to expand
	protected static Pattern sPropertyPattern = Pattern.compile("\\$\\{(\\w+)\\}");
		
	protected String mName;	
	protected String mValue;	
	protected Map<String,AbstractProperty> mProperties;	
	
	/**
	 * Creates a new property.
	 * @param name the name of the property
	 * @param value the value of the property
	 * @param properties a set of known propertis in this script
	 */
	public AbstractProperty(String name, String value, Map<String,AbstractProperty> properties) {
		this.mName = name;
		this.mValue = value;
		this.mProperties = properties;
	}
	
	/**
	 * Gets the name of this property
	 * @return
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
		assert(mValue != null);
		assert(mProperties != null);				        
	    // Expand properties in the value string     
        Matcher matcher = sPropertyPattern.matcher(mValue);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String propName = matcher.group(1);
            // FIXME make sure property exists
            String propValue = mProperties.get(propName).getValue();            
            matcher.appendReplacement(sb, Matcher.quoteReplacement(propValue));
        }
        matcher.appendTail(sb);
        return sb.toString();
	}
	
	/** 
	 * Gets the value of this property. Any inline property references on
	 * the form ${propName} will be automatically expanded.
	 * @param runnerProperties an extra set of properties to be used in the expansion
	 * @return the expanded property value
	 */
	public String getValue(Map<String, AbstractProperty> runnerProperties) {
		assert(mValue != null);
		assert(mProperties != null);				        
	    // Expand properties in the value string     
        Matcher matcher = sPropertyPattern.matcher(mValue);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String propName = matcher.group(1);
            // FIXME make sure property exists
            String propValue = null;  
            if (runnerProperties.containsKey(propName)) {
            	propValue = runnerProperties.get(propName).getValue(runnerProperties);
            } else {
            	propValue = mProperties.get(propName).getValue(runnerProperties);  
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(propValue));
        }
        matcher.appendTail(sb);
        return sb.toString();
	}
	
}
