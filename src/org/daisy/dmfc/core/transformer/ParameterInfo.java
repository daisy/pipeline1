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

import java.util.Collection;

import org.daisy.util.mime.MIMEType;
import org.daisy.util.mime.MIMETypeRegistryException;

/**
 * Information about a Transformer parameter.
 * @author Linus Ericson
 */
public interface ParameterInfo {

    /**
     * Gets the name of a parameter.
     * @return the name of the parameter
     */
    public String getName();
    
    /**
     * Gets the description of a parameter.
     * @return the description of the parameter.
     */
    public String getDescription();
    
    /**
     * Gets the example usage string of a parameter.
     * @return the example usage.
     */
    public String getExample();
    
    /**
     * Checks wether a parameter is required to be present
     * in the script file or not. 
     * @return <code>true</code> if the parameter is required, <code>false</code> otherwise.
     */
    public boolean isRequired();
    
    /**
     * Gets the direction of a parameter.
     * @return "in", if this is an input parameter, "out" otherwise.
     */
    public String getDirection();
    
    /**
     * Gets the type of a parameter.
     * @return the type of the parameter.
     */
    public String getType();
    
    public MIMEType getMIMEType() throws MIMETypeRegistryException;
    
    /**
     * Gets the default value of the parameter.
     * This field is only present if <code>isRequired()</code>
     * returns <code>false</code>.
     * @return the default value of the parameter.
     */
    public String getDefaultValue();
    
    /**
     * Gets the value of the parameter if the value was hard coded in the TDF.
     * @return the value of the parameter.
     */
    public String getValue();
    
    public Collection getEnumValues();
}
