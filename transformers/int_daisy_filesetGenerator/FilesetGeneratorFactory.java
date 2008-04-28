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
package int_daisy_filesetGenerator;

import int_daisy_filesetGenerator.impl.d202.D202TextOnlyGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.daisy.util.fileset.Fileset;

/**
 * Factory producing instances of IFilesetGenerator.
 * @author Markus Gylling
 */
public class FilesetGeneratorFactory {
	Map<Property, Object> mProperties;
	Set<IFilesetGenerator> mRegistry;
	
	FilesetGeneratorFactory() {
		mProperties = new HashMap<Property, Object>();
		mRegistry = new HashSet<IFilesetGenerator>();
		//TODO implement dynamic population of registry using Services API
		mRegistry.add(new D202TextOnlyGenerator());
	}
	
	static FilesetGeneratorFactory newInstance() {
		return new FilesetGeneratorFactory();
	}
	
	/**
	 * Produce an instance of IFilesetGenerator given properties set on this factory.
	 * @return An IFilesetGenerator instance, or null if none could be found supporting the current property configuration.
	 */
	@SuppressWarnings("unchecked")
	IFilesetGenerator newGenerator() {
		
		List<Fileset> input = (List<Fileset>) mProperties.get(Property.INPUT);
		if(input==null) throw new IllegalStateException(Property.INPUT.name());
		OutputType output = (OutputType) mProperties.get(Property.OUTPUT);
		if(output==null) throw new IllegalStateException(Property.OUTPUT.name());
		
		for(IFilesetGenerator gen : mRegistry) {
			try{
				gen.configure(
						input, 
							output,
								(Map<String,Object>)mProperties.get(Property.CONFIG));
				return gen;
			}catch (FilesetGeneratorException e) {
				continue;
			}
		}
		return null;
	}
	
	/**
	 * The following properties are supported:
	 * <dl>
	 *  <dt>INPUT</dt>
	 *  <dd>The object must be an instance of <code>List&lt;Fileset&gt;</code></dd>
	 *  <dt>OUTPUT</dt>
	 *  <dd>The object must be an instance of OutputType</dd>
	 *  <dt>CONFIG</dt>
	 *  <dd>The object must be an instance of Map&lt;String,Object&gt;</dd>
	 * </dl>
	 */
	void setProperty(Property key, Object value) {
		
		if(key == Property.INPUT) {
			try{
				List<?> input = (List<?>)value;
				for(Object o : input) {
					@SuppressWarnings("unused")
					Fileset f = (Fileset)o;
				}
			}catch (ClassCastException e) {
				throw new IllegalArgumentException(e.getLocalizedMessage(),e);
			}			
		}
		
		else if(key == Property.OUTPUT && !(value instanceof OutputType)) {
			throw new IllegalArgumentException();
		}
		
		else if(key == Property.CONFIG) {
			try{
				Map<?,?> map = (Map<?,?>)value;
				for(Object o : map.keySet()) {
					@SuppressWarnings("unused")
					String s = (String)o;
				}
			}catch (ClassCastException e) {
				throw new IllegalArgumentException(e.getLocalizedMessage(),e);
			}		
		}

		mProperties.put(key, value);
	}
	
	enum Property {
		INPUT,
		OUTPUT,
		CONFIG;
	}
	
	public enum OutputType {
		D202_TEXTONLY;
	}
}
