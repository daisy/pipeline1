package org_pef_text.pef2text;

import java.io.OutputStream;
import java.util.HashMap;

import org_pef_text.TableFactory;
import org_pef_text.pef2text.Paper.PaperSize;

/**
 * 
 * The embosser factory can build settings for the
 * EmbosserTypes.
 * 
 * @author  Joel Hakansson, TPB
 * @version 10 okt 2008
 * @since 1.0
 */
public class EmbosserFactory {
	public static enum EmbosserType {
		NONE, 
		INDEX_EVEREST, 
		INDEX_BASIC, 
		INDEX_EVEREST_V3, 
		INDEX_BASIC_D_V3,
		BRAILLO_200,
		BRAILLO_400_S, 
		BRAILLO_400_SR};

	private HashMap<String, String> settings;
	private EmbosserType t;
	private PaperSize s;
	
	public EmbosserFactory() {
		settings = new HashMap<String, String>();
		t = EmbosserType.NONE;
		s = PaperSize.UNDEFINED;
	}

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for this key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return previous value associated with specified key, or <tt>null</tt>
     *	       if there was no mapping for key.  A <tt>null</tt> return can
     *	       also indicate that the HashMap previously associated
     *	       <tt>null</tt> with the specified key.
     */
	public String setProperty(String key, String value) { return settings.put(key, value); }
	
	public String getProperty(String key) { return settings.get(key); }
	
	public void setEmbosserType(EmbosserType t) {
		this.t = t;
	}
	
	public void setPaperSize(PaperSize s) {
		this.s = s;
	}
	
	public EmbosserType getEmbosserType() {
		return t;
	}
	
	public AbstractEmbosser newEmbosser(OutputStream os) throws UnsupportedPaperException, EmbosserFactoryException {
		ConfigurableEmbosser.Builder b;
		TableFactory btb;
		if (getProperty("cellWidth")!=null && getProperty("cellWidth")!="") { 
			if (!getProperty("cellWidth").equals("6")) {
				throw new EmbosserFactoryException("Changing cell width has not been implemented.");
			}
		} else {
			setProperty("cellWidth", "6");
		}
		if (getProperty("cellHeight")!=null && getProperty("cellHeight")!="") { 
			if (!getProperty("cellHeight").equals("10")) {
				throw new EmbosserFactoryException("Changing cell height has not been implemented.");
			}
		} else {
			setProperty("cellHeight", "10");
		}

		Paper paper = Paper.newPaper(s);
		String unsupportedPaperFormat = "Unsupported paper size for " + t;
		switch (t) {
			case NONE:
				btb = new TableFactory();
				btb.setTable(settings.get("table"));
				btb.setFallback(settings.get("fallback"));
				btb.setReplacement(settings.get("replacement"));
				b = new ConfigurableEmbosser.Builder(os, btb.newTable());
				b.breaks(settings.get("breaks"));
				b.padNewline(settings.get("padNewline"));
				b.supports8dot(true);
				b.supportsDuplex(true);
				b.supportsAligning(false);
				// All paper sizes are supported
				b.setPaper(paper);
				return b.build();
			case INDEX_BASIC:
			case INDEX_EVEREST:
				btb = new TableFactory();
				btb.setTable(TableFactory.TableType.EN_US);
				btb.setFallback(settings.get("fallback"));
				btb.setReplacement(settings.get("replacement"));
				b = new ConfigurableEmbosser.Builder(os, btb.newTable());
				b.breaks(LineBreaks.Type.DOS);
				b.padNewline(ConfigurableEmbosser.Padding.NONE);
				b.supportsDuplex(true);
				b.supportsAligning(true);
    			b.footer(new byte[]{0x1a});
    			b.setPaper(paper);
				// Supports paper formats smaller than 100 cells wide
				b.header(getIndexV2Header(b.getWidth(), b.getHeight()));
    			return b.build();
			case INDEX_EVEREST_V3:
				btb = new TableFactory();
				btb.setTable(TableFactory.TableType.EN_US);
				btb.setFallback(settings.get("fallback"));
				btb.setReplacement(settings.get("replacement"));
				b = new ConfigurableEmbosser.Builder(os, btb.newTable())
					.breaks(LineBreaks.Type.DOS)
					.padNewline(ConfigurableEmbosser.Padding.NONE)
					.supportsDuplex(true)
					.supportsAligning(true)
					.footer(new byte[]{0x1a})
					.setPaper(paper);
				// Supports paper formats smaller than 100 cm in either direction
				b.header(getEverestV3Header(paper));
    			return b.build();
			case INDEX_BASIC_D_V3:
				btb = new TableFactory();
				btb.setTable(TableFactory.TableType.EN_US);
				btb.setFallback(settings.get("fallback"));
				btb.setReplacement(settings.get("replacement"));
				b = new ConfigurableEmbosser.Builder(os, btb.newTable())
					.breaks(LineBreaks.Type.DOS)
					.padNewline(ConfigurableEmbosser.Padding.NONE)
					.supportsDuplex(true)
					.supportsAligning(true)
					.footer(new byte[]{0x1a})
					.setPaper(paper);
				switch (s) {
					case W210MM_X_H10INCH:
						b.header(new byte[]{
								0x1b, 0x44, 'T', 'D', '0', ';',
								0x1b, 0x44, 'P', 'L', '1', '0', '0', ';', // 10 inch
								0x1b, 0x44, 'P', 'W', '0', '8', '2', ';', // Rounding to the next larger fraction which is 8,333 inch, because 21,0 cm is exactly 35 * 6 mm 
								0x1b, 0x44, 'D', 'P', '2', ';',
								});
						break;
					case W210MM_X_H11INCH:
						b.header(new byte[]{
								0x1b, 0x44, 'T', 'D', '0', ';',
								0x1b, 0x44, 'P', 'L', '1', '1', '0', ';', // 11 inch
								0x1b, 0x44, 'P', 'W', '0', '8', '2', ';', // Rounding to the next larger fraction which is 8,333 inch, because 21,0 cm is exactly 35 * 6 mm 
								0x1b, 0x44, 'D', 'P', '2', ';',
								});
						break;
					case W210MM_X_H12INCH:
						b.header(new byte[]{
								0x1b, 0x44, 'T', 'D', '0', ';',
								0x1b, 0x44, 'P', 'L', '1', '2', '0', ';', // 12 inch
								0x1b, 0x44, 'P', 'W', '0', '8', '2', ';', // Rounding to the next larger fraction which is 8,333 inch, because 21,0 cm is exactly 35 * 6 mm 
								0x1b, 0x44, 'D', 'P', '2', ';',
								});
						break;
					default:
						throw new UnsupportedPaperException(unsupportedPaperFormat);
				}
    			return b.build();
			case BRAILLO_200: case BRAILLO_400_S: case BRAILLO_400_SR:
				btb = new TableFactory();
				btb.setTable(settings.get("table"));
				btb.setFallback(settings.get("fallback"));
				btb.setReplacement(settings.get("replacement"));
				b = new ConfigurableEmbosser.Builder(os, btb.newTable())
					.breaks(LineBreaks.Type.DOS)
					.padNewline(ConfigurableEmbosser.Padding.BEFORE)
					.supportsDuplex(true)
					.supportsAligning(true)
					.setPaper(paper);
				b.header(getBrailloHeader(b.getWidth(), paper));
    			return b.build();
		}
		throw new IllegalArgumentException("Cannot find embosser type " + t);
	}
	
	private static byte[] getIndexV2Header(int width, int height) throws UnsupportedPaperException {
		if (width > 99) { 
			throw new UnsupportedPaperException("Paper too wide: " + width); 
		}
		byte[] w = toBytes(width-23, 2);
		/*return new byte[]{
		0x1b, 0x0f, 0x02, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c,
		0x1b, 0x0f, 
		0x1b, 0x0f, 0x02, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x39, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 
		0x1b, 0x0f, 
		0x1b, 0x0f, 0x02, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c,
		0x1b, 0x0f, 
		0x00};*/
		return new byte[]{
				//                                                                                                                            width
				//                0           1           2           3           4           5           6           7           8           9                 10
				0x1b, 0x0f, 0x02, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, w[0], w[1], 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x30, 0x2c, 0x30, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x31, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c, 0x78, 0x2c,
				0x1b, 0x0f};
	}
	
	private static byte[] getEverestV3Header(Paper paper) throws UnsupportedPaperException {
		int width = (int)Math.round(paper.getWidth());
		int height = (int)Math.round(paper.getHeight());
		if (width > 999 || height > 999) {
			throw new UnsupportedPaperException("Paper too wide or high: " + width + " x " + height);
		}
		byte[] w = toBytes(width, 3);
		byte[] h = toBytes(height, 3);
		return new byte[]{
			0x1b, 0x44, 'T', 'D', '0', ';',
			0x1b, 0x44, 'P', 'L', h[0], h[1], h[2], ';',
			0x1b, 0x44, 'P', 'W', w[0], w[1], w[2], ';',
			0x1b, 0x44, 'D', 'P', '2', ';',
		};
	}
	
	// B200, B400S, B400SR
	// Supported paper width (chars): 10 <= width <= 42
	// Supported paper height (inches): 4 <= height <= 14
	private static byte[] getBrailloHeader(int width, Paper paper) throws UnsupportedPaperException {
		// Round to the closest possible higher value, so that all characters fit on the page
		int height = (int)Math.ceil(2*paper.getHeight()/Paper.INCH_IN_MM);
		if (width > 42 || height > 28) { 
			throw new UnsupportedPaperException("Paper too wide or high: " + width + " chars x " + height / 2d + " inches."); 
		}
		if (width < 10 || height < 8) {
			throw new UnsupportedPaperException("Paper too narrow or short: " + width + " chars x " + height / 2d + " inches.");
		}
		byte[] w = toBytes(width, 2);
		byte[] h = toBytes(height, 2);
		return new byte[]{
			0x1b, 'S', '1',
			0x1b, 'C', '1',
			0x1b, 'J', '0',
			0x1b, 'A', h[0], h[1],
			0x1b, 'B', w[0], w[1],
			0x1b, 'N', '0',
			0x1b, 'R', '0'
			};
	}
	
	private static byte[] toBytes(int val, int size) {
		StringBuffer sb = new StringBuffer();
		String s = "" + val;
		if (s.length()>size) {
			throw new IllegalArgumentException("Number is too big.");
		}
		for (int i=0; i<size-s.length(); i++) {
			sb.append('0');
		}
		sb.append(s);
		return sb.toString().getBytes();
	}

}
