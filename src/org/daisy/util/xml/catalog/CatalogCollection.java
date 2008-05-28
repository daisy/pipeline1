/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
package org.daisy.util.xml.catalog;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.xml.sax.InputSource;

/**
 * Represents a collection of catalogs (Composite pattern).
 * @author Linus Ericson
 */
public class CatalogCollection implements Catalog {

    private Collection<Catalog> catalogs = null;
    
    /**
     * Creates an empty collection of catalogs.
     */
    public CatalogCollection() {
        catalogs = new ArrayList<Catalog>();
    }
    
    /**
     * Creates a new collection of catalogs with an initial item.
     * @param catalog the initial catalog item.
     */
    public CatalogCollection(Catalog catalog) {
        this();
        addCatalog(catalog);
    }
    
    /**
     * Adds a catalog to the collection.
     * @param catalog the catalog to add
     */
    public void addCatalog(Catalog catalog) {
        catalogs.add(catalog);
    }
    
    /*
     * (non-Javadoc)
     * @see org.daisy.util.xml.catalog.Catalog#getEntityLocalURL(java.lang.String)
     */
    public URL getEntityLocalURL(String id) throws CatalogExceptionEntityNotSupported {
        for (Catalog catalog : catalogs) {
            try {
                URL url = catalog.getEntityLocalURL(id);
                return url;
            } catch (CatalogExceptionEntityNotSupported e) {                
            }            
        }
        throw new CatalogExceptionEntityNotSupported("No support in catalog for public id: " + id);
    }

    /*
     * (non-Javadoc)
     * @see org.daisy.util.xml.catalog.Catalog#getPublicIdEntity(java.lang.String)
     */
    public InputSource getPublicIdEntity(String publicId) throws IOException, CatalogExceptionEntityNotSupported {
        for (Catalog catalog : catalogs) {
            try {
                InputSource is = catalog.getPublicIdEntity(publicId);
                return is;
            } catch (CatalogExceptionEntityNotSupported e) {                
            }            
        }
        throw new CatalogExceptionEntityNotSupported("No support in catalog for public id: " + publicId);
    }

    /*
     * (non-Javadoc)
     * @see org.daisy.util.xml.catalog.Catalog#getSize()
     */
    public int getSize() {
        int size = 0;
        for (Catalog catalog : catalogs) {
            size += catalog.getSize();
        }
        return size;
    }

    /*
     * (non-Javadoc)
     * @see org.daisy.util.xml.catalog.Catalog#getSystemIdEntity(java.lang.String)
     */
    public InputSource getSystemIdEntity(String systemId) throws IOException, CatalogExceptionEntityNotSupported {
        for (Catalog catalog : catalogs) {
            try {
                InputSource is = catalog.getSystemIdEntity(systemId);
                return is;
            } catch (CatalogExceptionEntityNotSupported e) {                
            }            
        }
        throw new CatalogExceptionEntityNotSupported("No support in catalog for system id: " + systemId);
    }

    /*
     * (non-Javadoc)
     * @see org.daisy.util.xml.catalog.Catalog#getSystemIdEntityFromSuffix(java.lang.String)
     */
    public InputSource getSystemIdEntityFromSuffix(String filename) throws IOException, CatalogExceptionEntityNotSupported {
        for (Catalog catalog : catalogs) {
            InputSource is = catalog.getSystemIdEntityFromSuffix(filename);
            if (is != null) {
                return is;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.daisy.util.xml.catalog.Catalog#supportsPublicId(java.lang.String)
     */
    public boolean supportsPublicId(String publicId) {
        for (Catalog catalog : catalogs) {
            if (catalog.supportsPublicId(publicId)) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.daisy.util.xml.catalog.Catalog#supportsSystemId(java.lang.String)
     */
    public boolean supportsSystemId(String systemId) {
        for (Catalog catalog : catalogs) {
            if (catalog.supportsSystemId(systemId)) {
                return true;
            }
        }
        return false;
    }

}
