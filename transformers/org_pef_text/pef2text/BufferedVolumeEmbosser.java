package org_pef_text.pef2text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import javax.print.PrintException;

import org_pef_text.AbstractTable;

/**
 * Provides a buffered volume embossers. This is similar to {@link ConfigurableEmbosser},
 * except that it supports writing each volume separately to a PrinterDevice rather than
 * to an OutputStream. 
 * 
 * @author  Joel Håkansson, TPB
 */
public class BufferedVolumeEmbosser extends BaseEmbosser {

	private LineBreaks breaks;
	private Padding padNewline;
	private PrinterDevice pd;
	private AbstractTable bf;
	private Stack<ArrayList<Byte>> pages;
	private VolumeWriter vw;
	
	public static class Builder {
		// required params
		private PrinterDevice pd;
		private AbstractTable bt;
		private VolumeWriter vw;
		
		// optional params
		private LineBreaks.Type breaks = LineBreaks.Type.DEFAULT;
		private Padding padNewline = Padding.values()[0];

		public Builder(PrinterDevice pd, AbstractTable bt, VolumeWriter vw) {
			this.pd = pd;
			this.bt = bt;
			this.vw = vw;
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

		public BufferedVolumeEmbosser build() {
			return new BufferedVolumeEmbosser(this);
		}
	}
	
	private BufferedVolumeEmbosser(Builder builder) {
		vw = builder.vw;
		bf = builder.bt;
		breaks = new LineBreaks(builder.breaks);
		padNewline = builder.padNewline;
		pd = builder.pd;
		init(builder.vw);
		initVolume();
	}
	
	private void initVolume() {
		pages = new Stack<ArrayList<Byte>>();
		pages.add(new ArrayList<Byte>());
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

	protected void add(byte b) {
		pages.peek().add(b);
	}
	
	protected void addAll(byte[] bytes) {
		ArrayList<Byte> page = pages.peek();
		for (byte b : bytes) {
			page.add(b);
		}
	}

	protected void formFeed() throws IOException {
		super.formFeed(); // form feed characters belong to the current page
        pages.add(new ArrayList<Byte>()); // start a new page
	}

	public void newVolumeSectionAndPage(boolean duplex) throws IOException {
		super.newVolumeSectionAndPage(duplex);
		finalizeVolume();
		initVolume();
	}
	
	private void finalizeVolume() throws IOException {
		File out = File.createTempFile("emboss", ".tmp");
		pages.pop();
		vw.write(pages, out);
		try {
			pd.transmit(out);
		} catch (PrintException e) {
			throw new IOException(e);
		} finally {
			out.deleteOnExit();
		}
	}
	
	public void close() throws IOException {
		finalizeVolume();
		super.close();
	}

}
