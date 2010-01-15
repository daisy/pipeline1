package org_pef_text.pef2text;

public class Paper {
	public static enum PaperSize {
		UNDEFINED, 
		A4, 
		W210MM_X_H10INCH, 
		W210MM_X_H11INCH, 
		W210MM_X_H12INCH,
		FA44,
		FA44_LEGACY};
	public static final double INCH_IN_MM = 25.4;
	
	private double width;
	private double height;
	
	public Paper(double width, double height, String name) {
		this.width = width;
		this.height = height;
	}
	
	public Paper(double width, double height) {
		this(width, height, "Custom");
	}
	
	public static Paper newPaper(PaperSize size) {
		switch (size) {
			case UNDEFINED:
				return null;
			case A4:
				return new Paper(210d, 297d, "A4");
			case W210MM_X_H10INCH:
				return new Paper(210d, 10*INCH_IN_MM, "210 mm wide, 10 inch high");
			case W210MM_X_H11INCH:
				return new Paper(210d, 11*INCH_IN_MM, "210 mm wide, 11 inch high");
			case W210MM_X_H12INCH:
				return new Paper(210d, 12*INCH_IN_MM, "210 mm wide, 12 inch high");
			case FA44:
				return new Paper(261d, 297d, "FA44 (accurate)");
			case FA44_LEGACY:
				return new Paper(252d, 297d, "FA44 (legacy)");
		}
		return null;
	}
	
	/**
	 * Get width, in mm.
	 * @return returns width in mm.
	 */
	public double getWidth() {
		return width;
	}
	
	/**
	 * Get height, in mm.
	 * @return returns height in mm.
	 */
	public double getHeight() {
		return height;
	}
	
	/**
	 * Get width, in units
	 * @param unit unit in mm
	 * @return returns width in units
	 */
	public int getWidth(double unit) {
		return (int)Math.floor(width / unit);
	}
	
	/**
	 * Get height, in units
	 * @param unit unit in mm
	 * @return returns width in units
	 */
	public int getHeight(double unit) {
		return (int)Math.floor(height / unit);
	}

}
