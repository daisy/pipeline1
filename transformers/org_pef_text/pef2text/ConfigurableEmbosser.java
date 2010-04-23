package org_pef_text.pef2text;

import java.io.IOException;
import java.io.OutputStream;

import org_pef_text.BrailleTable;

/**
 * 
 * Provides a configurable embosser. Outputs to a single OutputStream. 
 * 
 * @author  Joel Hakansson, TPB
 * @version 22 okt 2008
 * @since 1.0
 */
public class ConfigurableEmbosser extends BaseEmbosser {

	private final LineBreaks breaks;
	private final Padding padNewline;
	private final OutputStream os;
	private final BrailleTable bf;
	private final byte[] header;
	private final byte[] footer;
	private final boolean fillSheet;
	private final boolean lineFeedOnEmptySheet;
	
	public static class Builder {
		// required params
		private OutputStream os;
		private BrailleTable bt;
		
		// optional params
		private LineBreaks.Type breaks = LineBreaks.Type.DEFAULT;
		private Padding padNewline = Padding.values()[0];
		private byte[] header = new byte[0];
		private byte[] footer = new byte[0];
		private boolean fillSheet = false;
		private boolean lineFeedOnEmptySheet = false;
		EmbosserProperties props = new SimpleEmbosserProperties();
		
		public Builder(OutputStream os, BrailleTable bt) {
			this.os = os;
			this.bt = bt;
		}
		
		public Builder embosserProperties(EmbosserProperties props) {
			this.props = props;
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
		public Builder fillSheet(boolean value) { fillSheet = value; return this; }
		public Builder autoLineFeedOnEmptyPage(boolean value) { lineFeedOnEmptySheet = value; return this; }

		public ConfigurableEmbosser build() {
			return new ConfigurableEmbosser(this);
		}
	}
	
	protected void formFeed() throws IOException {
		if (lineFeedOnEmptySheet && pageIsEmpty()) {
			lineFeed();
		}
		super.formFeed();
	}

	private ConfigurableEmbosser(Builder builder) {
		bf = builder.bt;
		breaks = new LineBreaks(builder.breaks);
		padNewline = builder.padNewline;
		header = builder.header;
		footer = builder.footer;
		os = builder.os;
		fillSheet = builder.fillSheet;
		lineFeedOnEmptySheet = builder.lineFeedOnEmptySheet;
		init(builder.props);
	}

	protected void add(byte b) throws IOException {
		os.write(b);
	}
	
	protected void addAll(byte[] bytes)  throws IOException {
		os.write(bytes);
	}

	public BrailleTable getTable() {
		return bf;
	}

	public LineBreaks getLinebreakStyle() {
		return breaks;
	}
	
	public Padding getPaddingStyle() {
		return padNewline;
	}
	
	public void open(boolean duplex) throws IOException {
		super.open(duplex);
		os.write(header);
	}
	
	public void close() throws IOException {
		if (fillSheet && supportsDuplex() && currentPage() % 2 == 0) {
			formFeed();
		}
		os.write(footer);
		os.close();
		super.close();
	}

}
