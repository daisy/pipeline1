package org_pef_dtbook2pef.setups.sv_SE.definers;

import java.util.ArrayList;

import org_pef_dtbook2pef.system.tasks.layout.page.BaseLayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMasterConfigurator;
import org_pef_dtbook2pef.system.tasks.layout.page.field.CurrentPageField;
import org_pef_dtbook2pef.system.tasks.layout.page.field.NumeralField.NumeralStyle;

public class FrontLayoutMaster extends BaseLayoutMaster {

	public FrontLayoutMaster(LayoutMasterConfigurator config) { //int flowWidth, int pageHeight, int innerMargin, int outerMargin, float rowSpacing
		super(config.headerHeight(1).footerHeight(0));
		//super(flowWidth+innerMargin+outerMargin, pageHeight, 1, 0, innerMargin, outerMargin, rowSpacing);
	}
/*
	public ArrayList<ArrayList<String>> getHeader(Page p) {
		ArrayList<ArrayList<String>> ret = new ArrayList<ArrayList<String>>();
		ArrayList<String> r = new ArrayList<String>();
		int pagenum = p.getPageIndex() + 1;
		String pagenumStr = "" + RomanNumeral.int2roman(pagenum);
		if (pagenum % 2 == 0) { // EVEN PAGE
			r.add(pagenumStr);
		} else { // ODD PAGE
			r.add("");
			r.add(pagenumStr);
		}
		ret.add(r);
		return ret;
	}
	*/
	
	public ArrayList<ArrayList<Object>> getHeader(int pagenum) {
		ArrayList<ArrayList<Object>> ret = new ArrayList<ArrayList<Object>>();
		ArrayList<Object> r = new ArrayList<Object>();
		CurrentPageField pagenumStr = new CurrentPageField(NumeralStyle.ROMAN);
		if (pagenum % 2 == 0) { // EVEN PAGE
			r.add(pagenumStr);
		} else { // ODD PAGE
			r.add("");
			r.add(pagenumStr);
		}
		ret.add(r);
		return ret;
	}

	public ArrayList<ArrayList<Object>> getFooter(int pagenum) {
		return new ArrayList<ArrayList<Object>>();
	}

}
