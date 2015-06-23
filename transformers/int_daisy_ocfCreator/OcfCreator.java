/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package int_daisy_ocfCreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Namespace;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.script.datatype.FilesDatatype;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.TempFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetTypeNotSupportedException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.mime.MIMETypeException;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;

/**
 * Main transformer class. Create an OEBPS Container (OCF 1.0) containing one or several manifestations of a publication.
 * @author Markus Gylling
 */
public class OcfCreator extends Transformer implements FilesetErrorHandler {

	public OcfCreator(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@Override
	protected boolean execute(Map<String,String> parameters) throws TransformerRunException {
		try{	
			EFile outputEpubFile = new EFile(FilenameOrFileURI.toFile(parameters.remove("output")));
			if(!outputEpubFile.getExtension().equals("epub")) {
				this.sendMessage(i18n("EXTENSION_NOT_EPUB", outputEpubFile.getName()), MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT);
			}			
			this.sendMessage(0.1);
			Set<Publication> publications = getPublications(parameters.remove("input"));
			
			this.sendMessage(0.2);
			this.sendMessage(i18n("BUILDING_OCF", publications.size()), MessageEvent.Type.INFO_FINER, MessageEvent.Cause.INPUT);
						
			FileUtils.createDirectory(outputEpubFile.getParentFile());
			ZipOutputStream outputOcf = new ZipOutputStream(new FileOutputStream(outputEpubFile));
			
			List<OcfEntry> entries = new LinkedList<OcfEntry>();			
			entries.add(new OcfEntry(getMimeTypeFile(),"mimetype"));
			entries.add(new OcfEntry(buildContainerFile(publications),"META-INF/container.xml"));
			this.sendMessage(0.3);			
			
			for (Publication pub : publications) {
				if(pub.mPublication instanceof Fileset) {
					Fileset fileset = (Fileset) pub.mPublication;
					FilesetFile manifest = fileset.getManifestMember();
					for (Iterator<FilesetFile> iter = fileset.getLocalMembers().iterator(); iter.hasNext();) {				
						FilesetFile ffile = iter.next();
						URI relative = manifest.getRelativeURI(ffile);
						entries.add(new OcfEntry(ffile.getFile(),pub.mTypeLabel+"/"+relative.getPath()));
					}		
				}else{
					EFile file = (EFile) pub.mPublication;
					entries.add(new OcfEntry(file,pub.mTypeLabel+"/"+file.getName()));
				}
			}
			this.sendMessage(0.5);
			int count = 0;
			for (OcfEntry entry : entries) {
				count++;
				//#1770771 The mimetype file should not be compressed 
				if(count==1) {
					outputOcf.setMethod(ZipEntry.STORED);
				}else{
					outputOcf.setMethod(ZipEntry.DEFLATED);
					outputOcf.setLevel(Deflater.BEST_COMPRESSION);
				}
								
				File file = entry.mFile;								
				byte[] buffer = new byte[18024];
				FileInputStream in = new FileInputStream(file);
				
				ZipEntry ze = new ZipEntry(entry.mPath);
				if(count==1) {
					ze.setSize(entry.mFile.length());
					ze.setCrc(getCRC(entry.mFile));
				}
				
				outputOcf.putNextEntry(ze);	
		        
				int len;
		        while ((len = in.read(buffer)) > 0){
		        	outputOcf.write(buffer, 0, len);
		        }
		        outputOcf.closeEntry();
		        in.close();
			}
			
			outputOcf.close();
			this.sendMessage(0.9);
		}catch (Exception e) {
			this.sendMessage(i18n("ERROR_ABORTING", e.getMessage()), MessageEvent.Type.ERROR);
			throw new TransformerRunException(e.getMessage(),e);
		}
		return true;				
	}


	private long getCRC(File file) throws IOException {
		CheckedInputStream cis = new CheckedInputStream(new FileInputStream(file), new CRC32());
        byte[] buf = new byte[128];
        while(cis.read(buf) >= 0) {        	
        }
        return cis.getChecksum().getValue();
	}

	/**
	 * Create a set of Publication objects to include in OCF. A Publication is either a Fileset or a single file, represented as a URL
	 * @throws FilesetFatalException 
	 * @throws TransformerRunException 
	 * @throws MIMETypeException 
	 * @throws FileNotFoundException 
	 */
	private Set<Publication> getPublications(String inputParam) throws FilesetFatalException, TransformerRunException, MIMETypeException, FileNotFoundException {
		Set<Publication> publications = new HashSet<Publication>();
		String[] array = inputParam.split(FilesDatatype.SEPARATOR_STRING);
		for (String string : array) {
			EFile file = new EFile(FilenameOrFileURI.toFile(string.trim()));
			if(!file.exists()) throw new FileNotFoundException(file.getAbsolutePath());
			
			try {
				Fileset fileset = new FilesetImpl(file.toURI(),this,false,false);
				publications.add(new Publication(fileset,getTypeLabel(fileset)));
			} catch (FilesetFatalException ffe) {
				if(ffe.getRootCause() instanceof FilesetTypeNotSupportedException) {
					//TODO Could use SignatureDetector
					this.sendMessage(i18n("MIMETYPE_DETECTION_FAILED",file.getName()), MessageEvent.Type.WARNING,MessageEvent.Cause.INPUT);
					file.setMimeType("application/anonymous");
					publications.add(new Publication(file,getTypeLabel(file)));
				}else{
					throw ffe;
				}
			}				
		}//for
		
		//make sure we dont have several publications of the same type
		Set<String> types = new HashSet<String>();
		for (Publication pub : publications) {			
			if(types.contains(pub.mTypeLabel)) {
				throw new TransformerRunException("Cannot have several publications of the same type in an OCF: " + pub.mTypeLabel);
			}
			types.add(pub.mTypeLabel);			
		}
		
		return publications;
	}
	

	private File buildContainerFile(Set<Publication> publications) throws IOException, PoolException, XMLStreamException {
		File container = TempFile.create();				
		XMLOutputFactory xof = null;
		XMLEventFactory xef = null;				
		Map<String,Object> xofProperties = null;
		
		try{
			xofProperties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();						
			xof = StAXOutputFactoryPool.getInstance().acquire(xofProperties);
			xef = StAXEventFactoryPool.getInstance().acquire();
			FileOutputStream fos = new FileOutputStream(container);
			XMLEventWriter xew = xof.createXMLEventWriter(fos);

			Namespace containerNS = xef.createNamespace("urn:oasis:names:tc:opendocument:xmlns:container");
			Characters lineBreak = xef.createIgnorableSpace("\n");
			Characters tab = xef.createIgnorableSpace("\t");
			QName containerRoot = new QName(containerNS.getNamespaceURI(),"container");
			QName rootFiles = new QName(containerNS.getNamespaceURI(),"rootfiles");
			QName rootFile = new QName(containerNS.getNamespaceURI(),"rootfile");
			
			xew.add(xef.createStartDocument("utf-8","1.0"));
			xew.add(lineBreak);			
			//xew.add(xef.createStartElement(containerRoot,null,null));
			xew.add(xef.createStartElement("",containerRoot.getNamespaceURI(),containerRoot.getLocalPart()));
			xew.add(xef.createNamespace(containerRoot.getNamespaceURI()));
			xew.add(xef.createAttribute("version", "1.0"));
			xew.add(lineBreak);
			xew.add(tab);
			xew.add(xef.createStartElement(rootFiles,null,null));
			xew.add(lineBreak);
			for (Publication pub : publications) {				
				EFile rootfile = null;
				if(pub.mPublication instanceof Fileset) {
					rootfile = (EFile)((Fileset)pub.mPublication).getManifestMember();
				}else{					
					rootfile = (EFile)pub.mPublication;
				}	

				xew.add(tab);xew.add(tab);
				xew.add(xef.createStartElement(rootFile,null,null));
				xew.add(xef.createAttribute("full-path", pub.mTypeLabel+"/" + rootfile.getName()));
				xew.add(xef.createAttribute("media-type", rootfile.getMimeType().dropParametersPart()));
				xew.add(xef.createEndElement(rootFile,null));
				xew.add(lineBreak);
			}			
			xew.add(tab);
			xew.add(xef.createEndElement(rootFiles,null));
			xew.add(lineBreak);
			xew.add(xef.createEndElement(containerRoot,null));
			
			xew.flush();
			xew.close();
			fos.close();
			
		}finally {
			StAXOutputFactoryPool.getInstance().release(xof, xofProperties);
			StAXEventFactoryPool.getInstance().release(xef);
		}
						
		return container;
	}
	
	/**
	 * Create the static never-changing OCF mimetype file.
	 */
	private File getMimeTypeFile() throws IOException {		
		File mimetype = TempFile.create();		
		FileWriter writer = new FileWriter(mimetype);
		writer.write("application/epub+zip");
		writer.flush();
		writer.close();		
		return mimetype;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */
	public void error(FilesetFileException ffe) throws FilesetFileException {		
		this.sendMessage(ffe);
	}

	private String getTypeLabel(EFile file) {
		String label = file.getExtension();
		if(label!=null && label.length()>0) {
			return label.toUpperCase();
		}
		return "EXT";
	}

	private String getTypeLabel(Fileset fileset) {
		if(fileset.getFilesetType() == FilesetType.OPS_20){
			return "OEBPS";
		}else
		if(fileset.getFilesetType() == FilesetType.Z3986){
			return "Z3986";
		}else
		if(fileset.getFilesetType() == FilesetType.DAISY_202){
				return "D202";
		}else
		if(fileset.getFilesetType() == FilesetType.DTBOOK_DOCUMENT){
			return "DTBOOK";
		}else
		if(fileset.getFilesetType() == FilesetType.XHTML_DOCUMENT){
			return "XHTML";
		}else
		if(fileset.getFilesetType() == FilesetType.HTML_DOCUMENT){
			return "HTML";
		}
		//TODO issue warning
		return fileset.getManifestMember().getExtension().toUpperCase();
	}
	
	class OcfEntry {
		File mFile;
		String mPath;
		OcfEntry(File file, String path) {
			mFile = file;
			mPath = path;
		}
	}
	
	class Publication {
		Object mPublication;
		String mTypeLabel;
		
		Publication(EFile file, String typeLabel) {
			mPublication = file;
			mTypeLabel = typeLabel;
		}
		
		Publication(Fileset fileset, String label) {
			mPublication = fileset;
			mTypeLabel = label;
		}
		
	}
}
