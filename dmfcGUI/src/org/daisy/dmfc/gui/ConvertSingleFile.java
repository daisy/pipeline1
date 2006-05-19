package org.daisy.dmfc.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.daisy.dmfc.core.script.ScriptHandler;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
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
	
	
	
	public ConvertSingleFile() {
		this(new Shell(UIManager.display));
	}
	
	
	public ConvertSingleFile(final Shell shell) {
		super(shell, SWT.NONE);
		this.shell = shell;
		
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
				setFileOrDirFlag();
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
	
	
	//general utility methods
	/**
	 * Sets the file or directory flag and determines the extension
	 */
	public void setFileOrDirFlag(){
		String [] arFiles = getFileTypesForScriptHandler("out");
		int count = arFiles.length;
		
		//hack until all tdfs completed and mimetypes registered?
		if (count==0){
			boolOutputIsDir = true;
		}
		
		for (int i = 0; i<count;i++){
			String pattern = (String)arFiles[i];
			
			if (pattern.equalsIgnoreCase("application/x-filesystemDirectory")){   
				boolOutputIsDir=true;
			}
			else{
				outExtensionPattern = pattern;
			}
		}
	}	
	
	
//	Methods called by Listeners
	/**
	 * 
	 */
	public void setFileSelected() {
		FileDialog dlg = new FileDialog(shell, SWT.OPEN);
		dlg.setText("Choose an input file");
		//dlg.setFilterPath("");
		
		//filter names shown
		dlg.setFilterExtensions(getFileTypesForScriptHandler("in"));
		
		fileSelected = dlg.open();
		
		this.txtInputDoc.setText("");
		if (fileSelected!=null ){
			System.out.println("Directory Selected  " + fileSelected);
			this.txtInputDoc.setText(fileSelected);
		}
		else{
			this.txtInputDoc.setText("");
		}
		
		
		
	}
	
	public void setOutputPathSelected() {
		DirectoryDialog directoryDialog = new DirectoryDialog(shell);
		directoryDialog.setText("Choose a directory");
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
	}

	
	
	
	public String addOutputDirectory(String output){
		
		String lastDir = "";
		StringTokenizer st = new StringTokenizer(output, File.separator);
		while(st.hasMoreTokens()){
			lastDir = st.nextToken();	
		}
		//System.out.println("The last token is " + lastDir);
		String strSubfolderOfInputFolder =output + File.separator + lastDir + File.separator;
		return strSubfolderOfInputFolder;
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
			
			//if the output is a file,  add the existing file name 
			//and the proper extension
			
			File inputFile = new File (fileSelected);
			
			if (boolOutputIsDir==false){
				outputPath = addFileNameToOutputPath(outputPath, inputFile);
			}
			
			
			Job job = new Job();
			job.setInputFile(inputFile);
			job.setOutputFile(new File(outputPath));
			job.setScript(scriptHandler);
			job.setStatus(Status.WAITING);
			Window.getInstance().addToQueue(job);
			
			dispose();
		}
	}
	
	
	public String addFileNameToOutputPath(String outPath, File inPath){
		String outPutPath = outPath;
		
		//only created if the output path is a directory.
		//sanity check, already done in sendInfoToMain()
		
		if (boolOutputIsDir==false){
		
			//in this case the extension is an msglob, and the *.
			//need to be removed
			StringTokenizer stExt = new StringTokenizer(outExtensionPattern, ".");
			String outExPat = "";
			while (stExt.hasMoreTokens()){
				outExPat = stExt.nextToken();
			}
			
			//Name of the input path file
			String strInPath = inPath.getName();
			StringTokenizer st = new StringTokenizer(strInPath, ".");
			//get just the name
			
			if (strInPath.endsWith("\\")){
//				create a new path with the path, nameof file, and appropriate extension
				outPutPath = outPath + st.nextToken()+ "." + outExPat ;
			}
			else{
				//create a new path with the path, nameof file, and appropriate extension
				outPutPath = outPath + "\\"+ st.nextToken()+ "." + outExPat ;
			}
		}
		
		System.out.println ("The REAL output path should be " + outPutPath);
		return outPutPath;
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
	
	
	
	
	
	
	/**
	 * 
	 * @return
	 */
	public String [] getFileTypesForScriptHandler(String inOrOut){
		
		ArrayList alMsglobs = new ArrayList();
		
		System.out.println("GetFileTypes for Script");
		Collection msglobs = null;
		
		//container to hold all the file types valid for script
		ArrayList alValid = new ArrayList();
		String [] arCompatibleFiles = null;
		
		//get the file types for the scripts
		String fileType = null;
		List list= this.scriptHandler.getTransformerInfoList();
		
		// get info on first transformer, change to list.get(list.size() - 1) for the last transformer
		//no, can only take the file in first transformer in the script list...
		TransformerInfo tinfo = (TransformerInfo)list.get(0);
		
		//Returns a collection of parameter information
		Collection col = tinfo.getParameters();
		
		Iterator it = col.iterator();		
		
		while(it.hasNext()){
			ParameterInfo pi =(ParameterInfo)it.next();
			String parameter = pi.getDirection();
			
			
			if (parameter !=null && parameter.equalsIgnoreCase(inOrOut)){
				fileType = pi.getType();
				System.out.println("Valid types for this script " + fileType);
				
				try {
					MIMEType mt = MIMETypeFactory.newInstance().newMimeType(fileType);
					msglobs = mt.getFilenamePatterns();
				} catch (MIMETypeFactoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MIMETypeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Iterator itPatterns = msglobs.iterator();	
				int size = msglobs.size();
				//arCompatibleFiles = new String [size];
				
				for (int j = 0; j<size; j++){
					if (itPatterns.hasNext()){
						String mimePattern = (String)itPatterns.next();
						if (mimePattern!=null && !mimePattern.equalsIgnoreCase("")){
							alMsglobs.add(mimePattern);
							//arCompatibleFiles[j]=mimePattern;
						}
					}
				}
				
				int count = alMsglobs.size();
				arCompatibleFiles = new String [count];
				Iterator itGlobs = alMsglobs.iterator();
				
				for (int k = 0; k<count; k++){
					arCompatibleFiles[k]=(String)itGlobs.next();
				}	
			}
		}
		return arCompatibleFiles;
	}
	
}
