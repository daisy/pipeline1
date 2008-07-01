/**
 * 
 */
package us_rfbd_nimasCreator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.dtb.build.BuildException;
import org.daisy.util.dtb.build.OpfBuilder;
import org.daisy.util.file.Directory;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;

/**
 * Creates a NIMAS fileset from a dtbook file, PDF file, and metadata
 * @author James Pritchett (jpritchett@rfbd.org)
 *
 */
public class NimasCreator extends Transformer implements FilesetErrorHandler {

	/**
	 * @param inListener
	 * @param isInteractive
	 */
	public NimasCreator(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.Transformer#execute(java.util.Map)
	 */
	@Override
	protected boolean execute(Map<String, String> parameters)
			throws TransformerRunException {
		
		// Get the inputs and outputs  
		File inputXML = FilenameOrFileURI.toFile(parameters.get("xml"));
		File inputPDF = FilenameOrFileURI.toFile(parameters.get("pdf"));
		
		Directory outputDir;	
		try {
			outputDir = new Directory(FilenameOrFileURI.toFile(parameters.get("output")));
		} catch (IOException e) {
			throw new TransformerRunException("Can't get output directory: " + e.getMessage());
		}
		
		// Open up the dtbook fileset to find all referenced images, etc.
		Fileset fileset;
		try {
			fileset = new FilesetImpl(inputXML.toURI(),this);
		} catch (FilesetFatalException e) {
			throw new TransformerRunException("FilesetFatalException on dtbook open: " + e.getMessage());
		} 
		
		// Copy the dtbook fileset over to the output
		try {
			outputDir.addFileset(fileset, true);
		} catch (IOException e) {
			throw new TransformerRunException("IOException when copying dtbook fileset: " + e.getMessage());
		}
		
		// Copy the PDF file to the output
		try {
			outputDir.addFile(inputPDF, true);
		} catch (IOException e) {
			throw new TransformerRunException("IOException copying PDF file: " + e.getMessage());
		} 
		
		// Create the OPF file
		OpfBuilder ob = new OpfBuilder(OpfBuilder.OpfType.NIMAS_11);
		
			// Add the metadata
			// TODO Detect metadata from XML file if not given in arguments
			// TODO Rename the metadata parameters to something more user-friendly
			// TODO Add optional metadata items, per NIMAC spec
			// TODO Add autogen of identifier if needed
		try {
			ob.addMetadataItem("dc:Title", parameters.get("dc:title"));
			ob.addMetadataItem("dc:Identifier", parameters.get("dc:identifier"));
			ob.addMetadataItem("dc:Language", parameters.get("dc:language"));
			ob.addMetadataItem("dc:Publisher", parameters.get("dc:publisher"));
			ob.addMetadataItem("dc:Date", parameters.get("dc:date"));
			ob.addMetadataItem("dc:Rights", parameters.get("dc:rights"));
			ob.addMetadataItem("dc:Source", parameters.get("dc:source"));
			ob.addMetadataItem("nimas-SourceEdition", parameters.get("nimas-SourceEdition"));
			ob.addMetadataItem("nimas-SourceDate", parameters.get("nimas-SourceDate"));
		} catch (BuildException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		// Re-open the fileset from the copied version of the file
		try {
			fileset = new FilesetImpl(FilenameOrFileURI.toURI(outputDir.getPath() + "/" + inputXML.getName()), this);
		} catch (FilesetFatalException e) {
			throw new TransformerRunException("FilesetFatalException on dtbook open: " + e.getMessage());
		} 
		// Re-open the XML and PDF files, too
		inputXML = new File(outputDir.getPath() + "/" + inputXML.getName());
		inputPDF = new File(outputDir.getPath() + "/" + inputPDF.getName());
		
			// Add all files from the dtbook fileset
		for (FilesetFile f : fileset.getLocalMembers()) {
			ob.addManifestItem(f);
		}
			// Add the PDF, too
		try {
			ob.addManifestItem(inputPDF.toURI().toURL());
		} catch (FilesetFatalException e) {
			throw new TransformerRunException("FilesetFatalException adding PDF to OPF:  " + e.getMessage());
		} catch (MalformedURLException e) {
			throw new TransformerRunException("MalformedURLException on PDF add: " + e.getMessage());
		}
		
			// Add the XML to the spine
		try {
			ob.addSpineItem(inputXML.toURI().toURL());
		} catch (FilesetFatalException e) {
			throw new TransformerRunException("FilesetFatalException adding XML to spine:  " + e.getMessage());
		} catch (MalformedURLException e) {
			throw new TransformerRunException("MalformedURLException on XML spine add: " + e.getMessage());
		}

		// Now render the OPF to the output
		try {
			ob.render(new URL("file:///" + outputDir.getPath() + "/" + parameters.get("dc:identifier") + ".opf"));
		} catch (BuildException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public void error(FilesetFileException e) throws FilesetFileException {
		this.sendMessage(e);
	}
}
