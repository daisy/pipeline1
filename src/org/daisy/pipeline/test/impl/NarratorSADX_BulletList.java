package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class NarratorSADX_BulletList extends PipelineTest {

	public NarratorSADX_BulletList(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
	}

	@Override
	public List<String> getParameters() {
		mParameters.add("--input=" + mDataInputDir + "/sadx/BulletList.xml");
		mParameters.add("--outputPath=" + mDataOutputDir
				+ "/NarratorSADX-BulletList/");
		return mParameters;
	}

	@Override
	public String getResultDescription() {
		return "";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if ("Narrator-DtbookToDaisy202.taskScript".equals(scriptName)) {
			return true;
		}
		return false;
	}

}
