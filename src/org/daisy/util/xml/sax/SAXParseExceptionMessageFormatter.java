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
package org.daisy.util.xml.sax;

import org.xml.sax.SAXParseException;

public final class SAXParseExceptionMessageFormatter {

	/**
	 * Format a SAXParseException message into a string.
	 * @param prefix Typically used for prefixing the message; 'Fatal error', "Error", or "Warning".
	 * @param spe The SAXParseException to format.
	 */
	public static String formatMessage(String prefix, SAXParseException spe) {
        StringBuffer sb = new StringBuffer(100);
        sb.append(prefix);
        sb.append(" in ");
        sb.append(spe.getSystemId());
        sb.append(": ");
        sb.append(spe.getMessage());
        sb.append(". Line:" + spe.getLineNumber());
        sb.append(" Column:" + spe.getColumnNumber());
        return sb.toString();
	}
	
}
