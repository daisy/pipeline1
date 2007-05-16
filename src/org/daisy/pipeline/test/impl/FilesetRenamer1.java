package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class FilesetRenamer1 extends PipelineTest {

	public FilesetRenamer1(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		
		//mParameters.add("--filesetRenamerInputFile=" + mDataInputDir + "/dtb/d202/dontworrybehappy/ncc.html");
		mParameters.add("--filesetRenamerInputFile=" + "D:/dtbs/d202/hauy.202.rev4/ncc.html");
		mParameters.add("--filesetRenamerOutputPath=" + mDataOutputDir + "/FilesetRenamer1/");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("FilesetRenamer.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub		
	}

}
