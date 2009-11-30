package org_pef_text.pef2text;

import java.io.IOException;
import java.io.OutputStream;

import org_pef_text.AbstractTable;

/**
 * 
 * Create a configurable embosser.
 * This class can handle standard 6-dot embossers using us ascii.
 * 
 * @author  Joel Hakansson, TPB
 * @version 22 okt 2008
 * @since 1.0
 */
public class ConfigurableEmbosser implements AbstractEmbosser {
	public static enum Padding {BOTH, BEFORE, AFTER, NONE};
	
	private boolean supports8dot;
	private boolean supportsDuplex;
	private int rowgap;
	private int maxHeight;
	private int maxWidth;
	private LineBreaks breaks;
	private Padding padNewline;
	private OutputStream os;
	private AbstractTable bf;
	private byte[] header;
	private byte[] footer;
	private int currentPage;
	private boolean currentDuplex;
	private boolean isOpen;
	private boolean isClosed;
	private int charsOnRow;
	private int rowsOnPage;
	
	public static class Builder {
		// required params
		private OutputStream os;
		private AbstractTable bt;
		
		// optional params
		private boolean supports8dot=false;
		private boolean supportsDuplex=false;
		private int maxHeight=Integer.MAX_VALUE;
		private int maxWidth=Integer.MAX_VALUE;
		private LineBreaks.Type breaks = LineBreaks.Type.DEFAULT;
		private Padding padNewline = Padding.values()[0];
		private byte[] header = new byte[0];
		private byte[] footer = new byte[0];
		
		public Builder(OutputStream os, AbstractTable bt) {
			this.os = os;
			this.bt = bt;
		}
		
		public Builder supports8dot(boolean val) { supports8dot = val; return this; }
		public Builder supportsDuplex(boolean val) { supportsDuplex = val; return this; }
		public Builder height(int value) { 
			if (value<0) { throw new IllegalArgumentException("Positive integer expected."); }
			maxHeight = value;
			return this;
		}
		public Builder width(int value) { 
			if (value<0) { throw new IllegalArgumentException("Positive integer expected."); }
			maxWidth = value;
			return this;
		}
		public Builder breaks(String value) { 
			if (value!=null && !"".equals(value)) {
				return breaks(LineBreaks.Type.valueOf(value.toUpperCase()));
			}
			return this;
		}
		public Builder breaks(LineBreaks.Type value) {
			breaks = value; return this;
		}
		public Builder padNewline(String value) {
			if (value!=null && !"".equals(value)) {
				return padNewline(Padding.valueOf(value.toUpperCase()));
			}
			return this;
		}
		public Builder padNewline(Padding value) { padNewline = value; return this; }
		public Builder header(byte[] value) { header = value; return this; }
		public Builder footer(byte[] value) { footer = value; return this; }

		public ConfigurableEmbosser build() {
			return new ConfigurableEmbosser(this);
		}
	}
	
	private ConfigurableEmbosser(Builder builder) {
		bf = builder.bt;
		supports8dot = builder.supports8dot;
		supportsDuplex = builder.supportsDuplex;
		maxWidth = builder.maxWidth;
		maxHeight = builder.maxHeight;
		breaks = new LineBreaks(builder.breaks);
		padNewline = builder.padNewline;
		rowgap = 0;
		header = builder.header;
		footer = builder.footer;
		currentPage = 1;
		os = builder.os;
		isOpen = false;
		isClosed = false;
		charsOnRow = 0;
		rowsOnPage = 0;
	}
	
	public LineBreaks getLinebreakStyle() {
		return breaks;
	}
	
	public Padding getPaddingStyle() {
		return padNewline;
	}
	
	private void lineFeed() throws IOException {
		rowsOnPage++;
		charsOnRow = 0;
		os.write(breaks.getString().getBytes());
	}

	public void newLine() throws IOException {
		for (int i=0; i<((rowgap / 4)+1); i++) {
			lineFeed();
		}
	}
	
	private void formFeed() throws IOException {
		charsOnRow = 0;
		rowsOnPage++;
		if (rowsOnPage>getMaxHeight()) {
			throw new IOException("The maximum number of rows on a page was exceeded (page is too short).");
		}
        switch (padNewline) {
	    	case BEFORE:
	    		lineFeed();
	    	case NONE:
	    		os.write((byte)0x0c);
	    		break;
	    	case BOTH:
	    		lineFeed();
	    	case AFTER:
	    		os.write((byte)0x0c); 
	    		lineFeed();
	    		break;
	    }
        rowsOnPage = 0;
        currentPage++;
	}

	public void newPage() throws IOException {
		if (supportsDuplex() && !currentDuplex && (currentPage % 2)==1) {
			formFeed();
		}
		formFeed();
	}

	public void newSectionAndPage(boolean duplex) throws IOException {
		if (supportsDuplex() && (currentPage %2 )==1) {
			formFeed();
		}
		newPage();
		currentDuplex = duplex;
	}

	public void newVolume() throws IOException { 
		charsOnRow = 0;
	}

	public void write(String braille) throws IOException {
		charsOnRow += braille.length();
		if (charsOnRow>getMaxWidth()) {
			throw new IOException("The maximum number of characters on a row was exceeded (page is too narrow).");
		}
		os.write(String.valueOf(bf.toText(braille)).getBytes(bf.getPreferredCharset().name()));
	}
	
	public void open(boolean duplex) throws IOException {
		os.write(header);
		currentDuplex = duplex;
		isOpen=true;
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	
	public void close() throws IOException {
		os.write(footer);
		os.close();
		isClosed=true;
		isOpen=false;
	}
	
	public boolean isClosed() {
		return isClosed;
	}
	
	public void setRowGap(int value) {
		if (value<0) {
			throw new IllegalArgumentException("Non negative integer expected.");
		} else {
			rowgap = value;
		}
	}
	
	public int getRowGap() {
		return rowgap;
	}
	
	public boolean supportsVolumes() {
		return false;
	}
	
	public boolean supports8dot() {
		return supports8dot;
	}
	
	public boolean supportsDuplex() {
		return supportsDuplex;
	}
	
	public int getMaxWidth() {
		return maxWidth;
	}
	
	public int getMaxHeight() {
		return maxHeight;
	}

}
