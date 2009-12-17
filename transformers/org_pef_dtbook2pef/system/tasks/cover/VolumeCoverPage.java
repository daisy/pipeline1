package org_pef_dtbook2pef.system.tasks.cover;

import java.util.ArrayList;

import org_pef_dtbook2pef.system.tasks.layout.flow.Row;

public interface VolumeCoverPage {
	
	/**
	 * Build cover page for this volume
	 * @param volumeNo
	 * @param volumeCount
	 * @return
	 */
	public ArrayList<Row> buildPage(int volumeNo, int volumeCount);

}
