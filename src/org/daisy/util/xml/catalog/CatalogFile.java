package org.daisy.util.xml.catalog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Represents the catalog file (physically in <code>org/daisy/util/xml/catalog/catalog.xml</code>)<br/>
 * The physical catalog file supports a subset of the Oasis 1.1 Catalog specification.
 * @author markusg
 */

public final class CatalogFile extends File {
	private File catalog; //in order to access properties from the sax inner class	 
	private static Hashtable pIdTable = new Hashtable();   //holds PIDs in <pId>,<File> OR <pid>,<String> form
	private static Hashtable sIdTable = new Hashtable();   //holds SIDs in <sId>,<File> OR <pid>,<String> form
	private static Hashtable srcIdTable = new Hashtable(); //holds both sID and PID, in <id>,<file> form	
	/**
	 * Class instantiator
	 * @param url URL of the catalog file
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws SAXException
	 */
	public CatalogFile(URL url) throws URISyntaxException, IOException, SAXException {        
		super(url.toURI());                       
		catalog = this;

		try {
			//get a jaxp sax instance
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);     
			factory.setNamespaceAware(true);	
		    try {
				//intern strings in order to be able to use == instead of .equals
				factory.setFeature("http://xml.org/sax/features/string-interning",true);
		    	//turn of loaddtd on catalog file (else goes to oasis) - note apache specific, jaxp13 does not seem to support this
		    	factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",false);		    	
		    } catch (SAXNotRecognizedException snre) {
		    	System.err.println(snre.getMessage());
		    } catch (SAXNotSupportedException snse) {
				System.err.println(snse.getMessage());
		    }								    
			SAXParser p = factory.newSAXParser();
			p.parse(url.openStream(), new CatalogSaxHandler());            
		} catch (CatalogExceptionRecoverable cer) {			
			System.err.println(cer.getMessage());		
		} catch (ParserConfigurationException pce) {
			System.err.println(pce.getMessage());
		} 		
	}
	
	/**
	 * @param publicId DTD PUBLIC identifier string
	 * @return true if there is a match for this identifier in the catalog, false otherwise
	 */
	public boolean supportsPublicId(String publicId) {
		if (pIdTable.containsKey(publicId)) {
			return true;
		}
		return false;
	}

	/**
	 * @param systemId DTD SYSTEM identifier string
	 * @return true if there is a match for this identifier in the catalog, false otherwise
	 */
	public boolean supportsSystemId(String systemId) {
		if (sIdTable.containsKey(systemId)) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * @param publicId DTD PUBLIC identifier string
	 * @return a local input source if entity match exists in catalog
	 * @throws IOException
	 * @throws CatalogExceptionEntityNotSupported
	 * @throws CatalogExceptionEntityNotSupported if no entity match exists in catalog
	 */
	public InputSource getPublicIdEntity (String publicId) throws IOException, CatalogExceptionEntityNotSupported {        
		Object entity = pIdTable.get(publicId);
		if (entity==null) {
			throw new CatalogExceptionEntityNotSupported("No support in catalog for public id: "+publicId); 
			//System.err.println(("No support in catalog for public id: "+publicId));
			//return null;
		}else if (entity instanceof java.io.File){
			String p = fileToString(new FileInputStream((File)entity));                        
			pIdTable.put(publicId,p); //replace in table    
			return (new InputSource(new StringReader(p)));            
		}else{
			return (new InputSource(new StringReader((String)entity)));
		}  
	}

	/**
	 * @param systemId DTD SYSTEM identifier string
	 * @return a local input source if entity match exists in catalog
	 * @throws IOException
	 * @throws CatalogExceptionEntityNotSupported if no entity match exists in catalog
	 */
	public InputSource getSystemIdEntity (String systemId) throws IOException, CatalogExceptionEntityNotSupported {        
		Object entity = sIdTable.get(systemId);
		if (entity==null) {
			throw new CatalogExceptionEntityNotSupported("No support in catalog for system id: "+systemId); 
		}else if (entity instanceof java.io.File){
			String s = fileToString(new FileInputStream((File)entity));                        
			sIdTable.put(systemId,s); //replace in table    
			return (new InputSource(new StringReader(s)));            
		}
		return (new InputSource(new StringReader((String)entity)));          
	}
	
	public URL getEntityLocalURL (String id) throws IOException, CatalogExceptionEntityNotSupported {        
		File entity = (File)srcIdTable.get(id);
		if (entity==null) {
			throw new CatalogExceptionEntityNotSupported("No support in catalog for public id: "+id); 
		}
		String uri = entity.toURI().toASCIIString();
		return new URL(uri);		  
	}
	
	private String fileToString(FileInputStream fis) throws IOException {
		StringBuffer sb = new StringBuffer();
		InputStreamReader isr = new InputStreamReader(fis);
		int ch=0;            
		while((ch = fis.read())> -1){                              
			sb.append((char)ch);                            
		}
		fis.close();isr.close();
		return sb.toString();        
	}
	
	public int getSize() {
		return sIdTable.size() + pIdTable.size(); 
	}
	
	class CatalogSaxHandler extends DefaultHandler {        
		public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws CatalogExceptionRecoverable {
			String pId = null;
			String sId = null;
			File entity = null;
			if (qName == null) {
				System.err.println("qname==null");
				return;
			}

			if (qName == "public"||qName == "system") {
				if (attrs != null) {
					for (int i = 0; i < attrs.getLength(); i++) {                    
						if (attrs.getQName(i) == "publicId") {
							pId = attrs.getValue(i);
						}else if (attrs.getQName(i) == "systemId") {
							sId = attrs.getValue(i);
						}else if (attrs.getQName(i) == "uri") {
							//entity = new File(catalog.getParentFile(),attrs.getValue(i));
							//do this in case we are inside a jar:
							URL url = catalog.getClass().getResource("./" + attrs.getValue(i));
							if (url!=null) {
								try {
									entity = new File(url.toURI());                  
								} catch (URISyntaxException use) {
									System.err.println(use.getMessage());
									//throw new CatalogExceptionRecoverable(use);
								}
							}else{
								System.err.println("CatalogFile warning: Entity ./" + attrs.getValue(i) + " defined in catalog " +  catalog.getName() + " not retrievable");
								//throw new CatalogExceptionRecoverable("Entity ./" + attrs.getValue(i) + " defined in catalog " +  catalog.getName() + " not retrievable");
							}//if (url
						}//if (attrs  
					} //for 
					
					if (entity != null) {
						if (entity.exists()) {
							if (pId != null) {
								srcIdTable.put(pId,entity);
								pIdTable.put(pId,entity);
							}                            
							if (sId != null) {
								srcIdTable.put(sId,entity);
								sIdTable.put(sId,entity);
							}                            
						}else{
							System.err.println("CatalogFile warning: Entity " + entity.getName() + " defined in catalog " +  catalog.getName() + " not found in " + entity.getParentFile().getAbsolutePath());
							//throw new CatalogExceptionRecoverable("Entity " + entity.getName() + " defined in catalog " +  catalog.getName() + " not found in " + entity.getParentFile().getAbsolutePath());                        
						} //if (entity.exists()                                                                            
					} //if (((entity != null)||
					else {
						System.err.println("CatalogFile warning: public or system element without uri attribute in catalog "+  catalog.getName() + "; " + sId + "; " + pId);
						//throw new CatalogExceptionRecoverable("public or system element without uri attribute in catalog "+  catalog.getName());
					}
				}//if (attrs != null)
			}//if qname.equals
		}//startelement  
				
		public void fatalError(SAXParseException spe) throws CatalogExceptionNotRecoverable {
			throw new CatalogExceptionNotRecoverable("fatal parse error in " + catalog.getName() +":" + spe.getMessage() + "line: " + spe.getLineNumber());            
		}
		
		public InputSource resolveEntity(String arg0, String arg1) throws IOException, SAXException {
			//called if the catalog parse references its own entities (like in doctype; catalog.dtd)
			//should not happen since setFeature load-external-dtd = false
			System.err.println("CatalogFile warning: CatalogFile tries to retrieve own entity");
			return null;
		}
	}    
}