package org_pef_dtbook2pef.system.tasks.layout.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.page.PagedMediaOutput;
import org_pef_dtbook2pef.system.tasks.layout.page.Row;


public class DefaultPEFOutput implements PagedMediaOutput {
	private PrintStream pst;
	private Properties p;
	private boolean hasOpenVolume;
	private boolean hasOpenSection;
	private boolean hasOpenPage;
	private int cCols;
	private int cRows;
	
	public DefaultPEFOutput(Properties p) {
		this.p = p;
		hasOpenVolume = false;
		hasOpenSection = false;
		hasOpenPage = false;
		cCols = 0;
		cRows = 0;
	}

	public void open(File f) {
		try {
			pst = new PrintStream(f, "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		pst.println("<dc:title>" + p.getProperty("title", "title?") + "</dc:title>");
		pst.println("<dc:creator>" + p.getProperty("creator", "creator?") + "</dc:creator>");
		pst.println("<dc:language>" + p.getProperty("language", "language?") + "</dc:language>");
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

	public void newRow(Row row) {
		pst.print("<row>");
		pst.print(row.getChars());
		pst.print("</row>");
		pst.println();
	}

	public void newSection(LayoutMaster master) {
		if (!hasOpenVolume) {
			cCols = master.getPageWidth();
			cRows = master.getPageHeight();
			pst.println("<volume cols=\"" + cCols + 
					"\" rows=\"" + cRows +
					"\" rowgap=\"" + p.getProperty("rowgap", "0") +
					"\" duplex=\"" + p.getProperty("duplex", "true") +
					"\">");
			hasOpenVolume = true;
		}
		closeOpenSection();
		pst.println("<section");
		if (cCols!=master.getPageWidth() || cRows!=master.getPageHeight()) {
			if (cCols!=master.getPageWidth()) {
				pst.println(" cols=\"" + master.getPageWidth() + "\"");
			}
			if (cRows!=master.getPageHeight()) { 
				pst.println(" rows=\"" + master.getPageHeight() + "\"");
			}
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
