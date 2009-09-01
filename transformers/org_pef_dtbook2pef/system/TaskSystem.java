package org_pef_dtbook2pef.system;

import java.util.ArrayList;
import java.util.Map;


/**
 * TaskSystem is an interface used when compiling a series of InternalTasks
 * that may require both global Transformer parameters and individual
 * constructor arguments.
 * 
 * Implement this interface to create a new TaskSystem.
 * @author joha
 *
 */
public interface TaskSystem {
	
	/**
	 * Compile the TaskSystem using the supplied parameters
	 * @param parameters
	 * @return returns a list of InternalTasks
	 */
	public ArrayList<InternalTask> compile(Map<String, String> parameters);

}
