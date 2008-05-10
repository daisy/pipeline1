package org.daisy.util.xml;

import java.util.HashMap;
import java.util.Map;

public class IDGenProvider {
	
	private Map<String, IDGenerator> genMap;

	public IDGenProvider() {
		genMap = new HashMap<String, IDGenerator>();
	}
	
	public String generateId(String prefix){
		IDGenerator gen = genMap.get(prefix);
		if (gen==null){
			gen = new IDGenerator(prefix);
			genMap.put(prefix, gen);
		}
		return gen.generateId();
	}

}
