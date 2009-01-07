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
package org.daisy.pipeline.core.script.datatype;

import java.io.File;

/**
 * A datatype for a value consisting of multiple files.
 * @author Markus Gylling
 */
public class FilesDatatype extends Datatype {
	
	private static final long serialVersionUID = 1L;
	
	public static final char SEPARATOR_CHAR = ';'; 
	public static final String SEPARATOR_STRING = ";"; 
	private String mime;
	private String type;
	
	/**
	 * Constructor. Allowed values for the type attribute are "input" and "output".
	 * @param mime the mime type
	 * @param type 
	 */
	public FilesDatatype(String mime, String type) {
		super(Type.FILES);
		this.mime = mime;
		this.type = type;
	}

	/**
	 * Gets the mime type
	 * @return the mime type
	 */
	public String getMime() {
		return mime;
	}
	
	/**
	 * @return true if this is an input file datatype, false otherwise
	 */
	public boolean isInput() {
		return "input".equals(type);
	}
	
	/**
	 * @return true if this is an output file datatype, false otherwise
	 */
	public boolean isOutput() {
		return !isInput();
	}

	@Override
	public void validate(String value) throws DatatypeException {
		if (isInput()) {			
			String[] values = value.split(SEPARATOR_STRING);
			for (String string : values) {
				File file = new File(string.trim());
				if (!file.exists()) {
					throw new DatatypeException("Input file '" + file.toString() + "' does not exist.");
				}				
			}
		}		
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (isInput()) {
            builder.append("input ");
        } else {
            builder.append("output ");
        }
        if ("application/x-filesystemDirectory".equals(getMime())) {
            builder.append("directories");
        } else {
            builder.append("files (").append(getMime()).append(")");
        }
		return builder.toString();
	}
}
