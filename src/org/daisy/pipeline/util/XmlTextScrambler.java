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
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.file.EFile;
import org.daisy.util.file.Directory;
import org.daisy.util.i18n.CharUtils;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;
import org.daisy.util.xml.stax.AttributeByName;
import org.daisy.util.xml.stax.BookmarkedXMLEventReader;
import org.daisy.util.xml.stax.ContextStack;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Scramble the text nodes of an XML document, except for a static set of exceptions.
 * Output document is placed in the same dir as input document, with the text ".scrambled" added before the extension.
 * All image and CSS links are turned into references to "dummy.jpg" and "dummy.css" respectively.
 * @author Markus Gylling
 */
public class XmlTextScrambler {
	
	/**
	 * Scramble all XML docs within a directory, recursively.
	 * @param input
	 * @throws XMLStreamException
	 * @throws IOException 
	 */
	public XmlTextScrambler(Directory input) {
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
			try{
				scramble(new EFile(f));
			}catch (Exception e) {
				System.err.println("Exception while scrambling " + f.getAbsolutePath() + ": " + e.getMessage());
			}	
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
					}else if (isLinkElement(xe.asStartElement())) {
						xe = handleLinkElement(xe.asStartElement(),xef);
					}else if (isAnchorElement(xe.asStartElement())) {
						xe = handleAnchorElement(xe.asStartElement(),xef);
					}
				}else if(xe.isCharacters() && !skipElemTextScrambling 
						&& !CharUtils.isXMLWhiteSpace(xe.asCharacters().getData())) {
					xe = xef.createCharacters("["+Integer.toString(++c)+ "] " + 
							context.getContextXPath(
									ContextStack.XPATH_SELECT_ELEMENTS_ONLY, 
										ContextStack.XPATH_PREDICATES_NONE));
				}else if(xe.getEventType()==XMLEvent.PROCESSING_INSTRUCTION) {
					xe = handleProcessingInstruction((ProcessingInstruction)xe,xef);
				}else if(xe.isEndElement()) {
					skipElemTextScrambling = false;
				}
				if(xe!=null)writer.add(xe);				
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
	
	

	private XMLEvent handleAnchorElement(StartElement se,XMLEventFactory xef) {
		
		Set<Attribute> attrs = new HashSet<Attribute>();
		Attribute href = AttributeByName.get(new QName("href"), se);
		
		if(href!=null) {
			String value = "http";
			if(href.getValue().toLowerCase().contains(("mailto"))) {
				value = "mailto:dummy@dummy.org";
			}else if(href.getValue().trim().startsWith("#")) {				
				value = href.getValue();				
			} else if(href.getValue().toLowerCase().contains("smil")) {
					value = href.getValue();				
			} else {
					value = "http://dummy.org";				
			}
			attrs.add(xef.createAttribute(href.getName(), value));			
		}
		
		Iterator<?> i = se.getAttributes();
		while(i.hasNext()) {
			Attribute a = (Attribute)i.next();
			if(!a.getName().getLocalPart().equals("href")) {
				attrs.add(a);
			}
		}
		return xef.createStartElement(se.getName(), attrs.iterator(), se.getNamespaces());
	}

	private XMLEvent handleLinkElement(StartElement se, XMLEventFactory xef) {		
		//remove all links since they may refer to copyrighted stuff
		//unless its a css link in which case we create a new one
		Attribute type = AttributeByName.get(new QName("type"),se);
		Attribute rel = AttributeByName.get(new QName("rel"),se);
		if((type!=null && type.getValue().toLowerCase().equals("text/css"))
				||(rel!=null&&rel.getValue().toLowerCase().equals("stylesheet"))) {
			Set<Attribute> attrs = new HashSet<Attribute>();
			attrs.add(xef.createAttribute(new QName("href"), "dummy.css"));
			Iterator<?> i = se.getAttributes();
			while(i.hasNext()) {
				Attribute a = (Attribute)i.next();
				if(a.getName().getLocalPart()!="href") {
					attrs.add(a);
				}
			}
			return xef.createStartElement(se.getName(), attrs.iterator(), se.getNamespaces());
		}
		return null;
	}
	
	private XMLEvent handleProcessingInstruction(ProcessingInstruction pi, XMLEventFactory xef) {
		//remove all PIs since they may be copyrighted or freaky weird in other ways
		//unless its a css PI in which case we create a new one
		if(pi.getTarget().equals("xml-stylesheet") && pi.getData().toLowerCase().contains("css")) {
			return xef.createProcessingInstruction("xml-stylesheet", "type=\"text/css\" href=\"dummy.css\"");
		}
		return null;
	}

	private boolean isAnchorElement(StartElement se) {
		return se.getName().getLocalPart()=="a";
	}
	
	private boolean isLinkElement(StartElement se) {
		return se.getName().getLocalPart().equals("link");
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

				attrs = new HashSet<Attribute>();
				attrs.add(xef.createAttribute("class", "page-front"));
				skips.add(xef.createStartElement(new QName(Namespaces.XHTML_10_NS_URI,"span"), attrs.iterator(), null));

				attrs = new HashSet<Attribute>();
				attrs.add(xef.createAttribute("class", "page-special"));
				skips.add(xef.createStartElement(new QName(Namespaces.XHTML_10_NS_URI,"span"), attrs.iterator(), null));
				
				attrs = new HashSet<Attribute>();
				attrs.add(xef.createAttribute("class", "noteref"));
				skips.add(xef.createStartElement(new QName(Namespaces.XHTML_10_NS_URI,"span"), attrs.iterator(), null));

				
				StAXEventFactoryPool.getInstance().release(xef);
			}
			
			
			
			for(StartElement test : skips) {
				boolean matchesName = true;
				boolean matchesAttrs = true;
				if(se.getName().equals(test.getName())) {					
					Iterator<?> iter = test.getAttributes();						
					//real elem must have all attrs of test elem
					while(iter.hasNext()) {
						Attribute testAttr = (Attribute) iter.next();
						Attribute realAttr = AttributeByName.get(testAttr.getName(),se);
						if(realAttr==null || !realAttr.getValue().equals(testAttr.getValue())) {
							matchesAttrs = false;
						}
					}
					
				}else{
					matchesName = false;
				}
				if(matchesName && matchesAttrs) return true;
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
				new XmlTextScrambler(new Directory(file));	
			}else{
				new XmlTextScrambler(new EFile(file));
			}
											
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("textscrambler done.");
	}
}
