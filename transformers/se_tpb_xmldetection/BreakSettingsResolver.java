/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package se_tpb_xmldetection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.daisy.util.xml.catalog.CatalogExceptionEntityNotSupported;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.catalog.CatalogFile;
import org.xml.sax.SAXException;

/**
 * @author Linus Ericson
 */
/*package*/ class BreakSettingsResolver {

    private static CatalogFile catalog = null;
    private static BreakSettingsResolver instance = null;
    
    private BreakSettingsResolver() throws CatalogExceptionNotRecoverable {
        try {
            Class cls = Class.forName(this.getClass().getPackage().getName() + ".DummyClass", true, ClassLoader.getSystemClassLoader());
            URL catalogURL = cls.getResource("type.xml");
            catalog = new CatalogFile(catalogURL, cls);
        } catch (URISyntaxException use) {
            throw new CatalogExceptionNotRecoverable(use);
        } catch (IOException ioe) {
        	throw new CatalogExceptionNotRecoverable(ioe);
		} catch (SAXException se) {
        	throw new CatalogExceptionNotRecoverable(se);
		} catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static BreakSettingsResolver getInstance() throws CatalogExceptionNotRecoverable {
        if (instance == null) {
            synchronized(BreakSettingsResolver.class){
                if (instance == null) {
                    instance = new BreakSettingsResolver();
                }
            }
        }
        return instance;
    }
    
    /**
     * returns the URL of the local resolved entity resource
     */
    public URL resolve(String publicId, String systemId) throws IOException {
        if(publicId != null) {
            try {
                return catalog.getEntityLocalURL(publicId);    
            } catch (CatalogExceptionEntityNotSupported ceens) {
                //there was no match in catalog for inparam public id
            	//try systemId before giving up
            }
        }          
        try {
            return catalog.getEntityLocalURL(systemId);    
        } catch (CatalogExceptionEntityNotSupported ceens) {
            //there was no match in catalog for inparam system id either
        	return null;
        }
    	
    }
    
    /** 
     * Overrides and refuses clone since this is a singleton
     * @see java.lang.Object#clone()
     */
    public Object clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException();     
    }
    
}
