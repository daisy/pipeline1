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
 * @author Linus Ericson
 */
public class MIMEType {
    private String id;
    private String name;
    private Set parents = new HashSet();
    
    public MIMEType(String a_id, String a_name) {
        id = a_id;
        name = a_name;
    }
    
    public void addParent(MIMEType a_parent) {        
        parents.add(a_parent);
    }
    
    public boolean matches(MIMEType a_possibleMatch) {
        if (a_possibleMatch.equals(this)) {
            return true;
        }
        for (Iterator _iter = parents.iterator(); _iter.hasNext(); ) {
            MIMEType _mt = (MIMEType)_iter.next();
            if (_mt.matches(a_possibleMatch)) {
                return true;
            }
        }
        return false;
    }
    
    public String toString() {
        return name;
    }
}
