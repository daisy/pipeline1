package org_pef_dtbook2pef.system.tasks.layout.flow;

public class Leader {
	public enum Alignment {LEFT, RIGHT, CENTER};
	private final String pattern;
	private final int position;
	private final Alignment align;
	
	public Leader(String pattern, int position, Alignment align) {
		this.pattern = pattern;
		this.position = position;
		this.align = align;
	}
	
	public String getPattern() {
		return pattern;
	}

	public int getPosition() {
		return position;
	}
	
	public Alignment getAlignment() {
		return align;
	}

}
