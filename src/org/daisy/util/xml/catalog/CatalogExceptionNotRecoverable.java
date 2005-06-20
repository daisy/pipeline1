package org.daisy.util.xml.catalog;
/**
 * Thrown when an exception occurs that means that the catalog implementation will not be usable
 * @author markusg
 */

public class CatalogExceptionNotRecoverable extends CatalogException {

    public CatalogExceptionNotRecoverable(Exception e) {
        super(e);
    }

    public CatalogExceptionNotRecoverable(String message) {
        super(message);
    }

    public CatalogExceptionNotRecoverable(String message, Exception e) {
        super(message, e);
    }
}