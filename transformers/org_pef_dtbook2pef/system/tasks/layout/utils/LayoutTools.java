package org_pef_dtbook2pef.system.tasks.layout.utils;


public class LayoutTools {
	
	public static String fill(char c, int amount) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<amount; i++) {
			sb.append(c);
		}
		return sb.toString();
	}

}
