/*
 * Created on 2006-feb-13
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
