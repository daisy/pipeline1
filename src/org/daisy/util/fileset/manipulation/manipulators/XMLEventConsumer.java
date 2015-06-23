/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
package org.daisy.util.fileset.manipulation.manipulators;

import java.util.List;

import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.stax.ContextStack;

public interface XMLEventConsumer {
	/**
	 * @param event the source event
	 * @param context the context stack 
	 * @return a list of new Events to write instead of inparam event. If return null or the empty list, no event wíll be written
	 */
	public List<XMLEvent> nextEvent(XMLEvent event, ContextStack context);
	
}
