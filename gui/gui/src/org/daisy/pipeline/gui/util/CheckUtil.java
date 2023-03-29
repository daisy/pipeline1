/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.pipeline.gui.util;

/**
 * Contains static utility to perform miscellaneous code checks such as
 * validation of method arguments.
 * 
 * @author Romain Deltour
 * 
 */
public final class CheckUtil {

	/**
	 * Throws an {@link IllegalArgumentException} with the given message.
	 * <p>
	 * This method returns the type of <code>obj</code> so that it can be used
	 * in the first <code>super</code> statement of an overridden method.
	 * </p>
	 * 
	 * @param <T>
	 *            The type to be returned by this method.
	 * @param obj
	 *            The object the type of which is returned.
	 * @param message
	 *            The message of the raised exception.
	 * @return Nothing: an exception is raise.
	 * @throws IllegalArgumentException
	 *             Always.
	 */
	public static <T> T illegalArgument(T obj, String message) {
		throw new IllegalArgumentException(message);
	}

	// Static utility, doesn't need to be instantiated
	private CheckUtil() {
	}

}
