package org.daisy.util.xml.catalog;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>Threadsafe singleton implementation of a catalog entityresolver.</p>
 * <p>Focus on ease of instantiation; no runtime params (such as catalog urls) needed, one exception type to catch.</p>
 * <p>Focus on high speed: after first request, entitites are stored in a hashtable, meaning no disk access for subsequent requests of the same entity.</p>
 * <p>The catalog file (<code>classdir/catalog.xml</code>) used supports a subset of the OASIS Catalog 1.1 specification.</p>
 * <p>Entities not supported by the catalog are stored in a HashSet retrievable via {@link #getEntityNotSupportedExceptions()}
 * <p>
 * Use example within a SAX context:</p>
 * <code><pre>
 * import org.daisy.util.xml.catalog.CatalogEntityResolver;
 * import org.daisy.util.xml.catalog.CatalogException;
 * [...]
 * 
 * public InputSource resolveEntity(String arg0, String arg1) throws IOException {                        
 *   try {
 *     return CatalogEntityResolver.getInstance().resolveEntity(arg0, arg1);
 *   } catch (CatalogException ce) {
 *     //do something 
 *   }
 * }
 * [...]
 * </pre></code>
 * 
 * <p>The CatalogException is subclassed in recoverable and irrecoverable categories, meaning that if desired the exception catching can be extended using for example</p>
 *  
 * <code><pre>
 *   } catch (CatalogException ce) {
 *     if (ce instanceof CatalogExceptionRecoverable) {
 *       System.err.println(ce.getMessage());                      
 *     }else if (ce instanceof CatalogExceptionNotRecoverable) {
 *       throw new IOException(ce.getMessage());
 *     }
 *   }
 * </pre></code>
 * 
 * <p>Use example within a (JAXP) DOM context:</p>
 * <code><pre>
 *  [...]
 *  builder = factory.newDocumentBuilder();
 *  builder.setEntityResolver(CatalogEntityResolver.getInstance());  
 *  Document doc = builder.parse("E:/file.xml");
 *  [...]
 * </pre></code>
 * 
 * @author markusg
 */

public class CatalogEntityResolver implements EntityResolver {        
    private static CatalogFile catalog; 
    private HashSet EntityNotSupportedExceptions = new HashSet(); //<String>
    
    private CatalogEntityResolver() throws CatalogExceptionNotRecoverable {   
        URL catalogURL = this.getClass().getResource("./catalog.xml");                        
        try {
            catalog = new CatalogFile(catalogURL);
        } catch (URISyntaxException use) {
            throw new CatalogExceptionNotRecoverable(use);
        } catch (IOException ioe) {
        	throw new CatalogExceptionNotRecoverable(ioe);
		} catch (SAXException se) {
        	throw new CatalogExceptionNotRecoverable(se);
		}                                 
    }
    
    static private CatalogEntityResolver _instance = null;    
    /**
     * Singleton retrieval
     * @return The catalog entity resolver instance
     */
    static public CatalogEntityResolver getInstance() throws CatalogExceptionNotRecoverable {
        if (null == _instance) {
            synchronized(CatalogEntityResolver.class){
                if (null == _instance) {
                    _instance = new CatalogEntityResolver();
                }
            }
        }
        return _instance;
    }
    
    /** 
     * Catalog aware implementation of the SAX resolveEntity method 
     * The public id is always preferred, ie the value of the OASIS Catalog 1.1 <code>prefer</code> attribute is hardcoded to <code>public</code>
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String) 
     */
    public InputSource resolveEntity(String publicId, String systemId) throws IOException {                
        if(publicId!=null) {
            try {
                return catalog.getPublicIdEntity(publicId);    
            } catch (CatalogExceptionEntityNotSupported ceens) {
                //no match in catalog for inparam public id
            	//try systemId before giving up
            }
        }          
        try {
            return catalog.getSystemIdEntity(systemId);    
        } catch (CatalogExceptionEntityNotSupported ceens) {
        	//no match in catalog for inparam system id either
        	//try to match on filename alone (suffix)        	
        	try {				
				String filename = new File(new URI(systemId)).getName();
				try {
					return catalog.getSystemIdEntityFromSuffix(filename);
				} catch (CatalogExceptionEntityNotSupported ceens2) {
				    //no support for suffix (filename) either							 
				}								
			} catch (Exception e) {
			  //silence
			}
			EntityNotSupportedExceptions.add(publicId+"::"+systemId);
        	return null;
        }
    } 
    
    /**
     * returns the URL of the local resolved entity resource
     */
    public URL resolveEntityToURL(String publicId, String systemId) throws IOException {
        if(publicId!=null) {
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
        	EntityNotSupportedExceptions.add(publicId+"::"+systemId);
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
     * @return the number of supported entities in the catalog instance
     */
    public int getCatalogEntrySize() {
    	return catalog.getSize();
    }
	/**
	 * @return Returns the HashSet<String> collection with PIDs and SIDs not supported by catalog
	 * Since this is a synchronized singleton, there is no way of knowing which process/thread caused the entries in this collection
	 */
	public HashSet getEntityNotSupportedExceptions() {
		return EntityNotSupportedExceptions;
	}
}