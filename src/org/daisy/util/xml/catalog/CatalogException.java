package org.daisy.util.xml.catalog;

import org.xml.sax.SAXException;

/**
 * @author markusg
 *
 */
public abstract class CatalogException extends SAXException {
    
    CatalogException(Exception e) {
        super(e);
    }
    
    CatalogException(String message, Exception e ) {
        super(message, e);
    }  
    
    CatalogException(String message) {
        super(message);
    }   
}
