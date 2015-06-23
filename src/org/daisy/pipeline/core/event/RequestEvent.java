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
package org.daisy.pipeline.core.event;

/**
 * Event raised when the system requests input from the user of the system during exection time (followed by a user reply event).
 * @see org.daisy.pipeline.core.event.UserReplyEvent
 * @author Markus Gylling
 */
public class RequestEvent extends SystemEvent {
	
	private String mRequest;

	public RequestEvent(Object source, String request) {
		super(source);
		mRequest = request;
	}

	public String getRequest() {
		return mRequest;
	}
	
	private static final long serialVersionUID = 5302901987693469283L;

}
