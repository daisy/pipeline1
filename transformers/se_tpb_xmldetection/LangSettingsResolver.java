/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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
package se_tpb_xmldetection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

import org.daisy.pipeline.core.DirClassLoader;
import org.daisy.util.xml.catalog.CatalogExceptionEntityNotSupported;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.catalog.CatalogFile;
import org.xml.sax.SAXException;

/**
 * Resolves the paths to language settings files.
 * @author Linus Ericson
 */
/*package*/ class LangSettingsResolver {

    private static CatalogFile catalog = null;
    private static LangSettingsResolver instance = null;
    
    /**
     * Private constructor since this is a singleton.
     * @throws CatalogExceptionNotRecoverable
     */
    private LangSettingsResolver() throws CatalogExceptionNotRecoverable {
        try {
            ClassLoader cl = this.getClass().getClassLoader();
            if (cl instanceof DirClassLoader) {
                DirClassLoader dcl = (DirClassLoader)cl;
                //System.err.println("dcl");
                cl = new DirClassLoader(dcl.getClassDir(), dcl.getResourceDir().getParentFile());
            } else {
                //System.err.println("cl");
            }
//            Class cls = Class.forName(this.getClass().getPackage().getName() + ".DummyClass", true, cl);
//            URL catalogURL = cls.getResource("lang.xml");
//            catalog = new CatalogFile(catalogURL, cls);            
            //mg20070411:
            URL catalogURL = this.getClass().getResource("lang.xml"); 
            catalog = new CatalogFile(catalogURL, this.getClass());
            
                        
        } catch (URISyntaxException use) {
            throw new CatalogExceptionNotRecoverable(use);
        } catch (IOException ioe) {
        	throw new CatalogExceptionNotRecoverable(ioe);
		} catch (SAXException se) {
        	throw new CatalogExceptionNotRecoverable(se);
//		} catch (ClassNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
        }
    }
    
    /**
     * Method for fetching the singleton instance
     * @return a language settings resolver
     * @throws CatalogExceptionNotRecoverable
     */
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
    
    /**
     * Resolves the given locale to a language settings URL
     * @param locale the locale
     * @return a URL to a language settings file
     */
    public URL resolve(Locale locale) {
        try {
            return catalog.getEntityLocalURL(locale.toString());    
        } catch (CatalogExceptionEntityNotSupported ceens) {
        	return null;
        }
    }
    
    /**
     * Resolves the given locale to a language settings URL
     * @param locale the locale
     * @return a URL to a language settings file
     */
    public URL resolve(String locale) {
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
