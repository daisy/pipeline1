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
