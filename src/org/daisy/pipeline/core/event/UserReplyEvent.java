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
 * Event raised when a user provides input to the system during exection time (preceeded by a system prompt event).
 * @see org.daisy.pipeline.core.event.RequestEvent
 * @author Markus Gylling
 */
public class UserReplyEvent extends UserEvent {

	private String mUserInput = null;

	public UserReplyEvent(Object source, String reply) {
		super(source);
		mUserInput  = reply;
	}

	public String getReply() {
		return mUserInput;
	}
	
	private static final long serialVersionUID = 7458715035224831016L;

}
