package org_pef_pefFileMerger;

import java.util.ArrayList;

public class NumeralSortString implements Comparable<NumeralSortString> {
	private ArrayList<Part> parts;
	private String str;
	
	private static class Part implements Comparable<Part> {
		enum Type {STRING, NUMBER}
		Type type;
		Integer intValue;
		String strValue;

		public Part(String str) {
			this.strValue = str;
			try {
				this.intValue = Integer.parseInt(str);
				type = Type.NUMBER;
			} catch (NumberFormatException e) {
				this.intValue = null;
				type = Type.STRING;
			}
		}
		
		public Type getType() {
			return type;
		}
		
		public Integer asNumber() {
			return intValue;
		}

		public String asString() {
			return strValue;
		}

		public int compareTo(Part otherObj) {
			if (otherObj==null) {
				throw new NullPointerException();
			}
			if (this.getType()==otherObj.getType()) {
				switch (this.getType()) {
					case NUMBER:
						return this.asNumber().compareTo(otherObj.asNumber());
					case STRING:
						return this.asString().compareTo(otherObj.asString());
				}
				return 0;
			} else if (this.getType()==Type.NUMBER) {
				return -1;
			} else {
				return 1;
			}
		}
		
		public boolean equals(Part otherObj) {
			if (otherObj==null) {
				return false;
			} else if (this.getType()==otherObj.getType()) {
				switch (this.getType()) {
					case NUMBER:
						return this.asNumber().equals(otherObj.asNumber());
					case STRING:
						return this.asString().equals(otherObj.asString());
				}
			}
			return false;
		}

	}

	public NumeralSortString(String str) {
		parts = new ArrayList<Part>();
		this.str = str;
		String[] partStr = str.split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");
		for (String part : partStr) {
			parts.add(new Part(part));
		}
	}
	
	public Part getPart(int index) {
		return parts.get(index);
	}
	
	public int getPartCount() {
		return parts.size();
	}
	
	public String getValue() {
		return str;
	}

	public int compareTo(NumeralSortString otherObj) {
		if (otherObj==null) {
			throw new NullPointerException();
		}
		int thisLen = this.getPartCount();
		int otherLen = otherObj.getPartCount();
		int len = Math.min(thisLen, otherLen);
		for (int i=0; i<len; i++) {
			int c = this.getPart(i).compareTo(otherObj.getPart(i));
			if (c!=0) {
				return c;
			}
		}
		if (thisLen==otherLen) {
			return 0;
		} else if (thisLen < otherLen) {
			return -1;
		} else { 
			return 1;
		}
	}

	public boolean equals(NumeralSortString otherObj) {
		if (otherObj==null) {
			return false;
		}
		int thisLen = this.getPartCount();
		int otherLen = otherObj.getPartCount();
		if (thisLen==otherLen) {
			for (int i=0; i<thisLen; i++) {
				if (!this.getPart(i).equals(otherObj.getPart(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
