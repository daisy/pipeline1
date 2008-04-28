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
 */package org.daisy.util.fileset.validation.delegate.impl;


/**
 * Makes sure all XML files in a file set use a specific
 * character encoding. If no encoding is specified in the
 * constructor, the validation delegate looks for utf-8 by
 * default.
 * @author Linus Ericson
 */
public class XMLEncodingDelegate extends AbstractXMLDeclarationDelegate {
	
	public XMLEncodingDelegate() {
		super("1.0", "utf-8", null, true, true, true, false);
	}
	
	public XMLEncodingDelegate(String encoding) {
		super("1.0", encoding, null, true, true, true, false);
	}

}
