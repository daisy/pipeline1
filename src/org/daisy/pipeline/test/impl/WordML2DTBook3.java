package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class WordML2DTBook3 extends PipelineTest {

	public WordML2DTBook3(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--input=" + mDataInputDir + "/word/wml-sample.xml");
		mParameters.add("--output=" + mDataOutputDir + "/WordML2DTBook3/fromWord.xml");		
		mParameters.add("--title=PipelineTest Inserted Title");
		mParameters.add("--author=PipelineTest Inserted Author");
		mParameters.add("--uid=PipelineTest_Inserted_UID");
		mParameters.add("--images=false");
		mParameters.add("--convertImages=false");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Inserting title, author and uid, image conversion off";
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
