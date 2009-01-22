/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.pipeline.execution;

/**
 * A generic listener to the execution of a Pipeline job.
 * 
 * @author Romain Deltour
 * 
 */
public interface JobListener {

	/**
	 * Notification of a new Pipeline message.
	 * 
	 * @param message
	 *            the message text
	 * @param level
	 *            the message severity level
	 * @param path
	 *            the path to a source file, or <code>null</code>
	 * @param line
	 *            the line of the message cause in the source file, or
	 *            <code>-1</code>
	 * @param column
	 *            the column of the message cause in the source file, or
	 *            <code>-1</code>
	 */
	public void message(String message, Level level, String path, Integer line,
			Integer column);

	/**
	 * Notification of a progress change in the job execution.
	 * 
	 * @param taskName
	 *            the name of the task (transformer) that progressed.
	 * @param progress
	 *            the new progress value (between 0 and 1)
	 */
	public void progress(String taskName, double progress);

	/**
	 * Notification of the actual start of the job execution.
	 */
	public void start();

	/**
	 * Notification of the end of the job execution.
	 * 
	 * @param status
	 *            the final Status of the job
	 * @param message
	 *            an optional message about the job termination
	 */
	public void stop(Status status, String message);

	/**
	 * Notification of the start of the task (transformer) with the given name.
	 * 
	 * @param taskName
	 *            the name of the task (transformer) that just began.
	 */
	public void startTransformer(String taskName);

	/**
	 * 
	 * Notification of the end of the task (transformer) with the given name.
	 * 
	 * @param taskName
	 *            the name of the task (transformer) that just stopped.
	 */
	public void stopTransformer(String taskName);

}