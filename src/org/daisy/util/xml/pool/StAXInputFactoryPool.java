package org.daisy.util.xml.pool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;

/**
 * A singleton source for a StAX XMLInputFactory. Used for performance optimization.
 * <p>This class is named "pool" although it really isnt (as opposed to its SAX and 
 * DOM package brethren); just behaves a bit like one.</p>
 * <p>Usage of this class follows optimization recommendations given by the 
 * author of Woodstox at
 * http://www.cowtowncoder.com/blog/archives/2006/06/entry_2.html</p>
 * @author Markus Gylling
 */
public class StAXInputFactoryPool {
	protected static StAXInputFactoryPool instance = new StAXInputFactoryPool();
	private static XMLInputFactory xmlInputFactory = null;
	private static Map defaultProperties = new HashMap();

	static public StAXInputFactoryPool getInstance() {
		return instance;
	}

	private StAXInputFactoryPool() {
		super();
		xmlInputFactory = XMLInputFactory.newInstance();
		defaultProperties.put(XMLInputFactory.ALLOCATOR, xmlInputFactory.getEventAllocator());
		defaultProperties.put(XMLInputFactory.IS_COALESCING, xmlInputFactory.getProperty(XMLInputFactory.IS_COALESCING));
		defaultProperties.put(XMLInputFactory.IS_NAMESPACE_AWARE, xmlInputFactory.getProperty(XMLInputFactory.IS_NAMESPACE_AWARE));
		defaultProperties.put(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, xmlInputFactory.getProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES));
		defaultProperties.put(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, xmlInputFactory.getProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES));
		defaultProperties.put(XMLInputFactory.IS_VALIDATING, xmlInputFactory.getProperty(XMLInputFactory.IS_VALIDATING));
		defaultProperties.put(XMLInputFactory.REPORTER, xmlInputFactory.getXMLReporter());
		defaultProperties.put(XMLInputFactory.RESOLVER, xmlInputFactory.getXMLResolver());
		defaultProperties.put(XMLInputFactory.SUPPORT_DTD, xmlInputFactory.getProperty(XMLInputFactory.SUPPORT_DTD));
	}

	public XMLInputFactory acquire(Map properties) throws PoolException {
		setProperties(defaultProperties);
		setProperties(properties);
		return xmlInputFactory;
	}
	
	private void setProperties(Map properties) {
		if (properties != null) {
			for (Iterator i = properties.keySet().iterator(); i.hasNext();) {
				String property = (String) i.next();
				xmlInputFactory.setProperty(property, properties.get(property));
			}
		}
	}
}
