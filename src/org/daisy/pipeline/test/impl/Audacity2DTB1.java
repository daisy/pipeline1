package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class Audacity2DTB1 extends PipelineTest {

	public Audacity2DTB1(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {				
		mParameters.add("--inputAUP=" + "D:\\_temp_dev\\audacity test\\1\\test5.aup");				
		mParameters.add("--outputPath="+ "D:\\_temp_dev\\audacity-out\\"); 
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("AudacityToDTB.taskScript".equals(scriptName)) {
			return true;
		}
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub		
	}

}
