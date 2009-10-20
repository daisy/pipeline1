package org_pef_dtbook2pef.system.tasks.layout.writers;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org_pef_dtbook2pef.system.tasks.layout.page.PagedMediaWriter;
import org_pef_dtbook2pef.system.tasks.layout.page.PagedMediaWriterException;
import org_pef_dtbook2pef.system.tasks.layout.page.SectionProperties;

/**
 * PEFMediaWriter is a simple implementation of PagedMediaWriter which outputs a PEF 2008-1 file.
 * @author joha
 *
 */
public class TextMediaWriter implements PagedMediaWriter {
	private PrintStream pst;
	private Properties p;
	private boolean hasOpenVolume;
	private boolean hasOpenSection;
	private boolean hasOpenPage;
	private int cCols;
	private int cRows;
	private int cRowgap;
	private boolean cDuplex;
	private String encoding;
	
	/**
	 * Create a new PEFMediaWriter using the supplied Properties. Available properties are:
	 * "identifier", "date"
	 * @param p configuration Properties
	 */
	public TextMediaWriter(Properties p, String encoding) {
		this.p = p;
		hasOpenVolume = false;
		hasOpenSection = false;
		hasOpenPage = false;
		cCols = 0;
		cRows = 0;
		cRowgap = 0;
		cDuplex = true;
		this.encoding = encoding;
	}

	public void open(OutputStream os) throws PagedMediaWriterException {
		try {
			pst = new PrintStream(os, true, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new PagedMediaWriterException("Cannot open PrintStream with " + encoding, e);
		}
		hasOpenVolume = false;		
		hasOpenSection = false;
		hasOpenPage = false;
	}

	public void newPage() {
		closeOpenPage();
		hasOpenPage = true;
	}

	public void newRow(CharSequence row) {
		pst.println(row);
	}
	
	public void newRow() {
		pst.println();
	}

	public void newSection(SectionProperties master) {
		if (!hasOpenVolume) {
			cCols = master.getPageWidth();
			cRows = master.getPageHeight();
			cRowgap = Math.round((master.getRowSpacing()-1)*4);
			cDuplex = master.duplex();
			hasOpenVolume = true;
		}
		closeOpenSection();
		hasOpenSection = true;
	}
	
	private void closeOpenVolume() {
		closeOpenSection();
		if (hasOpenVolume) {
			hasOpenVolume = false;
		}
	}
	
	private void closeOpenSection() {
		closeOpenPage();
		if (hasOpenSection) {
			hasOpenSection = false;
		}
	}
	
	private void closeOpenPage() {
		if (hasOpenPage) {
			hasOpenPage = false;
		}
	}

	public void close() {
		closeOpenVolume();
		pst.close();
	}

}
