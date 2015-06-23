package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class DTBookMigrator1 extends PipelineTest {

	public DTBookMigrator1(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--input=" + mDataInputDir + "/dtbook/hauy-2005-1.xml");
		mParameters.add("--output=" + mDataOutputDir + "/DTBookMigrator1/out.xml");				
		mParameters.add("--version=" + "LATEST");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Should upgrade 2005-1 to 2005-3";
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
