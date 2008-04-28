/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
package org.daisy.util.xml.validation;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Parses and returns information from a string returned from 
 * a Schematron assertion (typically encapsulated in the ErrorHandler.SAXException.getMessage() method).</p>
 * 
 * <p>
 * The following rules apply to the usage of this class:</p>
 * <ul>
 * <li>A singular message is a string delimited by square brackets: <code>[message]</code></li>
 * <li>Any number of messages can occur in the same instantiator string.</li>
 * <li>In the instantiator string, the first message must be exactly: [sch]</li>
 * <li>All messages except the first must have two parts, 
 * separated by two colons: name part and content part: <code>[name::content]</code></li> 
 * <li>Square bracket characters are not allowed inside messages, only as message delimiters.</li>
 * </ul>
 * <p>An example schematron message in its Schematron document representation:<br/>
 * <code>[sch][id::anId][msg::a message]</code>
 * </p>
 * @author Markus Gylling
 */
public class SchematronMessage {

	private Map<String,String> messages = new HashMap<String,String>();
	private static String schToken = "[sch]";
	
	public SchematronMessage(String string) throws ValidationException{
		string = string.trim();
		if (string.indexOf(schToken)>=0){
			string = string.replaceAll("\\[sch\\]","");		
					
			char[] array = string.toCharArray();
			boolean open = false;
			//StringBuilder sb = new StringBuilder();
            //20060905 Piotr Kiernicki: org.daisy.util should still be jre_1.4 compatible
            StringBuffer sb = new StringBuffer();
			
			for (int i = 0; i < array.length; i++) {
				char c = array[i];
				
				if (c==']'){
					open = false;
					int split = sb.indexOf("::");
					if (split>=0) {
						try{
							messages.put(sb.substring(0,split),sb.substring(split+2));
						}catch (Exception e) {
							throw new ValidationException(e.getMessage(),e);
						}
												
					}else{
						throw new ValidationException("not a valid schematron message: " + string);
					}
					continue;
				}
				
				if(open) {
					sb.append(c);
					continue;
				}
				
				if(c=='[') {
					open = true;
					sb.delete(0,sb.length());
				} 
			}			
		}else{
			throw new ValidationException("not a valid schematron message: " + string);
		}
	}
	
	/**
	 * @param messageName the name part of a message ("name" from [name::content])
	 * @return the content part of a message ("content" from [name::content])
	 */
	public String getMessage(String messageName) {
		return messages.get(messageName);
	}
	
	/**
	 * Static helper to determine if an arbitrary string may be a schematron message string
	 */
	public static boolean isMessage(String string) {
		return string.indexOf(schToken)>=0;
	}
}
