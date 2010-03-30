package org_pef_text.pef2text;

public class SimpleEmbosserProperties implements EmbosserProperties {
	private final static double cellWidth = 6;
	private final static double cellHeight = 10;

	private boolean supports8dot=false;
	private boolean supportsDuplex=false;
	private boolean supportsAligning=false;
	private boolean supportsVolumes=false;
	private int maxHeight=Integer.MAX_VALUE;
	private int maxWidth=Integer.MAX_VALUE;

	
	public SimpleEmbosserProperties() { }
	
	public SimpleEmbosserProperties supports8dot(boolean val) { supports8dot = val; return this; }
	public SimpleEmbosserProperties supportsDuplex(boolean val) { supportsDuplex = val; return this; }
	public SimpleEmbosserProperties supportsAligning(boolean val) { supportsAligning = val; return this; }
	public SimpleEmbosserProperties supportsVolumes(boolean val) { supportsVolumes = val; return this; }
	public SimpleEmbosserProperties paper(Paper paper) {
		if (paper!=null) {
			maxWidth = paper.getWidth(cellWidth);
			maxHeight = paper.getHeight(cellHeight);
		}
		return this;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public boolean supports8dot() {
		return supports8dot;
	}

	public boolean supportsAligning() {
		return supportsAligning;
	}

	public boolean supportsDuplex() {
		return supportsDuplex;
	}

	public boolean supportsVolumes() {
		return supportsVolumes;
	}
}