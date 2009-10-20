package org_pef_dtbook2pef.system.tasks.layout.text;
/**
 * @author joha
 *
 */
public interface FilterFactory {

	/**
	 * Get the default StringFilter
	 * @return returns the default StringFilter
	 */
	public StringFilter getDefault();
	
	/**
	 * Get a new StringFilter for the specified FilterLocale
	 * @param target the FilterLocale for the StringFilter
	 * @return returns a new StringFilter for the specified FilterLocale
	 */
	public StringFilter newStringFilter(FilterLocale target);

	/**
	 * Set the default StringFilter for this Factory.
	 * @param filter the StringFilter to use
	 */
	public void setDefault(StringFilter filter);

}
