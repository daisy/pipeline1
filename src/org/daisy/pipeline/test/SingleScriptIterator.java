package org.daisy.pipeline.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.Location;

import org.daisy.pipeline.core.DMFCCore;
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
import org.daisy.pipeline.core.script.ScriptValidationException;
import org.daisy.pipeline.core.script.Task;
import org.daisy.pipeline.core.script.datatype.DatatypeException;
import org.daisy.pipeline.exception.DMFCConfigurationException;
import org.daisy.pipeline.exception.JobFailedException;
import org.daisy.pipeline.ui.CommandLineUI;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.detect.Signature;
import org.daisy.util.file.detect.SignatureDetectionException;
import org.daisy.util.file.detect.SignatureDetector;
import org.daisy.util.i18n.XMLProperties;
import org.daisy.util.mime.MIMEType;
import org.daisy.util.mime.MIMETypeException;
import org.daisy.util.mime.MIMETypeFactory;
import org.daisy.util.mime.MIMETypeFactoryException;
import org.daisy.util.xml.stax.ExtendedLocationImpl;

/**
 * Iterate over a single script using a collection of input files from a directory.
 * @author Markus Gylling
 */
public class SingleScriptIterator implements BusListener {
	private Job mCurrentJob = null;
	
	public SingleScriptIterator(EFolder inputDir, MIMEType inputFileType, File scriptFile, EFolder outputFolder, Map<String,String> scriptParameters) throws MIMETypeException, SignatureDetectionException, DMFCConfigurationException {
		
		String fileNamePattern = buildRegex(inputFileType);		
		Collection<File> inputFiles = inputDir.getFiles(true,fileNamePattern);	
		SignatureDetector detector = new SignatureDetector(true);
		XMLProperties properties = loadUserProperties();        
        EventBus.getInstance().subscribe(this, MessageEvent.class);
        EventBus.getInstance().subscribe(this, StateChangeEvent.class);
        Failures failures = new Failures();
		int i = 0;
		for(File f : inputFiles) {
			File outputDir = null;
			try{
				List<Signature> signatures = detector.detect(f);
				if(signatures.isEmpty()) continue;
				if(!signatures.get(0).getMIMEType().isEqualOrAlias(inputFileType)) continue;	
				i++;
				outputDir = FileUtils.createDirectory(new File(outputFolder, Integer.toString(i)));
				System.out.println("Running " + scriptFile.getName() + " using " + f.getName() + " as input and " + outputDir.getName() + " as output." );
				
	            DMFCCore dmfc = new DMFCCore(null, findHomeDirectory(),properties);
	            Script script = dmfc.newScript(scriptFile.toURI().toURL());
	            mCurrentJob = new Job(script);
	            
	            //set input and output parameters
	            ScriptParameter inputParam = getInputParam(mCurrentJob);
	            mCurrentJob.setParameterValue(inputParam.getName(), f.getAbsolutePath());
	            ScriptParameter outputParam = getOutputParam(mCurrentJob);
	            mCurrentJob.setParameterValue(outputParam.getName(), outputDir.getAbsolutePath());
	            
	            //set other parameters
	            for (String name : scriptParameters.keySet()) {
	                String value = scriptParameters.get(name);
	                ScriptParameter p = mCurrentJob.getScriptParameter(name);
	                if (p == null) {
	                    System.out.println("Error: Unknown parameter " + name);			                   
	                }
	                mCurrentJob.setParameterValue(name, value);
	            }
	
	            // Execute script
	            dmfc.execute(mCurrentJob);
	            	            
			}catch (Exception e) {
				failures.add(new Failure(e,mCurrentJob));
			}finally{
				System.out.println("Done running " + scriptFile.getName() + " using " + f.getName() + " as input and " + outputDir.getName() + " as output." );
			}
			
		}
		EventBus.getInstance().unsubscribe(this, MessageEvent.class);
        EventBus.getInstance().unsubscribe(this, StateChangeEvent.class);
        
        System.err.println("Iteration done. " + failures.size() + " failures (exceptions thrown from pipeline core) were recorded");
	}
	
	private ScriptParameter getInputParam(Job currentJob) {
		ScriptParameter param = currentJob.getScriptParameter("input");
        if (param != null) {
        	return param;
        }
        throw new IllegalStateException("could not locate input parameter in job");
	}

	private ScriptParameter getOutputParam(Job currentJob) {		
		ScriptParameter param = currentJob.getScriptParameter("output");
		if(param==null) param = currentJob.getScriptParameter("outputPath");
        if (param != null) {
        	return param;
        }
        throw new IllegalStateException("could not locate output parameter in job");
	}
	
	class Failures extends ArrayList<Failure> {
		
		public boolean add(Failure failure) {
			super.add(failure);
			StringBuilder sb = new StringBuilder();
			sb.append("Failure: ");
			sb.append('[');
			sb.append(failure.e.getClass().getSimpleName());
			sb.append(']');
			sb.append(' ');
			sb.append(failure.e.getLocalizedMessage());
			System.err.println(sb.toString());
			//Map<String,String> params = failure.j.getJobParameters();
			
			return true;
		}			
	}

	
	class Failure {
		private final Exception e;
		private final Job j;

		Failure(Exception e, Job j) {
			this.e = e;
			this.j = j;			
		}
	}
	
	private XMLProperties loadUserProperties() throws DMFCConfigurationException {        
        URL propsURL = CommandLineUI.class.getClassLoader().getResource(
                "pipeline.user.properties");
        XMLProperties properties = new XMLProperties();
        try {
            properties.loadFromXML(propsURL.openStream());
        } catch (IOException e) {
            throw new DMFCConfigurationException(
                    "Can't read pipeline.user.properties", e);
        }
        return properties;
		
	}

	private String buildRegex(MIMEType inputFileType) throws MIMETypeException {
		
		Collection<String> coll = inputFileType.getFilenamePatterns(MIMEType.WIDTH_LOCAL, MIMEType.FILENAME_PATTERN_REGEX);
		
		StringBuilder sb = new StringBuilder();
		
		for(String s : coll) {
		  sb.append(s).append('|');	
		}
		
		return sb.toString().substring(0, sb.length()-1);
		
	}

	/**
	 * Parameters:
	 * <ul>
	 * <li>0: the input data directory</li>
	 * <li>1: Mime string of input file type to use</li>
	 * <li>2: Path to the script to use</li>
	 * <li>3: Path to base output directory</li>
	 * </ul>
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		EFolder inputFolder;
		try {
			inputFolder = new EFolder(FilenameOrFileURI.toFile(args[0]));
			if(inputFolder==null||!inputFolder.exists()) throw new FileNotFoundException(args[0]);
			
			String inputMime = args[1];			
			MIMEType inputType = MIMETypeFactory.newInstance().newMimeType(inputMime);
			
			File script = FilenameOrFileURI.toFile(args[2]);
			if(script==null||!script.exists()) throw new FileNotFoundException(args[2]);
			
			EFolder outputFolder = new EFolder(FileUtils.createDirectory((FilenameOrFileURI.toFile(args[0]))));
			
			Map<String,String> parameters = new HashMap<String,String>(); //TODO get this from args[]
			
			new SingleScriptIterator(inputFolder, inputType, script, outputFolder, parameters);
			
			System.exit(0);
			
		} catch (IOException e) {			
			e.printStackTrace();
			System.exit(1);
		} catch (MIMETypeFactoryException e) {			
			e.printStackTrace();
			System.exit(1);
		} catch (MIMETypeException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (SignatureDetectionException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (DMFCConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
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
                	return; //reduce clutter in testdriver output
//                    type = "Transformer";
//                    Task task = (Task) sce.getSource();
//                    name = task.getTransformerInfo().getNiceName();
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
        URL propertiesURL = DMFCCore.class.getClassLoader().getResource(
                "pipeline.properties");
        File propertiesFile = null;
        try {
            propertiesFile = new File(propertiesURL.toURI());
        } catch (URISyntaxException e) {
            throw new DMFCConfigurationException(e.getMessage(), e);
        }
        // Is this the home dir?
        File folder = propertiesFile.getParentFile();
        if (DMFCCore.testHomeDirectory(folder)) {
            return folder;
        }
        // Test parent
        folder = folder.getParentFile();
        if (DMFCCore.testHomeDirectory(folder)) {
            return folder;
        }
        throw new DMFCConfigurationException(
                "Cannot locate the Daisy Pipeline home directory");
    }
}
