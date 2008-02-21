package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class DTBookMigrator3 extends PipelineTest {

	public DTBookMigrator3(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--input=" + mDataInputDir + "/dtbook/scrambled-110.xml");
		mParameters.add("--output=" + mDataOutputDir + "/DTBookMigrator3/out.xml");				
		mParameters.add("--version=" + "LATEST");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Upgrade 1.1.0 to latest";
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
