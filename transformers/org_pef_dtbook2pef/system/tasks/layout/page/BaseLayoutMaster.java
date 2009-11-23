package org_pef_dtbook2pef.system.tasks.layout.page;


//TODO remove
public class BaseLayoutMaster extends AbstractLayoutMaster {
	
	public BaseLayoutMaster(LayoutMasterConfigurator config) {
		super(config);
		
	}
	

/*
	public ArrayList<ArrayList<Object>> getHeader(int pagenum) {
		return templates.getTemplate(pagenum).getHeader();
		/*
		ArrayList<ArrayList<Object>> ret = new ArrayList<ArrayList<Object>>();
		ArrayList<Object> row = new ArrayList<Object>();
		for (int i = 0; i<getHeaderHeight(); i++) {
			ret.add(row);
		}
		return ret;*/
	//}
	/*
	public ArrayList<ArrayList<Object>> getFooter(int pagenum) {
		ArrayList<ArrayList<Object>> ret = new ArrayList<ArrayList<Object>>();
		ArrayList<Object> row = new ArrayList<Object>();
		for (int i = 0; i<getFooterHeight(); i++) {
			ret.add(row);
		}
		return ret;
	}*/

}
