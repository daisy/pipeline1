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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.dmfc.exception.MIMEException;
import org.daisy.dmfc.exception.NotSupposedToHappenException;
import org.daisy.util.mime.MIMEType;
import org.daisy.util.mime.MIMETypeRegistry;
import org.daisy.util.mime.MIMETypeRegistryException;

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
	 * Creates a new TDF Parameter. This method assumes a &lt;parameter&gt;
	 * start tag has just been read from the XML event reader.
	 * @param start the &lt;paramter&gt; start element
	 * @param er the XML event reader to get the parameter values from
	 * @param transformerDir the directory of the transformer
	 * @throws XMLStreamException
	 * @throws MIMEException
	 * @throws MIMETypeRegistryException 
	 */
	public Parameter(StartElement start, XMLEventReader er, File transformerDir) throws XMLStreamException, MIMEException, MIMETypeRegistryException {
	    tdfDir = transformerDir;
	    String current = null;
	    
	    Attribute att = start.getAttributeByName(new QName("required"));
	    if (att != null) {
	        required = Boolean.valueOf(att.getValue()).booleanValue();
	    }
	    
	    att = start.getAttributeByName(new QName("direction"));
	    direction = att!=null?att.getValue():null;
	    
	    att = start.getAttributeByName(new QName("type"));
	    type = att!=null?att.getValue():null;
	    // Make sure the specified MIME type exists
	    MIMETypeRegistry registry = MIMETypeRegistry.getInstance();
		if (!registry.hasEntry(type)) {
		    throw new MIMEException("Type attribute " + type + " is not a valid MIME type.");
		}
		
	    while (er.hasNext()) {
	        XMLEvent event = er.nextEvent();
	        switch (event.getEventType()) {
	        case XMLStreamConstants.START_ELEMENT:
	            StartElement se = event.asStartElement();
	        	String seName = se.getName().getLocalPart();
	        	if (seName.equals("name")) {
	        	    current = "name";
	        	} else if (seName.equals("description")) {
	        	    current = "description";
	        	} else if (seName.equals("example")) {
	        	    current = "example";
	        	} else if (seName.equals("default")) {
	        	    current = "default";
	        	} else if (seName.equals("value")) {
	        	    current = "value";
	        	} else if (seName.equals("enum")) {
	        	    enumValues = new ArrayList();
	        	    addEnums(er);
	        	}
	            break;	  
	        case XMLStreamConstants.CHARACTERS:
	            String data = event.asCharacters().getData();
	        	if (current == null) {
	        	    break;
	        	}
	            if (current.equals("name")) {
	                name = data;
	            } else if (current.equals("description")) {
	                description = data;
	            } else if (current.equals("example")) {
	                example = data;
	            } else if (current.equals("default")) {
	                defaultValue = expandPatterns(data);
	            } else if (current.equals("value")) {
	                value = expandPatterns(data);
	            }
	            break;
	        case XMLStreamConstants.END_ELEMENT:
	            EndElement ee = event.asEndElement();
	        	if (ee.getName().getLocalPart().equals("parameter")) {
	        	    // Stop when the </parameter> end tag is found.
	        	    return;
	        	}
	        	current = null;
	            break;
	        }
	    }
	    throw new NotSupposedToHappenException("Did not find </parameter> end tag in TDF");
	}
	
	/**
	 * Reads an enum definition. This method assumes a &lt;enum&gt; start tag
	 * has just been read from the XML event reader.
	 * @param er the XML event reader
	 * @throws XMLStreamException
	 */
	private void addEnums(XMLEventReader er) throws XMLStreamException {
	    boolean inValue = false;
	    while (er.hasNext()) {
	        XMLEvent event = er.nextEvent();
	        switch (event.getEventType()) {
	        case XMLStreamConstants.START_ELEMENT:
	            StartElement se = event.asStartElement();	
	        	String seName = se.getName().getLocalPart();
	        	if (seName.equals("value")) {
	        	    inValue = true;
	        	}
	            break;
	        case XMLStreamConstants.CHARACTERS:
	            if (!inValue) {
	                break;
	            }
	            String data = event.asCharacters().getData();
	            enumValues.add(data);
	            break;
	        case XMLStreamConstants.END_ELEMENT:
	            EndElement ee = event.asEndElement();
	        	String eeName = ee.getName().getLocalPart();
	        	if (eeName.equals("enum")) {
	        	    // Stop when </enum> end tag is found.
	        	    return;
	        	} else if (eeName.equals("value")) {
	        	    inValue = false;
	        	}
	            break;
	        }
	    }
	    throw new NotSupposedToHappenException("Did not find </enum> end tag in TDF");
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
	
	public MIMEType getMIMEType() throws MIMETypeRegistryException {
		return MIMETypeRegistry.getInstance().getEntryByName(this.getType());
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
