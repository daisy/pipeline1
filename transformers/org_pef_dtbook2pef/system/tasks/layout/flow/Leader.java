package org_pef_dtbook2pef.system.tasks.layout.flow;

public class Leader {
	public enum Alignment {LEFT, RIGHT, CENTER};
	private final String pattern;
	private final Position position;
	private final Alignment align;
	
	public static class Builder {
		// optional
		private String pattern;
		private Alignment align;
		private Position pos;
		
		public Builder() {
			this.pattern = " ";
			this.align = Alignment.LEFT;
			this.pos = new Position(0, false);
		}

		public Builder position(Position pos) {
			this.pos = pos;
			return this;
		}
		
		public Builder align(Alignment align) {
			this.align = align;
			return this;
		}
		
		public Builder pattern(String pattern) {
			this.pattern = pattern;
			return this;
		}
		
		public Leader build() {
			return new Leader(this);
		}
	}
	
	private Leader(Builder builder) {
		this.pattern = builder.pattern;
		this.position = builder.pos;
		this.align = builder.align;
	}
	
	public String getPattern() {
		return pattern;
	}

	public Position getPosition() {
		return position;
	}
	
	public Alignment getAlignment() {
		return align;
	}

}
