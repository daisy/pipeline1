package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class DTBook2Xhtml1 extends PipelineTest {

	public DTBook2Xhtml1(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--input=" + mDataInputDir + "/dtbook/hauy-2005-1.xml");
		mParameters.add("--output=" + mDataOutputDir + "/DTBook2Xhtml1/fromDTBook.html");		
		mParameters.add("--charsetSwitcherLineBreaks="+"dos");
		mParameters.add("--charsetSwitcherEncoding="+"US-ASCII");
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
