package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class DTBook2Xhtml2MathML extends PipelineTest {

	public DTBook2Xhtml2MathML(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--input=" + mDataInputDir + "/dtbook/dtbook-math.xml");
		mParameters.add("--output=" + mDataOutputDir + "/DTBook2Xhtml2MathML/fromDTBook.html");		
		mParameters.add("--charsetSwitcherLineBreaks="+"dos");
		mParameters.add("--charsetSwitcherEncoding="+"utf-8");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "s/b us-ascci";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("DtbookToXhtml.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}
