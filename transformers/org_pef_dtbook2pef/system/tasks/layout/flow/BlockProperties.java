package org_pef_dtbook2pef.system.tasks.layout.flow;

public class BlockProperties {
	public static enum ListType {NONE, OL, UL, PL};
	public static enum BreakBeforeType {AUTO, PAGE}; // TODO: Implement ODD_PAGE, EVEN_PAGE 
	private int leftMargin;
	private int rightMargin;
	private int topMargin;
	private int bottomMargin;
	private int textIndent;
	private int firstLineIndent;
	private ListType listType;
	private int listIterator;
	private BreakBeforeType breakBefore;
	
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
		
		public Builder() {
		}
		
		public Builder leftMargin(int leftMargin) {
			this.leftMargin = leftMargin;
			return this;
		}
		
		public Builder rightMargin(int rightMargin) {
			this.rightMargin = rightMargin;
			return this;
		}
		
		public Builder topMargin(int topMargin) {
			this.topMargin = topMargin;
			return this;
		}
		
		public Builder bottomMargin(int bottomMargin) {
			this.bottomMargin = bottomMargin;
			return this;
		}
		
		public Builder textIndent(int textIndent) {
			this.textIndent = textIndent;
			return this;
		}
		
		public Builder firstLineIndent(int firstLineIndent) {
			this.firstLineIndent = firstLineIndent;
			return this;
		}
		
		public Builder setListType(ListType listType) {
			this.listType = listType;
			return this;
		}
		
		public Builder setBreakBefore(BreakBeforeType breakBefore) {
			this.breakBefore = breakBefore;
			return this;
		}
		
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
	}
	
	public int getLeftMargin() {
		return leftMargin;
	}
	
	public int getRightMargin() {
		return rightMargin;
	}
	
	public int getTopMargin() {
		return topMargin;
	}
	
	public int getBottomMargin() {
		return bottomMargin;
	}
	
	public int getTextIndent() {
		return textIndent;
	}
	
	public int getFirstLineIndent() {
		return firstLineIndent;
	}
	
	public ListType getListType() {
		return listType;
	}
	
	public int nextListNumber() {
		listIterator++;
		return listIterator;
	}
	
	public BreakBeforeType getBreakBeforeType() {
		return breakBefore;
	}

}
