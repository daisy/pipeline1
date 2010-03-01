package org_pef_dtbook2pef.system.tasks.layout.flow;
/**
 * Class not in use.
 * @author joha
 *
 */
public class BlockPosition {
	public enum Alignment {TOP, BOTTOM, CENTER};
	private final Position position;
	private final Alignment align;
	
	public static class Builder {
		// optional
		private Alignment align;
		private Position pos;
		
		public Builder() {
			this.align = Alignment.TOP;
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
		
		public BlockPosition build() {
			return new BlockPosition(this);
		}
	}
	
	private BlockPosition(Builder builder) {
		this.position = builder.pos;
		this.align = builder.align;
	}

	public Position getPosition() {
		return position;
	}
	
	public Alignment getAlignment() {
		return align;
	}

}
