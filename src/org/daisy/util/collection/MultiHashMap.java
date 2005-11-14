/*
 * org.daisy.util - The DAISY java utility library
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
package org.daisy.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A HashMap where every key points to a collection of values.
 * @author Linus Ericson
 */
public class MultiHashMap implements Map {
    
    private Map map = new HashMap();
    private boolean dupes;
    
    /**
     * Creates a new MultiHashMap. If <code>allowDuplicates</code> is true,
     * each key may point to several object with the same value. 
     * @param allowDuplicates true if duplicate values for each key is allowed, false otherwise.
     */
    public MultiHashMap(boolean allowDuplicates) {
        dupes = allowDuplicates;
    }
    
    /**
     * Creates a shallow copy of the specified MultiHashMap.
     * The data structures, but not the values in the
     * data structures of the MultiHashMap are copied.
     * @param other the MultiHashMap to copy.
     */
    public MultiHashMap(MultiHashMap other) {
        MultiHashMap copy = (MultiHashMap)other.clone();
        map = copy.map;
        dupes = copy.dupes;
    }

    /* *** New methods *** */
    
    /**
     * Checks whether a specific key contains a specific value.
     * @param key the key
     * @param value the value
     * @return true if the specified key contains the specified value, false otherwise
     */
    public boolean containsValue(Object key, Object value) {
        Collection coll = (Collection)map.get(key);
        if (coll != null) {
            return coll.contains(value);
        }
        return false;
    }
    
    /**
     * Creates a mapping from the specifed key to all values in the specified collection.
     * @param key the key
     * @param collection the colleaction of values
     */
    public void putAll(Object key, Collection collection) {
        if (map.containsKey(key)) {
            Collection coll = (Collection)map.get(key);
            coll.addAll(collection);
        } else {
            Collection coll;
            if (dupes) {
                coll = new ArrayList();
            } else {
                coll = new HashSet();
            }
            coll.addAll(collection);
            map.put(key, coll);
        }
    }

    /**
     * Gets the collection of values from a specified key.
     * @param key the key
     * @return a collection of values, or null if there is no mapping for the key
     */
    public Collection getCollection(Object key) {
        return (Collection)this.get(key);
    }
    
    /* *** Methods from Map *** */
    
    /**
     * Gets the number of keys in the MultiHashMap.
     * @return the number of keys.
     */
    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /**
     * Checks whether a specific value exists for any of the keys.
     * @return true if the value exists for any key in the map.
     */
    public boolean containsValue(Object value) {
        for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            Collection coll = (Collection)map.get(key);
            if (coll.contains(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the collection for the specified key.
     * @param key the key
     * @return a Collection or null if the key did not exists.
     * @see #getCollection(Object)
     */
    public Object get(Object key) {
        return map.get(key);
    }    

    /**
     * Adds the specified value to the collection that the key maps to.
     * @param key the key
     * @param value the value
     */
    public Object put(Object key, Object value) {        
        if (map.containsKey(key)) {
            Collection coll = (Collection)map.get(key);
            coll.add(value);
        } else {
            Collection coll;
            if (dupes) {
                coll = new ArrayList();
            } else {
                coll = new HashSet();
            }
            coll.add(value);
            map.put(key, coll);
        }
        return null;
    }

    /**
     * Removes the specified key and its mappings from the map.
     */
    public Object remove(Object key) {
        return map.remove(key);
    }
   
    /**
     * Puts all values from the specified Map into this map.
     */
    public void putAll(Map othermap) {
        if (othermap instanceof MultiHashMap) {
            for (Iterator it = othermap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry)it.next();
                this.putAll(entry.getKey(), (Collection)entry.getValue());
            }            
        } else {
            for (Iterator it = othermap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry)it.next();
                this.put(entry.getKey(), entry.getValue());
            }            
        }
    }

    public void clear() {
        map.clear();
    }

    public Set keySet() {
        return map.keySet();
    }

    public Collection values() {
        Collection result = new ArrayList();
        for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            Collection coll = (Collection)map.get(key);
            result.addAll(coll);
        }
        return result;
    }

    public Set entrySet() {
        return map.entrySet();
    }
    
    /* *** Methods from Object *** */
    
    public Object clone() {
        MultiHashMap clone = new MultiHashMap(dupes);
        for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            clone.putAll(key, this.getCollection(key));
        }
        return clone;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer("[ ");
        for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            buffer.append(key).append("->{");
            buffer.append(map.get(key));
            buffer.append("}\n");
        }
        buffer.append("]");
        return buffer.toString();
    }
    
}
