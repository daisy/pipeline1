package org_pef_text.pef2text;

import java.io.IOException;
import java.io.OutputStream;

import org_pef_text.AbstractTable;

/**
 * 
 * Provides a configurable embosser. Outputs to a single OutputStream. 
 * 
 * @author  Joel Hakansson, TPB
 * @version 22 okt 2008
 * @since 1.0
 */
public class ConfigurableEmbosser extends BaseEmbosser {

	private LineBreaks breaks;
	private Padding padNewline;
	private OutputStream os;
	private AbstractTable bf;
	private byte[] header;
	private byte[] footer;
	
	public static class Builder {
		// required params
		private OutputStream os;
		private AbstractTable bt;
		
		// optional params
		private LineBreaks.Type breaks = LineBreaks.Type.DEFAULT;
		private Padding padNewline = Padding.values()[0];
		private byte[] header = new byte[0];
		private byte[] footer = new byte[0];
		EmbosserProperties props = new SimpleEmbosserProperties();
		
		public Builder(OutputStream os, AbstractTable bt) {
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

		public ConfigurableEmbosser build() {
			return new ConfigurableEmbosser(this);
		}
	}

	private ConfigurableEmbosser(Builder builder) {
		bf = builder.bt;
		breaks = new LineBreaks(builder.breaks);
		padNewline = builder.padNewline;
		header = builder.header;
		footer = builder.footer;
		os = builder.os;
		init(builder.props);
	}

	protected void add(byte b) throws IOException {
		os.write(b);
	}
	
	protected void addAll(byte[] bytes)  throws IOException {
		os.write(bytes);
	}

	public AbstractTable getTable() {
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
		os.write(footer);
		os.close();
		super.close();
	}

}
