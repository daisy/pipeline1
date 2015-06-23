package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class WordML2DTBook1 extends PipelineTest {

	public WordML2DTBook1(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--input=" + mDataInputDir + "/word/wml-sample.xml");
		mParameters.add("--output=" + mDataOutputDir + "/WordML2DTBook1/fromWord.xml");		
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "With minimal parameters, image conversion on";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("WordMLtoDTBook.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}
