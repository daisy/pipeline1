package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class DTBMigratorForward1 extends PipelineTest {

	public DTBMigratorForward1(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--input=" + mDataInputDir + "/dtb/d202/dontworrybehappy-nccOnly/ncc.html");
		mParameters.add("--output=" + mDataOutputDir + "/DTBMigrator1/");		
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Upgrade 2.02 NCC Only to z2005 NCX Only.";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("DTBForwardMigrator.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub		
	}

}
