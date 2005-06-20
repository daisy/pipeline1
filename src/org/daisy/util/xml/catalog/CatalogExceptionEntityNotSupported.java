package org.daisy.util.xml.catalog;

/**
 * Thrown when an entity call is made on an entity not represented in catalog
 * @author markusg
 */
public class CatalogExceptionEntityNotSupported extends CatalogExceptionRecoverable {

    public CatalogExceptionEntityNotSupported(Exception e) {
        super(e);
    }

    public CatalogExceptionEntityNotSupported(String message) {
        super("Entity not supported in catalog. " + message);    
    }

    public CatalogExceptionEntityNotSupported(String message, Exception e) {
        super("Entity not supported in catalog. " + message, e);    
    }
}
