package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class WordML2DTBook2 extends PipelineTest {

	public WordML2DTBook2(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--input=" + mDataInputDir + "/word/wml-sample.xml");
		mParameters.add("--output=" + mDataOutputDir + "/WordML2DTBook2/fromWord.xml");		
		mParameters.add("--title=PipelineTest Inserted Title");
		mParameters.add("--author=PipelineTest Inserted Author");
		mParameters.add("--uid=PipelineTest_Inserted_UID");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Inserting title, author and uid, image conversion on";
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
