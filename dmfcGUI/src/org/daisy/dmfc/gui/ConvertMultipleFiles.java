package org.daisy.dmfc.gui;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.daisy.dmfc.core.script.Property;
import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.core.transformer.ParameterInfo;
import org.daisy.dmfc.core.transformer.TransformerInfo;
import org.daisy.dmfc.gui.compatiblefilelist.FileLabelProvider;
import org.daisy.dmfc.gui.menus.MenuMultipleConvert;
import org.daisy.dmfc.gui.widgetproperties.ButtonProperties;
import org.daisy.dmfc.gui.widgetproperties.CompatibleFilesTableProperties;
import org.daisy.dmfc.gui.widgetproperties.FormAttachmentsHelper;
import org.daisy.dmfc.gui.widgetproperties.IProperties;
import org.daisy.dmfc.gui.widgetproperties.LabelProperties;
import org.daisy.dmfc.gui.widgetproperties.RadioButtonProperties;
import org.daisy.dmfc.gui.widgetproperties.TextProperties;
import org.daisy.dmfc.qmanager.Job;
import org.daisy.dmfc.qmanager.Status;
import org.daisy.util.mime.MIMEType;
import org.daisy.util.mime.MIMETypeException;
import org.daisy.util.mime.MIMETypeFactory;
import org.daisy.util.mime.MIMETypeFactoryException;
import org.daisy.util.mime.MIMETypeRegistry;
import org.daisy.util.mime.MIMETypeRegistryException;
import org.daisy.util.file.FileUtils;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


/**
 * Used to select multiple files.
 * All files selected will use the same conversion as listed 
 * on the top of the screen.
 * @author Laurie Sherve
 *
 */
public class ConvertMultipleFiles {
	
	
	Shell shell;
	Display display;
	Window window;
	FormAttachmentsHelper fah = new FormAttachmentsHelper();
	CompatibleFilesTableProperties cftp;
	IProperties labelProperties = new LabelProperties();
	IProperties textProperties = new TextProperties();
	IProperties buttonProperties = new ButtonProperties();
	IProperties radioButtonProperties = new RadioButtonProperties();
	
	// Labels
	Label lblNameConversion;
	Label lblInputDocument;
	Label lblOutputDocument;
	Label lblOnlyCompatibleShown;
	
	// Buttons
	Button btnBrowseInput;
	Button btnBrowseOutput;
	Button btnOK;
	Button btnCancel;
	public Button btnRadio1;
	Button btnRadio2;
	Button btnSelect;
	Button btnUnCheck;
	
	// TextFields
	Text txtConversionName;
	Text txtDirectorySelected;
	Text txtInputDoc;
	Text txtOutputDoc;
	
	//Tables
	Table tblCompatibleFiles;
	
	// String
	String dirSelected ;
	String outputPath = "";
	String script = "";
	String strSubfolderOfInputFolder;
	String strSubfolderOfOutputFolder;
	
	//Files
	File fileDirSelected;
	
	//boolean - is Directory
	boolean boolOutputIsDir=false;
	
	//String pattern of output path
	String outExtensionPattern= "";
	
	//GridData - reinitialized for each control.
	GridData data;
	
	//ScriptHandler
	ScriptHandler scriptHandler;
	
	//ArrayList to hold selected input files
	ArrayList al = new ArrayList();
	ArrayList alPatterns = new ArrayList();
	ArrayList alCompatibleFiles = new ArrayList();
	ArrayList alTableContents= new ArrayList();
	ArrayList alFileOrDir;
	
	//TableViewers
	CheckboxTableViewer tableFileViewer;
	
	String [] columnFileNames = new String [] {"File Name"};
	
	
	public ConvertMultipleFiles(){
		display= UIManager.display;
		shell = new Shell(display);
		new MenuMultipleConvert(shell);
		createContents();
		shell.pack();
	}
	
	
	public void createContents(){	
		
		
		shell.setText("Select Files to be Converted");
		shell.setLocation(100,100);
		
		GridLayout layout = new GridLayout();
		//different gridlayouts with various columns in each
		layout.numColumns=1;
		layout.marginTop=10;
		layout.marginBottom=10;
		layout.marginRight=10;
		layout.marginLeft=15;
		shell.setLayout(layout);
		
		
		
		//Composite top	
		Composite compConversionChosen = new Composite(shell, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		compConversionChosen.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=8;
		layout.numColumns=2;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compConversionChosen.setLayout(layout);
		
		//	Label
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=1;
		lblNameConversion = new Label(compConversionChosen, SWT.NONE);
		lblNameConversion.setText("Name of Conversion Selected");
		lblNameConversion.setLayoutData(data);
		
//		Text area
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
		data.widthHint=100;
		data.horizontalSpan=2;
		txtConversionName = new Text(compConversionChosen, SWT.BORDER);
		textProperties.setProperties(txtConversionName, null);	
		txtConversionName.setLayoutData(data);
		scriptHandler = window.getInstance().getConversionChosen();
		txtConversionName.setText(scriptHandler.getName());
		
//		
		//End Conversion stuff
		
		
		// Composite Input stuff
		Composite compInputFields = new Composite(shell, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		compInputFields.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=8;
		layout.numColumns=3;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compInputFields.setLayout(layout);
		
		GridLayout gridLayout = new GridLayout(3, false);
		compInputFields.setLayout(gridLayout);
		
		// Label folder to search in
		data =data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING); 
		lblInputDocument = new Label(compInputFields, SWT.NONE);
		lblInputDocument.setText("Folder to search for files to convert");
		data.horizontalSpan=1;
		lblInputDocument.setLayoutData(data);
		
		
		// TextField to hold folder chosen
		data =data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER); 
		txtDirectorySelected = new Text(compInputFields, SWT.BORDER);
		textProperties.setProperties(txtDirectorySelected, "");
		// data = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		data.horizontalSpan=1;
		data.widthHint = 400;
		txtDirectorySelected.setLayoutData(data);
		
		
		// Browse button
		data =data = new GridData(GridData.HORIZONTAL_ALIGN_END); 
		btnBrowseInput= new Button(compInputFields, SWT.BORDER);
		data = new GridData();
		data.horizontalSpan=1;
		btnBrowseInput.setLayoutData(data);
		buttonProperties.setProperties(btnBrowseInput, "Browse");
		
		this.btnBrowseInput.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setDirectorySelected();
				if (dirSelected!=null){
					
					populateCompatibleFilesTable();
					//determine if output is a file or folder
					setFileOrDirFlag();	
				}
			}
		});
		
		//End input stuff
		
		//Compatible file table and buttons 
		
		Composite compFilesTable = new Composite(shell, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		compFilesTable.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=15;
		layout.numColumns=3;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compFilesTable.setLayout(layout);
		
		data= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		lblOnlyCompatibleShown = new Label(compFilesTable, SWT.NONE);
		lblOnlyCompatibleShown.setLayoutData(data);
		lblOnlyCompatibleShown.setText("Only Compatible Files Shown");
		
		data= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		btnSelect = new Button(compFilesTable, SWT.BORDER);
		btnSelect.setLayoutData(data);
		btnSelect.setText("Select All");
		this.btnSelect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableFileViewer.setAllChecked(true);
			}
		});
		
		data= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		btnUnCheck = new Button(compFilesTable, SWT.BORDER);
		btnUnCheck.setLayoutData(data);
		btnUnCheck.setText("De-Select");
		this.btnUnCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableFileViewer.setAllChecked(false);
			}
		});
		
		
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan=3;
		data.heightHint=150;
		
		tblCompatibleFiles= new Table(compFilesTable, SWT.CHECK |SWT.BORDER |SWT.V_SCROLL |SWT.MULTI );
		tblCompatibleFiles.setLayoutData(data);
		cftp = new CompatibleFilesTableProperties(tblCompatibleFiles);
		
		createFileTableViewer();
		tableFileViewer.setContentProvider(new FileContentProvider());
		tableFileViewer.setLabelProvider(new FileLabelProvider()); 
		
		//set the input data once it is chosen.
		//end compFilesTable
		
		
//		Composite Output stuff
		Composite compOutputFields = new Composite(shell, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		compOutputFields.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=8;
		layout.numColumns=2;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		layout.makeColumnsEqualWidth=false;
		compOutputFields.setLayout(layout);
		
		//	 Label for Output Doc
		data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan=2;
		lblOutputDocument = new Label(compOutputFields, SWT.NONE);
		lblOutputDocument.setText("Output Path");
		lblOutputDocument.setLayoutData(data);
		
		
		//Composite for radio buttons
		Composite compRadioButtons = new Composite(compOutputFields, SWT.BORDER);
		layout = new GridLayout();
		layout.verticalSpacing=7;
		layout.numColumns=1;
		compRadioButtons.setLayout(layout);
		data = new GridData(GridData.VERTICAL_ALIGN_FILL);
		data.horizontalSpan=2;
		compRadioButtons.setLayoutData(data);
		
		data=new GridData();
		btnRadio1= new Button(compRadioButtons, SWT.RADIO);
		btnRadio1.setText("Subfolder of Input Document");
		//radioButtonProperties.setProperties(btnRadio1, "Subfolder of Input Document");
		btnRadio1.setLayoutData(data);
		btnRadio1.setSelection(true);
		this.btnRadio1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				btnBrowseOutput.setEnabled(false);
				setOutputPath();
			}
		});
		
		
		data=new GridData();
		btnRadio2= new Button(compRadioButtons, SWT.RADIO);
		btnRadio2.setText("Subfolder of Selected Folder");
		//radioButtonProperties.setProperties(btnRadio2, "Subfolder of Selected Folder");
		btnRadio2.setLayoutData(data);
		this.btnRadio2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				btnBrowseOutput.setEnabled(true);
				resetOutputPath();
			}
		});
		
		//Text field
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan=1;
		data.widthHint=400;
		txtOutputDoc=new Text(compOutputFields, SWT.BORDER);
		// textProperties.setProperties(txtOutputDoc, null);
		txtOutputDoc.setLayoutData(data);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=1;
		btnBrowseOutput = new Button(compOutputFields, SWT.BORDER);
		btnBrowseOutput.setEnabled(false);
		btnBrowseOutput.setText("Browse");
		// buttonProperties.setProperties(btnBrowseOutput, "Browse"); 
		btnBrowseOutput.setLayoutData(data); 
		this.btnBrowseOutput.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setOutputPathSelected();
			}
		});
		
		
		
		
		
		
		// bottom OK and Cancel buttons
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
		btnOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sendJobInfoToMain();
			}
		});
		
		data=new GridData(GridData.HORIZONTAL_ALIGN_CENTER );
		btnCancel= new Button(compOkCancelButtons, SWT.BORDER);
		buttonProperties.setProperties(btnCancel, "Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dispose();
			}
		});
		shell.pack();
	}	
	
	public void open() {
		shell.open();
		
		while (!shell.isDisposed())
			if (!UIManager.display.readAndDispatch())
				UIManager.display.sleep();
	}
	
	public void dispose() {
		shell.dispose();
	}
	
	
	public void createFileTableViewer(){
		tableFileViewer = new CheckboxTableViewer(tblCompatibleFiles);
		tableFileViewer.setUseHashlookup(true);
		tableFileViewer.setColumnProperties(columnFileNames);
	}
	
	//calls from listeners
	/**
	 * 
	 */
	public void setFileOrDirFlag(){
		alFileOrDir = getFileTypesForScriptHandler("out");
		System.out.println("the size of the returned out types is " + alFileOrDir.size());
		
		//a hack until all tdfs completed
		if (alFileOrDir.size()==0){
			boolOutputIsDir=true;
		}
		
		Iterator itFileOrDir = alFileOrDir.iterator();
		while (itFileOrDir.hasNext()){
			String pattern = (String)itFileOrDir.next(); 
			if (pattern.equalsIgnoreCase("application/x-filesystemDirectory")){ 
				System.out.println("The output type is a directory");
				boolOutputIsDir=true;
			}
			else{
				System.out.println("The output type is a file");
				outExtensionPattern = pattern;
			}
		}
	}	
	
	public void populateCompatibleFilesTable(){
		if (dirSelected==null){
			System.out.println("Directory selected is null");
		}
		else{
			fileDirSelected = new File(dirSelected);
			cftp.setDirSelected(fileDirSelected);
			alTableContents= cftp.setTableContents(getFileTypesForScriptHandler("in"));	
			tableFileViewer.setInput(alTableContents);
		}
	}
	
	
	public void sendJobInfoToMain() {
		Object [] checkedObject = tableFileViewer.getCheckedElements();
		int count = checkedObject.length;
		System.out.println("No of files selected " + count);
		
		if (tblCompatibleFiles.getItemCount()==0 
				|| txtOutputDoc.getText().equalsIgnoreCase("")
				|| txtOutputDoc == null) {
			
			// display an error message and return.
			
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
			messageBox.setMessage("Compatible Files Table and Output paths must be completed");
			messageBox.setText("Error:  Complete Fields");
			messageBox.open();			
		}
		
		
		else if(count<=0){
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
			messageBox.setMessage("Please check files to be converted.");
			messageBox.setText("Error:  Choose Compatible Files");
			messageBox.open();
		}
		
		else {
			//all use the same conversion
			//Create all job objects from each input file chosen
			
			outputPath = txtOutputDoc.getText();
			
			//escape all paths.  This is only used to make the directory.
			//make all directories, even ancestors that don't exist
			
			String makeDirectoryOnly = outputPath.replaceAll("\\\\", "\\\\\\\\");
			File makeDirFile = new File(makeDirectoryOnly);
			
			if (makeDirFile.exists() && makeDirFile.isFile()){
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
						SWT.CANCEL);
				messageBox.setMessage("The directory  \n" + makeDirFile.getPath()+" \n exists as a file and cannot be created. ");
				messageBox.setText("Error:  Directory exists as file");
				messageBox.open();	
				
			}
			
			else{
				try {
					FileUtils.createDirectory(makeDirFile);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				for(int i = 0; i<count;i++){
					Job job = new Job();
					
					File inPutFile = (File)checkedObject[i];
					if(boolOutputIsDir==false){
						outputPath=addFileNameToOutputPath(outputPath, inPutFile);
					}
					
					outputPath = outputPath.replaceAll("\\\\", "\\\\\\\\");
					//System.out.println("Double escaped output path?" + outputPath);
					File outFile = new File(outputPath);
					
					
					try {
						//if the file already exists, it will be overwritten
						if(!outFile.exists()){
							boolean success = outFile.createNewFile();
						}
					} catch (IOException e) {
						/*@todo a messsage that the output file was not created.*/
						e.printStackTrace();
					}
					
					job.setInputFile((File)checkedObject[i]);
					job.setOutputFile(outFile);
					job.setScript(scriptHandler);
					job.setStatus(Status.WAITING);
					Window.getInstance().addToQueue(job);
				}
			}
			al.clear();
			dispose();
		}
	}
	
	
	public String addFileNameToOutputPath(String outPath, File inPath){
		String outPutPath = outPath;
		
		//only created if the output path is a directory.
		//sanity check, already done in sendInfoToMain()
		
		
		if (boolOutputIsDir==false){
		
			//Name of the input path file
			String strInPath = inPath.getName();
			StringTokenizer st = new StringTokenizer(strInPath, ".");
			//get just the name
			
			//create a new path with the path, nameof file, and appropriate extension
			outPutPath = outPath + st.nextToken()+ "." + outExtensionPattern ;
			
		}
		
		System.out.println ("The REAL output path should be " + outPutPath);
		return outPutPath;
	}
	
	
	/**
	 * Sets directory selected.
	 * The listener then calls getFileTypesForScriptHandler(String inorout)
	 * and the directory selected is recursively traversed for all
	 * files compatible with the mime type in the tdf file.
	 * From the selecteddirectory, the default output path is also determined
	 * and placed in the output path text field .  This may be changed
	 * by the user. 
	 *
	 */
	public void setDirectorySelected() {
		
		File[] roots = File.listRoots();
		int size = roots.length;
		
		/*
		 for (int i = 0; i<size;i++){
			System.out.println("FileSystem roots are " + roots[i].getPath());
		}
		*/
		
		DirectoryDialog directoryDialog = new DirectoryDialog(shell);
		directoryDialog.setText("Choose a directory");
		directoryDialog.setFilterPath("/");
		dirSelected = directoryDialog.open();
		
		for (int i = 0; i<size; i++){
			String rootPath = roots[i].getPath();
			if (dirSelected!=null && dirSelected.equalsIgnoreCase(rootPath)){
				dirSelected=null;
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
						SWT.CANCEL);
				messageBox.setMessage("Please choose a subdirectory of "+  rootPath);
				messageBox.setText("Error:  Be More Selective");
				messageBox.open();	
			}
		}
		
		if (dirSelected!=null){
			System.out.println("Directory Selected  " + dirSelected);
			txtDirectorySelected.setText(dirSelected);
			
			//set the output path to a subfolder of the input directory selected
			
			String lastDir = "";
			StringTokenizer st = new StringTokenizer(dirSelected, File.separator);
			while(st.hasMoreTokens()){
				lastDir = st.nextToken();
				
			}
			
			//System.out.println("The last token is " + lastDir);
			strSubfolderOfInputFolder =dirSelected + File.separator + lastDir + File.separator;
			
//			if the output path is a directory...
//          the directory will be placed here
			//otherwise, a file of the same name but with output path extension
			//will be created as the output path
			
			
			txtOutputDoc.setText(strSubfolderOfInputFolder);
		}		
	}
	
	
	/**
	 * Sets directory selected by user
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
		
	//	fileSelected = dlg.open();		
		
	//	if (fileSelected!=null ){
	//		System.out.println("File selected  " + fileSelected);
	//		this.txtInputDoc.setText(fileSelected);
	//	}
		
	}
	
	
	
	public String[] getGlobFromMime(String mime) {
		if (mime == null) {
			return null;
		}
		try {
			MIMEType type = MIMETypeRegistry.getInstance().getEntryByName(mime);
			if (type != null) {
				Object[] arr = type.getFilenamePatterns().toArray();
				String[] ret = new String[arr.length + 1];
				for (int i = 0; i < arr.length; ++i) {
					ret[i] = (String)arr[i];
				}
				ret[arr.length] = "*.*";
				//System.err.println("Glob: " + ret);
				return ret;
			} 		
		} catch (MIMETypeRegistryException e) {
		} catch (MIMETypeException e) {
		}
		return null;
	}
	
	public String getMimeForProperty(String propertyName) {
		ScriptHandler handler = this.scriptHandler;
		Map properties = handler.getProperties();
		Property prop = (Property)properties.get(propertyName);
		if (prop.getType() != null && !prop.getType().equals("")) {
			return prop.getType();
		}
		return null;		
	}
	
	
	
	public void setOutputPath(){
		if (strSubfolderOfInputFolder==null){
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
			messageBox.setMessage("Sorry, First select an input folder.");
			messageBox.setText("Error:  Select Input Folder");
			messageBox.open();	
		}
		else{
			txtOutputDoc.setText(strSubfolderOfInputFolder);
		}
	}
	
	
	public void resetOutputPath(){
		txtOutputDoc.setText("");
	}
	
	
	public void setOutputPathSelected() {
		if (btnRadio2.isEnabled()){
			DirectoryDialog directoryDialog = new DirectoryDialog(shell);
			directoryDialog.setText("Choose a directory");
			directoryDialog.setFilterPath("/");
			outputPath = directoryDialog.open();
			
			if (outputPath!=null){
				
				String lastDir = "";
				StringTokenizer st = new StringTokenizer(outputPath, File.separator);
				while(st.hasMoreTokens()){
					lastDir = st.nextToken();
					
				}
				System.out.println("The last token is " + lastDir);
				strSubfolderOfOutputFolder =outputPath + File.separator + lastDir + File.separator;
				txtOutputDoc.setText(strSubfolderOfOutputFolder);	
			}
		}
	}
	
	/**
	 * Method used to return either possible "in" parameters
	 * (file types possible for the conversion)
	 * or "out" parameters - if output is a file or a directory.
	 * @return ArrayList of msglobs - file extensions
	 */
	public ArrayList getFileTypesForScriptHandler(String inOrOut){
		
		String inOut=inOrOut;
		System.out.println("GetFileTypes for Script");
		Collection msglobs = null;
		
		//container to hold all the file types valid for script
		ArrayList alValid = new ArrayList();
		
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
			
			
			if (parameter !=null &&parameter.equalsIgnoreCase(inOut)){
				fileType = pi.getType();
				System.out.println("Valid types for this script " + inOut + " "+ fileType);
				
				try {
					MIMEType mt = MIMETypeFactory.newInstance().newMimeType(fileType);
					msglobs = mt.getFilenamePatterns();
					System.out.println("The size of msglogs filepatterns is: " + msglobs.size());
					
				} catch (MIMETypeFactoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MIMETypeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Iterator itPatterns = msglobs.iterator();	
				String mimePattern="";
				int howmany = 0;
				
				while (itPatterns.hasNext()){
					System.out.println("in itPatterns hasNext, the next is: " + ++howmany);
					mimePattern = (String)itPatterns.next();
					System.out.println("The mimepattern is " + mimePattern);
					
					if (!mimePattern.equalsIgnoreCase("")){
						StringTokenizer st = new StringTokenizer(mimePattern, ".");
						String extension = "";
						while (st.hasMoreTokens()){
							//String firstToken = st.nextToken();
							//System.out.println("The first token is: " + firstToken);
							 extension = st.nextToken();
							System.out.println("File exension is "+ extension);
						}
						alValid.add(extension);
					}
				}
				//print out items in arraylist
				
				//Iterator itValid = alPatterns.iterator();
				Iterator itValid = alValid.iterator();
				while (itValid.hasNext())
					System.out.println ("Mime patterns in array " + (String)itValid.next());
			}
		}
		return alValid;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * InnerClass that acts as a proxy for the FileList 
	 * providing content for the Table. It implements the IFileListViewer 
	 * interface since it must register changeListeners with the 
	 * FileList 
	 */
	class FileContentProvider implements IStructuredContentProvider {
		
		public void inputChanged(Viewer v, Object oldInput, Object newInput){
			//nothing is changing
		}		
		
		public void dispose() {
		}
		
		// Return the file array as an array of Objects
		public Object[] getElements(Object parent) {
			return alTableContents.toArray();
		}	
	}
	
	public ArrayList getArrayListTableContents(){
		return this.alTableContents;
	}
	
	public Table getTableCompatibleFiles(){
		return this.tblCompatibleFiles;
	}
	
}
