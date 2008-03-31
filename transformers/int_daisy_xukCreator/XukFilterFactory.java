package int_daisy_xukCreator;

import int_daisy_xukCreator.impl.Daisy202toObi;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.daisy.util.fileset.Fileset;

/**
 * A factory producing filters that can generate XUK files from various input.
 * @author Markus Gylling
 */
public class XukFilterFactory {

	private Set<XukFilter> registry; 
	
	private XukFilterFactory() {
		registry = new HashSet<XukFilter>();
		registry.add(new Daisy202toObi());
	}
	
	static XukFilterFactory newInstance() {		
		return new XukFilterFactory();
	}
	
	/**
	 * Produce a XukFilter that support given input Fileset type
	 * with any given constraints as expressed in parameters.
	 * @return a compatible XukFilter if available, else null.
	 */
	XukFilter newFilter(Fileset input, Map<String,String> parameters) {
		for(XukFilter filter : registry) {
			if(filter.supports(input, parameters))
				return filter;
		}
		return null;
	}

}
