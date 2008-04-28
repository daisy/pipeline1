package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class DTBookFix2 extends PipelineTest {

	public DTBookFix2(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		//
		mParameters.add("--input=" + mDataInputDir + "/dtbook/dtbookfix-example-2-invalid.xml");
		mParameters.add("--output=" + mDataOutputDir + "/DTBookFix2/out.xml");
		mParameters.add("--runCategories=REPAIR_TIDY");	
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
