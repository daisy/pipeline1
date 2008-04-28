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
package org.daisy.util.xml.xslt;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogURIResolver;
import org.xml.sax.SAXException;

/**
 * A cache of precompiled stylesheets.
 * @author Linus Ericson
 */
public class TransformerCache {

    private Map<URI,Templates> cache = new HashMap<URI,Templates>();

    /**
     * Get a compiled stylesheet. If the stylesheet at the specified location already exists
     * in the cache, the precompiled version will be used. Otherwise a stylesheet will be
     * compiled.
     * @param fileOrFileUri filename or file URI to the stylesheet
     * @param factory the factory to use for the stylesheet compilation
     * @param errorListener an ErrorListener
     * @return a compiled stylesheet
     * @throws XSLTException
     */
    public Transformer get(String fileOrFileUri, String factory, ErrorListener errorListener) throws XSLTException {
        Templates entry = cache.get(FilenameOrFileURI.toURI(fileOrFileUri));
        try {
            if (entry == null) {
                // Create factory
                String property = "javax.xml.transform.TransformerFactory";
                String oldFactory = System.getProperty(property);
                if (factory != null) {
                    System.setProperty(property, factory);
                }
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                try {
    				transformerFactory.setAttribute("http://saxon.sf.net/feature/version-warning", Boolean.FALSE);
    			} catch (IllegalArgumentException iae) {
    				
    			}
    			
                if (errorListener != null) {
                    transformerFactory.setErrorListener(errorListener);
                }
                
                // Reset old factory property
                System.setProperty(property, (oldFactory==null?"":oldFactory));         
    
                // Create transformer template
                Source xslt = Stylesheet.file2source(FilenameOrFileURI.toFile(fileOrFileUri), CatalogEntityResolver.getInstance());
                entry = transformerFactory.newTemplates(xslt);
                cache.put(FilenameOrFileURI.toURI(fileOrFileUri), entry);
            }
            Transformer res = entry.newTransformer();
            res.setURIResolver(new CatalogURIResolver());
            return res;
        } catch (TransformerConfigurationException e) {
            throw new XSLTException(e.getMessage(), e);
        } catch (ParserConfigurationException e) {
            throw new XSLTException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new XSLTException(e.getMessage(), e);
        }        
    }
    
    /**
     * Get a compiled stylesheet. If the stylesheet at the specified location already exists
     * in the cache, the precompiled version will be used. Otherwise a stylesheet will be
     * compiled.
     * @param fileOrFileUri filename or file URI to the stylesheet
     * @param factory the factory to use for the stylesheet compilation
     * @return a compiled stylesheet
     * @throws XSLTException
     */
    public Transformer get(String fileOrFileUri, String factory) throws XSLTException {
        return get(fileOrFileUri, factory, null);
    }
    
}
