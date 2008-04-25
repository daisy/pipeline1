package int_daisy_filesetGenerator.util;

import java.util.HashMap;
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
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.daisy.util.xml.IDGenerator;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;
import org.daisy.util.xml.stax.BookmarkedXMLEventReader;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Add IDs to XML elements that dont already have an ID. Guarantees
 * that the added ID will not duplicate a preexisting ID.
 * @author Markus Gylling
 */
public class IDPopulator {
	private static Map<Namespace, Set<QName>> mExcludes = null;
	private Source mInput;
	private Result mOutput;
	
	public IDPopulator(Source input, Result output) {
		if(mExcludes == null) mExcludes = buildExcludes();
		mInput = input;
		mOutput = output;
	}

	public void execute() throws XMLStreamException {
		Map<String,Object> xifProperties = null;
		Map<String,Object> xofProperties = null;
		XMLInputFactory xif = null;
		XMLOutputFactory xof = null;
		XMLEventFactory xef = null;
		XMLEventWriter writer = null;
		try{
			xifProperties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xofProperties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
			xif = StAXInputFactoryPool.getInstance().acquire(xifProperties);
			xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
			xof = StAXOutputFactoryPool.getInstance().acquire(xofProperties);
			xef = StAXEventFactoryPool.getInstance().acquire();
			
			XMLEventReader baseReader = xif.createXMLEventReader(mInput);
			writer = xof.createXMLEventWriter(mOutput);
			IDGenerator idGen = new IDGenerator("id_");
			
			BookmarkedXMLEventReader reader = new BookmarkedXMLEventReader(baseReader);
			Set<String> existingIDs = new HashSet<String>();
			
			/*
			 * Gather existing IDs.
			 */
			reader.setBookmark("start");
			Attribute a;
			while(reader.hasNext()) {
				XMLEvent xe = reader.nextEvent();
				if(xe.isStartElement()) {
					Iterator<?> i = xe.asStartElement().getAttributes();
					while(i.hasNext()) {
						a = (Attribute)i.next();
						if(a.getName().getLocalPart()=="id") {
							existingIDs.add(a.getValue());
						}
					}
				}
			}			
			
			reader.gotoAndRemoveBookmark("start");
			
			/*
			 * Add new IDs.
			 */
			while(reader.hasNext()) {
				XMLEvent xe = reader.nextEvent();
				if(xe.isStartElement() && shouldAddID((StartElement)xe, xef)) {
					Set<Attribute> atts = new HashSet<Attribute>();
					Iterator<?> i = xe.asStartElement().getAttributes();
					while(i.hasNext()) {
						atts.add((Attribute)i.next());						
					}
					atts.add(xef.createAttribute("id", idGen.generateId(existingIDs)));
					
					writer.add(xef.createStartElement(xe.asStartElement().getName(), 
							atts.iterator(), xe.asStartElement().getNamespaces()));
				}else{
					writer.add(xe);
				}
			}

		} catch (CatalogExceptionNotRecoverable e) {
			e.printStackTrace();
		}finally{
			writer.flush();
			writer.close();
			StAXInputFactoryPool.getInstance().release(xif, xifProperties);
			StAXOutputFactoryPool.getInstance().release(xof, xofProperties);
			StAXEventFactoryPool.getInstance().release(xef);
		}		
		
	}
	
	/**
	 * Return true if inparam StartElement does not have an ID, and is not disabled for adding.
	 */
	private boolean shouldAddID(StartElement xe, XMLEventFactory xef) {
		try{
			Iterator<?> i = xe.asStartElement().getAttributes();
			while(i.hasNext()) {
				Attribute a = (Attribute)i.next();
				if(a.getName().getLocalPart()=="id") {
					return false;
				}
			}
					
			Namespace ns = xef.createNamespace(xe.getName().getNamespaceURI());		
			if(ns!=null) {
				Set<QName> nsSet = getSet(ns,mExcludes);
				if(nsSet!=null) {
					if(nsSet.contains(xe.getName())){
						return false;
					}
					return true;				
				}
			}	
		}catch (Exception e) {
			e.printStackTrace();
		}
		return true; //if we fail, add an ID anyway
	}

	private Set<QName> getSet(Namespace ns, Map<Namespace, Set<QName>> excludes) {
		for (Iterator<Namespace> iterator = excludes.keySet().iterator(); iterator.hasNext();) {
			Namespace key = iterator.next();
			if(key.getNamespaceURI().equals(ns.getNamespaceURI())) {
				return excludes.get(key);
			}			
		}
		 
		return null;
	}

	private Map<Namespace, Set<QName>> buildExcludes() {
		mExcludes = new HashMap<Namespace, Set<QName>>();
		
		XMLEventFactory xef = StAXEventFactoryPool.getInstance().acquire();
		
		Set<QName> xhtmlExcludes = new HashSet<QName>();
		xhtmlExcludes.add(new QName(Namespaces.XHTML_10_NS_URI,"html"));
		xhtmlExcludes.add(new QName(Namespaces.XHTML_10_NS_URI,"head"));
		xhtmlExcludes.add(new QName(Namespaces.XHTML_10_NS_URI,"meta"));
		xhtmlExcludes.add(new QName(Namespaces.XHTML_10_NS_URI,"title"));
		xhtmlExcludes.add(new QName(Namespaces.XHTML_10_NS_URI,"body"));
		xhtmlExcludes.add(new QName(Namespaces.XHTML_10_NS_URI,"hr"));
		xhtmlExcludes.add(new QName(Namespaces.XHTML_10_NS_URI,"br"));
		xhtmlExcludes.add(new QName(Namespaces.XHTML_10_NS_URI,"link"));
		xhtmlExcludes.add(new QName(Namespaces.XHTML_10_NS_URI,"img"));
		
		mExcludes.put(xef.createNamespace(Namespaces.XHTML_10_NS_URI), xhtmlExcludes);
		
		StAXEventFactoryPool.getInstance().release(xef);
			
		return mExcludes;
	}
}
