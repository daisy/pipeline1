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
 */package org.daisy.pipeline;

/**
 * Exposes global version information on the whole org.daisy.pipeline package.
 * <p>Version information is an ISO8601 date string (YYYY-MM-DD).</p>
 * @author Linus Ericson
 */
public class Version {

	private static final String VERSION = "20110317-RC";
	
	public static String getVersion() {
		return VERSION;
	}
	
}