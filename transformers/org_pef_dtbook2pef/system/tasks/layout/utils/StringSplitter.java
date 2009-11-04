package org_pef_dtbook2pef.system.tasks.layout.utils;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringSplitter {

	/**
	 * Split the input string using the regular expression. Similar to the {@link String#split(String) split}
	 * method in the {@link String} class. However, contrary to {@link String#split(String) split},
	 * all subsequences are returned, even the ones that match. I.e.,
	 * the input can be  reconstructed from the result.
	 * @param input the String to split
	 * @param regex the regular expression to use
	 * @return Returns an array of SplitResults that combined contains all the characters from the input.
	 */
	public static SplitResult[] split(CharSequence input, String regex) {
		ArrayList<SplitResult> ret = new ArrayList<SplitResult>();
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(input);

		int index = 0;
		while (m.find()) {
			if (m.start()>index) {
				ret.add(new SplitResult(input.subSequence(index, m.start()).toString(), false));
			}
			ret.add(new SplitResult(input.subSequence(m.start(), m.end()).toString(), true));
			index = m.end();
		}
		if (index==0) {
			return new SplitResult[] {new SplitResult(input.toString(), false)};
		}
		// add remaining segment
		if (index<input.length()) {
			ret.add(new SplitResult(input.subSequence(index, input.length()).toString(), false));
		}

		int resultSize = ret.size();
		SplitResult[] result = new SplitResult[resultSize];
		return ret.toArray(result);
	}

}
