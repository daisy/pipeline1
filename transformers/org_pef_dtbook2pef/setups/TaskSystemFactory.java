package org_pef_dtbook2pef.setups;

import java.net.URL;
import java.util.Map;

import org_pef_dtbook2pef.setups.en_US.DefaultTextSystem;
import org_pef_dtbook2pef.setups.sv_SE.SwedishBrailleSystem;
import org_pef_dtbook2pef.setups.sv_SE.SwedishTextSystem;
import org_pef_dtbook2pef.system.TaskSystem;

/**
 * Entry point for retrieving a TaskSystem implementation. Modify this file to 
 * add new TaskSystems.
 * @author Joel Håkansson, TPB
 *
 */
public class TaskSystemFactory {
	/**
	 * Enum of supported output formats.
	 * Add an output format to this list, if needed.
	 */
	public enum OutputFormat {PEF, TEXT};

	/**
	 * Enum of supported setups.
	 * Add setups to this list, if needed.
	 */
	public enum Setup {sv_SE, sv_SE_FA44, en_US};

	/**
	 *  System setups are defined here.
	 *  
	 *  Each system setup consists of a series of tasks that, put together, performs a format conversion. 
	 *  A system is labeled by an identifier when inserted into the HashMap.
	 *  The recommended practice is to use a language region (or sub region) as identifier.
	 *
	 *  New system setups can be added to the conversion system by following the example below.
	 */
	public TaskSystem newTaskSystem(OutputFormat outputFormat, Setup setup, Map<String, String> parameter) throws TaskSystemFactoryException {
		switch (outputFormat) {
			case PEF:
				switch (setup) {
					// Braille setups for Swedish //
					case sv_SE: 
						return new SwedishBrailleSystem(getResource("."), "sv_SE/config/default_A4.xml", "SwedishBrailleSystem A4");
					case sv_SE_FA44:
						return new SwedishBrailleSystem(getResource("."), "sv_SE/config/default_FA44.xml", "SwedishBrailleSystem FA44");
					// Add more Braille systems here //
				}
				break;
			case TEXT:
				switch (setup) {
					// Text setup for Swedish //
					case sv_SE: 
						return new SwedishTextSystem(getResource("."), "sv_SE/config/text_A4.xml", "SwedishTextSystem");
					case en_US:
						return new DefaultTextSystem(getResource("."), "en_US/config/text.xml", "DefaultTextSystem");
					// Add more text systems here //
				}
				break;
		}
		throw new TaskSystemFactoryException("Cannot create a TaskSystem for " + outputFormat + "/" + setup);
	}

	private URL getResource(String subPath) throws IllegalArgumentException {
		//TODO check the viability of this method
		URL url;
	    url = this.getClass().getResource(subPath);
	    if(null==url) {
	    	String qualifiedPath = this.getClass().getPackage().getName().replace('.','/') + "/";	    	
	    	url = this.getClass().getClassLoader().getResource(qualifiedPath+subPath);
	    }
	    if(url==null) throw new IllegalArgumentException(subPath + " in TaskSystemFactory");
	    return url;
	}

}