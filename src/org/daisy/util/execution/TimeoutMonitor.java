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
package org.daisy.util.execution;

/**
 * Thread used for timeout monitoring.
 * <p>
 * The monitor is configured with a <code>timeout</code> and
 * <code>interval</code> values, and its state can be updated with the
 * {@link TimeoutMonitor#reset()} method. Once started, it checks every
 * <code>interval</code> milliseconds that the time between the last call to
 * {@link #reset()} and the current time is lower than <code>timeout</code>, and
 * if not it calls the protected method {@link TimeoutMonitor#timeout()}.
 * </p>
 * <p>
 * The monitoring can be interrupted via the {@link #cancel()}
 * method.
 * </p>
 * 
 * @author Romain Deltour
 */
public abstract class TimeoutMonitor extends Thread {

	private long timeout;
	private long interval;
	private long lastUpdate;

	public TimeoutMonitor(long timeout, long interval) {
		super();
		this.timeout = timeout;
		this.interval = interval;
	}

	@Override
	public void run() {
		try {
			reset();
			while (!isInterrupted()
					&& System.currentTimeMillis() - lastUpdate < timeout) {
				Thread.sleep(interval);
			}
			if (!isInterrupted()) {
				timeout();
			}
		} catch (InterruptedException e) {
			// Terminate
		}
	}

	protected abstract void timeout();

	public void reset() {
		lastUpdate = System.currentTimeMillis();
	}

	public void cancel() {
		interrupt();
	}

}
