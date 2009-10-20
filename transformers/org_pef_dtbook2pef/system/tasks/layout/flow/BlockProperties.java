package org_pef_dtbook2pef.system.tasks.layout.flow;

/**
 * BlockProperties defines properties in a block of text in the flow.
 * @author Joel Håkansson, TPB
 */
public class BlockProperties {
	/**
	 * List types: <ul><li>NONE not a list</li><li>OL ordered list</li><li>UL unordered list</li><li>PL preformatted list</li></ul>
	 */
	public static enum ListType {NONE, OL, UL, PL};
	/**
	 * Break before types:
	 * <ul><li>AUTO no break</li><li>PAGE start block on a new page</li></ul>
	 */
	public static enum BreakBeforeType {AUTO, PAGE}; // TODO: Implement ODD_PAGE, EVEN_PAGE 
	/**
	 * 
	 * Keep types:
	 * <ul><li>AUTO do not keep</li><li>ALL all rows in a block</li></ul>
	 *
	 */
	public static enum KeepType {AUTO, ALL}
	private int leftMargin;
	private int rightMargin;
	private int topMargin;
	private int bottomMargin;
	private int textIndent;
	private int firstLineIndent;
	private ListType listType;
	private int listIterator;
	private BreakBeforeType breakBefore;
	private KeepType keep;
	private int keepWithNext;
	private int blockIndent;
	
	/**
	 * Builder for BlockProperties
	 * @author Joel Håkansson, TPB
	 *
	 */
	public static class Builder {
		// Optional parameters
		int leftMargin = 0;
		int rightMargin = 0;
		int topMargin = 0;
		int bottomMargin = 0;
		int textIndent = 0;
		int firstLineIndent = 0;
		ListType listType = ListType.NONE;
		BreakBeforeType breakBefore = BreakBeforeType.AUTO;
		KeepType keep = KeepType.AUTO;
		int keepWithNext = 0;
		int blockIndent = 0;
		
		public Builder() {
		}
		
		/**
		 * Set the left margin for the block, in characters.
		 * @param leftMargin left margin, in characters
		 * @return returns "this" object
		 */
		public Builder leftMargin(int leftMargin) {
			this.leftMargin = leftMargin;
			return this;
		}
		
		/**
		 * Set the right margin for the block, in characters.
		 * @param rightMargin right margin, in characters
		 * @return returns "this" object
		 */
		public Builder rightMargin(int rightMargin) {
			this.rightMargin = rightMargin;
			return this;
		}
		
		/**
		 * Set the top margin for the block, in characters.
		 * @param topMargin top margin, in characters
		 * @return returns "this" object
		 */
		public Builder topMargin(int topMargin) {
			this.topMargin = topMargin;
			return this;
		}
		
		/**
		 * Set the bottom margin for the block, in characters.
		 * @param bottomMargin bottom margin, in characters
		 * @return returns "this" object
		 */
		public Builder bottomMargin(int bottomMargin) {
			this.bottomMargin = bottomMargin;
			return this;
		}
		
		/**
		 * Set the text indent for the block, in characters.
		 * The text indent controls the indent of all text rows except 
		 * the first one, see {@link #firstLineIndent(int)}.
		 * The text indent is applied to text directly within 
		 * the block, but is not inherited to block children.
		 * @param textIndent the indent, in characters
		 * @return returns "this" object
		 */
		public Builder textIndent(int textIndent) {
			this.textIndent = textIndent;
			return this;
		}
		
		/**
		 * Set the first line indent for the block, in characters.
		 * The first line indent controls the indent of the first text 
		 * row in a block.
		 * The first line indent is applied to text directly within
		 * the block, but is not inherited to block children.
		 * @param firstLineIndent the indent, in characters.
		 * @return returns "this" object
		 */
		public Builder firstLineIndent(int firstLineIndent) {
			this.firstLineIndent = firstLineIndent;
			return this;
		}
		
		/**
		 * Set the list type for the block. The list type is
		 * applied to block's children.
		 * @param listType the type of list
		 * @return returns "this" object
		 */
		public Builder listType(ListType listType) {
			this.listType = listType;
			return this;
		}
		
		/**
		 * Set the break before property for the block.
		 * @param breakBefore the break before type
		 * @return returns "this" object
		 */
		public Builder breakBefore(BreakBeforeType breakBefore) {
			this.breakBefore = breakBefore;
			return this;
		}
		
		/**
		 * Set the keep property for the block.
		 * @param keep the keep type
		 * @return returns "this" object
		 */
		public Builder keep(KeepType keep) {
			this.keep = keep;
			return this;
		}
		
		/**
		 * Set the keep with next property for the block.
		 * @param keepWithNext the number of rows in the next 
		 * block to keep together with this block
		 * @return returns "this" object
		 */
		public Builder keepWithNext(int keepWithNext) {
			this.keepWithNext = keepWithNext;
			return this;
		}
		
		/**
		 * Set the block indent for the block, in characters.
		 * The block indent controls the indent of child blocks
		 * in a block. This is useful when building lists.
		 * @param blockIndent the indent, in characters
		 * @return returns "this" object
		 */
		public Builder blockIndent(int blockIndent) {
			this.blockIndent = blockIndent;
			return this;
		}
		
		/**
		 * Build BlockProperties with the current settings.
		 * @return returns a new BlockProperties object
		 */
		public BlockProperties build() {
			return new BlockProperties(this);
		}
	}

	private BlockProperties(Builder builder) {
		leftMargin = builder.leftMargin;
		rightMargin = builder.rightMargin;
		topMargin = builder.topMargin;
		bottomMargin = builder.bottomMargin;
		textIndent = builder.textIndent;
		firstLineIndent = builder.firstLineIndent;
		listType = builder.listType;
		listIterator = 0;
		breakBefore = builder.breakBefore;
		keep = builder.keep;
		keepWithNext = builder.keepWithNext;
		blockIndent = builder.blockIndent;
	}
	
	/**
	 * Get left margin, in characters
	 * @return returns the left margin
	 */
	public int getLeftMargin() {
		return leftMargin;
	}
	
	/**
	 * Get right margin, in characters
	 * @return returns the right margin
	 */
	public int getRightMargin() {
		return rightMargin;
	}
	
	/**
	 * Get top margin, in characters
	 * @return returns the top margin
	 */
	public int getTopMargin() {
		return topMargin;
	}
	
	/**
	 * Get bottom margin, in characters
	 * @return returns the bottom margin
	 */
	public int getBottomMargin() {
		return bottomMargin;
	}
	
	/**
	 * Get text indent, in characters
	 * @return returns the text indent
	 */
	public int getTextIndent() {
		return textIndent;
	}
	
	/**
	 * Get first line indent, in characters
	 * @return returns the first line indent
	 */
	public int getFirstLineIndent() {
		return firstLineIndent;
	}
	
	/**
	 * Get block indent, in characters
	 * @return returns the block indent
	 */
	public int getBlockIndent() {
		return blockIndent;
	}
	
	/**
	 * Get list type
	 * @return returns the list type
	 */
	public ListType getListType() {
		return listType;
	}
	
	/**
	 * 
	 * @return
	 */
	public int nextListNumber() {
		listIterator++;
		return listIterator;
	}
	
	/**
	 * Get current list number
	 * @return returns the current list number
	 */
	public int getListNumber() {
		return listIterator;
	}
	
	/**
	 * Get break before type
	 * @return returns the break before type
	 */
	public BreakBeforeType getBreakBeforeType() {
		return breakBefore;
	}
	
	/**
	 * Get keep type
	 * @return returns the keep type
	 */
	public KeepType getKeepType() {
		return keep;
	}
	
	/**
	 * Get keep with next, in characters
	 * @return returns keep with next
	 */
	public int getKeepWithNext() {
		return keepWithNext;
	}

}
