package org.daisy.dmfc.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class BusyCursor {

	static int nextBusyId = 1;
	static final String BUSYID_NAME = "SWT BusyCursor";
	
	private static Display disp;
	private static Integer bId;
	
	public static void showWhile(Display display, Runnable runnable) {
		disp = display;
		if (runnable == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		if (display == null) {
			display = Display.getCurrent();
			if (display == null) {
				runnable.run();
				return;
			}
		}


		UIManager.display.syncExec(new Runnable(){
			public void run(){
				Integer busyId = new Integer(nextBusyId);
				bId = busyId;
				nextBusyId++;
				Cursor cursor = disp.getSystemCursor(SWT.CURSOR_APPSTARTING);
				Shell[] shells = disp.getShells();
				for (int i = 0; i < shells.length; i++) {
					Integer id = (Integer)shells[i].getData(BUSYID_NAME);
					if (id == null) {
						shells[i].setCursor(cursor);
						shells[i].setData(BUSYID_NAME, busyId);
					}
				}
			}
		});
		
		try {
			runnable.run();
		} finally {
			UIManager.display.syncExec(new Runnable(){
				public void run(){
					Shell[] shells = disp.getShells();
					for (int i = 0; i < shells.length; i++) {
						Integer id = (Integer)shells[i].getData(BUSYID_NAME);
						if (id == bId) {
							shells[i].setCursor(null);
							shells[i].setData(BUSYID_NAME, null);
						}
					}
				}
			});
		}
	}

}
