package org.daisy.pipeline.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;
import org.daisy.util.i18n.CharUtils;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;
import org.daisy.util.xml.stax.BookmarkedXMLEventReader;
import org.daisy.util.xml.stax.ContextStack;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Scramble the text nodes of an XML document, except for a static set of exceptions.
 * Output document is placed in the same dir as input document, with the text ".scrambled"
 * added before the extension.
 * 
 * @author Markus Gylling
 */
public class XmlTextScrambler {
	
	/**
	 * Scramble all XML docs within a directory, recursively.
	 * @param input
	 * @throws XMLStreamException
	 * @throws IOException 
	 */
	public XmlTextScrambler(EFolder input) throws XMLStreamException, IOException {
		Collection<?> files = input.getFiles(true);
		Iterator<?> iter = files.iterator();
		while(iter.hasNext()) {			
			Peeker peeker = PeekerPool.getInstance().acquire();
			File f = (File) iter.next();
			try{
				peeker.peek(f);				
			}catch (Exception e) {
				continue; //not an xml file
			}
			scramble(new EFile(f));
			PeekerPool.getInstance().release(peeker);
		}
	}
	
	/**
	 * Scramble a single file.
	 * @param input
	 * @throws XMLStreamException
	 * @throws IOException 
	 */
	public XmlTextScrambler(EFile input) throws XMLStreamException, IOException {
		scramble(input);		
	}

	private void scramble(EFile input) throws XMLStreamException, IOException {
		File output = new File(input.getParentFile(),
				input.getNameMinusExtension()+".scrambled."+input.getExtension());
		
		Map<String,Object> xifProperties = null;
		Map<String,Object> xofProperties = null;
		XMLInputFactory xif = null;
		XMLOutputFactory xof = null;
		XMLEventWriter writer = null;
		XMLEventFactory xef = null;
		FileInputStream fis=null;
		FileOutputStream fos=null;
		try{
			xifProperties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xofProperties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
			xif = StAXInputFactoryPool.getInstance().acquire(xifProperties);
			xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
			xof = StAXOutputFactoryPool.getInstance().acquire(xofProperties);
			xef = StAXEventFactoryPool.getInstance().acquire();
			fis = new FileInputStream(input);
			fos = new FileOutputStream(output);
			
			XMLEventReader baseReader = xif.createXMLEventReader(fis);
			writer = xof.createXMLEventWriter(fos);					
			BookmarkedXMLEventReader reader = new BookmarkedXMLEventReader(baseReader);
			ContextStack context = new ContextStack(true);
			
			boolean skipElemTextScrambling = false;
			int c = 0;
			while(reader.hasNext()) {
				XMLEvent xe = reader.nextEvent();
				context.addEvent(xe);
				if(xe.isStartElement()) {
					skipElemTextScrambling=shouldSkip(xe.asStartElement());
					if(isMetaElement(xe.asStartElement())) {					
						xe = handleMetaElement(xe.asStartElement(),xef);
					}else if (isImageElement(xe.asStartElement())) {
						xe = handleImageElement(xe.asStartElement(),xef);
					}else if (isAcronymElement(xe.asStartElement())) {
						xe = handleAcronymElement(xe.asStartElement(),xef);
					}
				}else if(xe.isCharacters() && !skipElemTextScrambling 
						&& !CharUtils.isXMLWhiteSpace(xe.asCharacters().getData())) {
					xe = xef.createCharacters("["+Integer.toString(++c)+ "] " + 
							context.getContextXPath(
									ContextStack.XPATH_SELECT_ELEMENTS_ONLY, 
										ContextStack.XPATH_PREDICATES_NONE));
				}
				writer.add(xe);				
			}

		} catch (CatalogExceptionNotRecoverable e) {
			e.printStackTrace();
		}finally{
			fis.close();
			fos.close();
			writer.flush();
			writer.close();
			StAXInputFactoryPool.getInstance().release(xif, xifProperties);
			StAXOutputFactoryPool.getInstance().release(xof, xofProperties);
			StAXEventFactoryPool.getInstance().release(xef);
		}	
	}
	
	private boolean isAcronymElement(StartElement se) {
		return se.getName().getLocalPart().matches("acronym|abbr");
	}
	
	private boolean isImageElement(StartElement se) {
		return se.getName().getLocalPart()=="img";
	}

	private boolean isMetaElement(StartElement se) {
		return se.getName().getLocalPart()=="meta";
	}

	private XMLEvent handleAcronymElement(StartElement se, XMLEventFactory xef) {
		String nsURI = se.getName().getNamespaceURI();
		if(nsURI ==Namespaces.Z2005_DTBOOK_NS_URI
				|| nsURI ==Namespaces.XHTML_10_NS_URI) {
			
			Set<Attribute> attrs = new HashSet<Attribute>();
			Iterator<?> iter = se.getAttributes();
			while(iter.hasNext()) {
				Attribute a = (Attribute)iter.next();
				if(a.getName().getLocalPart()=="title") {					
					attrs.add(xef.createAttribute(a.getName(), "srambled"));
				}else{
					attrs.add(a);
				}
			}
			return xef.createStartElement(se.getName(), attrs.iterator(), se.getNamespaces());
		}
		System.err.println("Namespace " + se.getName().getNamespaceURI() + " not recognized, may not scramble completely.");
		return se;
	}
	
	private XMLEvent handleImageElement(StartElement se, XMLEventFactory xef) {
		String nsURI = se.getName().getNamespaceURI();
		if(nsURI ==Namespaces.Z2005_DTBOOK_NS_URI
				|| nsURI ==Namespaces.XHTML_10_NS_URI) {
			
			Set<Attribute> attrs = new HashSet<Attribute>();
			Iterator<?> iter = se.getAttributes();
			while(iter.hasNext()) {
				Attribute a = (Attribute)iter.next();
				if(a.getName().getLocalPart()=="src") {
					attrs.add(xef.createAttribute(a.getName(), "dummy.jpg"));
				} else if(a.getName().getLocalPart()=="alt") {
					attrs.add(xef.createAttribute(a.getName(), "srambled"));
				}else{
					attrs.add(a);
				}
			}
			return xef.createStartElement(se.getName(), attrs.iterator(), se.getNamespaces());
		}
		System.err.println("Namespace " + se.getName().getNamespaceURI() + " not recognized, may not scramble completely.");
		return se;
	}
	
	private XMLEvent handleMetaElement(StartElement se, XMLEventFactory xef) {		
				
		Attribute name = se.getAttributeByName(new QName("","name"));
		if(name!=null && name.getValue().toLowerCase().replace(".", ":").matches("dc:language|dc:date")) {
			return se;
		}
		Set<Attribute> attrs = new HashSet<Attribute>();
		Iterator<?> iter = se.getAttributes();
		while(iter.hasNext()) {
			Attribute a = (Attribute)iter.next();
			if(a.getName().getLocalPart()=="content") {
				attrs.add(xef.createAttribute(a.getName(), "scrambled"));
			}else{
				attrs.add(a);
			}
		}		
		return xef.createStartElement(se.getName(), attrs.iterator(), se.getNamespaces());
	}

	private static Set<StartElement> skips = null;
	private boolean shouldSkip(StartElement se) {
		try{
			if(skips==null) {
				skips = new HashSet<StartElement>();
				XMLEventFactory xef = StAXEventFactoryPool.getInstance().acquire();
				
				Set<Attribute> attrs;
				
				skips.add(xef.createStartElement(new QName(Namespaces.Z2005_DTBOOK_NS_URI,"pagenum"), null, null));
				skips.add(xef.createStartElement(new QName(Namespaces.Z2005_DTBOOK_NS_URI,"noteref"), null, null));
				skips.add(xef.createStartElement(new QName(Namespaces.Z2005_DTBOOK_NS_URI,"annoref"), null, null));
				
				attrs = new HashSet<Attribute>();
				attrs.add(xef.createAttribute("class", "page-normal"));
				skips.add(xef.createStartElement(new QName(Namespaces.XHTML_10_NS_URI,"span"), attrs.iterator(), null));
				
				StAXEventFactoryPool.getInstance().release(xef);
			}
			
			for(StartElement test : skips) {
				if(se.getName().getNamespaceURI().equals(test.getName().getNamespaceURI())) {
					if(se.getName().getLocalPart().equals(test.getName().getLocalPart())) {
						Iterator<?> iter = test.getAttributes();
						while(iter.hasNext()) {
							Attribute testAttr = (Attribute) iter.next();
							if(se.getAttributeByName(testAttr.getName())==null) {
								return false;
							}
						}
						return true;
					}	
				}
			}
	
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Scramble the text nodes of XML document instances, except for a static set of element exceptions.
	 * <p>The output document is placed in the same directory as the input document, with the text ".scrambled"
	 * added before the extension.</p>
	 * <p>Arguments: One, a file or a directory. In the latter case, all XML children of the directory (recursively) will be scrambled.</p>
	 * @author Markus Gylling
	 */
	public static void main(String[] args) {
		System.err.println("Running textscrambler...");
		try {
			File file = new File(args[0]);	
			if(!file.exists()) {
				throw new IOException(file.toString());
			}
			if(file.isDirectory()) {
				new XmlTextScrambler(new EFolder(file));	
			}else{
				new XmlTextScrambler(new EFile(file));
			}
											
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("textscrambler done.");
	}
}
