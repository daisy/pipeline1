package org_pef_dtbook2pef.setups.sv_SE.definers;

import java.util.ArrayList;

import org_pef_dtbook2pef.system.tasks.layout.page.BaseLayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMasterConfigurator;
import org_pef_dtbook2pef.system.tasks.layout.page.field.CompoundField;
import org_pef_dtbook2pef.system.tasks.layout.page.field.CurrentPageField;
import org_pef_dtbook2pef.system.tasks.layout.page.field.MarkerReferenceField;
import org_pef_dtbook2pef.system.tasks.layout.page.field.NumeralField.NumeralStyle;

public class BodyLayoutMaster extends BaseLayoutMaster {

	public BodyLayoutMaster(LayoutMasterConfigurator config) {// int flowWidth, int pageHeight, int innerMargin, int outerMargin, float rowSpacing) {
		//super(new Builder(12, 12).build());
		super(config.headerHeight(1).footerHeight(0));
		//super(flowWidth+innerMargin+outerMargin, pageHeight, 1, 0, innerMargin, outerMargin, rowSpacing);
	}
	
	public ArrayList<ArrayList<Object>> getHeader(int pagenum) {
		ArrayList<ArrayList<Object>> ret = new ArrayList<ArrayList<Object>>();
		ArrayList<Object> r = new ArrayList<Object>();
		CompoundField originalPage = new CompoundField();
		originalPage.add(new MarkerReferenceField("pagenum-turn", MarkerReferenceField.MarkerSearchDirection.FORWARD, MarkerReferenceField.MarkerSearchScope.PAGE));
		originalPage.add(new MarkerReferenceField("pagenum", MarkerReferenceField.MarkerSearchDirection.BACKWARD, MarkerReferenceField.MarkerSearchScope.SEQUENCE));

		CurrentPageField pagenumStr = new CurrentPageField(NumeralStyle.DEFAULT);
		if (pagenum % 2 == 0) { // EVEN PAGE
			r.add(pagenumStr);
			r.add(originalPage);
		} else { // ODD PAGE
			r.add(originalPage);
			r.add(pagenumStr);
		}
		ret.add(r);
		return ret;
	}
	
	public ArrayList<ArrayList<Object>> getFooter(int pagenum) {
		return new ArrayList<ArrayList<Object>>();
	}

}
