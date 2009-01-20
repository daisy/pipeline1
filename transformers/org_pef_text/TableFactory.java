package org_pef_text;

import java.nio.charset.Charset;

/**
 * The table factory will create an Abstract table based on the factory's 
 * current settings. 
 * 
 * @author Joel Håkansson, TPB
 *
 */
public class TableFactory {
	public enum TableType {EN_US, EN_GB, DA_DK, DE_DE, ES_ES, IT_IT_FIRENZE, SV_SE_CX, UNICODE_BRAILLE};
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
	
	/**
	 * Get a new table instance based on the factory's current settings.
	 * @return returns a new table instance.
	 */
	public AbstractTable newTable() {
		return newTable(tableType);
	}
	
	/**
	 * Get a new table instance based on the factory's current settings.
	 * @param t the type of table to return, this will override the factory's default table type.
	 * @return returns a new table instance.
	 */
	public AbstractTable newTable(TableType t) {
		switch (t) {
			case EN_US:
				return new SimpleTable(new String(" A1B'K2L@CIF/MSP\"E3H9O6R^DJG>NTQ,*5<-U8V.%[$+X!&;:4\\0Z7(_?W]#Y)="), Charset.forName("UTF-8"), fallback, replacement, true);
			case EN_GB:
				return new SimpleTable(new String(" a1b'k2l@cif/msp\"e3h9o6r^djg>ntq,*5<-u8v.%[$+x!&;:4\\0z7(_?w]#y)="), Charset.forName("UTF-8"), fallback, replacement, true);
			case DA_DK:
				return new SimpleTable(new String(" a,b.k;l'cif/msp`e:h*o!r~djgæntq@å?ê-u(v\\îøë§xèç^û_ü)z\"à|ôwï#yùé"), Charset.forName("IBM850"), fallback, replacement, true);
			case DE_DE:
				return new SimpleTable(new String(" a,b.k;l\"cif|msp!e:h*o+r>djg`ntq'1?2-u(v$3960x~&<5/8)z={_4w7#y}%"), Charset.forName("UTF-8"), fallback, replacement, true);
			case ES_ES:
				return new SimpleTable(new String(" a,b.k;l'cif/msp@e:h}o+r^djg|ntq_1?2-u<v{3960x$&\"5*8>z=(%4w7#y)\\"), Charset.forName("UTF-8"), fallback, replacement, true);
			case IT_IT_FIRENZE:
			    return new SimpleTable(new String(" a,b'k;l\"cif/msp)e:h*o!r%djg&ntq(1?2-u<v#396^x\\@+5.8>z=[$4w7_y]0"),  Charset.forName("UTF-8"), fallback, replacement, true);
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
}
