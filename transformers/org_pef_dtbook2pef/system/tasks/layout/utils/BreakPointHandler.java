package org_pef_dtbook2pef.system.tasks.layout.utils;

public class BreakPointHandler {
	private String charsStr;
	
	public BreakPointHandler(String str) {
		if (str==null) {
			throw new NullPointerException("Input string cannot be null.");
		}
		this.charsStr = str;
	}

	public class BreakPoint {
		private final String head;
		private final String tail;
		private final boolean hardBreak;

		private BreakPoint(String head, String tail, boolean hardBreak) {
			this.head = head;
			this.tail = tail;
			this.hardBreak = hardBreak;
		}
		
		public String getHead() {
			return head;
		}

		public String getTail() {
			return tail;
		}
		
		public boolean isHardBreak() {
			return hardBreak;
		}
	}

	public BreakPoint nextRow(int breakPoint) {
		if (charsStr.length()==0) {
			// pretty simple...
			return new BreakPoint("", "", false);
		}
		String head;
		String tail;
		boolean hard = false;
		assert charsStr.length()==charsStr.codePointCount(0, charsStr.length());
		if (charsStr.length()>breakPoint) {
			int strPos = -1;
			int len = 0;
			for (char c : charsStr.toCharArray()) {
				strPos++;
				switch (c) {
					case '\u00ad': 
						break;
					default:
						len++;
				}
				if (len>=breakPoint) {
					break;
				}
			}
			assert strPos<charsStr.length();
			
			int tailStart;
			
			/*if (strPos>=charsStr.length()-1) {
				head = charsStr.substring(0, strPos);
				System.out.println(head);
				tailStart = strPos;
			} else */
			// check next character to see if it can be removed.
			if (strPos==charsStr.length()-1) {
				head = charsStr.substring(0, strPos+1);
				tailStart = strPos+1;
			} else if (charsStr.charAt(strPos+1)==' ') {
				head = charsStr.substring(0, strPos+1);
				tailStart = strPos+2;
			} else { // back up
				int i=strPos;
whileLoop:		while (i>=0) {
					switch (charsStr.charAt(i)) {
						case ' ' : case '-' : case '\u00ad' : 
							break whileLoop;
					}
					i--;
				}
				if (i<0) { // no breakpoint found, break hard 
					hard = true;
					head = charsStr.substring(0, strPos+1);
					tailStart = strPos+1;
				} else if (charsStr.charAt(i)==' ') { // ignore space at breakpoint
					head = charsStr.substring(0, i);
					tailStart = i+1;
				} else if (charsStr.charAt(i)=='\u00ad'){ // convert soft hyphen to hard hyphen 
					head = charsStr.substring(0, i) + '-';
					tailStart = i+1;
				} else {
					head = charsStr.substring(0, i+1);
					tailStart = i+1;
				}
			}
			if (charsStr.length()>tailStart) {
				tail = charsStr.substring(tailStart);
			} else {
				tail = "";
			}
		} else {
			head = charsStr;
			tail = "";
		}
		charsStr = tail;
		return new BreakPoint(head, tail, hard);
	}

	public boolean hasNext() {
		return (charsStr!=null && charsStr.length()>0);
	}
}
