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

import javax.xml.namespace.QName;

/**
 * @author linus
 */
class ContextInfo {
    public ContextInfo(QName n) {
        name = n;
    }
    public QName name;
    
    public boolean hasElements = false;
    public boolean hasText = false;
    
    public boolean hasMustElement = false;
    public boolean hasWantElement = false;
    
    public boolean childOfWantElement = false;
    
    public boolean spanOpen = false;
    public int number = 0;
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("ContextInfo ").append(name.getLocalPart()).append(" (").append(number);
        buf.append(") hasElem:").append(hasElements).append(" hasText:").append(hasText);
        buf.append(" hasMust:").append(hasMustElement).append(" hasWant:").append(hasWantElement);
        return buf.toString();
    }
}
