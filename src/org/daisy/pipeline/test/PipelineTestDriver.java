package org.daisy.pipeline.test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.daisy.pipeline.test.impl.Aligner1;
import org.daisy.pipeline.test.impl.Audacity2DTB1;
import org.daisy.pipeline.test.impl.CharacterRepertoireManipulator1;
import org.daisy.pipeline.test.impl.CharsetSwitcher1;
import org.daisy.pipeline.test.impl.DTBAudioEncoder1;
import org.daisy.pipeline.test.impl.DTBAudioEncoderRenamer1;
import org.daisy.pipeline.test.impl.DTBAudioEncoderSplitter1;
import org.daisy.pipeline.test.impl.DTBSplitter1;
import org.daisy.pipeline.test.impl.DTBook2Xhtml1;
import org.daisy.pipeline.test.impl.DTBook2Xhtml2MathML;
import org.daisy.pipeline.test.impl.DTBook2rtf1;
import org.daisy.pipeline.test.impl.DTBookFix1;
import org.daisy.pipeline.test.impl.DTBookMigrator1;
import org.daisy.pipeline.test.impl.DTBookMigrator2;
import org.daisy.pipeline.test.impl.DTBookMigrator3;
import org.daisy.pipeline.test.impl.Daisy202ToZ398620051;
import org.daisy.pipeline.test.impl.FilesetCreator1;
import org.daisy.pipeline.test.impl.FilesetRenamer1;
import org.daisy.pipeline.test.impl.FilesetRenamer2;
import org.daisy.pipeline.test.impl.Html2Xhtml1;
import org.daisy.pipeline.test.impl.MixedContentNormalizer1;
import org.daisy.pipeline.test.impl.MultiFormatMedia1;
import org.daisy.pipeline.test.impl.Narrator1;
import org.daisy.pipeline.test.impl.Narrator2;
import org.daisy.pipeline.test.impl.Narrator3;
import org.daisy.pipeline.test.impl.OcfCreator1;
import org.daisy.pipeline.test.impl.Odf2dtbook1;
import org.daisy.pipeline.test.impl.Odf2xhtml1;
import org.daisy.pipeline.test.impl.OpsCreator1;
import org.daisy.pipeline.test.impl.OpsCreator2;
import org.daisy.pipeline.test.impl.OpsCreator3;
import org.daisy.pipeline.test.impl.OpsCreator4;
import org.daisy.pipeline.test.impl.PrettyPrinter1;
import org.daisy.pipeline.test.impl.PrettyPrinter2;
import org.daisy.pipeline.test.impl.RenamerTaggerValidator1;
import org.daisy.pipeline.test.impl.Rtf2Xhtml1;
import org.daisy.pipeline.test.impl.Rtf2dtbook1;
import org.daisy.pipeline.test.impl.UnicodeNormalizer1;
import org.daisy.pipeline.test.impl.ValidatorConfigurable1;
import org.daisy.pipeline.test.impl.ValidatorDTBd2021;
import org.daisy.pipeline.test.impl.ValidatorDTBd2022;
import org.daisy.pipeline.test.impl.ValidatorDTBook1;
import org.daisy.pipeline.test.impl.ValidatorDTBook2;
import org.daisy.pipeline.test.impl.ValidatorDTBz39861;
import org.daisy.pipeline.test.impl.ValidatorEpubCheck1;
import org.daisy.pipeline.test.impl.ValidatorEpubCheck2;
import org.daisy.pipeline.test.impl.ValidatorNVDL1;
import org.daisy.pipeline.test.impl.WordML2DTBook1;
import org.daisy.pipeline.test.impl.WordML2DTBook2;
import org.daisy.pipeline.test.impl.WordML2DTBook3;
import org.daisy.pipeline.test.impl.WordML2Xhtml1;
import org.daisy.pipeline.test.impl.WordML2Xhtml2;
import org.daisy.pipeline.test.impl.Xhtml2DTBook1;
import org.daisy.pipeline.ui.CommandLineUI;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;

/**
 * A test runner for the Daisy Pipeline
 * @author Markus Gylling
 */
public class PipelineTestDriver {

	static EFolder inputDir = null;
	static EFolder outputDir = null;
	
	public PipelineTestDriver(EFolder samplesDirectory, EFolder scriptsDirectory) throws Exception {
		inputDir = new EFolder(samplesDirectory, "input");
		outputDir = new EFolder(samplesDirectory, "output");
		FileUtils.createDirectory(outputDir);
		
		assert(scriptsDirectory.exists() && samplesDirectory.exists() && outputDir.exists() && inputDir.exists());
		
		System.out.println("Using testdata directory " + samplesDirectory.getAbsolutePath());
		System.out.println("Using scripts directory " + scriptsDirectory.getAbsolutePath());

		System.out.print("Deleting output directory contents...");
		outputDir.deleteContents(true);				
		System.out.println(" done.");
						
		Collection<File> scripts = scriptsDirectory.getFiles(true, ".+\\.taskScript");		
		System.out.println("Found " + scripts.size() + " scripts.");
		
		Collection<PipelineTest> tests = getTests(); 
		System.out.println("Found " + tests.size() + " tests .");
		System.out.println("-----------------------");
		
		
		Set<File> scriptsWithoutTests = new HashSet<File>();
		Set<FailedTest> failedTests = new HashSet<FailedTest>();
		
		for (File script: scripts) {
			boolean testExistsForScript = false;
			for (PipelineTest test : tests) {
				if(test.supportsScript(script.getName())) {
					testExistsForScript = true;
					try{
						System.out.println("Test " + test.getClass().getName() +": " + test.getResultDescription());	
						List<String> parametersList = new LinkedList<String>();
						parametersList.add(script.getAbsolutePath());
						parametersList.addAll(test.getParameters());
						String[] array = parametersList.toArray(new String[parametersList.size()]);
						CommandLineUI.main(array);
						test.confirm();
					}catch (Exception e) {
						if(tests.size()>1) {
							//we are running several tests at once,
							//collect, continue, and then inform outside lop
							failedTests.add(new FailedTest(script,test,e));
							e.printStackTrace();
						}else{
							throw e;
						}
					}	
					System.out.println("-----------------------");
				}				
			}
			if(!testExistsForScript) {
				scriptsWithoutTests.add(script);
			}
		}
						
		System.out.println("Pipeline test drive done.");

		
		if(tests.size()>1) {
			
			if(!failedTests.isEmpty()) {
				System.out.println("The following " + failedTests.size() + " tests failed (the driver caught an exception while invoking them))");
				for(FailedTest t : failedTests) {
					System.out.println(t.mTest.getClass().getSimpleName() + ": using script " + t.mScript.getName());
					System.out.println("Exception: " + t.mException.getMessage() + "[" + t.mException.getCause().getClass().getSimpleName() + "]");
					System.err.println();
				}
			}else{
				System.out.println("No tests failed (in the sense that no exceptions where caught in the driver");
			}
						
			if(!scriptsWithoutTests.isEmpty()) {
				System.out.println("The following " + scriptsWithoutTests.size() + " scripts were not tested:");
				for(File f : scriptsWithoutTests) {
					System.out.println(f.getName());
				}
			}else{
				System.out.println("All scripts in "+ scriptsDirectory.getAbsolutePath() + " were tested");
			}
		}
	}

	/**
	 * Run a series of instantiations of CommandLineGUI.
	 * Use scripts from the pipeline canonical script collection, use local input data.
	 * Main runnable with Eclipse run profile params '${project_loc}/samples ${project_loc}/scripts'.
	 * <p>
	 * When running the test driver, args[0] shall contain the full path to the pipeline
	 * samples directory and args[1] shall contain full path to the pipeline scripts directory.
	 * </p>
	 * @param args command line arguments
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
				
		EFolder samplesDirectory = new EFolder(args[0]);		
		EFolder scriptsDirectory = new EFolder(args[1]);		
		
		PipelineTestDriver pdt = new PipelineTestDriver(samplesDirectory,scriptsDirectory);
		
	}

	/**
	 * Populate the test collection
	 */
	public static Collection<PipelineTest> getTests() {
		List<PipelineTest> tests = new LinkedList<PipelineTest>(); 
		
		/**
		 * Current broken (due to bug in epubcheck):
		 * tests.add(new OpsCreator3(inputDir, outputDir)); (input is XHTML 1.0 and epubcheck crashes)		 
		 */
		
		/*
		 * Tests with input data in samples dir.
		 * To run a complete Pipeline testrun, 
		 * decomment all lines below
		 */
		
//		tests.add(new OpsCreator1(inputDir, outputDir));
//		tests.add(new OpsCreator2(inputDir, outputDir)); 
//		tests.add(new OpsCreator3(inputDir, outputDir)); // (input is XHTML 1.0 and epubcheck crashes)
//		tests.add(new OpsCreator4(inputDir, outputDir));			 
//		tests.add(new OcfCreator1(inputDir, outputDir));			
//		tests.add(new WordML2DTBook1(inputDir, outputDir));
//		tests.add(new WordML2DTBook2(inputDir, outputDir));
//		tests.add(new WordML2DTBook3(inputDir, outputDir));
//		tests.add(new WordML2Xhtml1(inputDir, outputDir));		
//		tests.add(new WordML2Xhtml2(inputDir, outputDir));
//		tests.add(new Odf2dtbook1(inputDir, outputDir));
//		tests.add(new Odf2xhtml1(inputDir, outputDir));
//		tests.add(new Narrator1(inputDir, outputDir));
//		tests.add(new Narrator2(inputDir, outputDir));
//		tests.add(new Narrator3(inputDir, outputDir));		
//		tests.add(new ValidatorNVDL1(inputDir, outputDir));
//		tests.add(new ValidatorDTBook1(inputDir, outputDir));
//		tests.add(new ValidatorDTBook2(inputDir, outputDir));
//		tests.add(new ValidatorDTBd2021(inputDir, outputDir));
//		tests.add(new ValidatorDTBd2022(inputDir, outputDir));
//		tests.add(new ValidatorDTBz39861(inputDir, outputDir));
//		tests.add(new ValidatorEpubCheck1(inputDir, outputDir));
//		tests.add(new ValidatorEpubCheck2(inputDir, outputDir));		
//		tests.add(new ValidatorConfigurable1(inputDir, outputDir));		
//		tests.add(new PrettyPrinter1(inputDir, outputDir));
//		tests.add(new PrettyPrinter2(inputDir, outputDir));		
//		tests.add(new CharacterRepertoireManipulator1(inputDir, outputDir));
//		tests.add(new CharsetSwitcher1(inputDir, outputDir));
//		tests.add(new UnicodeNormalizer1(inputDir, outputDir));				
//		tests.add(new Rtf2dtbook1(inputDir, outputDir));		
//		tests.add(new Rtf2Xhtml1(inputDir, outputDir));
//		tests.add(new DTBook2rtf1(inputDir, outputDir));
//		tests.add(new Xhtml2DTBook1(inputDir, outputDir));  		
//		tests.add(new DTBook2Xhtml1(inputDir, outputDir));
//		tests.add(new DTBook2Xhtml2MathML(inputDir, outputDir));
//		tests.add(new MixedContentNormalizer1(inputDir, outputDir));
//		tests.add(new DTBookFix1(inputDir, outputDir));		
//		tests.add(new DTBookMigrator1(inputDir, outputDir));
//		tests.add(new DTBookMigrator2(inputDir, outputDir));
//		tests.add(new DTBookMigrator3(inputDir, outputDir));
//		tests.add(new Html2Xhtml1(inputDir, outputDir));
//		tests.add(new DTBSplitter1(inputDir, outputDir));
//		tests.add(new FilesetRenamer1(inputDir, outputDir));		
//		tests.add(new RenamerTaggerValidator1(inputDir, outputDir));				
//		tests.add(new Daisy202ToZ398620051(inputDir, outputDir));				
//		tests.add(new MultiFormatMedia1(inputDir, outputDir));
	

		/*
		 * End Tests with input data in samples dir
		 */
		
		
		
		
		/*
		 * Tests with input data not in samples dir:
		 * Dont decomment these unless you have access
		 * to the extra (non SVN) input data collection, 
		 * or mod the inparams accordingly.
		 */			
//		tests.add(new FilesetCreator1(inputDir, outputDir));	
//		tests.add(new FilesetRenamer2(inputDir, outputDir));
//		tests.add(new Aligner1(inputDir, outputDir));
//		tests.add(new Audacity2DTB1(inputDir, outputDir));
//		tests.add(new DTBAudioEncoder1(inputDir, outputDir));
//		tests.add(new DTBAudioEncoderSplitter1(inputDir, outputDir));
//		tests.add(new DTBAudioEncoderRenamer1(inputDir, outputDir));

		/*
		 * End Tests with input data not in samples dir:
		 */
		
		return tests;
	}

	class FailedTest {
		File mScript;
		PipelineTest mTest;
		Exception mException;
		FailedTest(File script, PipelineTest test, Exception failure) {
			mScript = script;
			mTest = test;
			mException = failure;
		}
	}
	
	
}
