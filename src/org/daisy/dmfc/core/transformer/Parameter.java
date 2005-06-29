/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
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
package org.daisy.dmfc.core.transformer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.daisy.dmfc.core.MIMERegistry;
import org.daisy.dmfc.exception.MIMEException;
import org.daisy.util.xml.XPathUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A parameter in the Transformer Description File (TDF).
 * @author Linus Ericson
 */
public class Parameter implements ParameterInfo {
	
    private static Pattern variablePattern = Pattern.compile("\\$\\{(\\w+)\\}");
    
	private String name;
	private String description;
	private String example;
	private boolean required;
	private String direction;
	private String type;
	private String defaultValue;
	private String value = null;
	private Collection enumValues = null;
	
	private File tdfDir;
	
	/**
	 * Creates a new Parameter.
	 * @param parameter the dom4j element to get the data from.
	 * @throws MIMEException if a type attribute is not a valid MIME type
	 */
	public Parameter(Element parameter, File transformerDir) throws MIMEException {
	    tdfDir = transformerDir;
		name = getFromXPath(parameter, "name");
		description = getFromXPath(parameter, "description");
		example = getFromXPath(parameter, "example");
		
		NodeList enumValueNodes = XPathUtils.selectNodes(parameter, "enum/value");
		if (enumValueNodes.getLength() > 0) {
		    enumValues = new ArrayList();
		    for (int i = 0; i < enumValueNodes.getLength(); ++i) {
		        Element enumValue = (Element)enumValueNodes.item(i);
		        enumValues.add(XPathUtils.valueOf(enumValue, "."));
		    }
		}
		
		if (XPathUtils.selectSingleNode(parameter, "@required") != null) {
		    required = Boolean.valueOf(XPathUtils.valueOf(parameter, "@required")).booleanValue();
		}
		direction = getFromXPath(parameter, "@direction");

		// Make sure the 'type' matches a MIME type
		type = getFromXPath(parameter, "@type");
		MIMERegistry mime = MIMERegistry.instance();
		if (!mime.contains(type)) {
		    throw new MIMEException("Type attribute " + type + " of parameter " + name + " is not a valid MIME type.");
		}

		defaultValue = expandPatterns(getFromXPath(parameter, "default"));
		value = expandPatterns(getFromXPath(parameter, "value"));
	}
	
	/**
	 * Expand patterns. The function currently expands:
	 * <dl>
	 * <dt>${transformer_dir}</dt><dd>The directory of the TDF.</dd>
	 * <dt>${dollar}</dt><dd>A dollar sign.</dd>
	 * </dl>
	 * @param a_valueWithPattern a string to expand
	 * @return the expanded string
	 */
	private String expandPatterns(String a_valueWithPattern) {
        if (a_valueWithPattern == null) {
            return null;
        }
        Matcher _matcher = variablePattern.matcher(a_valueWithPattern);
        StringBuffer _sb = new StringBuffer();
        while (_matcher.find()) {
            String _variable = _matcher.group(1);            
            if (_variable.equals("transformer_dir")) {
                String _dir = tdfDir.getPath();
                _dir = _dir.replaceAll("\\\\", "\\\\\\\\");       // Replace one backslash with two
                _matcher.appendReplacement(_sb, _dir);
            } else if (_variable.equals("dollar")) {
                _matcher.appendReplacement(_sb, "\\$");
            }            
        }
        _matcher.appendTail(_sb);        
        return _sb.toString();
    }
	
	/**
	 * Gets the value of the Node at the location specified by the XPath
	 * if the Node exists, or null if the specified Node does not exist.
	 * @param a_node the originating Node
	 * @param a_xpath an XPath expression
	 * @return the value of the XPath expression or null
	 */
	private String getFromXPath(Node a_node, String a_xpath) {
	    String result = XPathUtils.valueOf(a_node, a_xpath);
	    if (result != "") {
	        return result;
	    }	    
	    return null;
	}
	
	/**
	 * @return Returns the direction.
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * @return Returns the example.
	 */
	public String getExample() {
		return example;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the required.
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }
    
    /**
     * @return Returns the defaultValue.
     */
    public String getDefaultValue() {
        return defaultValue;
    }
    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

     public Collection getEnumValues() {
        return enumValues;
    }
}
