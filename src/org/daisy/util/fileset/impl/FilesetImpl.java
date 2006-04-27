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
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import javazoom.jl.decoder.BitstreamException;

import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetException;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.ManifestFile;
import org.daisy.util.fileset.interfaces.Referring;
import org.daisy.util.fileset.interfaces.xml.d202.D202MasterSmilFile;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.mime.MIMETypeException;
import org.daisy.util.xml.Peeker;
import org.daisy.util.xml.PeekerImpl;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * @author Markus Gylling
 */
public class FilesetImpl implements FilesetErrorHandler, Fileset {
	private Map localMembers = new HashMap();	//<URI>, <FilesetFile>	
	private HashSet remoteMembers = new HashSet();	//<String> 
	private HashSet errors = new HashSet();			//<Exception>	
	private HashSet missingURIs = new HashSet();
	private ManifestFile manifestMember;	
	private FilesetType filesetType = null;
	private FilesetRegex regex = FilesetRegex.getInstance();
	private Peeker peeker; 
	private boolean setReferringCollections; 
	
	/**
	 * Default class constructor
	 * @param manifest the URI of the object being input port for fileset retrieval (ncc, opf, playlist, etc)
	 * @see #FilesetImpl(URI, boolean)
	 * @see #FilesetImpl(URI,boolean, boolean)
	 */
	public FilesetImpl(URI manifest) throws FilesetException {
		initialize(manifest,false,false);	
	}
	
	/**
	 * Extended class constructor
	 * @param manifest the URI of the object being input port for fileset retrieval (ncc, opf, playlist, etc)
	 * @param dtdValidate sets whether XML members in the fileset will be DTD validated in the case they reference a DTD. The default value of this property is false.
	 * @see #FilesetImpl(URI)
	 * @see #FilesetImpl(URI,boolean, boolean) 
	 */
	public FilesetImpl(URI manifest, boolean dtdValidate) throws FilesetException {
		initialize(manifest,dtdValidate,false);	
	}
	
	/**
	 * Extended class constructor. 	 
	 * @param manifest the URI of the object being input port for fileset retrieval (ncc, opf, playlist, etc)
	 * @param dtdValidate sets whether XML members in the fileset will be DTD validated.  The default value of this property is false.
	 * @param setReferringCollections sets whether the referringLocalMembers collection will be created on each member. Note - this is a costly procedure in terms of timeconsumption for large filesets.  The default value of this property is false.
	 * @see #FilesetImpl(URI)
	 * @see #FilesetImpl(URI, boolean)
	 * @see {@link org.daisy.util.fileset.interfaces.Referable} 
	 */
	public FilesetImpl(URI manifest, boolean dtdValidate, boolean setReferringCollections) throws FilesetException {
		initialize(manifest,dtdValidate,setReferringCollections);	
	}
	
	private void initialize(URI manifest, boolean dtdValidate, boolean setReferringCollections) throws FilesetException  {
		
		//speed up JAXP		
		//TODO System.getProperty("java.version") || java.vendor
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
		System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
		System.setProperty("org.apache.xerces.xni.parser.XMLParserConfiguration","com.sun.org.apache.xerces.internal.parsers.XML11Configuration");
		
		this.setReferringCollections = setReferringCollections;
		if (dtdValidate) {
		  System.setProperty("org.daisy.util.fileset.validating", "true");
		} 
		
		peeker = new PeekerImpl();		
		File f = new File(manifest);
						
		if(f.exists() && f.canRead()){

			try {
				peeker.peek(f.toURI()); 
			}catch (Exception e) {
				//it wasnt an xmlfile or something else went wrong
				peeker.reset(); //this sets all peeker.gets to the empty string, not null.
			}
	
			
			try{
				if ((regex.matches(regex.FILE_NCC, f.getName()))&&(peeker.getRootElementLocalName().equals("html"))) {
					//set the fileset type
					this.filesetType = FilesetType.DAISY_202;
					//instantiate the appropriate filetype with this as errorhandler					
					this.manifestMember = new D202NccFileImpl(f.toURI(),this);						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent(manifestMember);
					//do some obscure stuff
					File test = new File(manifestMember.getFile().getParentFile(), "master.smil");
					if (test.exists()){
						D202MasterSmilFile msmil = new D202MasterSmilFileImpl(test.toURI());
						this.fileInstantiatedEvent(msmil);
					}
										
				}else if((regex.matches(regex.FILE_OPF, f.getName()))&&(peeker.getRootElementLocalName().equals("package"))) {
					//mg: dont set the fileset type here, since there are different types of opf
					//this.filesetType = FilesetType.Z3986;
					
					//instantiate the appropriate filetype with this as errorhandler
					this.manifestMember = new OpfFileImpl(f.toURI(),this);
					//find out what kind of opf it is (oeb,idpf,z3986,nimas)
					//we need to parse get to the info we need
					OpfFileImpl opf = (OpfFileImpl)this.manifestMember;
					opf.parse();					
					if(opf.getMetaDcFormat().indexOf("Z39.86")>=0){
						this.filesetType = FilesetType.Z3986;
					}else if(opf.getMetaDcFormat().indexOf("NIMAS")>=0){
						this.filesetType = FilesetType.NIMAS;
					}
					
					//send it to the observer which handles the rest generically
					//note, the opf will be reparsed there... twice!=nice.
					this.fileInstantiatedEvent(this.manifestMember);
					//do some obscure stuff					
					opf.buildSpineMap(this);

				}else if((regex.matches(regex.FILE_RESOURCE, f.getName()))&&(peeker.getRootElementLocalName().equals("resources"))) {
					//set the fileset type
					this.filesetType = FilesetType.Z3986_RESOURCEFILE;
					//instantiate the appropriate filetype with this as errorhandler
					this.manifestMember = new Z3986ResourceFileImpl(f.toURI(),this);					 						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent(this.manifestMember);		

				}else if(regex.matches(regex.FILE_CSS, f.getName())) {
					//set the fileset type
					this.filesetType = FilesetType.CSS;
					//instantiate the appropriate filetype with this as errorhandler
					this.manifestMember = new CssFileImpl(f.toURI(),this);					 						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent(this.manifestMember);		

				}else if(peeker.getRootElementLocalName().equals("dtbook")) {
					//set the fileset type
					this.filesetType = FilesetType.DTBOOK_DOCUMENT;
					//instantiate the appropriate filetype with this as errorhandler
					this.manifestMember = new Z3986DtbookFileImpl(f.toURI(),this);					 						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent(this.manifestMember);		

				}else if(peeker.getRootElementLocalName().equals("html")) {
					//set the fileset type
					this.filesetType = FilesetType.XHTML_DOCUMENT;
					//instantiate the appropriate filetype with this as errorhandler
					this.manifestMember = new Xhtml10FileImpl(f.toURI(),this);					 						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent(this.manifestMember);	
					
				}else if(regex.matches(regex.FILE_M3U, f.getName())) {
					//set the fileset type
					this.filesetType = FilesetType.PLAYLIST_M3U;
					//instantiate the appropriate filetype with this as errorhandler
					this.manifestMember = new M3UFileImpl(f.toURI());					 						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent(this.manifestMember);		
					
				}else if(regex.matches(regex.FILE_PLS, f.getName())) {
						//set the fileset type
						this.filesetType = FilesetType.PLAYLIST_PLS;
						//instantiate the appropriate filetype with this as errorhandler
						this.manifestMember = new PlsFileImpl(f.toURI());					 						
						//send it to the observer which handles the rest generically
						this.fileInstantiatedEvent(this.manifestMember);	
				}else{
					// other types
				    throw new FilesetException("Unsupported manifest type");
				}
			} catch (Exception e){
				//thrown if the manifest could not be instantiated
				//only throw outwards for the manifest file instantiation
				throw new FilesetException(e);				
			}
		}else{			
			throw new FilesetException(new IOException("manifest not readable"));								 
		}  
		
		//if we get here the fileset is completely populated without fatal errors
		
		if(this.setReferringCollections){
			//		populate the reffering property
			Iterator it = localMembers.keySet().iterator();
			while(it.hasNext()) {
				FilesetFileImpl file = (FilesetFileImpl) localMembers.get(it.next());				
				file.setReferringLocalMembers(localMembers);				  
			}
		}
		//System.err.println("fileset completely populated");
		//System.err.println("size: " + this.localMembers.size());		
		System.clearProperty("org.daisy.util.fileset.validating");
		
	}
	
	void fileInstantiatedEvent(FilesetFile member) throws ParserConfigurationException {
		//all file instantiations are reported here
		//but never by the member itself
		
		//System.out.println("loading " + member.getName());		
		
		//add to this.localMembers			
		localMembers.put(member.getFile().toURI(),member);
		
		if (member instanceof Referring) {
			//if its referring we need to find out whom else it points to
			try {				
				member.parse();					
			} catch (IOException ioe) {
				System.err.println("ioexception in fileInstantiatedEvent member.parse()");
				errors.add(ioe);
				return;
			} catch (SAXParseException spe) {
				//malformedness, dont return
				//added to errors by errhandler
				System.err.println("saxparseexception in fileInstantiatedEvent member.parse()");
			} catch (SAXException se) {
				//other sax error
				errors.add(se);
				System.err.println("saxexception in fileInstantiatedEvent member.parse()");
				return;
			} catch (BitstreamException bse) {
				errors.add(bse);
				System.err.println("bitstreamexception in fileInstantiatedEvent member.parse()");
				return;							
			}						
			Referring referer = (Referring)member;
			Iterator it = referer.getUriIterator();
			String value;
			URI resolvedURI = null;
			URI cachedURI = null;
			while (it.hasNext()) {
				value = (String)it.next();					
				if(!regex.matches(regex.URI_REMOTE,value)) {
					//strip fragment if existing
					value = stripFragment(value);					
					//resolve the uri string
					resolvedURI = referer.getFile().toURI().resolve(value);										
					if (!resolvedURI.equals(cachedURI) && !value.equals("")) {
						cachedURI = resolvedURI; 					
     					//check if this file has already been added to main collection
						FilesetFile newmember = (FilesetFile)localMembers.get(resolvedURI);
						if (newmember == null) {
							//this is a member that hasnt been namedropped before													
							try {
								//determine what type to instantiate
								newmember = getType(member, resolvedURI, value);
							} catch (ParserConfigurationException pce) {
								errors.add(pce);
								System.err.println("pce in getType");
								throw(pce);
							} catch (SAXException se) {
								errors.add(se);
								System.err.println("se in getType");
								continue;
							} catch (FileNotFoundException fnfe) {
								errors.add(fnfe);
								missingURIs.add(resolvedURI);
								//System.err.println("ioe in getType");
								continue;
							} catch (IOException ioe) {
								errors.add(ioe);
								System.err.println("ioe in getType");
								continue;
							} catch (FilesetException fse) {
								errors.add(fse);
								System.err.println("fse in getType: " + fse.getMessage());
								continue;
							} catch (BitstreamException bse) {
								errors.add(bse);
								System.err.println("bse in getType");
								continue;
							} catch (MIMETypeException mte) {
								errors.add(mte);
								System.err.println("mte in getType");
								continue;
							}														
							//report fileInstantiatedEvent
							this.fileInstantiatedEvent(newmember);						
						} //if (newmember == null)
						//put in the incoming members references list
						referer.putReferencedMember(newmember.getFile().toURI(),newmember);
					} //!resolvedURI.equals(cache)
				}//if matches URI_LOCAL
				else {
					remoteMembers.add(value);
				}
			}//while (it.hasNext())			
		}//if (member instanceof QReferring)  		
	}
	
	/**
	 * identifies for an incoming reference the most appropriate FilesetFile instance type based primarily on URI and name heuristics
	 * Can also use the {@link org.daisy.util.xml.Peeker} class for XML instances 
	 * @param owner the FilesetFile in which the reference to this instance occurs
	 * @param uri the absolute URI of the new instance (resolved from owners URI)
	 * @param value the string value of the reference (pre-URI-resolve, aka attr value as-is in the owner)
	 * @throws MIMETypeException 
	 */
	private FilesetFile getType(FilesetFile owner, URI uri, String value) throws ParserConfigurationException, BitstreamException, SAXException, FilesetException, FileNotFoundException, IOException, MIMETypeException {
		FilesetFile file = null;
				
		if (regex.matches(regex.FILE_SMIL,value)) {
			if (this.filesetType == FilesetType.DAISY_202) {				
				return new D202SmilFileImpl(uri, this);
			}else if (this.filesetType == FilesetType.Z3986) {			
				return new Z3986SmilFileImpl(uri, this);
			}else{
				try{
					peeker.peek(uri);
					if (peeker.getRootElementLocalName()!=null && peeker.getRootElementLocalName().equals("smil")) {
						return new SmilFileImpl(uri, this);
					}
				}catch (Exception e) {
					//the file wasnt an xml file or something else went wrong
				}
			}
		}
		
		else if (regex.matches(regex.FILE_MP3,value)){
			return new Mp3FileImpl(uri);
		}
		
		else if (regex.matches(regex.FILE_WAV,value)){
			return new WavFileImpl(uri);
		}
		
		else if (regex.matches(regex.FILE_XHTML,value)) {	
			if (this.filesetType == FilesetType.DAISY_202) {
				return new D202TextualContentFileImpl(uri, this);
			}
			return new Xhtml10FileImpl(uri, this);
			
		}
		
		else if (regex.matches(regex.FILE_NCC,value)) {			
			return new D202NccFileImpl(uri, this);			
		}
		
		else if (regex.matches(regex.FILE_NCX,value)) {			
			return new Z3986NcxFileImpl(uri, this);			
		}
		
		else if (regex.matches(regex.FILE_OPF,value)) {
			return new OpfFileImpl(uri, this);
		}
		
		else if (regex.matches(regex.FILE_CSS,value)){
			return new CssFileImpl(uri, this);
		}
		
		else if (regex.matches(regex.FILE_JPG,value)){
			return new JpgFileImpl(uri);
		}

		else if (regex.matches(regex.FILE_GIF,value)){
			return new GifFileImpl(uri);
		}
		
		else if (regex.matches(regex.FILE_PNG,value)){
			return new PngFileImpl(uri);
		}
				
		else if (regex.matches(regex.FILE_RESOURCE,value)){
			return new Z3986ResourceFileImpl(uri);
		}
		
		else if (regex.matches(regex.FILE_XML,value)){
			peeker.peek(uri);
			if (peeker.getRootElementLocalName().equals("dtbook")) {
				return new Z3986DtbookFileImpl(uri, this);
			}			
		}	
		
		//if no factual match, still instantiate it		
		errors.add(new FilesetException("no matching file type found for " + value + ": this file appears as AnonymousFile"));
		return new AnonymousFileImpl(uri);			
		
	}
	
	private String stripFragment(String value) {				
		StringBuilder sb = new StringBuilder();
		int length = value.length();
		char hash = '#';
		for (int i = 0; i < length; i++) {
			if (value.charAt(i)==hash) {
				return sb.toString();
			}
			sb.append(value.charAt(i));			
		}
		return sb.toString();								
	}
	
	public ManifestFile getManifestMember() {		
		return manifestMember;
	}
		
	public Set getLocalMembersURIs() {
	    return localMembers.keySet();
	}
	
//	public Iterator getLocalMembersURIIterator() {
//		return localMembers.keySet().iterator();		
//	}
	
	public FilesetFile getLocalMember(URI absoluteURI) {
		return (FilesetFile)localMembers.get(absoluteURI);		
	}
	
	public Collection getLocalMembers() {
		return localMembers.values();		
	}
	
	public boolean hadErrors() {		
		return (!errors.isEmpty());
	}
	
//	public Iterator getErrorsIterator() {
//		return errors.iterator();
//	}	
	
	public Collection getErrors() {
		return this.errors;
	}
	
	public FilesetType getFilesetType() {
		return this.filesetType;
	}
	
	public void error(FilesetException exception) throws FilesetException {
		errors.add(exception);
		System.err.println("error QFilesetException in Fileset errhandler");
	}
	
	public void warning(FilesetException exception) throws FilesetException {
		errors.add(exception);		
		System.err.println("warning QFilesetException in Fileset errhandler");
	}	
	
	public void warning(SAXParseException exception) throws SAXException {
		errors.add(exception);		
		// System.err.println("warning saxexception in Fileset errhandler");
	}
	
	public void error(SAXParseException exception) throws SAXException {
		errors.add(exception);		
		//	System.err.println("error saxexception in Fileset errhandler");
	}
	
	public void fatalError(SAXParseException exception) throws SAXException {
		errors.add(exception);			
		// System.err.println("fatal saxexception in Fileset errhandler");
	}
	
	public void warning(CSSParseException exception) throws CSSException {
		errors.add(exception);
		System.err.println("warning CSSParseException in Fileset errhandler");
	}
	
	public void error(CSSParseException exception) throws CSSException {
		errors.add(exception);
		System.err.println("error CSSParseException in Fileset errhandler");
	}
	
	public void fatalError(CSSParseException exception) throws CSSException {
		errors.add(exception);
		System.err.println("fatal CSSParseException in Fileset errhandler");
	}
		
	public long getLocalMemberSize() {		
		return this.localMembers.size();
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

	public Collection getRemoteResources() {
      return (Collection)this.remoteMembers;
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
	
	public Collection getMissingURIs() {
	    return missingURIs;
	}
	
}
