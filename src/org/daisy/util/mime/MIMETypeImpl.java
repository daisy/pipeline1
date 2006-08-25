/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.daisy.util.mime;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Implements an atomic MIME type.
 * @author Markus Gylling
 */
public class MIMETypeImpl implements MIMEType {

	// the string identifier, including the optional charset string
	// matches a MimeTypeRegistry entry minus the
	// optional charset string.
	private String mimeString = null;

	// the id in the MimeTypeRegistry.xml representation
			String registryId = null;

	// convenience substrings
	private String droppedParameterPart = null;
	private String parameterPart = null;
	private String contentTypePart = null;
	private String subTypePart = null;

	//the bare unparsed attr values from the xml representation
			String aliasIdrefs = null; // an IDREFS string or null
			String parentIdrefs = null; // an IDREFS string or null
	 		String namePatterns = null; // an space separated string or null
	
	//these collections are built the first time they are requested
	private Map aliases = null; // Map<MimeType>, is null until first request, then may be empty 
	private Map ancestors = null; // Map<MimeType>, is null until first request, then may be empty
	private Map descendants = null; // Map<MimeType>, is null until first request, then may be empty 

	/**
	 * Constructor. This is package private: only way to get a MimeType from the
	 * outside is through the MimeTypeFactory.
	 */

	MIMETypeImpl(String mime, String id, String aliases, String parents, String namePatterns) {
		this.mimeString = mime;
		this.registryId = id;
		if(aliases!=null||aliases.length()>0) this.aliasIdrefs = aliases; // an IDREFS string or ""
		if(parents!=null||parents.length()>0) this.parentIdrefs = parents; // an IDREFS string or ""
		if(namePatterns!=null||namePatterns.length()>0) this.namePatterns = namePatterns; // a space separated string or ""
	}

	public Map getAncestors() throws MIMETypeException {
		if (ancestors == null) { //first request
			ancestors = new HashMap();
			if (parentIdrefs != null) {
				String[] array = parentIdrefs.split("\\s+");
				for (int i = 0; i < array.length; i++) {
					MIMEType m = null;
					try {
						m = MIMETypeRegistry.getInstance().getEntryById(array[i]);
					} catch (MIMETypeRegistryException e) {
						throw new MIMETypeException(e.getMessage(),e);
					}
					if (m != null) {
						ancestors.put(m.getId(),m);
						//recurse
						ancestors.putAll(m.getAncestors());
					}
				}
			}
		}
		return ancestors;
	}

	public boolean hasAncestors() throws MIMETypeException {
		return !getAncestors().isEmpty();
	}

	public boolean isAncestor(MIMEType mime) throws MIMETypeException {
		return getAncestors().containsKey(mime.getId());		
	}

	public Map getAliases() throws MIMETypeException {
		if (aliases == null) { //first request
			aliases = new HashMap();
			if (aliasIdrefs != null) {
				String[] array = aliasIdrefs.split("\\s+");
				for (int i = 0; i < array.length; i++) {
					MIMEType m = null;
					try {
						m = MIMETypeRegistry.getInstance().getEntryById(array[i]);
					} catch (MIMETypeRegistryException e) {
						throw new MIMETypeException(e.getMessage(),e);
					}
					if (m != null) {
						aliases.put(m.getId(),m);
						//recurse
						aliases.putAll(m.getAliases());
					}
				}
			}
		}
		return aliases;
	}
	
	public boolean hasAliases() throws MIMETypeException {
		return !getAliases().isEmpty();
	}

	public boolean isEqualOrAlias(MIMEType mime) throws MIMETypeException {
		return this.getId().equals(mime.getId())
		|| getAliases().containsKey(mime.getId());		
	}

	public Map getDescendants() throws MIMETypeException {
		//return all MimeTypes in the registry that marks this as an ancestor
		if (descendants == null) { //first request
			descendants = new HashMap();
			try {
				Map regMap = MIMETypeRegistry.getInstance().getEntries();
				Iterator i = regMap.keySet().iterator();
				while(i.hasNext()) {
					MIMEType m = (MIMEType) regMap.get(i.next());
					if(m.getAncestors().containsKey(this.getId())) {
						descendants.put(m.getId(),m);
					}
				}
			} catch (MIMETypeRegistryException e) {
				throw new MIMETypeException(e.getMessage(),e);
			}
		}
		return descendants;
	}

	public boolean isDescendant(MIMEType mime) throws MIMETypeException {
		return getDescendants().containsKey(mime.getId());
	}

	public boolean hasDescendants() throws MIMETypeException {
		return !getDescendants().isEmpty();
	}
	
	public boolean isRelative(MIMEType mime) throws MIMETypeException {
		return getDescendants().containsKey(mime.getId())
		||getAncestors().containsKey(mime.getId());
	}
		
	public String getString() {
		return mimeString;
	}

	public String getContentTypePart() {
		// assumes ContentTypePart/SubTypePart(;parameterPart)
		if (contentTypePart == null) {
			contentTypePart = MIMEConstants.getContentTypePart(mimeString);
		}
		return contentTypePart;
	}

	public String getSubTypePart() {
		// assumes ContentTypePart/SubTypePart(;parameterPart)
		if (subTypePart == null) {
			subTypePart = MIMEConstants.getSubTypePart(mimeString);
		}
		return subTypePart;
	}

	public String getParametersPart() {
		// assumes ContentTypePart/SubTypePart(;parameterPart)
		if (parameterPart == null) {
			parameterPart = MIMEConstants.getParametersPart(mimeString);
		}
		return parameterPart;
	}

	public String dropParametersPart() {
		// assumes ContentTypePart/SubTypePart(;parameterPart)
		if (droppedParameterPart == null) {
			droppedParameterPart = MIMEConstants.dropParametersPart(mimeString);
		}
		return droppedParameterPart;
	}

	public String getId() {
		return registryId;
	}

	public Collection getFilenamePatterns() throws MIMETypeException  {		
		return getFilenamePatterns(MIMEType.WIDTH_LOCAL_PLUS_ALIASES);
	}

	public Collection getFilenamePatterns(int width) throws MIMETypeException {
		String regex="\\s+";
		//collect all instances that we should get patterns from
		Map instances = new HashMap();
		
		instances.put(this.getId(),this);
		
		if(width>WIDTH_LOCAL) {
			instances.putAll(this.getAliases());
		}
		
		if(width==WIDTH_LOCAL_PLUS_ALIASES_PLUS_ANCESTORS||width==WIDTH_LOCAL_PLUS_ALIASES_PLUS_DESCENDANTS_PLUS_ANCESTORS) {
			instances.putAll(this.getAncestors());
		}	
		
		if(width==WIDTH_LOCAL_PLUS_ALIASES_PLUS_DESCENDANTS||width==WIDTH_LOCAL_PLUS_ALIASES_PLUS_DESCENDANTS_PLUS_ANCESTORS) {
			instances.putAll(this.getDescendants());
		}

		// build the filename pattern collection from the instances map
		Set patterns = new HashSet();
		for (Iterator iter = instances.keySet().iterator(); iter.hasNext();) {
			MIMETypeImpl mt = (MIMETypeImpl) instances.get(iter.next());
			patterns.addAll(this.splitString(mt.namePatterns,regex));
		}
		return patterns;
	}

	public Collection getFilenamePatterns(int width, int patternType) throws MIMETypeException {
		Collection c = getFilenamePatterns(width);
		if(patternType == FILENAME_PATTERN_REGEX) {
			Set translated = new HashSet();
			for (Iterator iter = c.iterator(); iter.hasNext();) {
				String pt = (String) iter.next();
				translated.add(globToRegex(pt));				
			}
			 return translated;
		}
		return c;
	}
	
	private Collection splitString(String string, String regex){
		Set set = new HashSet();
		if(string != null && !"".equals(string)){
			String[] array = string.split(regex);
			for (int i = 0; i < array.length; i++) {
				set.add(array[i]);
			}
		}
		return set;
	}

	private String globToRegex(String glob){
		//translate all non '*' chars to [CHARchar]
		//translate . to (\\.)
		//translate * to (\\w)
		StringBuilder regex = new StringBuilder();
		regex.append('^');
		for (int i = 0; i < glob.length(); i++) {
		  char ch = glob.charAt(i);
		  if(ch == '*') {
			  regex.append('(');
			  regex.append('\\');
			  regex.append('w');
			  regex.append(')');
			  regex.append('+');
		  }else if (ch=='.'){
			  regex.append('\\');
			  regex.append('.');
		  }else{	  			  
			  regex.append('[');
			  regex.append(Character.toUpperCase(ch));
			  regex.append(Character.toLowerCase(ch));
			  regex.append(']');
		  }
		}		
		regex.append('$');
		return regex.toString();
	}
}
