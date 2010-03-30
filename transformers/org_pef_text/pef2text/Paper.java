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
	
	private final double width;
	private final double height;
	private final String name;
	
	/**
	 * 
	 * @param width paper width, in millimeters
	 * @param height paper height, in millimeters
	 * @param name name of the paper
	 */
	public Paper(double width, double height, String name) {
		this.width = width;
		this.height = height;
		this.name = name;
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
	
	/**
	 * Get the display name for this paper.
	 * @return returns the display name
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(height);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		temp = Double.doubleToLongBits(width);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Paper other = (Paper) obj;
		if (Double.doubleToLongBits(height) != Double
				.doubleToLongBits(other.height))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(width) != Double
				.doubleToLongBits(other.width))
			return false;
		return true;
	}

}
