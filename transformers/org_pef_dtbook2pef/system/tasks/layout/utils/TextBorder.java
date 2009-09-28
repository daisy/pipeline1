package org_pef_dtbook2pef.system.tasks.layout.utils;

import java.util.ArrayList;

import org_pef_dtbook2pef.system.tasks.layout.utils.BreakPointHandler.BreakPoint;

public class TextBorder {
	public enum Align {LEFT, CENTER, RIGHT};
	private int topFill, rowFill, bottomFill;
	private String 	topLeftCorner, topBorder, topRightCorner, 
					leftBorder, rightBorder,
					bottomLeftCorner, bottomBorder, bottomRightCorner;
	private Align align;
	
	public static class Builder {
		int width;
		String 	topLeftCorner, topBorder, topRightCorner, 
				leftBorder, rightBorder,
				bottomLeftCorner, bottomBorder, bottomRightCorner;
		Align align;
		
		public Builder(int width) {
			this.width = width;
			this.topLeftCorner = "";
			this.topBorder = "";
			this.topRightCorner = "";
			this.leftBorder = "";
			this.rightBorder = "";
			this.bottomLeftCorner = "";
			this.bottomBorder = "";
			this.bottomRightCorner = "";
			this.align = Align.LEFT;
		}
		
		public Builder topLeftCorner(String pattern) {
			this.topLeftCorner = pattern;
			return this;
		}

		public Builder topBorder(String pattern) {
			this.topBorder = pattern;
			return this;
		}
		
		public Builder topRightCorner(String pattern) {
			this.topRightCorner = pattern;
			return this;
		}
		
		public Builder leftBorder(String pattern) {
			this.leftBorder = pattern;
			return this;
		}
		
		public Builder rightBorder(String pattern) {
			this.rightBorder = pattern;
			return this;
		}
		
		public Builder bottomLeftCorner(String pattern) {
			this.bottomLeftCorner = pattern;
			return this;
		}

		public Builder bottomBorder(String pattern) {
			this.bottomBorder = pattern;
			return this;
		}
		
		public Builder bottomRightCorner(String pattern) {
			this.bottomRightCorner = pattern;
			return this;
		}
		
		public Builder alignment(Align align) {
			this.align = align;
			return this;
		}
		
		public TextBorder build() {
			return new TextBorder(this);
		}
	}

	private TextBorder(Builder builder) {
		this.topLeftCorner = builder.topLeftCorner;
		this.topBorder = builder.topBorder;
		this.topRightCorner = builder.topRightCorner;
		this.leftBorder = builder.leftBorder;
		this.rightBorder = builder.rightBorder;
		this.bottomLeftCorner = builder.bottomLeftCorner;
		this.bottomBorder = builder.bottomBorder;
		this.bottomRightCorner = builder.bottomRightCorner;
		this.align = builder.align;
		this.topFill = builder.width - (topLeftCorner.length() + topRightCorner.length());
		this.rowFill = builder.width - (leftBorder.length() + rightBorder.length());
		this.bottomFill = builder.width - (bottomLeftCorner.length() + bottomRightCorner.length());
	}
	
	public String getTopBorder() {
		return topLeftCorner + LayoutTools.fill(topBorder, topFill) + topRightCorner;
	}
	
	public String getBottomBorder() { 
		return bottomLeftCorner + LayoutTools.fill(bottomBorder, bottomFill) + bottomRightCorner;
	}
	
	public ArrayList<String> addBorderToParagraph(String text) {
		ArrayList<String> ret = new ArrayList<String>();
		BreakPointHandler bph = new BreakPointHandler(text);
    	BreakPoint bp;
    	while (bph.hasNext()) {
    		bp = bph.nextRow(rowFill);
    		ret.add(addBorderToRow(bp.getHead().replaceAll("\u00ad", "")));
    	}
    	return ret;
	}

	public String addBorderToRow(String text) {
    	if (text.length()>rowFill) {
    		throw new IllegalArgumentException("String length must be <= width");
    	}
    	StringBuffer sb = new StringBuffer();
    	sb.append(leftBorder);
    	switch (align) {
	    	case LEFT: break;
	    	case CENTER:
	    		sb.append(LayoutTools.fill(' ', (int)Math.floor( (rowFill - text.length())/2d) ));
	    		break;
	    	case RIGHT:
	    		sb.append(LayoutTools.fill(' ', rowFill - text.length()));
	    		break;
    	}
    	sb.append(text);
    	switch (align) {
	    	case LEFT:
	    		sb.append(LayoutTools.fill(' ', rowFill - text.length()));
	    		break;
	    	case CENTER:
	    		sb.append(LayoutTools.fill(' ', (int)Math.ceil( (rowFill - text.length())/2d) ));
	    		break;
	    	case RIGHT: break;
    	}
    	sb.append(rightBorder);
    	return sb.toString();
	}
 
}
