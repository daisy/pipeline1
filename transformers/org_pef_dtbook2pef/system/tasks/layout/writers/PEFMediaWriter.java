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
public class PEFMediaWriter implements PagedMediaWriter {
	private PrintStream pst;
	private Properties p;
	private boolean hasOpenVolume;
	private boolean hasOpenSection;
	private boolean hasOpenPage;
	private int cCols;
	private int cRows;
	private int cRowgap;
	private boolean cDuplex;
	
	/**
	 * Create a new PEFMediaWriter using the supplied Properties. Available properties are:
	 * "identifier", "date"
	 * @param p configuration Properties
	 */
	public PEFMediaWriter(Properties p) {
		this.p = p;
		hasOpenVolume = false;
		hasOpenSection = false;
		hasOpenPage = false;
		cCols = 0;
		cRows = 0;
		cRowgap = 0;
		cDuplex = true;
	}

	public void open(OutputStream os) throws PagedMediaWriterException {
		try {
			pst = new PrintStream(os, true, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// should never happen
			throw new PagedMediaWriterException("Cannot open PrintStream with UTF-8.", e);
		}
		hasOpenVolume = false;		
		hasOpenSection = false;
		hasOpenPage = false;
		pst.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		pst.println("<pef version=\"2008-1\" xmlns=\"http://www.daisy.org/ns/2008/pef\">");
		pst.println("<head>");
		pst.println("<meta xmlns:dc=\"http://purl.org/dc/elements/1.1/\">");
		pst.println("<dc:format>application/x-pef+xml</dc:format>");
		pst.println("<dc:identifier>" + p.getProperty("identifier", "identifier?") + "</dc:identifier>");
		pst.println("<dc:date>" + p.getProperty("date", "date?") + "</dc:date>");
		pst.println("</meta>");
		pst.println("</head>");
		pst.println("<body>");
	}

	public void newPage() {
		closeOpenPage();
		pst.println("<page>");
		hasOpenPage = true;
	}

	public void newRow(CharSequence row) {
		pst.print("<row>");
		pst.print(row);
		pst.print("</row>");
		pst.println();
	}
	
	public void newRow() {
		pst.println("<row/>");
	}

	public void newSection(SectionProperties master) {
		if (!hasOpenVolume) {
			cCols = master.getPageWidth();
			cRows = master.getPageHeight();
			cRowgap = Math.round((master.getRowSpacing()-1)*4);
			cDuplex = master.duplex();
			pst.println("<volume cols=\"" + cCols + 
					"\" rows=\"" + cRows +
					"\" rowgap=\"" + cRowgap +
					"\" duplex=\"" + cDuplex +
					"\">");
			hasOpenVolume = true;
		}
		closeOpenSection();
		pst.print("<section");

		if (cCols!=master.getPageWidth()) {
			pst.print(" cols=\"" + master.getPageWidth() + "\"");
		}
		if (cRows!=master.getPageHeight()) { 
			pst.print(" rows=\"" + master.getPageHeight() + "\"");
		}
		if (cRowgap!=Math.round((master.getRowSpacing()-1)*4)) {
			pst.print(" rowgap=\"" + Math.round((master.getRowSpacing()-1)*4) + "\"");
		}
		if (cDuplex!=master.duplex()) {
			pst.print(" duplex=\"" + master.duplex() + "\"");
		}
		pst.println(">");
		hasOpenSection = true;
	}
	
	private void closeOpenVolume() {
		closeOpenSection();
		if (hasOpenVolume) {
			pst.println("</volume>");
			hasOpenVolume = false;
		}
	}
	
	private void closeOpenSection() {
		closeOpenPage();
		if (hasOpenSection) {
			pst.println("</section>");
			hasOpenSection = false;
		}
	}
	
	private void closeOpenPage() {
		if (hasOpenPage) {
			pst.println("</page>");
			hasOpenPage = false;
		}
	}

	public void close() {
		closeOpenVolume();
		pst.println("</body>");
		pst.println("</pef>");
		pst.close();
	}

}
