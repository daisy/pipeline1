/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package se_tpb_syncPointNormalizer;

import java.util.Iterator;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.events.XMLEvent;

/*package*/ class AttributedContext {
    
    protected Stack<ContextInfo> context = new Stack<ContextInfo>();
      
    public boolean register(XMLEvent event) {
        if (event.isStartElement()) {            
            context.push(new ContextInfo(event.asStartElement().getName()));
        } else if (event.isEndElement()) {
            context.pop();
        }
        return true;
    }
    
    public void setHasMustElement() {
        for (int i = 0; i < context.size() - 1; ++i) {
            (context.elementAt(i)).hasMustElement = true;
        }
    }
    
    public void setHasWantElement() {
        for (int i = 0; i < context.size() - 1; ++i) {
            (context.elementAt(i)).hasWantElement = true;
        }
    }
    
    public ContextInfo peek() {
        return context.peek();
    }
    
    public boolean isEmpty() {
        return context.empty();
    }
    
    public boolean hasParent() {
        return context.size() > 1;
    }
    
    public ContextInfo getParent() {
        return context.elementAt(context.size() - 2);
    }
    
    public String getContextPath() {
        StringBuffer buffer = new StringBuffer();
        for (Iterator<ContextInfo> it = context.iterator(); it.hasNext(); ) {
            QName name = (it.next()).name;
            buffer.append("/").append(name.getLocalPart());
        }
        if (context.isEmpty()) {
            buffer.append("/");
        }
        return buffer.toString();
    }    
}
