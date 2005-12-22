/*
 * org.daisy.util - The DAISY java utility library
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
package org.daisy.util.xml.settings;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.daisy.util.xml.catalog.CatalogExceptionEntityNotSupported;
import org.daisy.util.xml.catalog.CatalogFile;
import org.xml.sax.SAXException;

/**
 * A class for resolving URLs to configuration files based on doctypes or
 * root element namespaces. 
 * @author Linus Ericson
 */
public class SettingsResolver {

    private static Map resolverInstances = new HashMap();
    protected final static Pattern dtdPattern = Pattern.compile("<!DOCTYPE\\s+\\w+(\\s+((SYSTEM\\s+(\"[^\"]*\"|'[^']*')|PUBLIC\\s+(\"[^\"]*\"|'[^']*')\\s+(\"[^\"]*\"|'[^']*'))))?\\s*(\\[.*\\]\\s*)?>");
    
    private CatalogFile catalog = null;    
    
    private SettingsResolver(String resourceName, Class cls) throws SettingsResolverException {
        //System.err.println("SettingsResolver - cls:" + cls.getName() + ", loader:" + cls.getClassLoader());
        URL catalogURL = cls.getResource(resourceName);
        if (catalogURL == null) {
            catalogURL = cls.getClassLoader().getResource(resourceName);
        }
        //System.err.println("SettingsResolver: URL " + catalogURL + " found (" + cls.getName() + ").");
        try {
            catalog = new CatalogFile(catalogURL, cls);
        } catch (URISyntaxException e) {
            throw new SettingsResolverException(e.getMessage(), e);
        } catch (IOException e) {            
            throw new SettingsResolverException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new SettingsResolverException(e.getMessage(), e);
        }
    }
        
    /**
     * Gets a <code>SettingsResolver</code> instance. The <code>resourceName</code>
     * is a configuration file catalog using the Oasis 1.1 catalog specification
     * subset syntax supported by the {@link org.daisy.util.xml.catalog.CatalogFile}
     * class.
     * @param resourceName catalog resource name
     * @param cls the class to use for resource resolution
     * @return a <code>SettingsResolver</code> instance
     * @throws SettingsResolverException
     */
    public static SettingsResolver getInstance(String resourceName, Class cls) throws SettingsResolverException {
        String key = cls.getName() + resourceName;
        SettingsResolver resolver = (SettingsResolver)resolverInstances.get(key);
        if (resolver == null) {
            synchronized(SettingsResolver.class) {
                if (resolver == null) {
                    resolver = new SettingsResolver(resourceName, cls);
                    resolverInstances.put(key, resolver);
                }
            }
        }
        return resolver;
    }

    /**
     * Resolve a config file based on the namespace URI.
     * @param namespaceURI the namespace URI
     * @return an URL to a configuration file, or <code>null</code> if no match was found in the catalog
     */
    public URL resolve(String namespaceURI) {
        try {
            return catalog.getEntityLocalURL(namespaceURI);
        } catch (CatalogExceptionEntityNotSupported e) {
            return null;
        } 
    }
    
    /**
     * Resolve a configuration file based on PUBLIC and SYSTEM identifiers.
     * @param publicId the PUBLIC identifier
     * @param systemId the SYSTEM identifier
     * @return an URL to a configuration file, or <code>null</code> if no match was found in the catalog
     */
    public URL resolve(String publicId, String systemId) {
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
    
    /**
     * Parses a DOCTYPE declaration and resolves a configuration file.
     * @param doctype a DOCTYPE declaration string
     * @return an URL to a configuration file
     * @throws UnsupportedDocumentTypeException if no match was found in the catalog
     */
    public URL parseDoctype(String doctype) throws UnsupportedDocumentTypeException {
        Matcher matcher = dtdPattern.matcher(doctype);
        String pub = "";
        String sys = "";
        if (matcher.matches()) {
            if (matcher.group(3).startsWith("PUBLIC")) {
                pub = matcher.group(5);
                sys = matcher.group(6);
                pub = pub.substring(1, pub.length() - 1);
                sys = sys.substring(1, sys.length() - 1);
                return this.resolve(pub, sys);                
            } 
            sys = matcher.group(4);                        
            sys = sys.substring(1, sys.length() - 1);
            return this.resolve(null, sys);            
        } 
        throw new UnsupportedDocumentTypeException("Cannot parse doctype declaration", pub, sys);        
    }
    
}
