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

/**
 * @author Linus Ericson
 */
/*package*/ class Item {
    private String key;
    private String value;
    private String endSent;    
    private String id;
    private String lang;
    private int priority = -1;
    
    public Item(String key, String value, String mayEndSentence) {
        this.key = key;
        this.value = value;
        endSent = mayEndSentence;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getEndSentence() {
        return endSent;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getKey() {
        return key;
    }
    
    public String toString() {
        return value;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getLang() {
        return lang;
    }
    
    public void setLang(String lang) {
        this.lang = lang;
    }
    
    
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
