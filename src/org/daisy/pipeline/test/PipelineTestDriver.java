package org.daisy.pipeline.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.Location;

import org.daisy.pipeline.core.PipelineCore;
import org.daisy.pipeline.core.event.BusListener;
import org.daisy.pipeline.core.event.CoreMessageEvent;
import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.JobStateChangeEvent;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.event.StateChangeEvent;
import org.daisy.pipeline.core.event.TaskMessageEvent;
import org.daisy.pipeline.core.event.TaskStateChangeEvent;
import org.daisy.pipeline.core.script.Job;
import org.daisy.pipeline.core.script.Script;
import org.daisy.pipeline.core.script.ScriptParameter;
import org.daisy.pipeline.core.script.Task;
import org.daisy.pipeline.exception.DMFCConfigurationException;
import org.daisy.pipeline.test.impl.NarratorMathML1;
import org.daisy.pipeline.ui.CommandLineUI;
import org.daisy.util.file.Directory;
import org.daisy.util.file.FileUtils;
import org.daisy.util.i18n.XMLProperties;
import org.daisy.util.xml.stax.ExtendedLocationImpl;

/**
 * A test runner for the Daisy Pipeline
 * @author Markus Gylling
 */
public class PipelineTestDriver implements BusListener {

	static Directory inputDir = null;
	static Directory outputDir = null;
	
	@SuppressWarnings("unchecked")
	public PipelineTestDriver(Directory samplesDirectory, Directory scriptsDirectory) throws Exception {
		inputDir = new Directory(samplesDirectory, "input");
		outputDir = new Directory(samplesDirectory, "output");
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
		
		
        /*
         *  Load user properties
         */
        URL propsURL = CommandLineUI.class.getClassLoader().getResource(
                "pipeline.user.properties");
        XMLProperties properties = new XMLProperties();
        try {
            properties.loadFromXML(propsURL.openStream());
        } catch (IOException e) {
            throw new DMFCConfigurationException(
                    "Can't read pipeline.user.properties", e);
        }
		
        /*
         * Subscribe to all message and state change events
         */
        EventBus.getInstance().subscribe(this, MessageEvent.class);
        EventBus.getInstance().subscribe(this, StateChangeEvent.class);
        
		Pattern paramPattern = Pattern.compile("--(\\w+)=(.+)");
		
		for (File scriptFile: scripts) {
			boolean testExistsForScript = false;
			for (PipelineTest test : tests) {
				if(test.supportsScript(scriptFile.getName())) {
					testExistsForScript = true;
					try{
						System.out.println("Test " + test.getClass().getName() +": " + test.getResultDescription());	
													            
			            PipelineCore dmfc = new PipelineCore(null, findHomeDirectory(),properties);
			            Script script = dmfc.newScript(scriptFile.toURI().toURL());
			            Job job = new Job(script);
						
			            String[] array = test.getParameters().toArray(new String[test.getParameters().size()]);			            
						Map<String, String> parameters = new HashMap<String, String>();
				        
						for (int i = 0; i < array.length; i++) {
				        	Matcher matcher = paramPattern.matcher(array[i]);
				            if (matcher.matches()) {
				                String key = matcher.group(1);
				                String value = matcher.group(2);
				                parameters.put(key, value);
				            } else {
				                throw new InvalidParameterException("Error: invalid parameter '" + array[i]);
				            }
				        }
			            
			            for (String name : parameters.keySet()) {
			                String value = parameters.get(name);
			                ScriptParameter param = job.getScriptParameter(name);
			                if (param == null) {
			                    System.out.println("Error: Unknown parameter " + name);			                   
			                }
			                job.setParameterValue(name, value);
			            }

			            // Execute script
			            dmfc.execute(job);
			            
						test.confirm();
					}catch (Exception e) {
						if(tests.size()>1) {
							//we are running several tests at once,
							//collect, continue, and then inform outside loop
							failedTests.add(new FailedTest(scriptFile,test,e));
							e.printStackTrace();
						}else{
							throw e;
						}
					}	
					System.out.println("-----------------------");
				}				
			}
			if(!testExistsForScript) {
				scriptsWithoutTests.add(scriptFile);
			}
		}

        /*
         * Subscribe to all message and state change events
         */
        EventBus.getInstance().unsubscribe(this, MessageEvent.class);
        EventBus.getInstance().unsubscribe(this, StateChangeEvent.class);
		
		System.out.println("Pipeline test drive done.");

		
		if(tests.size()>1) {
			
			if(!failedTests.isEmpty()) {
				System.out.println("The following " + failedTests.size() + " tests failed (the driver caught an exception while invoking them))");
				for(FailedTest t : failedTests) {
					System.out.println(t.mTest.getClass().getSimpleName() + ": using script " + t.mScript.getName());
					if(t.mException!=null) {
						if(t.mException.getCause() !=null) {
							System.out.println("Exception: " + t.mException.getMessage() + "[" + t.mException.getCause().getClass().getSimpleName() + "]");
						}else{
							System.out.println("Exception: " + t.mException.getMessage() + "[cause is null]");
						}
					}
					
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
	 * Run a series of instantiations of the Pipeline.
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
				
		Directory samplesDirectory = new Directory(args[0]);		
		Directory scriptsDirectory = new Directory(args[1]);		
		
		new PipelineTestDriver(samplesDirectory,scriptsDirectory);
		
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
		 * decomment all lines below.
		 * 
		 * Note - some of these tests does generated invalid output.
		 * When this is expected a comment is/should be printed as the
		 * first line.
		 */
		
			
//		tests.add(new OpsCreator1(inputDir, outputDir));
//		tests.add(new OpsCreator2(inputDir, outputDir)); 
//		tests.add(new OpsCreator3(inputDir, outputDir));
//		tests.add(new OpsCreator4(inputDir, outputDir));
//		tests.add(new OpsCreator5(inputDir, outputDir));			
//		tests.add(new OcfCreator1(inputDir, outputDir));			
//		tests.add(new WordML2DTBook1(inputDir, outputDir));
//		tests.add(new WordML2DTBook2(inputDir, outputDir));
//		tests.add(new WordML2DTBook3(inputDir, outputDir));
//		tests.add(new WordML2Xhtml1(inputDir, outputDir));		
//		tests.add(new WordML2Xhtml2(inputDir, outputDir));
		
//		tests.add(new Narrator1(inputDir, outputDir));
//		tests.add(new Narrator2(inputDir, outputDir));
//		tests.add(new Narrator3(inputDir, outputDir));
//		tests.add(new Narrator4(inputDir, outputDir));
//		tests.add(new NarratorAutumn20081(inputDir, outputDir));
		tests.add(new NarratorMathML1(inputDir, outputDir));		
//		tests.add(new MultiFormatMedia1(inputDir, outputDir));
		
//		tests.add(new NarratorSADX_AnotherBulletList(inputDir, outputDir));
//		tests.add(new NarratorSADX_BulletList(inputDir, outputDir));
//		tests.add(new NarratorSADX_EmptyHeading(inputDir, outputDir));
//		tests.add(new NarratorSADX_EmptyHeadingComplex(inputDir, outputDir));
//		tests.add(new NarratorSADX_EmptyHeadingOnly(inputDir, outputDir));
//		tests.add(new NarratorSADX_FullyFeatured(inputDir, outputDir));
//		tests.add(new NarratorSADX_Head1Head3_NoHead2(inputDir, outputDir));
//		tests.add(new NarratorSADX_Head1Only(inputDir, outputDir));
//		tests.add(new NarratorSADX_NoHead1(inputDir, outputDir));
//		tests.add(new NarratorSADX_NoTitle(inputDir, outputDir));
//		tests.add(new NarratorSADX_OrderedList(inputDir, outputDir)); 
//		tests.add(new NarratorSADX_ParaOnly_NoTitleMeta(inputDir, outputDir));
//		tests.add(new NarratorSADX_ParaOnly(inputDir, outputDir));
//		tests.add(new NarratorSADX_Simple_NoCreatorMeta(inputDir, outputDir));
//		tests.add(new NarratorSADX_Simple_NoPublisherMeta(inputDir, outputDir));
//		tests.add(new NarratorSADX_Simple_NoTitleMeta(inputDir, outputDir));
//		tests.add(new NarratorSADX_Simple_NoUidMeta(inputDir, outputDir));
//		tests.add(new NarratorSADX_Simple(inputDir, outputDir));
//		tests.add(new NarratorSADX_Simple_NoXmlLang(inputDir, outputDir));
//		tests.add(new NarratorSADX_Simple_NoLangAtAll(inputDir, outputDir));
//		tests.add(new NarratorSADX_TitleOnly(inputDir, outputDir));
//		tests.add(new NarratorSADX_TitleParaOnly(inputDir, outputDir));		
		
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
//		tests.add(new DTBookFix2(inputDir, outputDir));		
//		tests.add(new DTBookMigrator1(inputDir, outputDir));
//		tests.add(new DTBookMigrator2(inputDir, outputDir));
//		tests.add(new DTBookMigrator3(inputDir, outputDir));		
//		tests.add(new Html2Xhtml1(inputDir, outputDir));
//		tests.add(new DTBSplitter1(inputDir, outputDir));
//		tests.add(new FilesetRenamer1(inputDir, outputDir));		
//		tests.add(new RenamerTaggerValidator1(inputDir, outputDir));						
//		tests.add(new NccNcxOnly1(inputDir, outputDir));
//		tests.add(new DTBMigratorForward1(inputDir, outputDir));		
//		tests.add(new DTBMigratorBackward1(inputDir, outputDir));
//		tests.add(new XukCreator1(inputDir, outputDir));
//		tests.add(new FilesetGenerator1(inputDir, outputDir));
//		tests.add(new FilesetGenerator2(inputDir, outputDir));		
		
		//tests.add(new NarratorAutumn20081(inputDir, outputDir));
		//tests.add(new FilesetGenerator3(inputDir, outputDir));

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
		
		/*
		 * Tests for deprecated transformers/scripts
		 */					
		
//		tests.add(new Odf2dtbook1(inputDir, outputDir));
//		tests.add(new Odf2xhtml1(inputDir, outputDir));
//		tests.add(new Daisy202ToZ398620051(inputDir, outputDir));				

		/*
		 * End Tests for deprecated transformers/scripts
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

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.event.BusListener#received(java.util.EventObject)
	 */
    public void received(EventObject event) {
        // we are subscribing to MessageEvent and StateChangeEvent

        try {
            if (event instanceof MessageEvent) {
                MessageEvent sme = (MessageEvent) event;
                StringBuilder message = new StringBuilder();

                String type = null;
                switch (sme.getType()) {
                case INFO:
                    type = "INFO";
                    return; //reduce clutter in testdriver output
                case INFO_FINER:
                    type = "INFO_FINER";
                    return;  //reduce clutter in testdriver output
                case WARNING:
                    type = "WARNING";
                    break;
                case ERROR:
                    type = "ERROR";
                    break;
                case DEBUG:
                    type = "DEBUG";
                    return; //reduce clutter in testdriver output
                }

                String who = null;
                if (sme instanceof CoreMessageEvent) {
                    who = "Pipeline Core";
                } else if (sme instanceof TaskMessageEvent) {
                    Task task = (Task) sme.getSource();
                    if (task.getTransformerInfo() != null) {
                        who = task.getTransformerInfo().getNiceName();
                    } else {
                        who = task.getName();
                    }
                } else {
                    who = "???";
                }

                StringBuilder location = new StringBuilder();
                if (sme.getLocation() != null) {
                    Location loc = sme.getLocation();
                    String sysId = loc.getSystemId();
                    if (sysId != null && sysId.length() > 0) {
                        File file = new File(sysId);
                        location.append(" Location: ");
                        location.append(file.getPath());
                        if (loc.getLineNumber() > -1) {
                            location.append(' ');
                            location.append(loc.getLineNumber());
                            if (loc.getColumnNumber() > -1) {
                                location.append(':');
                                location.append(loc.getColumnNumber());
                            }
                        }
                    }
                                   
                    //mg20070904: printing extended location info
                    if(loc instanceof ExtendedLocationImpl) {                    		                        
                    	ExtendedLocationImpl eLoc = (ExtendedLocationImpl)loc;                    	
                    	ExtendedLocationImpl.InformationType[] types = ExtendedLocationImpl.InformationType.values();                    	                    	
                    	for (int i = 0; i < types.length; i++) {                    		
                    		location.append("\n\t");
                    		location.append(types[i].toString()).append(':').append(' ');
                    		String value = eLoc.getExtendedLocationInfo(types[i]);
                    		location.append(value==null?"N/A":value);                            		
						}                    	
                    }
                    
                }//if (sme.getLocation() != null)

                message.append('[');
                message.append(type);
                message.append(',').append(' ');
                message.append(who);
                message.append(']').append(' ');
                message.append(sme.getMessage());
                message.append(location.toString());

                System.out.println(message.toString());

            } else if (event instanceof StateChangeEvent) {
                StateChangeEvent sce = (StateChangeEvent) event;

                String type = null;
                String name = null;
                String state = (sce.getState() == StateChangeEvent.Status.STARTED) ? "started"
                        : "stopped";

                if (event instanceof JobStateChangeEvent) {
                    type = "Job"; // we refer to scripts as "Jobs" to users.
                    Job job = (Job) sce.getSource();
                    name = job.getScript().getNicename();
                } else if (event instanceof TaskStateChangeEvent) {
                	//return; //reduce clutter in testdriver output
                	if(state.equals("stopped")) return;
                    type = "Transformer";
                    Task task = (Task) sce.getSource();
                    name = task.getTransformerInfo().getNiceName();
                } else {
                    System.err.println(event.getClass().getSimpleName());
                }

                System.out.println("[STATE] " + type + " " + name + " just "
                        + state);

            } else {
                System.err.println(event.getClass().getSimpleName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds the pipeline home directory.
     * 
     * @param propertiesURL
     * @return
     * @throws DMFCConfigurationException
     */
    private static File findHomeDirectory() throws DMFCConfigurationException {
        URL propertiesURL = PipelineCore.class.getClassLoader().getResource(
                "pipeline.properties");
        File propertiesFile = null;
        try {
            propertiesFile = new File(propertiesURL.toURI());
        } catch (URISyntaxException e) {
            throw new DMFCConfigurationException(e.getMessage(), e);
        }
        // Is this the home dir?
        File folder = propertiesFile.getParentFile();
        if (PipelineCore.testHomeDirectory(folder)) {
            return folder;
        }
        // Test parent
        folder = folder.getParentFile();
        if (PipelineCore.testHomeDirectory(folder)) {
            return folder;
        }
        throw new DMFCConfigurationException(
                "Cannot locate the Daisy Pipeline home directory");
    }
	
}
