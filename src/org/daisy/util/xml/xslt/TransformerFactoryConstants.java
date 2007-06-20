package org.daisy.util.xml.xslt;

/**
 * String constants for our most beloved XSLT TranformerFactories.
 * @author Markus Gylling
 */
public class TransformerFactoryConstants {
	
	public static String SAXON8 = "net.sf.saxon.TransformerFactoryImpl";
	public static String SAXON = "com.icl.saxon.TransformerFactoryImpl";
	public static String XALAN = "org.apache.xalan.processor.TransformerFactoryImpl";
	public static String XALAN_XSLTC_TRANSLET = "org.apache.xalan.xsltc.trax.TransformerFactoryImpl";
	
	/*
	 * The below should not be used since its a Sun JRE specific identifier
	 */
	//public static String XALAN_XSLTC_INTERNAL = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";
		
}
