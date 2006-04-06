package org.daisy.dmfc.gui;

import org.daisy.dmfc.gui.widgetproperties.ButtonProperties;
import org.daisy.dmfc.gui.widgetproperties.ColorChoices;
import org.daisy.dmfc.gui.widgetproperties.FontChoices;
import org.daisy.dmfc.gui.widgetproperties.FormAttachmentsHelper;
import org.daisy.dmfc.gui.widgetproperties.IProperties;
import org.daisy.dmfc.gui.widgetproperties.LabelProperties;
import org.daisy.dmfc.gui.widgetproperties.ProgressBarProperties;
import org.daisy.dmfc.gui.widgetproperties.TextProperties;
import org.daisy.dmfc.gui.widgetproperties.TransformerListTableProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;


/**
 * Gives the details of the current job.
 * @todo - originally to be displayed on primary screen, but 
 * there is limited real estate there.  
 * 
 * Shows details of the conversion running.
 * 
 * @author Laurie Sherve
 *
 */
public class CurrentJobDetails extends Composite{

	Shell shell;
	Window window;
	
	FormAttachmentsHelper fah = new FormAttachmentsHelper();
	IProperties labelProperties = new LabelProperties();
	IProperties textProperties = new TextProperties();
	IProperties buttonProperties = new ButtonProperties();
	IProperties pbProperties = new ProgressBarProperties();
	
	//Buttons
	Button btnTerminate;
	Button btnMain;
	
	//Labels
	Label lblListTransformers;
	Label lblElapsedTime;
	Label lblEstimatedTime;
	Label lblTotalConversionProgress;
	Label lblListChecked;

	
	//TextFields
	Text txtElapsedTime;
	Text txtEstimatedTime;
	
	//Table
	Table tblListTransformers;
	
	//Progress bar
	ProgressBar pb;
	
	
	public CurrentJobDetails() {
		this(new Shell(UIManager.display));
	}
	
	
	public CurrentJobDetails(final Shell shell) {
		super(shell, SWT.NONE);
		this.shell = shell;
		
		shell.setText("Daisy Multi Format Converter");
		shell.setMaximized(true);
		shell.setBackground(ColorChoices.white);
		shell.setLayout(new FormLayout());
		
   //Title
		Label lblDaisyMFC = new Label(shell, SWT.NONE);
		lblDaisyMFC.setForeground(ColorChoices.darkBlue);
		lblDaisyMFC.setText("Current Job Details");
		lblDaisyMFC.setFont(FontChoices.fontSubTitle);
		lblDaisyMFC.setBackground(ColorChoices.white);
		
		FormData formData = new FormData();
		fah.setFormData(formData, 0,20,35,20,8,10,90,10);
		lblDaisyMFC.setLayoutData(formData);
		
	//Composite Left
		Composite compLeft = new Composite(shell, SWT.NONE);
		compLeft.setBackground(ColorChoices.white);
		RowLayout rowLeft = new RowLayout(SWT.VERTICAL);
		rowLeft.spacing = 13;
		rowLeft.wrap = true;
		rowLeft.fill = false;
		rowLeft.justify = false;
		compLeft.setLayout(rowLeft);
	
		
		this.lblListTransformers = new Label(compLeft, SWT.NONE);
		labelProperties.setProperties(lblListTransformers, "Current Conversion");
		
		this.lblListChecked = new Label(compLeft, SWT.NONE);
		labelProperties.setProperties(lblListChecked, "Each Transformer Checked as Completed");
		
		this.tblListTransformers = new Table(compLeft, SWT.CHECK |SWT.BORDER |SWT.V_SCROLL | SWT.H_SCROLL  |SWT.FULL_SELECTION );
		TransformerListTableProperties tltp = new TransformerListTableProperties(tblListTransformers);
		
		
		FormData formDataLeft = new FormData();
		formDataLeft.top = new FormAttachment(lblDaisyMFC, 20);
		formDataLeft.left = new FormAttachment(10, 20);
		formDataLeft.bottom = new FormAttachment(45, 10);
		formDataLeft.right = new FormAttachment(45,10);
		compLeft.setLayoutData(formDataLeft);
		
		
	//Conposite Right
		Composite compRight = new Composite(shell, SWT.NONE);
		compRight.setBackground(ColorChoices.white);
		RowLayout rowRight = new RowLayout(SWT.VERTICAL);
		rowRight.spacing = 20;
		rowRight.wrap = true;
		rowRight.fill = false;
		rowRight.justify = false;
		compRight.setLayout(rowRight);
		
		lblElapsedTime = new Label(compRight, SWT.NONE);
		labelProperties.setProperties(lblElapsedTime, "Elapsed Time of Transformation");
		
		txtElapsedTime = new Text(compRight, SWT.BORDER);
		textProperties.setProperties(txtElapsedTime, null);
		
		lblEstimatedTime = new Label(compRight, SWT.NONE);
		labelProperties.setProperties(lblEstimatedTime, "Estimated Time to Completion");
		
		txtEstimatedTime= new Text(compRight, SWT.BORDER);
		textProperties.setProperties(txtEstimatedTime, null);
		
		lblTotalConversionProgress= new Label(compRight, SWT.NONE);
		labelProperties.setProperties(lblTotalConversionProgress, "Conversion Progress");
		
		pb= new ProgressBar(compRight, SWT.NONE);
		pb.setSelection(35);
		
		FormData formDataRight = new FormData();
		formDataRight.top = new FormAttachment(lblDaisyMFC, 20);
		formDataRight.left = new FormAttachment(compLeft, 50);
		formDataRight.bottom = new FormAttachment(45, 10);
		formDataRight.right = new FormAttachment(90,10);
		compRight.setLayoutData(formDataRight);
		
		
	//Composite Bottom
		
		Composite compBottom = new Composite(shell, SWT.NONE);
		compBottom.setBackground(ColorChoices.white);
		RowLayout rowBottom = new RowLayout(SWT.HORIZONTAL);
		rowBottom.pack = false;
		rowBottom.spacing = 10;
		compBottom.setLayout(rowBottom);
		
		this.btnMain = new Button(compBottom, SWT.SHADOW_OUT);
		buttonProperties.setProperties(btnMain, " Go to Main Screen");
		btnMain.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					dispose();
				}
			});
		
		this.btnTerminate = new Button(compBottom, SWT.SHADOW_OUT);
		buttonProperties.setProperties(btnTerminate, " Terminate Run");
		
		FormData formDataBottom = new FormData();
		formDataBottom.top = new FormAttachment(compRight, 60);
		formDataBottom.left = new FormAttachment(30, 20);
		formDataBottom.bottom = new FormAttachment(70, 10);
		formDataBottom.right = new FormAttachment(65,10);
		compBottom.setLayoutData(formDataBottom);
		
		
	}	
	
	public void open() {
		shell.open();
		
		while (!shell.isDisposed())
			if (!UIManager.display.readAndDispatch()) UIManager.display.sleep();
	}
	
	public void dispose() {
		shell.dispose();
	}
	
}
