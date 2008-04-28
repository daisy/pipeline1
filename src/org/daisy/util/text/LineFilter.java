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
package org.daisy.util.text;

/**
 * A filter for strings. The <code>LineFilter</code> class is used by the
 * {@link org.daisy.util.file.StreamRedirector} and {@link org.daisy.util.execution.Command}
 * classes to filter each line in a text stream before it is sent to its destination.
 * @author Linus Ericson
 */
public interface LineFilter {

	/**
	 * Filter a line of text.
	 * @param line the line to filter
	 * @return the (possibly modified) line
	 */
	public String filterLine(String line);
}
