package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class DTBMigratorBackward1 extends PipelineTest {

	public DTBMigratorBackward1(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--input=D:/dtbs/z2005/fp2003/06-speechgen.opf");
		//mParameters.add("--input=" + mDataInputDir +"/dtb/z3986-2005/06-speechgen.opf");
		mParameters.add("--output=" + mDataOutputDir + "/DTBMigratorBackward1/");		
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Downgrade z2005 to 2.02";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("DTBBackwardMigrator.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}
	
	@Override
	public void confirm() {
		// TODO Auto-generated method stub		
	}

}
