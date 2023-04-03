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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;

import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.daisy.util.runtime.Service;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Threadsafe singleton implementation of a catalog entityresolver.
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
 * Information about entities not supported by the catalog can be retrieved by registering
 * an instance of CatalogListener.
 * {@link #setListener(CatalogListener)}
 * <p>
 * Use example within a SAX context:
 * </p>
 * <code>
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
 * </code>
 * 
 * <p>
 * The CatalogException is subclassed in recoverable and irrecoverable
 * categories, meaning that if desired the exception catching can be extended
 * using for example
 * </p>
 * 
 * <code>
 *    } catch (CatalogException ce) {
 *      if (ce instanceof CatalogExceptionRecoverable) {
 *        System.err.println(ce.getMessage());                      
 *      }else if (ce instanceof CatalogExceptionNotRecoverable) {
 *        throw new IOException(ce.getMessage());
 *      }
 *    }
 * </code>
 * 
 * <p>
 * Use example within a (JAXP) DOM context:
 * </p>
 * <code>
 *   [...]
 *   builder = factory.newDocumentBuilder();
 *   builder.setEntityResolver(CatalogEntityResolver.getInstance());  
 *   Document doc = builder.parse(&quot;E:/file.xml&quot;);
 *   [...]
 * </code>
 * 
 * @author markusg
 */

public class CatalogEntityResolver implements EntityResolver, LSResourceResolver, URIResolver, XMLResolver {
    private static CatalogCollection catalog;    
    private static DOMImplementationLS mDOMImplementationLS = null;
    private static CatalogEntityResolver mInstance = null;
    private static CatalogListener mListener = null;
    
    private CatalogEntityResolver() throws CatalogExceptionNotRecoverable {
        URL catalogURL = this.getClass().getResource("catalog.xml");
        try {
            Catalog cat = new CatalogFile(catalogURL, this.getClass());
            catalog = new CatalogCollection(cat);
            addExternalCatalogs();
        } catch (Exception e) {
            throw new CatalogExceptionNotRecoverable(e);
        }
    }

    /**
     * Singleton retrieval
     * 
     * @return The catalog entity resolver instance
     */
    static public CatalogEntityResolver getInstance() throws CatalogExceptionNotRecoverable {
        if (null == mInstance) {
            synchronized (CatalogEntityResolver.class) {
                if (null == mInstance) {
                    mInstance = new CatalogEntityResolver();
                }
            }
        }
        return mInstance;
    }
    
    /**
     * Add external catalog files using the JAR services framework.
     * @throws IOException
     * @throws SAXException
     * @throws URISyntaxException
     */
    private void addExternalCatalogs() throws IOException, SAXException, URISyntaxException {
    	Service<CatalogLocator> service = new Service<CatalogLocator>(CatalogLocator.class);
    	Enumeration<CatalogLocator> providers = service.getProviders();
    	while (providers.hasMoreElements()) {
    		CatalogLocator provider = providers.nextElement();
    		URL catalogURL = provider.getCatalogURL();
            Catalog cat = new CatalogFile(catalogURL, provider.getClass());
            catalog.addCatalog(cat);
    	}
    }

    /**
     * Catalog aware implementation of the SAX resolveEntity method.
     * <p>
     * The public id is always preferred, ie the value of the OASIS Catalog 1.1
     * </p>
     * <code>prefer</code> attribute is hardcoded to <code>public</code>
     * 
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
     *      java.lang.String)
     */
    public InputSource resolveEntity(String publicId, String systemId) throws IOException {

    	if (publicId != null) {
            try {
                return catalog.getPublicIdEntity(publicId);
            } catch (CatalogExceptionEntityNotSupported ceens) {
                // no match in catalog for inparam public id
            	informListener(publicId);
                // try systemId before giving up
            }
        }

    	if (systemId != null) {
	        try {
	            return catalog.getSystemIdEntity(systemId);
	        } catch (CatalogExceptionEntityNotSupported ceens) {
	            // no match in catalog for inparam system id either
	        	informListener(systemId);
	            // try to match on filename alone (suffix)
	            
	            try {
	            	String filename = systemId;
	            	int last = systemId.lastIndexOf('/');
	                if(last > -1 && last < systemId.length()) {
	                	filename = systemId.substring(systemId.lastIndexOf('/')+1);	
	                }	
	                try {
	                    return catalog.getSystemIdEntityFromSuffix(filename);
	                } catch (CatalogExceptionEntityNotSupported ceens2) {
	                	informListener(systemId + ": suffix");
	                    if (System.getProperty("org.daisy.debug") != null) {
	                        System.out
	                                .println("DEBUG: CatalogEntityResolver#resolveEntity: entity not supported: publicId: "
	                                        + publicId + ":: systemId:" + systemId);
	                    }
	                }
	                
	            } catch (Exception e) {
	                // silence
	            }
	            
	        }
    	}
    	//System.err.println("returning null on " + systemId);
    	return null;
    }

    /**
     * @return the URL of the local resolved entity resource
     */
    @SuppressWarnings("unused")
	public URL resolveEntityToURL(String publicId, String systemId) throws IOException {
        if (publicId != null) {
            try {
                return catalog.getEntityLocalURL(publicId);
            } catch (CatalogExceptionEntityNotSupported ceens) {
                // there was no match in catalog for inparam public id
            	informListener(publicId);
                // try systemId before giving up
            }
        }
        try {
            return catalog.getEntityLocalURL(systemId);
        } catch (CatalogExceptionEntityNotSupported ceens) {
            // there was no match in catalog for inparam system id either
        	informListener(systemId);
            return null;
        }
    }

    /**
     * @return the URL of the local resolved entity resource
     */
    @SuppressWarnings("unused")
	public URL resolveEntityToURL(String publicOrSystemId) throws IOException {
        try {
            return catalog.getEntityLocalURL(publicOrSystemId);
        } catch (CatalogExceptionEntityNotSupported ceens) {
        	informListener(publicOrSystemId);
        }
        return null;
    }

    /**
     * LSResourceResolver impl 
     */
    @SuppressWarnings("unused")
    public LSInput resolveResource(String type, String namespaceURI,
            String publicId, String systemId, String baseURI) {
        try {
            URL url = resolveEntityToURL(publicId, systemId);
            if(url!=null) {
	            if(mDOMImplementationLS==null){
		    		DOMImplementationRegistry registry = null;
		    		try {
		    			registry = DOMImplementationRegistry.newInstance();
		    		} catch (Exception e) {
		    			e.printStackTrace();
		    			return null;
		    		} 		
		    		DOMImplementation domImpl = registry.getDOMImplementation("LS 3.0");
		    		mDOMImplementationLS = (DOMImplementationLS) domImpl;
	            }
	            LSInput lsi = mDOMImplementationLS.createLSInput();            
	            lsi.setByteStream(url.openStream());
	            return lsi;
            }                        
        } catch (IOException e) {
        	e.printStackTrace();
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

    /*
     * (non-Javadoc)
     * @see javax.xml.transform.URIResolver#resolve(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unused")
    public Source resolve(String href, String base) throws TransformerException {     
        try {
            URL url = this.resolveEntityToURL(href);
            if (url != null) {            
	            StreamSource ss = new StreamSource(url.openStream());
	            ss.setSystemId(url.toExternalForm());
	            return ss;
            }
        } catch (IOException e) {
        	throw new TransformerException(e);
        }
        return null;
    }
    
    /*
     * (non-Javadoc)
     * @see javax.xml.stream.XMLResolver#resolveEntity(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unused")
	public Object resolveEntity(String publicID, String systemID, 
			String baseURI, 
			String namespace) throws XMLStreamException {		
        try {
            InputSource is = resolveEntity(publicID, systemID);
            if (is == null) {
                return null;
            }
            InputStream istr = is.getByteStream();
            if (istr != null) {
                return istr;
            }
            Reader rdr = is.getCharacterStream();
            if (rdr != null) {
                StringBuffer buffer = new StringBuffer();
                int ch = 0;
                while ((ch = rdr.read())> -1) {
                    buffer.append((char)ch);
                }
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer.toString().getBytes());
                return bais;
            }
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
        return null;
	}
    
    public void setListener(CatalogListener listener) {
    	mListener = listener;
    }
    
    private void informListener(String notResolved) {
    	if(mListener!=null) {
    		mListener.entityNotSupported(notResolved);
    	}
    }

    
}