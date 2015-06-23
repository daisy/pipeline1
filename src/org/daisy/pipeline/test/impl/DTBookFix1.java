package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class DTBookFix1 extends PipelineTest {

	public DTBookFix1(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		//
		mParameters.add("--input=" + mDataInputDir + "/dtbook/dtbookfix-example (invalid).xml");
		mParameters.add("--output=" + mDataOutputDir + "/DTBookFix1/out.xml");				
		mParameters.add("--fixCharset=" + "true");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("DTBookfix.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}
