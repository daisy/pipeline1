package org_pef_dtbook2pef.setups.sv_SE.definers;

import java.util.ArrayList;

import org_pef_dtbook2pef.system.tasks.layout.page.BaseLayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.page.field.CompoundField;
import org_pef_dtbook2pef.system.tasks.layout.page.field.CurrentPageField;
import org_pef_dtbook2pef.system.tasks.layout.page.field.MarkerReferenceField;
import org_pef_dtbook2pef.system.tasks.layout.page.field.NumeralField.NumeralStyle;

public class BodyLayoutMaster extends BaseLayoutMaster {

	public BodyLayoutMaster(int pageWidth, int pageHeight, int innerMargin, int outerMargin) {
		super(pageWidth, pageHeight, 1, 0, innerMargin, outerMargin);
	}
/*
	public ArrayList<ArrayList<Field>> getHeader(Page p) {
		ArrayList<ArrayList<Field>> ret = new ArrayList<ArrayList<Field>>();
		ArrayList<Field> r = new ArrayList<Field>();
		int pagenum = p.getPageIndex() + 1;
		String originalPage =	findMarker(p, "pagenum-turn", MarkerSearchDirection.FORWARD, MarkerSearchScope.PAGE) +
								findMarker(p, "pagenum", MarkerSearchDirection.BACKWARD, MarkerSearchScope.SEQUENCE);
		String pagenumStr = "" + pagenum;
		if (pagenum % 2 == 0) { // EVEN PAGE
			r.add(pagenumStr);
			r.add(originalPage);
		} else { // ODD PAGE
			r.add(originalPage);
			r.add(pagenumStr);
		}
		ret.add(r);
		return ret;
	}*/
	
	public ArrayList<ArrayList<Object>> getHeader(int pagenum) {
		ArrayList<ArrayList<Object>> ret = new ArrayList<ArrayList<Object>>();
		ArrayList<Object> r = new ArrayList<Object>();
		CompoundField originalPage = new CompoundField();
		originalPage.add(new MarkerReferenceField("pagenum-turn", MarkerReferenceField.MarkerSearchDirection.FORWARD, MarkerReferenceField.MarkerSearchScope.PAGE));
		originalPage.add(new MarkerReferenceField("pagenum", MarkerReferenceField.MarkerSearchDirection.BACKWARD, MarkerReferenceField.MarkerSearchScope.SEQUENCE));
			
			//findMarker(p, "pagenum-turn", MarkerSearchDirection.FORWARD, MarkerSearchScope.PAGE) +
				//				findMarker(p, "pagenum", MarkerSearchDirection.BACKWARD, MarkerSearchScope.SEQUENCE);
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
