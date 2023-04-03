/*
 * DAISY Pipeline GUI Copyright (C) 2006 Daisy Consortium
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
package org.daisy.util.file;

import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;

/**
 * A file filter that returns HTML files only (checks the presence of a html
 * root element).
 * 
 * @author Romain Deltour
 * 
 */
public class HtmlFileFilter extends EFileFilter {

	/**
	 * Creates the HTML file filter.
	 */
	public HtmlFileFilter() {
		super();
	}

	@Override
	protected boolean acceptEFile(EFile file) {
		if (!super.acceptEFile(file)) {
			return false;
		}
		Peeker peeker = null;
		try {
			peeker = PeekerPool.getInstance().acquire();
			PeekResult result = peeker.peek(file);
			return "html".equals(result.getRootElementLocalName());
		} catch (Exception e) {
			// TODO log("Couldn't peek in file "+ file.getAbsolutePath(), e);
			return false;
		}
	}

}
