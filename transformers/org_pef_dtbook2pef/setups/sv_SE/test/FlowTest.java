package org_pef_dtbook2pef.setups.sv_SE.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org_pef_dtbook2pef.system.InternalTaskException;
import org_pef_dtbook2pef.system.tasks.LayoutEngineTask;
import org_pef_dtbook2pef.system.tasks.layout.impl.DefaultLayoutPerformer;
import org_pef_dtbook2pef.system.tasks.layout.impl.PaginatorImpl;
import org_pef_dtbook2pef.system.tasks.layout.page.PagedMediaWriter;
import org_pef_dtbook2pef.system.tasks.layout.text.brailleFilters.BrailleFilterFactory;
import org_pef_dtbook2pef.system.tasks.layout.writers.PEFMediaWriter;
import org_pef_dtbook2pef.system.tasks.layout.writers.TextMediaWriter;
import org_pef_dtbook2pef.test.unit.Test;
import org_pef_dtbook2pef.test.unit.TestResult;


public class FlowTest implements Test {
	enum Mode {PEF, TEXT};
	private final URL input;
	private final URL reference;
	private final Mode mode;
	
	public FlowTest(URL input, URL reference, Mode mode) {
		this.input = input;
		this.reference = reference;
		this.mode = mode;
	}

	private void copy(InputStream is, OutputStream os) throws IOException {
		InputStream bis = new BufferedInputStream(is);
		OutputStream bos = new BufferedOutputStream(os);
		int b;
		while ((b = bis.read())!=-1) {
			bos.write(b);
		}
		bos.flush();
		bos.close();
		bis.close();
	}
	
	private boolean fc(InputStream f1, InputStream f2) throws IOException {
		InputStream bf1 = new BufferedInputStream(f1);
		InputStream bf2 = new BufferedInputStream(f2);
		int b1;
		int b2;
		while ((b1 = bf1.read())!=-1 & b1 == (b2 = bf2.read())) { 
			//continue
		}
		if (b1!=-1 || b2!=-1) {
			return false;
		}
		return true;
	}

	public String getName() {
		return "Flow";
	}

	public TestResult runTest() {
		try {
			// Init
			File inputFile = File.createTempFile("FlowTest", ".tmp");
			inputFile.deleteOnExit();
			File outputFile = File.createTempFile("FlowTest", ".tmp");
			outputFile.deleteOnExit();
			copy(input.openStream(), new FileOutputStream(inputFile));
			InputStream referenceStream = reference.openStream();
			
			HashMap<String, String> p = new HashMap<String, String>();
			Properties prop = new Properties();
			BrailleFilterFactory factory = BrailleFilterFactory.newInstance();
			prop.putAll(p);
			PagedMediaWriter paged;
			switch (mode) {
				case PEF:
					paged = new PEFMediaWriter(prop);
					break;
				case TEXT:
					paged = new TextMediaWriter(prop, "utf-8");
					break;
				default:
					return new TestResult(this, null, false, "Unknown mode");
			}
			PaginatorImpl paginator = new PaginatorImpl(factory.getDefault());
			DefaultLayoutPerformer flow = new DefaultLayoutPerformer(factory);
			LayoutEngineTask ft = new LayoutEngineTask("FLOW to Text converter", flow, paginator, paged);
			if (false)
				ft.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(this.getClass().getResource("../../common/validation/flow.xsd")));
			String details = "'" + inputFile + "' -> '" + outputFile + "'";
			try {
				ft.execute(inputFile, outputFile);
				boolean success = fc(new FileInputStream(outputFile), referenceStream);
				return new TestResult(this, null, success, "File has been processed (" + details + ")");
			} catch (InternalTaskException e) {
				e.printStackTrace();
				return new TestResult(this, null, false, "InternalTaskException (" + details + ")");
			} catch (FileNotFoundException e) {
				return new TestResult(this, null, false, "FileNotFoundException (" + details + ")");
			} catch (IOException e) {
				return new TestResult(this, null, false, "IOException (" + details + ")");
			}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new TestResult(this, null, false, "SAXException");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new TestResult(this, null, false, "IOException");
		}
	}

	public Object getExpected() {
		return null;
	}

	public Object getInput() {
		return null;
	}

}
