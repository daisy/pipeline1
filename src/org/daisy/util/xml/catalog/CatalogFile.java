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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamConstants;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Represents the catalog file (physically in
 * <code>org/daisy/util/xml/catalog/catalog.xml</code>)<br/> The physical
 * catalog file supports a subset of the Oasis 1.1 Catalog specification.
 * 
 * @author markusg
 */
/*
 * 2005-09-14 Piotr Kiernicki - made changes in order to be able to use the
 * catalog.xml in a jar file. 2005-09-29 Linus Ericson - made it possible to
 * load resources from other directories - made Hastables non-static to support
 * multiple independent instances
 */
/*
 * 2007-12-27 mgylling - changed reader of catalog file to use StAX pool to save some initialization time.
 */
public final class CatalogFile {
    private Class<?> resourceClass;
    private URL catalogURL;
    private Hashtable<String,Object> pIdTable = new Hashtable<String,Object>(); //initially stored as URL, but turned into string after first access
    private Hashtable<String,Object> sIdTable = new Hashtable<String,Object>(); //initially stored as URL, but turned into string after first access
    private Hashtable<String,URL> srcIdTable = new Hashtable<String,URL>();    //maintains the URL state of the key
    
    

    /**
     * Class instantiator
     * 
     * @param url
     *            URL of the catalog file
     * @throws IOException 
     * @throws XMLStreamException 
     */
    @SuppressWarnings("unused")
	public CatalogFile(URL url, Class<?> resourceBase) throws IOException, SAXException, URISyntaxException{
    	//we maintain the old throws signature
    	final String ns_uri = "urn:oasis:names:tc:entity:xmlns:xml:catalog";
    	final String public_name = "public";
    	final String publicId_name = "publicId";
    	final String system_name = "system";
    	final String systemId_name = "systemId";
    	final String uri_name = "uri";
    	
        resourceClass = resourceBase;
        if (resourceBase == null) {
            resourceClass = this.getClass();
        }
        catalogURL = url;

        Map<String, Object> properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
        properties.put(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        XMLInputFactory xif = null;
        try{
        	xif = StAXInputFactoryPool.getInstance().acquire(properties);
        	XMLStreamReader reader = xif.createXMLStreamReader(catalogURL.openStream());
        	String key = null;
        	String value = null;        	
        	while(reader.hasNext()) {
        		int type = reader.next();
        		
        		if(type == XMLStreamConstants.START_ELEMENT) {
	        		if(reader.getLocalName().equals(system_name)) {
	        		   key = reader.getAttributeValue(null, systemId_name);        			
	        		}else if(reader.getLocalName().equals(public_name)) {
	        	       key = reader.getAttributeValue(null, publicId_name);
	        		}else{
	        			continue;
	        		}
	        		if(key!=null) {
	        			value = reader.getAttributeValue(null, uri_name);
	                    if (value.indexOf("./") == 0) {
	                    	value = value.substring(2);
	                    }
	                    URL entityUrl = resourceClass.getResource(value);
	                    if (entityUrl == null) {
	                    	entityUrl = resourceClass.getClassLoader().getResource(value);
	                    }
	                    
	                    if(entityUrl!=null) {
		                    if (reader.getLocalName().equals(public_name)) {
		                        pIdTable.put(key, entityUrl);
		                    }else{
		                        sIdTable.put(key, entityUrl);
		                    }
		                    srcIdTable.put(key, entityUrl);
	                    }else{
	                        System.err.println("CatalogFile warning: Entity "
	                                + key + " defined in catalog "
	                                + catalogURL + " not found.");  
	                    }                    
	        		}        		
	        		key = null;
	        		value = null;    
        		}
        	}
        	
		} catch (XMLStreamException e) {			
			throw new IOException(e.getMessage());
		}finally{
        	StAXInputFactoryPool.getInstance().release(xif, properties);
        }
        
//        try {
//            // get a jaxp sax instance
//            SAXParserFactory factory = SAXParserFactory.newInstance();
//            factory.setValidating(false);
//            factory.setNamespaceAware(true);
//            try {
//                // intern strings in order to be able to use == instead of
//                // .equals
//                factory.setFeature(
//                        "http://xml.org/sax/features/string-interning", true);
//                // turn of loaddtd on catalog file (else goes to oasis) - note
//                // apache specific, jaxp13 does not seem to support this
//                factory
//                        .setFeature(
//                                "http://apache.org/xml/features/nonvalidating/load-external-dtd",
//                                false);
//            } catch (SAXNotRecognizedException snre) {
//            	if(System.getProperty("org.daisy.debug")!=null) {
//            		System.err.println("DEBUG org.daisy.util.xml.catalog.CatalogFile " + snre.getMessage());
//            	}
//            } catch (SAXNotSupportedException snse) {
//                System.err.println(snse.getMessage());
//            }
//            SAXParser p = factory.newSAXParser();
//            p.parse(url.openStream(), new CatalogSaxHandler());
//        } catch (CatalogExceptionRecoverable cer) {
//            System.err.println(cer.getMessage());
//        } catch (ParserConfigurationException pce) {
//            System.err.println(pce.getMessage());
//        }
    }

    /**
     * @param publicId
     *            DTD PUBLIC identifier string
     * @return true if there is a match for this identifier in the catalog,
     *         false otherwise
     */
    public boolean supportsPublicId(String publicId) {
        if (pIdTable.containsKey(publicId)) {
            return true;
        }
        return false;
    }

    /**
     * @param systemId
     *            DTD SYSTEM identifier string
     * @return true if there is a match for this identifier in the catalog,
     *         false otherwise
     */
    public boolean supportsSystemId(String systemId) {
        if (sIdTable.containsKey(systemId)) {
            return true;
        }
        return false;
    }

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
            CatalogExceptionEntityNotSupported {
        Object entity = pIdTable.get(publicId);
        if (entity == null) {
            throw new CatalogExceptionEntityNotSupported(
                    "No support in catalog for public id: " + publicId);
            // System.err.println(("No support in catalog for public id:
            // "+publicId));
            // return null;
        } else if (entity instanceof java.net.URL) {
            String s = streamToString(((URL) entity).openStream());
            pIdTable.put(publicId, s); // replace in table
            return (new InputSource(new StringReader(s)));
        } else {
            return (new InputSource(new StringReader((String) entity)));
        }
    }

    /**
     * @param systemId
     *            DTD SYSTEM identifier string
     * @return a local input source if entity match exists in catalog
     * @throws IOException
     * @throws CatalogExceptionEntityNotSupported
     *             if no entity match exists in catalog
     */
    public InputSource getSystemIdEntity(String systemId) throws IOException,
            CatalogExceptionEntityNotSupported {
        Object entity = sIdTable.get(systemId);
        if (entity == null) {
            throw new CatalogExceptionEntityNotSupported(
                    "No support in catalog for system id: " + systemId);
        } else if (entity instanceof java.net.URL) {
            String s = streamToString(((URL) entity).openStream());
            sIdTable.put(systemId, s); // replace in table
            return (new InputSource(new StringReader(s)));
        }
        return (new InputSource(new StringReader((String) entity)));
    }

    public InputSource getSystemIdEntityFromSuffix(String filename) throws IOException, CatalogExceptionEntityNotSupported {
        // typically: check the filename only in an unmatched URI/Path
        for (Iterator<String> iter = sIdTable.keySet().iterator(); iter.hasNext();) {
            String key = iter.next();  
            int last = key.lastIndexOf('/');          
            if(last > -1 && last < key.length()) {
            	String cmp = key.substring(key.lastIndexOf('/')+1);	            	            
	            if (cmp.equals(filename)) {
	                return this.getSystemIdEntity(key);
	            }
            }
        }
        return null;
    }

    public URL getEntityLocalURL(String id) throws CatalogExceptionEntityNotSupported {
    	
        URL entity = srcIdTable.get(id);
        if (entity == null) {
            throw new CatalogExceptionEntityNotSupported(
                    "No support in catalog for public id: " + id);
        }
        try {
            InputStream is = entity.openStream();
            is.close();
        } catch (IOException e) {
            throw new CatalogExceptionEntityNotSupported(
                    "No support in catalog for public id: " + id);
        }
        return entity;
    }

    private String streamToString(InputStream fis) throws IOException {
        StringBuffer sb = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(fis);
        int ch = 0;
        while ((ch = fis.read()) > -1) {
            sb.append((char) ch);
        }
        fis.close();
        isr.close();
        return sb.toString();
    }

    public int getSize() {
        return sIdTable.size() + pIdTable.size();
    }

//    class CatalogSaxHandler extends DefaultHandler {
//        public void startElement(String namespaceURI, String sName,
//                String qName, Attributes attrs)
//                throws CatalogExceptionRecoverable {
//            String pId = null;
//            String sId = null;
//            URL entity = null;
//            if (qName == null) {
//                System.err.println("qname==null");
//                return;
//            }
//
//            if (qName == "public" || qName == "system") {
//                if (attrs != null) {
//                    for (int i = 0; i < attrs.getLength(); i++) {
//                        if (attrs.getQName(i) == "publicId") {
//                            pId = attrs.getValue(i);
//                        } else if (attrs.getQName(i) == "systemId") {
//                            sId = attrs.getValue(i);
//                        } else if (attrs.getQName(i) == "uri") {
//                            // entity = new
//                            // File(catalog.getParentFile(),attrs.getValue(i));
//                            // do this in case we are inside a jar:
//                            String uri = attrs.getValue(i);
//                            if (uri.indexOf("./") == 0) {
//                                uri = uri.substring(2);
//                            }
//                            URL url = resourceClass.getResource(uri);
//                            if (url == null) {
//                                url = resourceClass.getClassLoader()
//                                        .getResource(uri);
//                            }
//                            if (url != null) {
//                                entity = url;
//                            } else {
//                                System.err
//                                        .println("CatalogFile warning: Entity "
//                                                + attrs.getValue(i)
//                                                + " defined in catalog "
//                                                + catalogURL
//                                                + " not retrievable");
//                            }// if (url
//                        }// if (attrs
//                    } // for
//
//                    if (entity != null) {
//                        if (true) {
//                            if (pId != null) {
//                                srcIdTable.put(pId, entity);
//                                pIdTable.put(pId, entity);
//                            }
//                            if (sId != null) {
//                                srcIdTable.put(sId, entity);
//                                sIdTable.put(sId, entity);
//                            }
//                        } else {
//                            System.err.println("CatalogFile warning: Entity "
//                                    + entity + " defined in catalog "
//                                    + catalogURL + " not found.");                        
//                        } //if (entity.exists()                                                                            
//                    } //if (((entity != null)||
//                    else {
//                        System.err
//                                .println("CatalogFile warning: public or system element without uri attribute in catalog "
//                                        + catalogURL + "; " + sId + "; " + pId);
//                    }
//                }//if (attrs != null)
//            }//if qname.equals
//        }//startelement  
//
//        public void fatalError(SAXParseException spe)
//                throws CatalogExceptionNotRecoverable {
//            throw new CatalogExceptionNotRecoverable("fatal parse error in "
//                    + catalogURL + ":" + spe.getMessage() + "line: "
//                    + spe.getLineNumber());
//        }
//
//        public InputSource resolveEntity(String arg0, String arg1)
//                throws IOException, SAXException {
//            //called if the catalog parse references its own entities (like in doctype; catalog.dtd)
//            //should not happen since setFeature load-external-dtd = false
//            System.err
//                    .println("CatalogFile warning: CatalogFile tries to retrieve own entity");
//            return null;
//        }
//    }
}