package org.daisy.pipeline.test;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.daisy.dmfc.ui.CommandLineUI;
import org.daisy.pipeline.test.impl.CharsetSwitcher1;
import org.daisy.pipeline.test.impl.ConfigurableValidator1;
import org.daisy.pipeline.test.impl.D202dtbValidator1;
import org.daisy.pipeline.test.impl.DTBAudioEncoder1;
import org.daisy.pipeline.test.impl.DTBAudioEncoderSplitter1;
import org.daisy.pipeline.test.impl.DTBSplitter1;
import org.daisy.pipeline.test.impl.DTBook2Xhtml1;
import org.daisy.pipeline.test.impl.DTBookValidator1;
import org.daisy.pipeline.test.impl.DTBookValidator2;
import org.daisy.pipeline.test.impl.Daisy202ToZ398620051;
import org.daisy.pipeline.test.impl.FilesetRenamer1;
import org.daisy.pipeline.test.impl.Html2Xhtml1;
import org.daisy.pipeline.test.impl.Narrator1;
import org.daisy.pipeline.test.impl.OcfCreator1;
import org.daisy.pipeline.test.impl.Odf2dtbook1;
import org.daisy.pipeline.test.impl.OpsCreator1;
import org.daisy.pipeline.test.impl.PrettyPrinter1;
import org.daisy.pipeline.test.impl.RenamerTaggerValidator1;
import org.daisy.pipeline.test.impl.Rtf2Xhtml1;
import org.daisy.pipeline.test.impl.Rtf2dtbook1;
import org.daisy.pipeline.test.impl.UnicodeNormalizer1;
import org.daisy.pipeline.test.impl.WordML2Xhtml1;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;

/**
 * A test runner for the Daisy Pipeline
 * @author Markus Gylling
 */
public class PipelineTestDriver {

	static EFolder inputDir = null;
	static EFolder outputDir = null;

	/**
	 * Run a series of instantiations of CommandLineGUI.
	 * Use scripts from the pipeline canonical script collection, use local input data.
	 * Main runnable with Eclipse run profile params '${project_loc}/samples ${project_loc}/scripts'
	 * @param args[0] full path to the pipeline samples directory
	 * @param args[1] full path to the pipeline scripts directory
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
				
		EFolder samplesDirectory = new EFolder(args[0]);		
		EFolder scriptsDirectory = new EFolder(args[1]);		
		inputDir = new EFolder(samplesDirectory, "input");
		outputDir = new EFolder(samplesDirectory, "output");
		FileUtils.createDirectory(outputDir);
		
		assert(scriptsDirectory.exists() && samplesDirectory.exists() && outputDir.exists() && inputDir.exists());
		
		System.out.println("Using testdata directory " + samplesDirectory.getAbsolutePath());
		System.out.println("Using scripts directory " + scriptsDirectory.getAbsolutePath());

		System.out.print("Deleting output directory contents...");
		outputDir.deleteContents(true);				
		System.out.println(" done.");
		
		System.out.print("Collecting scripts... ");		
		Collection<File> scripts = scriptsDirectory.getFiles(true, ".+\\.taskScript");		
		System.out.println("found " + scripts.size() + ".");
		
		System.out.print("Collecting tests... ");
		Collection<PipelineTest> tests = getTests(); 
		System.out.println("found " + tests.size() + ".");
		System.out.println("-----------------------");
		
		for (File script: scripts) {
			boolean testExistsForScript = false;
			for (PipelineTest test : tests) {
				if(test.supportsScript(script.getName())) {
					testExistsForScript = true;
					System.out.println("Test " + test.getClass().getName() +": " + test.getResultDescription());	
					List<String> parametersList = new LinkedList<String>();
					parametersList.add(script.getAbsolutePath());
					parametersList.addAll(test.getParameters());
					String[] array = parametersList.toArray(new String[parametersList.size()]);
					CommandLineUI.main(array);
					test.confirm();
					System.out.println("-----------------------");
				}				
			}
			if(!testExistsForScript) {
				System.err.println("No test for script " + script.getName());
			}
		}
						
		System.out.println("Pipeline test drive done.");
	}

	public static Collection<PipelineTest> getTests() {
		List<PipelineTest> tests = new LinkedList<PipelineTest>(); 

//		tests.add(new ConfigurableValidator1(inputDir, outputDir));
//		tests.add(new D202dtbValidator1(inputDir, outputDir));
//		tests.add(new DTBookValidator1(inputDir, outputDir));
//		tests.add(new DTBookValidator2(inputDir, outputDir));
//		tests.add(new FilesetRenamer1(inputDir, outputDir));
//		tests.add(new Narrator1(inputDir, outputDir));
//		tests.add(new OcfCreator1(inputDir, outputDir));
//		tests.add(new OpsCreator1(inputDir, outputDir));
//		tests.add(new Odf2dtbook1(inputDir, outputDir));
//		tests.add(new PrettyPrinter1(inputDir, outputDir));
//		tests.add(new RenamerTaggerValidator1(inputDir, outputDir));		
//		tests.add(new UnicodeNormalizer1(inputDir, outputDir));
//		tests.add(new Rtf2dtbook1(inputDir, outputDir));
//		tests.add(new DTBSplitter1(inputDir, outputDir));
//		tests.add(new Daisy202ToZ398620051(inputDir, outputDir));
//		tests.add(new CharsetSwitcher1(inputDir, outputDir));
//		tests.add(new WordML2Xhtml1(inputDir, outputDir));
//		tests.add(new DTBook2Xhtml1(inputDir, outputDir));		
//		tests.add(new Rtf2Xhtml1(inputDir, outputDir));
//		tests.add(new PrettyPrinter1(inputDir, outputDir));
//		tests.add(new Html2Xhtml1(inputDir, outputDir));
		tests.add(new Odf2dtbook1(inputDir, outputDir));
		
////////// tests with input data not in samples dir 
		
		//tests.add(new DTBAudioEncoder1(inputDir, outputDir));
		//tests.add(new DTBAudioEncoderSplitter1(inputDir, outputDir));
		
		
////////// end tests with input data not in samples dir 
		
		return tests;
	}

	
//	tests.add(new CharsetTranscoder1(inputDir, outputDir));
}
