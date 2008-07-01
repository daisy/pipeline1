/**
 * 
 */
package us_rfbd_nimasCreator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.dtb.build.BuildException;
import org.daisy.util.dtb.build.OpfBuilder;
import org.daisy.util.dtb.meta.MetadataItem;
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
			// TODO Deal with creator types
			// TODO Deal with repeatable metadata (grade level, creator, ISBN, others?)
			// TODO Detect metadata from XML file if not given in arguments
			// TODO ?? Maybe get from Overdrive Excel spreadsheet?
		MetadataItem item = null;
		try {
			// Date stuff
			Calendar cal = Calendar.getInstance();
			NumberFormat nf = NumberFormat.getIntegerInstance();
			nf.setMinimumIntegerDigits(2);
			String createDate = String.valueOf(cal.get(Calendar.YEAR)) + "-" + nf.format(cal.get(Calendar.MONTH)) + "-" + nf.format(cal.get(Calendar.DAY_OF_MONTH));
			item = ob.addMetadataItem("dc:Date", createDate);
			item.addAttribute("event","DCTERMS.created");

			item = ob.addMetadataItem("dc:Identifier", parameters.get("ISBN")+"NIMAS");
			item.addAttribute("scheme", "NIMAS");

			ob.addMetadataItem("dc:Title", parameters.get("title"));
			ob.addMetadataItem("dc:Language", parameters.get("language"));
			ob.addMetadataItem("dc:Publisher", parameters.get("publisher"));
			ob.addMetadataItem("dc:Rights", "The only legal and authorized use of these files is for the production of alternate media materials for blind, visually impaired, or print disabled students as specified in the NIMAC limitation of use agreement. The copyright for these files is the sole property of the original owner.");
			ob.addMetadataItem("dc:Source", parameters.get("ISBN"));
			ob.addMetadataItem("dc:Subject", parameters.get("subject"));	
			ob.addMetadataItem("nimas-SourceEdition", parameters.get("edition"));
			ob.addMetadataItem("nimas-SourceDate", parameters.get("publicationDate"));
			ob.addMetadataItem("DCTERMS.description.version", parameters.get("edition"));
			ob.addMetadataItem("DCTERMS.date.issued", parameters.get("publicationDate"));
			ob.addMetadataItem("DCTERMS.description.note", parameters.get("contentType"));
			ob.addMetadataItem("DCTERMS.date.dateCopyrighted", parameters.get("copyrightDate"));
			ob.addMetadataItem("DCTERMS.audience.educationLevel", parameters.get("gradeLevel"));			
			ob.addMetadataItem("DCTERMS.publisher.place", parameters.get("publisherPlace"));			
			ob.addMetadataItem("DCTERMS.relation.isPartOf", parameters.get("series"));			
			ob.addMetadataItem("DCTERMS.description.version", parameters.get("stateEdition"));			
		} catch (BuildException e) {
			throw new TransformerRunException("Error adding metadata to OPF: " + e.getMessage());
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
			ob.render(new URL("file:///" + outputDir.getPath() + "/" + parameters.get("ISBN") + "NIMAS.opf"));
		} catch (BuildException e) {
			throw new TransformerRunException("Build error rendering OPF: " + e.getMessage());
		} catch (MalformedURLException e) {
			throw new TransformerRunException("Malformed URL rendering OPF: " + e.getMessage());
		} catch (IOException e) {
			throw new TransformerRunException("IO error rendering OPF: " + e.getMessage());
		} catch (XMLStreamException e) {
			throw new TransformerRunException("XML error rendering OPF: " + e.getMessage());
		}
		
		return true;
	}

	public void error(FilesetFileException e) throws FilesetFileException {
		this.sendMessage(e);
	}
}
