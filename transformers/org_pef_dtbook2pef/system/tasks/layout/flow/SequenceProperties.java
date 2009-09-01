package org_pef_dtbook2pef.system.tasks.layout.flow;

/**
 * SequenceProperties stores the properties for the sequence.
 * @author joha
 *
 */
public class SequenceProperties {
	private String masterName;
	private int initialPageNumber;
	
	public static class Builder {
		//Required parameters
		String masterName;
		
		//Optional parameters
		int initialPageNumber = 1;
		
		public Builder(String masterName) {
			this.masterName = masterName;
		}
		
		public Builder initialPageNumber(int value) {
			initialPageNumber = value;
			return this;
		}
		
		public SequenceProperties build() {
			return new SequenceProperties(this);
		}
		
	}
	
	private SequenceProperties(Builder builder) {
		this.masterName = builder.masterName;
		this.initialPageNumber = builder.initialPageNumber;
	}
	
	public String getMasterName() {
		return masterName;
	}
	
	public int getInitialPageNumber() {
		return initialPageNumber;
	}

}
