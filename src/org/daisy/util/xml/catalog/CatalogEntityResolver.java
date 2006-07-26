package org.daisy.util.xml.catalog;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * Threadsafe singleton implementation of a catalog entityresolver.
 * </p>
 * <p>
 * Focus on ease of instantiation; no runtime params (such as catalog urls)
 * needed, one exception type to catch.
 * </p>
 * <p>
 * Focus on high speed: after first request, entitites are stored in a
 * hashtable, meaning no disk access for subsequent requests of the same entity.
 * </p>
 * <p>
 * The catalog file (<code>classdir/catalog.xml</code>) used supports a
 * subset of the OASIS Catalog 1.1 specification.
 * </p>
 * <p>
 * Entities not supported by the catalog are stored in a HashSet retrievable via
 * {@link #getEntityNotSupportedExceptions()}
 * <p>
 * Use example within a SAX context:
 * </p>
 * <code><pre>
 *  import org.daisy.util.xml.catalog.CatalogEntityResolver;
 *  import org.daisy.util.xml.catalog.CatalogException;
 *  [...]
 *  
 *  public InputSource resolveEntity(String arg0, String arg1) throws IOException {                        
 *    try {
 *      return CatalogEntityResolver.getInstance().resolveEntity(arg0, arg1);
 *    } catch (CatalogException ce) {
 *      //do something 
 *    }
 *  }
 *  [...]
 * </pre></code>
 * 
 * <p>
 * The CatalogException is subclassed in recoverable and irrecoverable
 * categories, meaning that if desired the exception catching can be extended
 * using for example
 * </p>
 * 
 * <code><pre>
 *    } catch (CatalogException ce) {
 *      if (ce instanceof CatalogExceptionRecoverable) {
 *        System.err.println(ce.getMessage());                      
 *      }else if (ce instanceof CatalogExceptionNotRecoverable) {
 *        throw new IOException(ce.getMessage());
 *      }
 *    }
 * </pre></code>
 * 
 * <p>
 * Use example within a (JAXP) DOM context:
 * </p>
 * <code><pre>
 *   [...]
 *   builder = factory.newDocumentBuilder();
 *   builder.setEntityResolver(CatalogEntityResolver.getInstance());  
 *   Document doc = builder.parse(&quot;E:/file.xml&quot;);
 *   [...]
 * </pre></code>
 * 
 * @author markusg
 */

public class CatalogEntityResolver implements EntityResolver, LSResourceResolver, URIResolver {
    private static CatalogFile catalog;
    private EntityNotSupportedExceptions entityNotSupportedExceptions = null;
    static private CatalogEntityResolver _instance = null;
    //static private CatalogEntityResolver _instance = new CatalogEntityResolver();
    
    //private HashSet EntityNotSupportedExceptions = new HashSet(); // <String>

    private CatalogEntityResolver() throws CatalogExceptionNotRecoverable {
        URL catalogURL = this.getClass().getResource("catalog.xml");
        try {
            catalog = new CatalogFile(catalogURL, this.getClass());
            entityNotSupportedExceptions = new EntityNotSupportedExceptions();
        } catch (IOException ioe) {
            throw new CatalogExceptionNotRecoverable(ioe);
        } catch (SAXException se) {
            throw new CatalogExceptionNotRecoverable(se);
        } catch (URISyntaxException e) {
        	throw new CatalogExceptionNotRecoverable(e);
		}
    }

    

    /**
     * Singleton retrieval 
     * @return The catalog entity resolver instance
     */
    static public CatalogEntityResolver getInstance() throws CatalogExceptionNotRecoverable {
        if (null == _instance) {
            synchronized (CatalogEntityResolver.class) {
                if (null == _instance) {
                    _instance = new CatalogEntityResolver();
                }
            }
        }
        return _instance;
    }

//    static public void reload() throws CatalogExceptionNotRecoverable{
//    	_instance = new CatalogEntityResolver();
//    }
    
    /**
     * Catalog aware implementation of the SAX resolveEntity method.
     * <p>The public id is always preferred, ie the value of the OASIS Catalog 1.1</p>
     * <code>prefer</code> attribute is hardcoded to <code>public</code>
     * 
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
     */
    public InputSource resolveEntity(String publicId, String systemId) throws IOException {
    	//System.out.println("Catalog request: publicId: " + publicId + ":: systemId:" + systemId );
        if (publicId != null) {
            try {
                return catalog.getPublicIdEntity(publicId);
            } catch (CatalogExceptionEntityNotSupported ceens) {
                // no match in catalog for inparam public id
            	entityNotSupportedExceptions.add(publicId);
                // try systemId before giving up
            }
        }
        
        try {
            return catalog.getSystemIdEntity(systemId);
        } catch (CatalogExceptionEntityNotSupported ceens) {
            // no match in catalog for inparam system id either
        	entityNotSupportedExceptions.add(systemId);
            // try to match on filename alone (suffix)
            try {
                String filename = new File(new URI(systemId)).getName();
                try {
                    return catalog.getSystemIdEntityFromSuffix(filename);
                } catch (CatalogExceptionEntityNotSupported ceens2) {
                	entityNotSupportedExceptions.add(systemId+": suffix");
                }
            } catch (Exception e) {
                // silence
            }                                   
            return null;
        }
    }

    /**
     * @return the URL of the local resolved entity resource
     */
    public URL resolveEntityToURL(String publicId, String systemId) throws IOException {
        if (publicId != null) {
            try {
                return catalog.getEntityLocalURL(publicId);
            } catch (CatalogExceptionEntityNotSupported ceens) {
                // there was no match in catalog for inparam public id
            	entityNotSupportedExceptions.add(publicId);
                // try systemId before giving up
            }
        }
        try {
            return catalog.getEntityLocalURL(systemId);
        } catch (CatalogExceptionEntityNotSupported ceens) {
            // there was no match in catalog for inparam system id either
            entityNotSupportedExceptions.add(systemId);
            return null;
        }                        
    }

    /**
     * @return the URL of the local resolved entity resource
     */
    public URL resolveEntityToURL(String publicOrSystemId) throws IOException {

        try {
            return catalog.getEntityLocalURL(publicOrSystemId);
        } catch (CatalogExceptionEntityNotSupported ceens) {
        	entityNotSupportedExceptions.add(publicOrSystemId);
        }
        return null;
    }

    /**
     * LSResourceResolver impl (jaxp 1.3)
     */
	public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
		try {
			URL url = resolveEntityToURL(publicId,systemId);
			return (LSInput)url.openStream();
		} catch (IOException e) {
		
		}
		return null;
	}
    
    /**
     * Overrides and refuses clone since this is a singleton
     * 
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
     * @return Returns the HashSet<String> collection with PIDs and SIDs not
     *         supported by catalog. Since this is a synchronized singleton,
     *         there is no way of knowing which process/thread caused the
     *         entries in this collection
     */
    public HashSet getEntityNotSupportedExceptions() {
        return entityNotSupportedExceptions.getSet();
    }

    /**
     * URIResolver impl. Note: only use this
     * if you want to resolve URIs in schemas when
     * the URI destinations are in the Catalog. 
     * For all other use cases, use CatalogURIResolver.
     */
	public Source resolve(String href, String base) throws TransformerException {
		//href is a reference inside the schema
		//to a sub schema, for example ../relaxngcommon/attributes.rng
		try {
			URL url = this.resolveEntityToURL(href);
			if(url==null) {
				
			}
			StreamSource ss = new StreamSource(url.openStream());
			ss.setSystemId(url.toExternalForm());
			return ss;
		} catch (IOException e) {
			
		}
		
		return null;
	}

	class EntityNotSupportedExceptions {
		private HashSet exceptions = null; //String
		
		EntityNotSupportedExceptions(){
			exceptions = new HashSet(); 
		}
		
		void add(String describer) {
            if(System.getProperty("org.daisy.debug")!=null) {
            	System.err.println("DEBUG org.daisy.util.xml.catalag.CatalogEntityResolver: entity not supported: " + describer);
            }
			exceptions.add(describer);
		}
		
		HashSet getSet(){
			return exceptions;
		}
	}
}