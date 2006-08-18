package org.daisy.util.fileset.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import javazoom.jl.decoder.BitstreamException;

import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
import org.daisy.util.fileset.exception.FilesetFileWarningException;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.ManifestFile;
import org.daisy.util.fileset.interfaces.Referring;
import org.daisy.util.fileset.interfaces.xml.d202.D202MasterSmilFile;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.fileset.util.URIStringParser;
import org.daisy.util.mime.MIMETypeException;
import org.daisy.util.xml.Peeker;
import org.daisy.util.xml.PeekerImpl;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * @author Markus Gylling
 */
public class FilesetImpl implements Fileset {
	private Map localMembers = new HashMap();		//<URI>, <FilesetFile>	
	private HashSet remoteMembers = new HashSet();	//<String> 
	//private HashSet exceptions = new HashSet();			//<FilesetFileException>	
	private FilesetExceptionCollector exc = null; 
	private HashSet missingURIs = new HashSet();	//<URI>
	private ManifestFile manifestMember;	
	private FilesetType filesetType = null;
	private static FilesetRegex regex = FilesetRegex.getInstance();
	//private static Peeker peeker; 
	private static Peeker peeker = new PeekerImpl();
	private boolean setReferringCollections; 
	private FilesetErrorHandler errorListener = null;
	
	/**
	 * Default class constructor
	 * @param manifestURI the URI of the object being input port for fileset retrieval (ncc, opf, playlist, etc)
	 * @param errh the FilesetErrorHandler that will recieve notification of nonfatal errors
	 * @throws FilesetNonFatalException 
	 * @see #FilesetImpl(URI, boolean)
	 * @see #FilesetImpl(URI,boolean, boolean)
	 */
	public FilesetImpl(URI manifestURI, FilesetErrorHandler errh) throws FilesetFatalException {
		initialize(manifestURI,errh,false,false);
	}
	
	/**
	 * Extended class constructor
	 * @param manifestURI the URI of the object being input port for fileset retrieval (ncc, opf, playlist, etc)
	 * @param errh the FilesetErrorHandler that will recieve notification of nonfatal errors
	 * @param dtdValidate sets whether XML members in the fileset will be DTD validated in the case they reference a DTD. The default value of this property is false.
	 * @throws FilesetNonFatalException 
	 * @see #FilesetImpl(URI)
	 * @see #FilesetImpl(URI,boolean, boolean) 
	 */
	public FilesetImpl(URI manifestURI, FilesetErrorHandler errh, boolean dtdValidate) throws FilesetFatalException {
		initialize(manifestURI,errh,dtdValidate,false);
	}
	
	/**
	 * Extended class constructor. 	 
	 * @param manifestURI the URI of the object being input port for fileset retrieval (ncc, opf, playlist, etc)
	 * @param errh the FilesetErrorHandler that will recieve notification of nonfatal errors
	 * @param dtdValidate sets whether XML members in the fileset will be DTD validated.  The default value of this property is false.
	 * @param setReferringCollections sets whether the referringLocalMembers collection will be created on each member. Note - this is a costly procedure in terms of timeconsumption for large filesets.  The default value of this property is false.
	 * @throws FilesetNonFatalException 
	 * @see #FilesetImpl(URI)
	 * @see #FilesetImpl(URI, boolean)
	 * @see {@link org.daisy.util.fileset.interfaces.Referable} 
	 */
	public FilesetImpl(URI manifestURI, FilesetErrorHandler errh, boolean dtdValidate, boolean setReferringCollections) throws FilesetFatalException {
		initialize(manifestURI,errh,dtdValidate,setReferringCollections);
	}
		
	private void initialize(URI manifestURI, FilesetErrorHandler errh, boolean dtdValidate, boolean setReferringCollections) throws FilesetFatalException  {
		
		this.errorListener = errh;
		if (this.errorListener == null) throw new FilesetFatalException("no FilesetErrorHandler set");
		exc = new FilesetExceptionCollector(this.errorListener);
		
		//speed up JAXP		
		//TODO System.getProperty("java.version") || java.vendor
//		System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
//		System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
//		System.setProperty("org.apache.xerces.xni.parser.XMLParserConfiguration","com.sun.org.apache.xerces.internal.parsers.XML11Configuration");
		
		this.setReferringCollections = setReferringCollections;
		if (dtdValidate) {
		  System.setProperty("org.daisy.util.fileset.validating", "true");
		} 
		
		//peeker = new PeekerImpl();		
		File f = new File(manifestURI);
						
		if(f.exists() && f.canRead()){
			try {
				peeker.peek(f.toURI()); 
			}catch (Exception e) {
				//System.err.println("stop");
				//it wasnt an xmlfile or something else went wrong
			}
				
			try{
				if ((regex.matches(regex.FILE_NCC, f.getName()))&&(peeker.getRootElementLocalName().equals("html"))) {
					//set the fileset type
					this.filesetType = FilesetType.DAISY_202;
					//instantiate the appropriate filetype					
					this.manifestMember = new D202NccFileImpl(f.toURI());						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent((FilesetFileImpl)manifestMember);
					
					//do some obscure stuff specific to this fileset type
					D202NccFileImpl ncc = (D202NccFileImpl)this.manifestMember;
					ncc.buildSpineMap(this);
					
					File test = new File(manifestMember.getFile().getParentFile(), "master.smil");
					if (test.exists()){
						D202MasterSmilFile msmil = new D202MasterSmilFileImpl(test.toURI());
						this.fileInstantiatedEvent((FilesetFileImpl)msmil);
					}
										
				}else if((regex.matches(regex.FILE_OPF, f.getName()))&&(peeker.getRootElementLocalName().equals("package"))) {		
					//need to preparse to find out what kind of opf it is
					OpfFileImpl temp =  new OpfFileImpl(f.toURI());
					temp.parse();
					//instantiate the appropriate filetype
					if(temp.getMetaDcFormat()!=null && temp.getMetaDcFormat().indexOf("Z39.86")>=0){
						this.filesetType = FilesetType.Z3986;
						this.manifestMember = new Z3986OpfFileImpl(f.toURI());
					}else if(temp.getMetaDcFormat()!=null && temp.getMetaDcFormat().indexOf("NIMAS")>=0){
						this.manifestMember = new NimasOpfFileImpl(f.toURI());
						this.filesetType = FilesetType.NIMAS;
					}else{
						throw new FilesetFatalException("could not detect version of opf (no dc:format)");
					}
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent((FilesetFileImpl)this.manifestMember);
					//do some obscure stuff specific to this fileset type	
					OpfFileImpl opf = (OpfFileImpl)this.manifestMember;
					opf.buildSpineMap(this);

				}else if((regex.matches(regex.FILE_RESOURCE, f.getName()))&&(peeker.getRootElementLocalName().equals("resources"))) {
					//set the fileset type
					this.filesetType = FilesetType.Z3986_RESOURCEFILE;
					//instantiate the appropriate filetype
					this.manifestMember = new Z3986ResourceFileImpl(f.toURI());					 						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent((FilesetFileImpl)this.manifestMember);		

				}else if(regex.matches(regex.FILE_CSS, f.getName())) {
					//set the fileset type
					this.filesetType = FilesetType.CSS;
					//instantiate the appropriate filetype
					this.manifestMember = new CssFileImpl(f.toURI());
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent((FilesetFileImpl)this.manifestMember);		

				}else if(peeker.getRootElementLocalName().equals("dtbook")) {
					//set the fileset type
					this.filesetType = FilesetType.DTBOOK_DOCUMENT;
					//instantiate the appropriate filetype
					this.manifestMember = new Z3986DtbookFileImpl(f.toURI());					 						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent((FilesetFileImpl)this.manifestMember);		

				}else if((peeker.getRootElementLocalName().equals("html"))&&((peeker.getFirstSystemId().indexOf("x")>=0)
						||(peeker.getFirstPublicId().indexOf("X")>=0)
						||(peeker.getRootElementNsUri().indexOf("x")>=0))) {
					//set the fileset type
					this.filesetType = FilesetType.XHTML_DOCUMENT;
					//instantiate the appropriate filetype
					this.manifestMember = new Xhtml10FileImpl(f.toURI());					 						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent((FilesetFileImpl)this.manifestMember);	
					
				}else if(regex.matches(regex.FILE_XHTML, f.getName())) {
					//set the fileset type
					this.filesetType = FilesetType.HTML_DOCUMENT;
					//instantiate the appropriate filetype
					this.manifestMember = new HtmlFileImpl(f.toURI());					 						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent((FilesetFileImpl)this.manifestMember);	
					
				}else if(regex.matches(regex.FILE_M3U, f.getName())) {
					//set the fileset type
					this.filesetType = FilesetType.PLAYLIST_M3U;
					//instantiate the appropriate filetype
					this.manifestMember = new M3UFileImpl(f.toURI());					 						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent((FilesetFileImpl)this.manifestMember);		
					
				}else if(regex.matches(regex.FILE_PLS, f.getName())) {
						//set the fileset type
						this.filesetType = FilesetType.PLAYLIST_PLS;
						//instantiate the appropriate filetype with this as errorhandler
						this.manifestMember = new PlsFileImpl(f.toURI());					 						
						//send it to the observer which handles the rest generically
						this.fileInstantiatedEvent((FilesetFileImpl)this.manifestMember);	
				}else{
					// other types
				    throw new FilesetFatalException("Unsupported manifest type");
				}
			} catch (Exception e){
				//thrown if the manifest could not be instantiated
				//only throw outwards for the manifest file instantiation
				throw new FilesetFatalException(e.getMessage(),e);				
			}
		}else{			
			throw new FilesetFatalException(new IOException("manifest not readable"));								 
		}  
		
		//if we get here the fileset is completely populated without fatal errors
		
		if(this.setReferringCollections){
			//populate the reffering property
			Iterator it = localMembers.keySet().iterator();
			while(it.hasNext()) {
				FilesetFileImpl file = (FilesetFileImpl) localMembers.get(it.next());				
				file.setReferringLocalMembers(localMembers);				  
			}
		}
				
		System.clearProperty("org.daisy.util.fileset.validating");
				
	}
	
	void fileInstantiatedEvent(FilesetFileImpl member) throws ParserConfigurationException, FilesetFatalException {
		//all file instantiations are reported here
		//but never by the instantiated member itself
		
		//add to this.localMembers			
		localMembers.put(member.getFile().toURI(),member);
		
		//invoke the generic parse method
		try {				
			member.parse();					
		} catch (IOException ioe) {								
			//exceptions.add(new FilesetFileFatalErrorException(member,ioe));
			exc.add(new FilesetFileFatalErrorException(member,ioe));			
			return;
		} catch (SAXParseException spe) {
			//malformedness, dont add/return; already added by XmlFile errhandler				
		} catch (SAXException se) {
			//other serious sax error
			//exceptions.add(new FilesetFileFatalErrorException(member,se));
			exc.add(new FilesetFileFatalErrorException(member,se));
			return;
		} catch (BitstreamException bse) {
			//exceptions.add(new FilesetFileFatalErrorException(member,bse));
			exc.add(new FilesetFileFatalErrorException(member,bse));
			return;							
		}		
		
		//collect and handle any nonthrown exceptions
		Iterator iter = member.getErrors().iterator();
		while(iter.hasNext()) { 
			try{
				Exception e = (Exception) iter.next();				
				if(e instanceof FilesetFileException) {
					//this.exceptions.add(e);
					exc.add((FilesetFileException)e);
				}else{
					//this.exceptions.add(new FilesetFileException(file,e));
					exc.add(new FilesetFileException(member,e));
				}				
			}catch (ClassCastException cce) {
				throw new FilesetFatalException(cce);
			}
		}
		
		if (member instanceof Referring) {
			//if its referring we need to find out whom else it points to		
			Referring referer = (Referring)member;
			Iterator it = referer.getUriStrings().iterator();
			String value;
			URI resolvedURI = null;
			URI cachedURI = null;
			while (it.hasNext()) {
				value = (String)it.next();					
				if(!regex.matches(regex.URI_REMOTE,value)) {
					//strip fragment if existing
					value = URIStringParser.stripFragment(value);					
					//resolve the uri string
					resolvedURI = referer.getFile().toURI().resolve(value);										
					if (!resolvedURI.equals(cachedURI) && !value.equals("")) {
						cachedURI = resolvedURI; 					
     					//check if this file has already been added to main collection						
						FilesetFileImpl newmember = (FilesetFileImpl)localMembers.get(resolvedURI);
						if (newmember == null) {
							//this is a member that hasnt been namedropped before													
							try {
								//determine what type to instantiate
								newmember = getType(member, resolvedURI, value, this.filesetType);
								if(newmember instanceof AnonymousFileImpl) {
									//exceptions.add(new FilesetFileException(newmember, new AnonymousFileException("no matching file type found for " + value + ": this file appears as AnonymousFile")));
									exc.add(new FilesetFileWarningException(newmember, new IOException("no matching file type found for " + value + ": this file appears as AnonymousFile")));
								}
							} catch (FileNotFoundException fnfe) {
								//exceptions.add(new FilesetFileFatalErrorException(member,fnfe));								
								exc.add(new FilesetFileFatalErrorException(member,fnfe));
								missingURIs.add(resolvedURI);
								continue;
							} catch (IOException ioe) {
								//exceptions.add(new FilesetFileFatalErrorException(member,ioe));
								exc.add(new FilesetFileFatalErrorException(member,ioe));
								continue;
							} 
							//report fileInstantiatedEvent
							this.fileInstantiatedEvent(newmember);						
						} //if (newmember == null)
						//put in the incoming members references list
						referer.putReferencedMember(newmember);
					} //!resolvedURI.equals(cache)
				}//if matches URI_LOCAL
				else {
					remoteMembers.add(value);
				}
			}//while (it.hasNext())			
		}//if (member instanceof Referring) 				
	}
	
	
	/**
	 * identifies for an incoming reference the most appropriate FilesetFile 
	 * instance type based primarily on root and name heuristics
	 * @param owner the FilesetFile in which the reference to this instance occurs. This param can be null.
	 * @param uri the absolute URI of the new instance (resolved from owners URI)
	 * @param value the string value of the reference (pre-URI-resolve, aka attr value as-is in the owner)
	 * @throws MIMETypeException 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	
	/*package*/ static FilesetFileImpl getType(FilesetFileImpl owner, URI uri, String value, FilesetType filesetType) throws FileNotFoundException, IOException {
		//this is sequenced so that most probable types 
		//(from a dtb perspective) come first
		//this means its not beautiful, but a tad faster 		
				
		if (regex.matches(regex.FILE_MP3,value)){
			return new Mp3FileImpl(uri);
		}
		
		if (regex.matches(regex.FILE_WAV,value)){
			return new WavFileImpl(uri);
		}
		
		try{
			peeker.peek(uri);
			String rootName = peeker.getRootElementLocalName().intern();	
			
			//test for xml files
			if (rootName==("smil")) {
				if (filesetType == FilesetType.DAISY_202) {				
					return new D202SmilFileImpl(uri);
				}else if (filesetType == FilesetType.Z3986) {			
					return new Z3986SmilFileImpl(uri);
				}else{					
					return new SmilFileImpl(uri);					
				}
			}
			
			if (rootName=="html") {				
				//an (non xhtml) html file may end up here if peeker didnt crash before	root was over			
				if (regex.matches(regex.FILE_NCC,value)) {			
					return new D202NccFileImpl(uri);			
				}else if (filesetType == FilesetType.DAISY_202) {
					return new D202TextualContentFileImpl(uri);
				}else if ((peeker.getFirstSystemId().indexOf("x")>=0)
						||(peeker.getFirstPublicId().indexOf("X")>=0)
						||(peeker.getRootElementNsUri().indexOf("x")>=0)){
					//there are no x chars in the html equivalents
					return new Xhtml10FileImpl(uri);
				}else{
					return new HtmlFileImpl(uri);
				}
			}
			
			if (rootName== "dtbook") {
				return new Z3986DtbookFileImpl(uri);
			}
			
			if (rootName== "ncx") {			
				return new Z3986NcxFileImpl(uri);			
			}
			
			if (rootName== "opf") {
				if(filesetType==FilesetType.Z3986) {
					return new Z3986OpfFileImpl(uri);
				}else if(filesetType==FilesetType.NIMAS) {
					return new NimasOpfFileImpl(uri);
				}else{	
					return new OpfFileImpl(uri);
				}	
			}
						
			if (rootName== "resources"){
				return new Z3986ResourceFileImpl(uri);
			}
			
			if (rootName== "stylesheet"){
				return new XslFileImpl(uri);
			}

// jpritchett@rfbd.org:  Added SVG here
			if (rootName == "svg") {
				return new SvgFileImpl(uri);
			}
			
			if(rootName== "schema"){
				if(peeker.getRootElementNsUri().equals("http://www.ascc.net/xml/schematron")
						||peeker.getRootElementNsUri().equals("http://purl.oclc.org/dsdl/schematron")){
					return new SchematronFileImpl(uri);
				}

				if(peeker.getRootElementNsUri().equals("http://www.w3.org/2001/XMLSchema")){
					return new XsdFileImpl(uri);
				}
								
			}
			
			if(rootName== "grammar" && peeker.getRootElementNsUri().equals("http://relaxng.org/ns/structure/1.0")) {
				return new RelaxngFileImpl(uri);
			}
			
		} catch (Exception e) { //peeker.peek
			//the file wasnt an xml file or something else went wrong
//			e.printStackTrace();
		}//peeker.peek	
		
		
		//now we know its not an xml file, nor any of the common audiofilestypes mp3|wav
						
		//need to test for (non xhtml) html again		
		if (regex.matches(regex.FILE_XHTML,value)){
			return new HtmlFileImpl(uri);
		}
		
		if (regex.matches(regex.FILE_CSS,value)){
			return new CssFileImpl(uri);
		}
	
		if (regex.matches(regex.FILE_JPG,value)){
			return new JpgFileImpl(uri);
		}

		if (regex.matches(regex.FILE_GIF,value)){
			return new GifFileImpl(uri);
		}
	
		if (regex.matches(regex.FILE_PNG,value)){
			return new PngFileImpl(uri);
		}
		
		if (regex.matches(regex.FILE_BMP,value)){
			return new BmpFileImpl(uri);
		}
		
		if (regex.matches(regex.FILE_MP2,value)){
			return new Mp2FileImpl(uri);
		}		

		if (regex.matches(regex.FILE_DTD,value)){
			return new DtdFileImpl(uri);
		}
		
		if (regex.matches(regex.FILE_PDF,value)){
			return new PdfFileImpl(uri);
		}
		
		//if no factual match, still instantiate it				
		return new AnonymousFileImpl(uri);			
		
	}
		
	public ManifestFile getManifestMember() {		
		return manifestMember;
	}
			
	public Collection getLocalMembers() {
		return localMembers.values();		
	}
		
	public Collection getLocalMembersURIs() {
		return localMembers.keySet();
	}
	
	public FilesetFile getLocalMember(URI absoluteURI) {
		return (FilesetFile)localMembers.get(absoluteURI);		
	}
	
	public boolean hadErrors() {		
		//return (!exceptions.isEmpty());
		return exc.hasExceptions();
	}
	
	public Collection getErrors() {
		//return this.exceptions;
		return exc.getExceptions();
	}
	
	public FilesetType getFilesetType() {
		return this.filesetType;
	}
			
	public Collection getRemoteResources() {
      return this.remoteMembers;
	}
	
	/**
	 * @deprecated
	 * @see org.daisy.util.FilesetFile#getRelativeURI
	 */
	public URI getRelativeURI(FilesetFile filesetFile) {
	    ManifestFile manifest = this.getManifestMember();
	    URI parent = manifest.getFile().getParentFile().toURI();
	    URI filesetFileURI = filesetFile.getFile().toURI();
	    URI relative = parent.relativize(filesetFileURI);
	    return relative;
	}
	
	public Collection getMissingMembersURIs() {
	    return missingURIs;
	}
	
	public long getByteSize() {
		long bytesize = 0;	
		Collection c = getLocalMembers();
		Iterator it = c.iterator();
		while (it.hasNext()) {
			File f = (File)it.next();
			bytesize += f.length();
		}
		return bytesize;
	}
}