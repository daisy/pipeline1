package org.daisy.dmfc.gui;




import java.awt.BorderLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Splash screen, under development
 * @author Laurie Sherve
 */
public class AboutDialog extends Composite {
	private Shell shell;
	
	public AboutDialog() {
		this(new Shell(UIManager.display, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM));
	}
	
	AboutDialog(final Shell shell) {
		super(shell, SWT.NONE);
		this.shell = shell;
		shell.setLayout(new FillLayout());
		shell.setText("About DMFC...");
		shell.setSize(356, 275);
		shell.setLocation(334, 246);
			
		
		Canvas canvas = new Canvas(this, SWT.NONE) {
			public Point computeSize(int widthHint, int heightHint) {
				Point p = super.computeSize(widthHint, heightHint);
				
				return p;
			}
		};
		/*
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(UIManager.browserPNG, 0, 0);
			}
		});
		*/
		canvas.setLayoutData(BorderLayout.WEST);
		
		Label label = new Label(shell, SWT.LEFT | SWT.WRAP);
		label.setText("DAISY MultiFormat Converter  Copyright 2006 The DAISY Consortium\n\n\n" +
				"DISCLAMER:\n" + 
				"We are not responsible for any damage" + 
				" directly or indirectly caused by the " + 
				" DAISY Multi-Format Converter" + 
				" Use at your own risk." + 
				" This or any other class by DAISY is not" + 
				" certified for use in life support systems, by" + 
				" Lockheed Martin engineers, in developement" + 
				" or use of nuclear reactors, weapons of mass" + 
				" destruction, or in inter-planetary conflict." + 
				"  (Unless otherwise specified)");
	}
	
	public void open() {
		shell.open();
		layout();
		
		while (!shell.isDisposed())
			if (!UIManager.display.readAndDispatch()) UIManager.display.sleep();
	}
}




