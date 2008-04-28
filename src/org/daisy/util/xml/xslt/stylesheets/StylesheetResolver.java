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
package org.daisy.util.xml.xslt.stylesheets;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.daisy.util.xml.catalog.CatalogExceptionEntityNotSupported;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.catalog.CatalogFile;
import org.xml.sax.SAXException;

/**
 * A resolver for XSLT stylesheets.
 * @author Markus Gylling
 */

/*package*/ class StylesheetResolver {

    private static CatalogFile catalog = null;
    private static StylesheetResolver instance = null;
    
    private StylesheetResolver() throws CatalogExceptionNotRecoverable {
        try {
            URL catalogURL = this.getClass().getResource("catalog.xml"); 
            catalog = new CatalogFile(catalogURL, this.getClass());                                    
        } catch (URISyntaxException use) {
            throw new CatalogExceptionNotRecoverable(use);
        } catch (IOException ioe) {
        	throw new CatalogExceptionNotRecoverable(ioe);
		} catch (SAXException se) {
        	throw new CatalogExceptionNotRecoverable(se);
        }
    }
    
    /*package*/ static StylesheetResolver getInstance() throws CatalogExceptionNotRecoverable {
        if (instance == null) {
            synchronized(StylesheetResolver.class){
                if (instance == null) {
                    instance = new StylesheetResolver();
                }
            }
        }
        return instance;
    }
    
    
    /*package*/ URL resolve(String identity) {
        try {
            return catalog.getEntityLocalURL(identity);    
        } catch (CatalogExceptionEntityNotSupported ceens) {
        	return null;
        }
    }
    
    /** 
     * Overrides and refuses clone since this is a singleton
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException();     
    }
    
}
