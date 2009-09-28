package org_pef_dtbook2pef.system.tasks.layout.page;

import java.io.File;

public interface PagedMediaOutput {
	
	public void newSection(LayoutMaster master);
	public void newPage();
	public void newRow(CharSequence row);
	public void open(File f);
	public void close();

}
