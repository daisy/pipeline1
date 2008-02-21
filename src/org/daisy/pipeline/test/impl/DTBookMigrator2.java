package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class DTBookMigrator2 extends PipelineTest {

	public DTBookMigrator2(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--input=" + mDataInputDir + "/dtbook/hauy_valid.xml");
		mParameters.add("--output=" + mDataOutputDir + "/DTBookMigrator2/out.xml");				
		mParameters.add("--version=" + "2005-2");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Should gracefully let through 2005-2 input to 2005-2 output";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("DTBookMigrator.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub		
	}

}
