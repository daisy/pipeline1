package org_pef_text.pef2text;

public class LineBreaks {
	public static enum Type {DOS, UNIX, MAC, DEFAULT};
	private final String newline;
	
	public LineBreaks(Type t) {
		newline = getString(t);
	}
	
	public String getString() {
		return newline;
	}
	
	public static String getString(Type t) {
        switch (t) {
	    	case UNIX: return "\n";
	    	case DOS: return "\r\n";
	    	case MAC: return "\r";
	    	default: return System.getProperty("line.separator", "\r\n");
        }
	}
}
