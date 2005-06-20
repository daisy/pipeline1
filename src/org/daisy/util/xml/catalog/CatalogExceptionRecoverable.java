package org.daisy.util.xml.catalog;

/**
 * Thrown when an exception occurs that means that the catalog implementation may still be usable
 * @author markusg
 *
 */
public class CatalogExceptionRecoverable extends CatalogException {

    public CatalogExceptionRecoverable(Exception e) {
        super(e);
    }

    public CatalogExceptionRecoverable(String message) {
        super(message);
    }

    public CatalogExceptionRecoverable(String message, Exception e) {
        super(message, e);
    }
}
