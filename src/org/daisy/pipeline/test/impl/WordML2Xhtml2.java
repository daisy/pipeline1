package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class WordML2Xhtml2 extends PipelineTest {

	public WordML2Xhtml2(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--input=" + mDataInputDir + "/word/wml-sample.xml");
		mParameters.add("--output=" + mDataOutputDir + "/WordML2Xhtml2/fromWord.html");		
		mParameters.add("--charsetSwitcherLineBreaks="+"dos");
		mParameters.add("--charsetSwitcherEncoding="+"utf-8");
		mParameters.add("--dtbookFixRunCategories="+"NOTHING");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "No DTBookFix applied (useing enum NOTHING), DOS linebreaks";
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
