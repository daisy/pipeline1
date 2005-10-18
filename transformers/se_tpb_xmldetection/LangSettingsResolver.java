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
import java.util.Locale;

import org.daisy.util.xml.catalog.CatalogExceptionEntityNotSupported;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.catalog.CatalogFile;
import org.xml.sax.SAXException;

/**
 * @author Linus Ericson
 */
/*package*/ class LangSettingsResolver {

    private static CatalogFile catalog = null;
    private static LangSettingsResolver instance = null;
    
    private LangSettingsResolver() throws CatalogExceptionNotRecoverable {
        try {
            Class cls = Class.forName(this.getClass().getPackage().getName() + ".DummyClass", true, ClassLoader.getSystemClassLoader());
            URL catalogURL = cls.getResource("lang.xml");
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
    
    public static LangSettingsResolver getInstance() throws CatalogExceptionNotRecoverable {
        if (instance == null) {
            synchronized(LangSettingsResolver.class){
                if (instance == null) {
                    instance = new LangSettingsResolver();
                }
            }
        }
        return instance;
    }
    
    public URL resolve(Locale locale) throws IOException {
        try {
            return catalog.getEntityLocalURL(locale.toString());    
        } catch (CatalogExceptionEntityNotSupported ceens) {
        	return null;
        }
    }
    
    public URL resolve(String locale) throws IOException {
        try {
            return catalog.getEntityLocalURL(locale);    
        } catch (CatalogExceptionEntityNotSupported ceens) {
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
