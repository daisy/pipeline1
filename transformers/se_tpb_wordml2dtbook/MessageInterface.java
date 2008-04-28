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
package se_tpb_wordml2dtbook;

import org.daisy.pipeline.core.event.MessageEvent;

/**
 * 
 * Emit a message.
 * 
 * @author  Joel Hakansson, TPB
 * @version 2007 apr 11
 * @since 1.0
 */
public interface MessageInterface {

	/**
	 * Emit a message.
	 *  
	 * @param type
	 * @param idstr
	 * @param params
	 */
	public void sendMessage(MessageEvent.Type type, String idstr, Object[] params);
}
