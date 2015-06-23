package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class CharacterRepertoireManipulator1 extends PipelineTest {

	public CharacterRepertoireManipulator1(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {				
		mParameters.add("--input=" + mDataInputDir + "/xhtml/multi-language-unicode.html");
		mParameters.add("--output=" + mDataOutputDir + "/CharacterRepertoireManipulator1/");
		mParameters.add("--substitutionTables=" + mDataInputDir + "/transl/example-table.xml");
		mParameters.add("--excludeCharset=" + "us-ascii");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("CharacterRepertoireManipulator.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub		
	}

}
