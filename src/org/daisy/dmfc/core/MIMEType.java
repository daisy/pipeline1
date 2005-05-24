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
package org.daisy.dmfc.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * An entry in the MIME registry.
 * @author Linus Ericson
 */
public class MIMEType {
    private String id;
    private String name;
    private Set parents = new HashSet();
    
    /**
     * Creates a new <code>MIMEType</code>.
     * Each <code>MIMEType</code> can have a set of parent types.
     * @param typeId 
     * @param typeName
     */
    public MIMEType(String typeId, String typeName) {
        id = typeId;
        name = typeName;
    }
    
    /**
     * Adds a parent to this MIME type.
     * @param parent the MIME type to add as a parent of this type.
     */
    public void addParent(MIMEType parent) {        
        parents.add(parent);
    }
    
    /**
     * Checks if two MIME types match each other. For example:
     * <pre>boolean b = mimeTypeA.matches(mimeTypeB);</pre>
     * For the example above to return true, <code>typeA</code> or one of
     * its ancestors must be equal to <code>typeB</code>. 
     * @param possibleMatch a MIME type to match against
     * @return <code>true</code> if this MIME type or one of its ancestors
     * is equal to <code>a_possibleMatch</code>, <code>false</code> otherwise.
     */
    public boolean matches(MIMEType possibleMatch) {
        if (possibleMatch.equals(this)) {
            return true;
        }
        for (Iterator it = parents.iterator(); it.hasNext(); ) {
            MIMEType mt = (MIMEType)it.next();
            if (mt.matches(possibleMatch)) {
                return true;
            }
        }
        return false;
    }
    
    public String toString() {
        return name;
    }
}
