package org_pef_text;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;

/**
 *  
 * 
 * @author  Joel Hakansson, TPB
 * @version 1 sep 2008
 * @since 1.0
 */
public class BrailleFormat {
	public enum Mode {EN_US, DA_DK, SV_SE_CX, UNICODE_BRAILLE};
	public enum EightDotFallbackMethod {MASK, REPLACE}; //, REMOVE
	private final String tableDef;
	private final Charset charset;
	private HashMap<Character, Character> b2t;
	private HashMap<Character, Character> t2b;
	private final boolean eightDot;
	private EightDotFallbackMethod fallback;
	private char replacement;
	
	public static class Builder {
		// required params
		private Mode mode;
		// optional params
		private EightDotFallbackMethod fallback = EightDotFallbackMethod.values()[0];
		private char replacement = '\u2800';
		
		public Builder(Mode mode) {
			this.mode = mode;
		}

		public Builder fallback(EightDotFallbackMethod value) { fallback = value; return this; }
		public Builder replacement(char value) {
			int val = (value+"").codePointAt(0);
			if (val>=0x2800 && val<=0x283F) {
				replacement = value;
				return this;
			} else {
				throw new IllegalArgumentException("Replacement value out of range");
			}
		}
		
		public BrailleFormat build() throws UnsupportedEncodingException { return new BrailleFormat(this); }
		
	}
	
	private BrailleFormat(Builder builder) throws UnsupportedEncodingException {
		fallback = builder.fallback;
		replacement = builder.replacement;

		b2t = new HashMap<Character, Character>();
		t2b = new HashMap<Character, Character>();
		//lower case def.
		switch (builder.mode) {
			case EN_US:
				this.charset = Charset.forName("UTF-8");
				//this.tableDef = new String(" a1b'k2l@cif/msp\"e3h9o6r^djg>ntq,*5<-u8v.%[$+x!&;:4\\0z7(_?w]#y)=");
				this.tableDef = new String(" A1B'K2L@CIF/MSP\"E3H9O6R^DJG>NTQ,*5<-U8V.%[$+X!&;:4\\0Z7(_?W]#Y)=");
				break;
			case DA_DK:
				this.charset = Charset.forName("IBM850");
				this.tableDef = new String(" a,b.k;l'cif/msp`e:h*o!r~djgæntq@å?ê-u(v\\îøë§xèç^û_ü)z\"à|ôwï#yùé");
				break;
			case SV_SE_CX:
				this.charset = Charset.forName("UTF-8");
				this.tableDef = new String(" a,b.k;l^cif/msp'e:h*o!r~djgäntq_å?ê-u(v@îöë§xèç\"û+ü)z=à|ôwï#yùé");
				break;
			case UNICODE_BRAILLE:
				this.charset = Charset.forName("UTF-8");
				StringBuffer tmp = new StringBuffer();
				for (int i=0; i<256; i++) {
					tmp.append((char)(0x2800+i));
				}
				this.tableDef = tmp.toString();
				break;
			default:
					// should never get here
					tableDef = "";
					charset = null;
		}
		this.eightDot=tableDef.length()==256;
		int i = 0;
		char b;
		for (char t : tableDef.toCharArray()) {
			b = (char)(0x2800+i);
			put(b, t);
			i++;
		}
	}
	
	private void put(char braille, char glyph) {
		t2b.put(Character.toLowerCase(glyph), braille);
		b2t.put(braille, glyph);
	}
	
	/**
	 * Detects the braille mode based on a sample of text.
	 * @param sample a single line of text, without control characters
	 * @return returns a suitable braille mode or null if none is found
	 */
	public static BrailleFormat.Mode detect(String sample) {
		//TODO: Fix. Change the exception in toBraille so that it can be caught
		Builder b;
		BrailleFormat bf;
		for (BrailleFormat.Mode mode : BrailleFormat.Mode.values()) {
			b = new Builder(mode);
			try {
				bf = b.build();
				bf.toBraille(sample);
				
			} catch (UnsupportedEncodingException e) {				
				e.printStackTrace();
			}
		}
		return null;
	}

	public char toBraille(char c) {
		//TODO: add case sensitive option
		c = Character.toLowerCase(c);
		if (t2b.get(c)==null) throw new IllegalArgumentException("Character '" + c + "' not found.");
		return (t2b.get(c));
	}

	public String toBraille(String s) {
		StringBuffer sb = new StringBuffer();
		for (char c : s.toCharArray()) {
			sb.append(toBraille(c));
		}
		return sb.toString();
	}

	public char toText(char braillePattern) {
		// TODO: add option to output uppercase
		if (b2t.get(braillePattern)==null) {
			int val = (braillePattern+"").codePointAt(0);
			if (val>=0x2840 && val<=0x28FF) {
				switch (fallback) {
				case MASK:
					return toText((char)(val&0x283F));
				case REPLACE:
					if (b2t.get(replacement)!=null) {
						return toText(replacement);
					} else {
						throw new IllegalArgumentException("Replacement char not found.");
					}
				//FIXME: add support for remove action
				}
			} else {			
				throw new IllegalArgumentException("Braille pattern '" + braillePattern + "' not found.");
			}
		}
		return (b2t.get(braillePattern));
	}

	public String toText(String s) {
		StringBuffer sb = new StringBuffer();
		for (char c : s.toCharArray()) {
			sb.append(toText(c));
		}
		return sb.toString();
	}

	public boolean supportsEightDot() {
		return eightDot;
	}

	/**
	 * @deprecated
	 * @return
	 */
	public char[] getCharTable() {
		return tableDef.toCharArray();
	}
	
	/**
	 * Get the preferred charset for this braille format
	 * @return
	 */
	public Charset getPreferredCharset() {
		return charset;
	}
	
	/*
	  				table = new int[] {
						32,
						97,
						44,
						98,
						46,
						107,
						59,
						108,
						39,
						99,
						105,
						102,
						47,
						109,
						115,
						112,
						96,
						101,
						58,
						104,
						42,
						111,
						33,
						114,
						126,
						100,
						106,
						103,
						145,
						110,
						116,
						113,
						64,
						134,
						63,
						136,
						45,
						117,
						40,
						118,
						92,
						140,
						155,
						137,
						245,
						120,
						138,
						135,
						94,
						150,
						95,
						129,
						41,
						122,
						34,
						133,
						124,
						147,
						119,
						139,
						35,
						121,
						151,
						130
					};*/

}
