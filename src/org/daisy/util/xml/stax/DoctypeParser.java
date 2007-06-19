/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2007  Daisy Consortium
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
package org.daisy.util.xml.stax;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse a DOCTYPE declaration. This class can be used to parse the document
 * type declaration that is returned by the StAX DTD event. It is possible
 * to get the values of the root element (declared in the DOCTYPE), the public
 * identifier, the system identifier and the internal subset. All data are
 * returned as strings.
 *  
 * @author Linus Ericson
 */
public class DoctypeParser {
	private final static Pattern sDtdPattern = Pattern.compile("<!DOCTYPE\\s+(\\w+)(\\s+((SYSTEM\\s+(\"[^\"]*\"|'[^']*')|PUBLIC\\s+(\"[^\"]*\"|'[^']*')\\s+(\"[^\"]*\"|'[^']*'))))?\\s*(\\[.*\\])?\\s*>", Pattern.DOTALL);
	private final static int sROOT = 1;
	private final static int sTYPE = 4;
	private final static int sPUBLIC_PUBLICID = 6;
	private final static int sPUBLIC_SYSTEMID = 7;
	private final static int sSYSTEM_SYSTEMID = 5;
	private final static int sINTERNAL = 8;
	
	private String mPublicId = null;
	private String mSystemId = null;
	private String mInternal = null;
	private String mRootElem = null;
	
	/**
	 * Constructor.
	 * @param doctype the DOCTYPE string to parse
	 */
	public DoctypeParser(String doctype) {		
		Matcher matcher = sDtdPattern.matcher(doctype);
		
        String pub = "";
        String sys = "";
        if (matcher.matches()) {
        	if (matcher.group(sROOT) != null) {
        		mRootElem = matcher.group(sROOT);
        	}
            if (matcher.group(sTYPE).startsWith("PUBLIC")) {
                pub = matcher.group(sPUBLIC_PUBLICID);
                sys = matcher.group(sPUBLIC_SYSTEMID);
                pub = pub.substring(1, pub.length() - 1);
                sys = sys.substring(1, sys.length() - 1);
                mPublicId = pub;
                mSystemId = sys;               
            } else {
            	sys = matcher.group(sSYSTEM_SYSTEMID);                        
            	sys = sys.substring(1, sys.length() - 1);
            	mSystemId = sys;
            }
            if (matcher.group(sINTERNAL) != null) {
            	String internal = matcher.group(sINTERNAL);
            	internal = internal.trim();
            	internal = internal.substring(1, internal.length() -1);
            	mInternal = internal;
            }
        } 
	}
	
	/**
	 * Get the PUBLIC ID
	 * @return the public identifier
	 */
	public String getPublicId() {
		return mPublicId;
	}
	
	/**
	 * Get the SYSTEM ID
	 * @return the system identifier
	 */
	public String getSystemId() {
		return mSystemId;
	}
	
	/**
	 * Get the internal subset
	 * @return the internal subset
	 */
	public String getInternalSubset() {
		return mInternal;
	}
	
	/**
	 * Get the root element name as defined by the doctype declaration
	 * @return the root element name
	 */
	public String getRootElem() {
		return mRootElem;
	}
}
