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

package org.daisy.util.xml;

import org.daisy.util.i18n.CharUtils;

/**
 * An object that during its lifetime
 * guarantees to generate unique (in relation to itself) 
 * and XML Name compliant strings.
 * @author Markus Gylling
 */
public class IDGenerator {
	private String prefix = "id_";
	private int counter = 0;
	
	public IDGenerator () {
		
	}
	
	public IDGenerator (String prefix) {
		
		for (int i = 0; i < prefix.length(); i++) {
			if(i==0) {
				if(!CharUtils.isXMLNameFirstCharacter(prefix.charAt(i))){
					  throw new IllegalArgumentException("first character is not valid in an xml name");	
				}
			}else{
				if(!CharUtils.isXMLNameCharacter(prefix.charAt(i))){
					  throw new IllegalArgumentException("character is not valid in an xml name");	
				}				
			}
		}
		
		this.prefix = prefix;
	}
	
	public String generateId(){
		counter++;		
		return this.prefix + counter;
	}
	

}
