package org_pef_dtbook2pef.system.tasks.layout.text;

import java.util.ArrayList;

public class StringFilterHandler extends ArrayList<StringFilter> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5284947865448511026L;

	public String filter(String str) {
    	for (StringFilter e : this) {
    		str = e.replace(str);
    	}
    	return str;
    }

}
