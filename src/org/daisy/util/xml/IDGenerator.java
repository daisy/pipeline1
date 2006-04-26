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
	
	IDGenerator () {
		
	}
	
	IDGenerator (String prefix) throws Exception {
		
		for (int i = 0; i < prefix.length(); i++) {
			if(i==0) {
				if(CharUtils.isXMLNameFirstCharacter(prefix.charAt(i))){
					  throw new Exception("first character is not valid in an xml name");	
				}
			}else{
				if(CharUtils.isXMLNameCharacter(prefix.charAt(i))){
					  throw new Exception("character is not valid in an xml name");	
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
