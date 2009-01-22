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

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Represents the status of a job execution.
 * 
 * @author Romain Deltour
 * 
 */
public enum Status {
	IDLE, WAITING, RUNNING, FINISHED, FAILED, SYSTEM_FAILED, ABORTING, ABORTED;
	public final static Set<Status> FINISHED_SET = Collections
			.unmodifiableSet(EnumSet.of(FINISHED, FAILED, SYSTEM_FAILED,
					ABORTED));

	private final static EnumSet<Status> AFTER_IDLE = EnumSet.of(WAITING,
			RUNNING, ABORTING, ABORTED);
	private final static EnumSet<Status> AFTER_WAITING = EnumSet.of(RUNNING,
			ABORTING, ABORTED);
	private final static EnumSet<Status> AFTER_RUNNING = EnumSet.of(WAITING,
			RUNNING, ABORTING, ABORTED);
	private final static EnumSet<Status> AFTER_ABORTING = EnumSet.of(ABORTED);

	public static boolean isValidTransition(Status oldStatus, Status newStatus) {
		switch (oldStatus) {
		case IDLE:
			return AFTER_IDLE.contains(newStatus);
		case WAITING:
			return AFTER_WAITING.contains(newStatus);
		case RUNNING:
			return AFTER_RUNNING.contains(newStatus);
		case ABORTING:
			return AFTER_ABORTING.contains(newStatus);
		case FINISHED:
		case FAILED:
		case SYSTEM_FAILED:
		case ABORTED:
			return false;
		default:
			return false;
		}
	}
}
