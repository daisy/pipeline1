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
package org.daisy.util.xml.stax;

import java.util.Iterator;
import java.util.Locale;
import java.util.Stack;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * A class that keeps track of a context path in an XML document.
 * <p>For every event that you read from your EventReader, add the
 * event to an instance of this class. The function
 * <code>getContextPath()</code> will return where the EventReader
 * is located in the XML document, e.g. 
 * <code>/dtbook/book/bodymatter/level1</code>.</p>
 * <p>This class also keeps track of the current language, as specified by
 * the <code>xml:lang</code> attributes. Just ask for 
 * <code>getCurrentLocale()</code>.</p>
 * @author Linus Ericson
 */
public class ContextStack {

    protected class ContextInfo {
        public ContextInfo(QName n, String xmlLang) {
            name = n;
            if (xmlLang != null) {
                locale = new Locale(xmlLang);
            } else {
                locale = null;
            }
        }
        public QName name;
        public Locale locale;
    }
    
    protected Stack context = new Stack();
    
    /* *** EVENT FILTER INTERFACE *** */
    
    public boolean addEvent(XMLEvent event) {
        if (event.isStartElement()) {
            String xmlLang = null;
            StartElement se = event.asStartElement();
            Attribute attr = se.getAttributeByName(new QName(XMLConstants.XML_NS_URI, "lang", XMLConstants.XML_NS_PREFIX));
            if (attr != null) {
                xmlLang = attr.getValue();
            }
            context.push(new ContextInfo(se.getName(), xmlLang));
        } else if (event.isEndElement()) {
            context.pop();
        }
        return true;
    }


    /* *** OTHER METHODS *** */
    
    public Stack getContext() {
        Stack result = new Stack();
        for (Iterator it = context.iterator(); it.hasNext(); ) {
            ContextInfo ci = (ContextInfo)it.next();
            result.push(ci.name);
        }
        return result;
    }
    
    public Stack getParentContext() {
        Stack result = new Stack();
        for (Iterator it = context.iterator(); it.hasNext(); ) {
            ContextInfo ci = (ContextInfo)it.next();
            result.push(ci.name);
        }      
        result.pop();
        return result;
    }
    
    public String getContextPath(Stack list) {
        StringBuffer buffer = new StringBuffer();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            QName name = (QName)it.next();
            buffer.append("/").append(name.getLocalPart());
        }
        if (list.isEmpty()) {
            buffer.append("/");
        }
        return buffer.toString();
    }
    
    public String getContextPath() {
        return getContextPath(getContext());
    }
    
    public Locale getCurrentLocale() {
        for (int i = context.size() - 1; i >= 0; --i) {
            ContextInfo info = (ContextInfo)context.elementAt(i);
            if (info.locale != null) {
                return info.locale;
            }
        }
        return null;
    }
        
}
