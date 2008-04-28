/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.util.text;

/**
 * A collection of text and string related utilities.
 * @author Linus Ericson
 */
public class TextUtils {

	private static final int MED_INS_COST = 1;   // Cost of insertion
	private static final int MED_SUBST_COST = 2; // Cost of substitution
	private static final int MED_DEL_COST = 1;   // Cost of deletion
	
	/**
	 * Find the lowest value between three integers. 
	 * @return the lowest value
	 */
	private static int min(int a, int b, int c) {
		int min = a;
		if (b < min) {
			min = b;
		}
		if (c < min) {
			min = c;
		}
		return min;
	}
	
	/**
	 * Computes the minimum edit distance between two strings.
	 * @param target the target string
	 * @param source the source string
	 * @return the minimum edit distance
	 */
	public static int minimumEditDistance(String target, String source) {
		int n = target.length();
		int m = source.length();
		int distance[][] = new int[n+1][m+1];
		for (int i = 0; i <= n; ++i) {
			distance[i][0] = i;
		}
		for (int j = 0; j <= m; ++j) {
			distance[0][j] = j;
		}
		for (int i = 1; i <= n; ++i) {
			char target_i = target.charAt(i-1);
			for (int j = 1; j <= m; ++j) {
				char source_j = source.charAt(j-1);
				int subst_cost = (target_i==source_j) ? 0 : MED_SUBST_COST;
				distance[i][j] = min(distance[i-1][j] + MED_INS_COST, 
						             distance[i-1][j-1] + subst_cost, 
						             distance[i][j-1] + MED_DEL_COST);
			}
		}
		return distance[n][m];
	}
		
}
