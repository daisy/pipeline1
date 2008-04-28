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
package se_tpb_dtbookFix;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A Category bundles an ordered list of Executor, and holds
 * category-wide properties. 
 * @author Markus Gylling
 */
class Category extends LinkedList<Executor> {	
	Name mName = null; 
	Set<InputState> mSupportedStates = null;
	
	/**
	 * Constructor
	 * @param categoryName The name by which this category is referred to
	 * @param tdl TransformerDelegateListener
	 * @param supportedStates The input state(s) that this category is designed to deal with
	 * @param executors The Executor instances to be bundled in this category
	 */
	Category(Name categoryName, Set<InputState> supportedStates, List<Executor> executors) {
		mName = categoryName;
		mSupportedStates = supportedStates;
		this.addAll(executors);
	}
		
	Name getName() {
		return mName;
	}
	
	boolean supportsInputState(InputState state) {
		return mSupportedStates.contains(state);
	}
	
	enum Name {	
		INDENT,
		TIDY,
		REPAIR,
		NARRATOR;
	}
	
	private static final long serialVersionUID = 8014675633930802166L;
}
