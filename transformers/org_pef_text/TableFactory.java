package org_pef_text;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class TableFactory {
	public enum TableType {EN_US, EN_GB, DA_DK, SV_SE_CX, UNICODE_BRAILLE};
	public enum EightDotFallbackMethod {MASK, REPLACE, REMOVE}; //, FAIL
	private TableFactory.TableType tableType;
	private EightDotFallbackMethod fallback;
	private char replacement;

	public TableFactory() {
		tableType = TableFactory.TableType.EN_US;
		fallback = EightDotFallbackMethod.values()[0];
		replacement = '\u2800';
	}

	public void setTable(String value) {
		if (value!=null && !"".equals(value)) {
			setTable(TableFactory.TableType.valueOf(value.toUpperCase()));
		}
	}
	public void setTable(TableFactory.TableType value) { tableType = value; }
	public void setFallback(String value) {
		if (value!=null && !"".equals(value)) {
			setFallback(EightDotFallbackMethod.valueOf(value.toUpperCase()));
		}
	}
	public void setFallback(EightDotFallbackMethod value) { fallback = value; }
	/** hex value between 2800-283F
	 * 
	 * @param value
	 * @return
	 */
	public void setReplacement(String value) {
		if (value!=null && !"".equals(value)) {
			setReplacement((char)Integer.parseInt(value, 16));
		}
	}
	public void setReplacement(char value) {
		int val = (value+"").codePointAt(0);
		if (val>=0x2800 && val<=0x283F) {
			replacement = value;
		} else {
			throw new IllegalArgumentException("Replacement value out of range");
		}
	}
	
	public AbstractTable newTable() {
		return newTable(tableType);
	}
	
	public AbstractTable newTable(TableType t) {
		switch (t) {
			case EN_US:
				return new SimpleTable(new String(" A1B'K2L@CIF/MSP\"E3H9O6R^DJG>NTQ,*5<-U8V.%[$+X!&;:4\\0Z7(_?W]#Y)="), Charset.forName("UTF-8"), fallback, replacement, true);
			case EN_GB:
				return new SimpleTable(new String(" a1b'k2l@cif/msp\"e3h9o6r^djg>ntq,*5<-u8v.%[$+x!&;:4\\0z7(_?w]#y)="), Charset.forName("UTF-8"), fallback, replacement, true);
			case DA_DK:
				return new SimpleTable(new String(" a,b.k;l'cif/msp`e:h*o!r~djgæntq@å?ê-u(v\\îøë§xèç^û_ü)z\"à|ôwï#yùé"), Charset.forName("IBM850"), fallback, replacement, true);
			case SV_SE_CX:
				return new SimpleTable(new String(" a,b.k;l^cif/msp'e:h*o!r~djgäntq_å?ê-u(v@îöë§xèç\"û+ü)z=à|ôwï#yùé"), Charset.forName("UTF-8"), fallback, replacement, true);
			case UNICODE_BRAILLE:
				StringBuffer tmp = new StringBuffer();
				for (int i=0; i<256; i++) {
					tmp.append((char)(0x2800+i));
				}
				return new SimpleTable(tmp.toString(), Charset.forName("UTF-8"), fallback, replacement, true);
			default:
				throw new IllegalArgumentException("Cannot find table type " + tableType);
		}
	}

	/**
	 * Creates an appropriate table based on a sample of text.
	 * @param sample a single line of text, without control characters
	 * @return returns a suitable braille table or null if none is found
	 */
	/*
	public static Table newInstance(String sample) {
		Builder b;
		BrailleTranscoder bf;
		//TODO: Fix. Change the exception in toBraille so that it can be caught
		//TODO: String, byte[] or file sample?
		for (TableType t : TableType.values()) {
			b = new Builder();
			b.table(t);
			try {
				bf = b.build();
				bf.toBraille(sample);
			} catch (UnsupportedEncodingException e) {				
				e.printStackTrace();
			}			
		}
		return null;
	}*/

}
