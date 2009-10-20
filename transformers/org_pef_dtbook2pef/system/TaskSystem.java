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
	 * @param parameters the parameters to pass to the TaskSystem
	 * @return returns a list of InternalTasks
	 * @throws TaskSystemException throws TaskSystemException if something went wrong when compiling the TaskSystem
	 */
	public ArrayList<InternalTask> compile(Map<String, String> parameters) throws TaskSystemException;

}
