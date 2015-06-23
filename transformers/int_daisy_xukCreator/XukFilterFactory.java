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
	XukFilter newFilter(Fileset input, Map<String,Object> parameters) {
		for(XukFilter filter : registry) {
			if(filter.supports(input, parameters))
				return filter;
		}
		return null;
	}

}
