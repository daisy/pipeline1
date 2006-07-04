package org.daisy.util.xml.pool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;

/**
 * A singleton source for a StAX XMLOutputFactory. Used for performance optimization.
 * 
 * <p>This is named "pool" although it really isnt 
 * (as opposed to its SAX and DOM package brethren); just behaves a bit like one.</p>
 * <p>Usage of this class follows optimization recommendations given by the 
 * author of Woodstox at
 * http://www.cowtowncoder.com/blog/archives/2006/06/entry_2.html</p> 
 * @author Markus Gylling
 */
public class StAXOutputFactoryPool {
	protected static StAXOutputFactoryPool instance = new StAXOutputFactoryPool();
	private static XMLOutputFactory xmlOutputFactory = null;
	private static Map defaultProperties = new HashMap();

	static public StAXOutputFactoryPool getInstance() {
		return instance;
	}

	private StAXOutputFactoryPool() {		
		xmlOutputFactory = XMLOutputFactory.newInstance();
		defaultProperties.put("javax.xml.stream.isRepairingNamespaces", xmlOutputFactory.IS_REPAIRING_NAMESPACES);
	}

	public XMLOutputFactory acquire(Map properties) throws PoolException {
		setProperties(defaultProperties);
		setProperties(properties);
		return xmlOutputFactory;
	}
	
	private void setProperties(Map properties) {
		if (properties != null) {
			for (Iterator i = properties.keySet().iterator(); i.hasNext();) {
				String property = (String) i.next();
				xmlOutputFactory.setProperty(property, properties.get(property));
			}
		}
	}
}
