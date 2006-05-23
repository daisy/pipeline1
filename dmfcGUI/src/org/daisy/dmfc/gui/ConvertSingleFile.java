package org.daisy.dmfc.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.daisy.dmfc.core.DMFCCore;
import org.daisy.dmfc.core.script.Parameter;
import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.core.script.Task;
import org.daisy.dmfc.core.transformer.ParameterInfo;
import org.daisy.dmfc.core.transformer.TransformerInfo;
import org.daisy.dmfc.gui.menus.MenuSingleConvert;
import org.daisy.dmfc.gui.widgetproperties.ButtonProperties;
import org.daisy.dmfc.gui.widgetproperties.FormAttachmentsHelper;
import org.daisy.dmfc.gui.widgetproperties.IProperties;
import org.daisy.dmfc.gui.widgetproperties.LabelProperties;
import org.daisy.dmfc.gui.widgetproperties.TextProperties;
import org.daisy.dmfc.qmanager.Job;
import org.daisy.dmfc.qmanager.Status;
import org.daisy.util.mime.MIMEType;
import org.daisy.util.mime.MIMETypeException;
import org.daisy.util.mime.MIMETypeFactory;
import org.daisy.util.mime.MIMETypeFactoryException;
import org.daisy.util.mime.MIMETypeRegistry;
import org.daisy.util.mime.MIMETypeRegistryException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * User chooses a single file, and output file to convert on this screen
 * @author Laurie Sherve
 *
 */
public class ConvertSingleFile extends Composite {
	
	Shell shell;
	Window window;
	FormAttachmentsHelper fah = new FormAttachmentsHelper();
	IProperties labelProperties = new LabelProperties();
	IProperties textProperties = new TextProperties();
	IProperties buttonProperties = new ButtonProperties();
	
	
	//Labels
	Label lblNameConversion;
	Label lblInputDocument;
	Label lblOutputDocument;
	Label lblDaisyMFC;
	
	//Buttons
	public Button btnBrowseInput;
	public Button btnBrowseOutput;
	public Button btnOK;
	public Button btnCancel;
	
	//TextFields
	Text txtConversionName;
	Text txtInputDoc;
	Text txtOutputDoc;
	
	//String
	String fileSelected;
	String outputPath;
	String script;
	String outExtensionPattern;
	
	//boolean - is this an edit or a single file selection?
	//defaults to editFile, false.
	boolean editFile = false;
	boolean boolOutputIsDir = false;
	
	//Array of output mime types
	String [] arFileOrDir = null;
	
	//ArrayList of Jobs
	ArrayList alJobs = new ArrayList();
	
	//Original job.  If used in edit mode, this is returned if canceled
	Job editJob;
	
	//ScriptHandler object, get name of the conversion
	ScriptHandler scriptHandler;
	
	//GridData
	GridData data;
	
	private DMFCCore dmfc;
	
	
	public ConvertSingleFile(DMFCCore core) {
		this(core, new Shell(UIManager.display));
	}
	
	
	public ConvertSingleFile(DMFCCore core, final Shell shell) {
		super(shell, SWT.NONE);
		this.shell = shell;
		this.dmfc = core;
		
		UIManager.windowNum++;
		new MenuSingleConvert(shell);
		
		shell.setText("Convert Single File");
		//shell.setBounds(0,0,500,500);
		shell.setLocation(150, 150);
		
		GridLayout layout = new GridLayout();
		layout.numColumns=1;
		layout.marginTop=0;
		layout.marginBottom=10;
		layout.marginRight=10;
		layout.marginLeft=15;
		shell.setLayout(layout);
		
		
		
		//3 composites with borders for accessibility
		
		
		
		//Composite conversion stuff	
		Composite compConversionChosen = new Composite(shell, SWT.BORDER);
		//compConversionChosen.setBackground(ColorChoices.white);
		data = new GridData(GridData.FILL_HORIZONTAL);
		compConversionChosen.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=8;
		layout.numColumns=2;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compConversionChosen.setLayout(layout);
		
		
		
//		Label
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		lblNameConversion = new Label(compConversionChosen, SWT.NONE);
		lblNameConversion.setText("Name of Conversion Selected");
		lblNameConversion.setLayoutData(data);
		
//		Text area
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
		data.widthHint=100;
		txtConversionName = new Text(compConversionChosen, SWT.BORDER);
		textProperties.setProperties(txtConversionName, null);	
		txtConversionName.setLayoutData(data);
		scriptHandler = window.getInstance().getConversionChosen();
		txtConversionName.setText(scriptHandler.getName());
		
//		
		
		//End ScriptHandler stuff
		
		
		
		
		
		
		//Input stuff
		Composite compInputFields = new Composite(shell, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		compInputFields.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=8;
		layout.numColumns=2;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compInputFields.setLayout(layout);
		
		
//		Label for input Doc
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=2;
		lblInputDocument = new Label(compInputFields, SWT.NONE);
		labelProperties.setProperties(lblInputDocument, "File to Convert");
		lblInputDocument.setLayoutData(data);
		
//		Text Field, input choice
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.widthHint=200;
		txtInputDoc = new Text(compInputFields, SWT.BORDER);
		textProperties.setProperties(txtInputDoc, null);
		txtInputDoc.setLayoutData(data);
		
//		Browse button
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		btnBrowseInput= new Button(compInputFields, SWT.BORDER);
		buttonProperties.setProperties(btnBrowseInput, "Browse");
		btnBrowseInput.setLayoutData(data);
		this.btnBrowseInput.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setFileSelected();
			}
		});
		
		
		
		//Composite output
		
		Composite compOutputFields = new Composite(shell, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		compOutputFields.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=8;
		layout.numColumns=2;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compOutputFields.setLayout(layout);
		
		
//		Label for Output Doc
		lblOutputDocument = new Label(compOutputFields, SWT.NONE);
		lblOutputDocument.setText("Output File");
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=2;
		lblOutputDocument.setLayoutData(data);
		
		//Text field
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.widthHint=200;
		txtOutputDoc=new Text(compOutputFields, SWT.BORDER);
		textProperties.setProperties(txtOutputDoc, null);
		txtOutputDoc.setLayoutData(data);
		
		
		//Browse Button
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		btnBrowseOutput = new Button(compOutputFields, SWT.BORDER);
		buttonProperties.setProperties(btnBrowseOutput, "Browse");
		btnBrowseInput.setLayoutData(data);
		this.btnBrowseOutput.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setOutputPathSelected();
			}
		});
		
		
		
		
		//bottom OK and Cancel buttons
		Composite compOkCancelButtons = new Composite (shell, SWT.NONE);
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER );
		compOkCancelButtons.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=12;
		layout.numColumns=2;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compOkCancelButtons.setLayout(layout);
		
		data=new GridData(GridData.HORIZONTAL_ALIGN_CENTER );
		btnOK = new Button(compOkCancelButtons, SWT.BORDER);
		buttonProperties.setProperties(btnOK, "OK");
		btnOK.setLayoutData(data);
		
		btnOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//if (lblDaisyMFC.getText().equalsIgnoreCase("Edit Conversion")){
				if (editFile == true){
					sendEditedJobToMain();
				}
				sendJobInfoToMain();
			}
		});
		
		data=new GridData(GridData.HORIZONTAL_ALIGN_CENTER );
		btnCancel= new Button(compOkCancelButtons, SWT.BORDER);
		buttonProperties.setProperties(btnCancel, "Cancel");
		btnCancel.setLayoutData(data);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//if (lblDaisyMFC.getText().equalsIgnoreCase("Edit Conversion")){
				if (editFile == true){
					Window.getInstance().addToQueue(editJob);
				}
				dispose();
			}
		});
		
		
		
		
		shell.pack();
	}
	
	public void open() {
		shell.open();
		
		while (!shell.isDisposed())
			if (!UIManager.display.readAndDispatch()) UIManager.display.sleep();
	}
	
	public void dispose() {
		shell.dispose();
	}
	
	
//	Methods called by Listeners
	/**
	 * 
	 */
	public void setFileSelected() {
		FileDialog dlg = new FileDialog(shell, SWT.OPEN);
		dlg.setText("Choose an input file");
		if (txtInputDoc != null) {
			File file = new File(txtInputDoc.getText());
			if (file.exists() && !file.isDirectory()) {
				file = file.getParentFile();
			}
			dlg.setFilterPath(file.getAbsolutePath());
		}
		
		//filter names shown
		dlg.setFilterExtensions(this.getGlobFromMime(this.getMimeForProperty("input")));
		
		fileSelected = dlg.open();		
		
		if (fileSelected!=null ){
			System.out.println("File selected  " + fileSelected);
			this.txtInputDoc.setText(fileSelected);
		}
		
	}
	
	public void setOutputPathSelected() {
		String mimeOut = this.getMimeForProperty("outputPath");
		if ("application/x-filesystemDirectory".equals(mimeOut)) {	
			// Directory
			DirectoryDialog directoryDialog = new DirectoryDialog(shell);
			directoryDialog.setText("Choose output directory");
			directoryDialog.setFilterPath("c://");
			outputPath = directoryDialog.open();
			System.out.println("outputPath Selected  " + outputPath);
			this.txtOutputDoc.setText("");
			if (outputPath==null ){
				System.out.println("outputPath is not selected " );
			}
			else{
				//add a new directory to this...
				this.txtOutputDoc.setText(outputPath);
			}
		} else {
			// File
			FileDialog dlg = new FileDialog(shell, SWT.SAVE);
			dlg.setText("Choose output file");
			if (txtOutputDoc != null) {
				File file = new File(txtOutputDoc.getText());
				if (file.exists() && !file.isDirectory()) {
					file = file.getParentFile();
				}
				dlg.setFilterPath(file.getAbsolutePath());
			}
			dlg.setFilterExtensions(this.getGlobFromMime(mimeOut));			
			outputPath = dlg.open();					
			if (outputPath!=null ){
				this.txtOutputDoc.setText(outputPath);
			}
		}
	}
	
	public void sendJobInfoToMain() {
		if (txtInputDoc.getText().equalsIgnoreCase("") || txtInputDoc == null
				|| txtOutputDoc.getText().equalsIgnoreCase("")
				|| txtOutputDoc == null) {
			
			// display an error message and return.
			
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
			messageBox.setMessage("File to Convert and Output File must be completed");
			messageBox.setText("Error:  Complete Fields");
			messageBox.open();
			
			
		} else {			
			Job job = new Job();
			job.setInputFile(new File(fileSelected));
			job.setOutputFile(new File(outputPath));
			job.setScript(scriptHandler);
			job.setStatus(Status.WAITING);
			Window.getInstance().addToQueue(job);
			
			dispose();
		}
	}
	
	
	/**
	 * Sets information to be edited.
	 * @param Job job, called from Job table at Window.
	 */
	public void editConversion(Job job){
		this.editJob=job;
		editFile= true;
		shell.setText("Daisy Multi-Format Converter, Edit Conversion");
		txtConversionName.setText(job.getScript().getName());
		txtInputDoc.setText(job.getInputFile().getPath());
		txtOutputDoc.setText(job.getOutputFile().getPath());
		scriptHandler = job.getScript();
		
	}
	
	public void sendEditedJobToMain(){
		this.fileSelected= txtInputDoc.getText();
		this.outputPath=txtOutputDoc.getText();
	}
	
	
	
	public String[] getGlobFromMime(String mime) {
		try {
			MIMEType type = MIMETypeRegistry.getInstance().getEntryByName(mime);
			Object[] arr = type.getFilenamePatterns().toArray();
			String[] ret = new String[arr.length + 1];
			for (int i = 0; i < arr.length; ++i) {
				ret[i] = (String)arr[i];
			}
			ret[arr.length] = "*.*";
			//System.err.println("Glob: " + ret);
			return ret;
		} catch (MIMETypeRegistryException e) {
		} catch (MIMETypeException e) {
		}
		return null;
	}
	
	public String getMimeForProperty(String property) {
		ScriptHandler handler = this.scriptHandler;
		
		// Generate random string
		Random random =  new Random();
        long long1 = random.nextLong();
        long long2 = random.nextLong();
        String hash1 = Long.toHexString(long1);
        String hash2 = Long.toHexString(long2);
        String hash = hash1 + hash2;
        
        // Set property
        handler.setProperty(property, hash);
        
        // Search for script param
        String taskName = null;
        String paramName = null;
        for (Iterator it = handler.getTasks().iterator(); it.hasNext(); ) {
        	Task task = (Task)it.next();
        	Collection params = task.getParameters().values();
        	for (Iterator it2 = params.iterator(); it2.hasNext(); ) {
        		Parameter param = (Parameter)it2.next();
        		if (hash.equals(param.getValue())) {
        			taskName = task.getName();
        			paramName = param.getName();
        			//System.err.println("Found script: " + taskName + ", param: " + paramName);
        		}        		
        	}
        }
        
        // Search for parameter
        TransformerInfo tInfo = dmfc.getTransformerInfo(taskName);
    	Collection params = tInfo.getParameters();
    	for (Iterator it = params.iterator(); it.hasNext(); ) {
    		ParameterInfo pInfo = (ParameterInfo)it.next();
    		if (pInfo.getName().equals(paramName)) {
    			//System.err.println("The type is: " + pInfo.getType());
    			return pInfo.getType();
    		}
    	}
    	
    	// Return null if nothing is found
		return null;
	}
	
	
}
