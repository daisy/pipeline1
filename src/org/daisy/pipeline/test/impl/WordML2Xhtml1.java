package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class WordML2Xhtml1 extends PipelineTest {

	public WordML2Xhtml1(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--input=" + mDataInputDir + "/word/wml-sample.xml");
		mParameters.add("--output=" + mDataOutputDir + "/WordML2Xhtml1/fromWord.html");		
		mParameters.add("--charsetSwitcherLineBreaks="+"unix");
		mParameters.add("--charsetSwitcherEncoding="+"utf-8");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("WordMLtoXhtml.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}
