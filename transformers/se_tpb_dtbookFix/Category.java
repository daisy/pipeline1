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
