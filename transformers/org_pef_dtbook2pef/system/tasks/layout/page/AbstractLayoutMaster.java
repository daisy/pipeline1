package org_pef_dtbook2pef.system.tasks.layout.page;

import java.util.ArrayList;

import org_pef_dtbook2pef.system.tasks.layout.flow.Marker;

public abstract class AbstractLayoutMaster implements LayoutMaster {
	public static enum MarkerSearchDirection {FORWARD, BACKWARD};
	public static enum MarkerSearchScope {PAGE, SEQUENCE};
	
	protected final int headerHeight;
	protected final int footerHeight;
	protected final int pageWidth;
	protected final int pageHeight;

	public AbstractLayoutMaster(int pageWidth, int pageHeight, int headerHeight, int footerHeight) {
		this.headerHeight = headerHeight;
		this.footerHeight = footerHeight;
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
	}

	public int getPageWidth() {
		return pageWidth;
	}
	
	public int getPageHeight() {
		return pageHeight;
	}
	
	public int getFlowHeight() {
		return pageHeight-(getHeaderHeight()+getFooterHeight());
	}

	public int getHeaderHeight() {
		return headerHeight;
	}

	public int getFooterHeight() {
		return footerHeight;
	}

	public String findMarker(Page page, String name, MarkerSearchDirection searchDir, MarkerSearchScope searchScope) {
		int dir = 1;
		int index = 0;
		int count = 0;
		ArrayList<Marker> m = page.getMarkers();
		if (searchDir == MarkerSearchDirection.BACKWARD) {
			dir = -1;
			index = m.size()-1;
		}
		while (count < m.size()) {
			Marker m2 = m.get(index);
			if (m2.getName().equals(name)) {
				return m2.getValue();
			}
			index += dir; 
			count++;
		}
		int nextPage = page.getPageIndex() + dir;
		if (searchScope == MarkerSearchScope.SEQUENCE && nextPage < page.getParent().getPages().size() && nextPage >= 0) {
			Page next = page.getParent().getPages().get(nextPage);
			return findMarker(next, name, searchDir, searchScope);
		}
		return "";
	}

}
