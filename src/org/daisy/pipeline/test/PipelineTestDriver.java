package org.daisy.pipeline.test;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.daisy.dmfc.ui.CommandLineUI;
import org.daisy.pipeline.test.impl.D202dtbValidator1;
import org.daisy.pipeline.test.impl.DTBookValidator1;
import org.daisy.pipeline.test.impl.DTBookValidator2;
import org.daisy.pipeline.test.impl.Narrator1;
import org.daisy.pipeline.test.impl.Odf2dtbook1;
import org.daisy.pipeline.test.impl.PrettyPrinter1;
import org.daisy.pipeline.test.impl.UnicodeNormalizer1;
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
	 * @param args[0] full path to the testdata directory
	 * @param args[1] full path to the pipeline scripts directory
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
				
		EFolder testdataDirectory = new EFolder(args[0]);		
		EFolder scriptsDirectory = new EFolder(args[1]);		
		inputDir = new EFolder(testdataDirectory, "input");
		outputDir = new EFolder(testdataDirectory, "output");
		FileUtils.createDirectory(outputDir);
		
		assert(scriptsDirectory.exists() && testdataDirectory.exists() && outputDir.exists() && inputDir.exists());
		
		System.out.println("Using testdata directory " + testdataDirectory.getAbsolutePath());
		System.out.println("Using scripts directory " + scriptsDirectory.getAbsolutePath());

		System.out.print("Deleting output directory contents...");
		outputDir.deleteContents(true);				
		System.out.println(" done.");
		
		System.out.print("Collecting scripts... ");		
		Collection<File> scripts = scriptsDirectory.getFiles(true, ".+\\.taskScript");		
		System.out.println("found " + scripts.size() + ".");
		
		System.out.print("Collecting tests... ");
		Collection<PipelineTest> tests = gatherTests(); 
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

	private static Collection<PipelineTest> gatherTests() {
		List<PipelineTest> tests = new LinkedList<PipelineTest>(); 
				
		tests.add(new DTBookValidator1(inputDir, outputDir));
		tests.add(new DTBookValidator2(inputDir, outputDir));
		tests.add(new Narrator1(inputDir, outputDir));
		tests.add(new PrettyPrinter1(inputDir, outputDir));
		tests.add(new D202dtbValidator1(inputDir, outputDir));
		tests.add(new Odf2dtbook1(inputDir, outputDir));
		tests.add(new UnicodeNormalizer1(inputDir, outputDir));
		
		return tests;
	}

}
