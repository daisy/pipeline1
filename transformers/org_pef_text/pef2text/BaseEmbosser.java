package org_pef_text.pef2text;

import java.io.IOException;

import org_pef_text.BrailleTable;

/**
 * Provides an abstract base for embossers. 
 * @author Joel Håkansson, TPB
 *
 */
public abstract class BaseEmbosser implements AbstractEmbosser {
	public static enum Padding {BOTH, BEFORE, AFTER, NONE};
	/*
	protected boolean supports8dot;
	protected boolean supportsDuplex;
	protected boolean supportsAligning;
	protected int maxHeight;
	protected int maxWidth;*/
	private int rowgap;
	private boolean isOpen;
	private boolean isClosed;
	private boolean currentDuplex;
	private int currentPage;
	private int charsOnRow;
	private int rowsOnPage;
	private EmbosserProperties props;

	public abstract LineBreaks getLinebreakStyle();
	public abstract Padding getPaddingStyle();
	public abstract BrailleTable getTable();
	protected abstract void add(byte b) throws IOException;
	protected abstract void addAll(byte[] b) throws IOException;
	
	protected void init(EmbosserProperties props) {
		this.props = props;
		isOpen = false;
		isClosed = false;
	}
	
	public void newLine() throws IOException {
		for (int i=0; i<((rowgap / 4)+1); i++) {
			lineFeed();
		}
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
	
	public void open(boolean duplex) throws IOException {
		charsOnRow = 0;
		rowsOnPage = 0;
		rowgap = 0;
		currentPage = 1;
		isOpen=true;
		currentDuplex = duplex;
	}
	
	public int currentPage() {
		return currentPage;
	}
	
	public boolean pageIsEmpty() {
		return (charsOnRow+rowsOnPage)==0;
	}
	
	public void close() throws IOException {
		isClosed=true;
		isOpen=false;
	}
	
	public void write(String braille) throws IOException {
		charsOnRow += braille.length();
		if (charsOnRow>props.getMaxWidth()) {
			throw new IOException("The maximum number of characters on a row was exceeded (page is too narrow).");
		}
		addAll(String.valueOf(getTable().toText(braille)).getBytes(getTable().getPreferredCharset().name()));
	}
	
	protected void lineFeed() throws IOException {
		rowsOnPage++;
		charsOnRow = 0;
		addAll(getLinebreakStyle().getString().getBytes());
	}

	protected void formFeed() throws IOException {
		rowsOnPage++;
		if (rowsOnPage>props.getMaxHeight()) {
			throw new IOException("The maximum number of rows on a page was exceeded (page is too short)");
		}
        switch (getPaddingStyle()) {
	    	case BEFORE:
	    		lineFeed();
	    	case NONE:
	    		add((byte)0x0c);
	    		break;
	    	case BOTH:
	    		lineFeed();
	    	case AFTER:
	    		add((byte)0x0c); 
	    		lineFeed();
	    		break;
	    }
        currentPage++;
	    rowsOnPage = 0;
	    charsOnRow = 0;
	}

	public void newPage() throws IOException {
		if (props.supportsDuplex() && !currentDuplex && (currentPage % 2)==1) {
			formFeed();
		}
		formFeed();
	}

	public void newSectionAndPage(boolean duplex) throws IOException {
		if (props.supportsDuplex() && (currentPage % 2)==1) {
			formFeed();
		}
		newPage();
		currentDuplex = duplex;
	}

	public void newVolumeSectionAndPage(boolean duplex) throws IOException {
		newSectionAndPage(duplex);
	}

	public boolean isOpen() {
		return isOpen;
	}
	
	public boolean isClosed() {
		return isClosed;
	}

	public int getMaxHeight() {
		return props.getMaxHeight();
	}

	public int getMaxWidth() {
		return props.getMaxWidth();
	}

	public boolean supports8dot() {
		return props.supports8dot();
	}

	public boolean supportsAligning() {
		return props.supportsAligning();
	}

	public boolean supportsDuplex() {
		return props.supportsDuplex();
	}

	public boolean supportsVolumes() {
		return props.supportsVolumes();
	}

}