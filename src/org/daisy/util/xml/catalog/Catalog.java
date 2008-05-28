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

import org.xml.sax.InputSource;

/**
 * Interface for catalog implementations.
 * @author Linus Ericson
 */
public interface Catalog {

    /**
     * @param publicId
     *            DTD PUBLIC identifier string
     * @return true if there is a match for this identifier in the catalog,
     *         false otherwise
     */
    public boolean supportsPublicId(String publicId);

    /**
     * @param systemId
     *            DTD SYSTEM identifier string
     * @return true if there is a match for this identifier in the catalog,
     *         false otherwise
     */
    public boolean supportsSystemId(String systemId);

    /**
     * @param publicId
     *            DTD PUBLIC identifier string
     * @return a local input source if entity match exists in catalog
     * @throws IOException
     * @throws CatalogExceptionEntityNotSupported
     * @throws CatalogExceptionEntityNotSupported
     *             if no entity match exists in catalog
     */
    public InputSource getPublicIdEntity(String publicId) throws IOException,
            CatalogExceptionEntityNotSupported;

    /**
     * @param systemId
     *            DTD SYSTEM identifier string
     * @return a local input source if entity match exists in catalog
     * @throws IOException
     * @throws CatalogExceptionEntityNotSupported
     *             if no entity match exists in catalog
     */
    public InputSource getSystemIdEntity(String systemId) throws IOException,
            CatalogExceptionEntityNotSupported;

    public InputSource getSystemIdEntityFromSuffix(String filename)
            throws IOException, CatalogExceptionEntityNotSupported;

    /**
     * Gets the local URL of a PUBLIC or SYSTEM identifier.
     * @param id the DTD PUBLIC or SYSTEM identifier
     * @return a URL to a local resource if the entity exists in the catalog
     * @throws CatalogExceptionEntityNotSupported if no entity match exists in catalog
     */
    public URL getEntityLocalURL(String id)
            throws CatalogExceptionEntityNotSupported;

    /**
     * Gets the number of entries in the catalog.
     * @return the number of entries in the catalog.
     */
    public int getSize();

}