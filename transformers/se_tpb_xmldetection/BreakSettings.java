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
package se_tpb_xmldetection;

import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

/**
 * @author Linus Ericson
 */
public interface BreakSettings {

    public boolean setup(String publicId, String systemId) throws UnsupportedDocumentTypeException;
    
    public boolean setup(String namespaceURI);
    
    public boolean isSentenceBreaking(QName elementName);
    
    public boolean skipContent(QName elementName);
        
    public boolean mayContainText(QName tagName);
    
    public QName getBreakElement();
    
    public Map getBreakAttributes();
    
    public Set getDefaultPaths();
    
    public QName getAbbrElement();
    
    public QName getAcronymElement();
    
    public QName getInitialismElement();
    
    public QName getFixElement();
    
    public Map getAbbrAttributes();
    
    public Map getAcronymAttributes();
    
    public Map getInitialismAttributes();
    
    public Map getFixAttributes();
    
    public String getInitialismExpandAttribute();
    
    public String getAcronymExpandAttribute();
    
    public String getAbbrExpandAttribute();
    
    public String getFixExpandAttribute();
        
}
