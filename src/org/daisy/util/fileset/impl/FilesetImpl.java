/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.util.fileset.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.daisy.util.fileset.exception.FilesetTypeNotSupportedException;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.ManifestFile;
import org.daisy.util.fileset.interfaces.Referring;
import org.daisy.util.fileset.interfaces.xml.d202.D202MasterSmilFile;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.fileset.util.URIStringParser;
import org.daisy.util.mime.MIMETypeException;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.PoolException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * @author Markus Gylling
 */
public class FilesetImpl implements Fileset {
	private Map mLocalMembers = new HashMap();								//<URI>, <FilesetFile>	
	private HashSet mRemoteMembers = new HashSet();							//<String> 
	private FilesetExceptionCollector mFilesetExceptionCollector = null; 
	private HashSet mMissingURIs = new HashSet();							//<URI>
	private ManifestFile mManifestMember;	
	private FilesetType mFilesetType = null;
	private static FilesetRegex mRegex = FilesetRegex.getInstance();		
	private boolean mSetReferringCollections; 
	private FilesetErrorHandler mErrorListener = null;
	
	/**
	 * Default class constructor
	 * @param manifestURI the URI of the object being input port for fileset retrieval (ncc, opf, playlist, etc)
	 * @param errh the FilesetErrorHandler that will receive notification of nonfatal errors
	 * @throws FilesetNonFatalException 
	 */
	public FilesetImpl(URI manifestURI, FilesetErrorHandler errh) throws FilesetFatalException {
		initialize(manifestURI,errh,false,false);
	}
	
	/**
	 * Extended class constructor
	 * @param manifestURI the URI of the object being input port for fileset retrieval (ncc, opf, playlist, etc)
	 * @param errh the FilesetErrorHandler that will receive notification of nonfatal errors
	 * @param dtdValidate sets whether XML members in the fileset will be DTD validated in the case they reference a DTD. The default value of this property is false.
	 * @throws FilesetNonFatalException 
	 */
	public FilesetImpl(URI manifestURI, FilesetErrorHandler errh, boolean dtdValidate) throws FilesetFatalException {
		initialize(manifestURI,errh,dtdValidate,false);
	}
	
	/**
	 * Extended class constructor. 	 
	 * @param manifestURI the URI of the object being input port for fileset retrieval (ncc, opf, playlist, etc)
	 * @param errh the FilesetErrorHandler that will receive notification of nonfatal errors
	 * @param dtdValidate sets whether XML members in the fileset will be DTD validated.  The default value of this property is false.
	 * @param setReferringCollections sets whether the referringLocalMembers collection will be created on each member. Note - this is a costly procedure in terms of timeconsumption for large filesets.  The default value of this property is false.
	 * @throws FilesetNonFatalException  
	 */
	public FilesetImpl(URI manifestURI, FilesetErrorHandler errh, boolean dtdValidate, boolean setReferringCollections) throws FilesetFatalException {
		initialize(manifestURI,errh,dtdValidate,setReferringCollections);
	}
		
	private void initialize(URI manifestURI, FilesetErrorHandler errh, boolean dtdValidate, boolean setReferringCollections) throws FilesetFatalException  {
				
		this.mErrorListener = errh;
		if (this.mErrorListener == null) throw new FilesetFatalException("no FilesetErrorHandler set");
		
		mFilesetExceptionCollector = new FilesetExceptionCollector(this.mErrorListener);
				
		this.mSetReferringCollections = setReferringCollections;
		
		if (dtdValidate) {
		  System.setProperty("org.daisy.util.fileset.validating", "true");
		} 
		
		PeekResult manifestPeekResult = null;
		Peeker peeker = null;			
		File f = new File(manifestURI);
						
		if(f.exists() && f.canRead()){
			try {
				peeker = PeekerPool.getInstance().acquire();
				manifestPeekResult = peeker.peek(manifestURI);
			}catch (Exception e) {
				//it wasnt an xmlfile or something else went wrong
				//some manifests are not XML so this is ok
			}
				
			try{
				if ((mRegex.matches(mRegex.FILE_NCC, f.getName()))
						&&(manifestPeekResult!=null && manifestPeekResult.getRootElementLocalName().equals("html"))) {
					//set the fileset type
					this.mFilesetType = FilesetType.DAISY_202;
					//instantiate the appropriate filetype					
					this.mManifestMember = new D202NccFileImpl(f.toURI());						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent((FilesetFileImpl)mManifestMember);
					
					//do some obscure stuff specific to this fileset type
					D202NccFileImpl ncc = (D202NccFileImpl)this.mManifestMember;
					ncc.buildSpineMap(this);
					
					File test = new File(mManifestMember.getFile().getParentFile(), "master.smil");
					if (test.exists()){
						D202MasterSmilFile msmil = new D202MasterSmilFileImpl(test.toURI());
						this.fileInstantiatedEvent((FilesetFileImpl)msmil);
					}
										
				}else if((mRegex.matches(mRegex.FILE_OPF, f.getName()))
						&&(manifestPeekResult!=null && manifestPeekResult.getRootElementLocalName().equals("package"))) {		
					//need to preparse to find out what kind of opf it is
					OpfFileImpl temp =  new OpfFileImpl(f.toURI());
					temp.parse();
					//instantiate the appropriate filetype
					if(temp.getMetaDcFormat()!=null && temp.getMetaDcFormat().indexOf("Z39.86")>=0){
						this.mFilesetType = FilesetType.Z3986;
						this.mManifestMember = new Z3986OpfFileImpl(f.toURI());
					}else if(temp.getMetaDcFormat()!=null && temp.getMetaDcFormat().indexOf("NIMAS")>=0){
						this.mManifestMember = new NimasOpfFileImpl(f.toURI());
						this.mFilesetType = FilesetType.NIMAS;
					}else{
						throw new FilesetFatalException("could not detect version of opf (no dc:format)");
					}
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent((FilesetFileImpl)this.mManifestMember);
					//do some obscure stuff specific to this fileset type	
					OpfFileImpl opf = (OpfFileImpl)this.mManifestMember;
					opf.buildSpineMap(this);

				}else if((mRegex.matches(mRegex.FILE_RESOURCE, f.getName()))
						&&(manifestPeekResult!=null && manifestPeekResult.getRootElementLocalName().equals("resources"))) {						
					//set the fileset type
					this.mFilesetType = FilesetType.Z3986_RESOURCEFILE;
					//instantiate the appropriate filetype
					this.mManifestMember = new Z3986ResourceFileImpl(f.toURI());					 						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent((FilesetFileImpl)this.mManifestMember);		

				}else if(mRegex.matches(mRegex.FILE_CSS, f.getName())) {
					//set the fileset type
					this.mFilesetType = FilesetType.CSS;
					//instantiate the appropriate filetype
					this.mManifestMember = new CssFileImpl(f.toURI());
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent((FilesetFileImpl)this.mManifestMember);		

				}else if(manifestPeekResult!=null && manifestPeekResult.getRootElementLocalName().equals("dtbook")) {
					//set the fileset type
					this.mFilesetType = FilesetType.DTBOOK_DOCUMENT;
					//instantiate the appropriate filetype
					this.mManifestMember = new Z3986DtbookFileImpl(f.toURI());					 						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent((FilesetFileImpl)this.mManifestMember);		
					
				}else if((manifestPeekResult!=null)
						&&((manifestPeekResult.getRootElementLocalName() != null 
								&& manifestPeekResult.getRootElementLocalName().equals("html"))
								&&((manifestPeekResult.getPrologSystemId() != null 
										&& manifestPeekResult.getPrologSystemId().indexOf("x")>=0)
								||(manifestPeekResult.getPrologPublicId() != null 
										&& manifestPeekResult.getPrologPublicId().indexOf("X")>=0)
								||(manifestPeekResult.getRootElementNsUri()!= null 
										&& manifestPeekResult.getRootElementNsUri().indexOf("x")>=0)))) {					
					//set the fileset type
					this.mFilesetType = FilesetType.XHTML_DOCUMENT;
					//instantiate the appropriate filetype
					this.mManifestMember = new Xhtml10FileImpl(f.toURI());					 						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent((FilesetFileImpl)this.mManifestMember);	
					
				}else if(mRegex.matches(mRegex.FILE_XHTML, f.getName())) {
					//set the fileset type
					this.mFilesetType = FilesetType.HTML_DOCUMENT;
					//instantiate the appropriate filetype
					this.mManifestMember = new HtmlFileImpl(f.toURI());					 						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent((FilesetFileImpl)this.mManifestMember);	
					
				}else if(mRegex.matches(mRegex.FILE_M3U, f.getName())) {
					//set the fileset type
					this.mFilesetType = FilesetType.PLAYLIST_M3U;
					//instantiate the appropriate filetype
					this.mManifestMember = new M3UFileImpl(f.toURI());					 						
					//send it to the observer which handles the rest generically
					this.fileInstantiatedEvent((FilesetFileImpl)this.mManifestMember);		
					
				}else if(mRegex.matches(mRegex.FILE_PLS, f.getName())) {
						//set the fileset type
						this.mFilesetType = FilesetType.PLAYLIST_PLS;
						//instantiate the appropriate filetype with this as errorhandler
						this.mManifestMember = new PlsFileImpl(f.toURI());					 						
						//send it to the observer which handles the rest generically
						this.fileInstantiatedEvent((FilesetFileImpl)this.mManifestMember);	
				}else{
					//other types
				    //throw new FilesetFatalException("Unsupported manifest type");
					//mg 20060830;
					throw new FilesetTypeNotSupportedException("Unsupported manifest type: " + manifestURI.toString());
				}
			} catch (Exception e){
				//thrown if the manifest could not be instantiated
				//only throw outwards for the manifest file instantiation
				throw new FilesetFatalException(e.getMessage(),e);				
			}finally{
				try {
					System.clearProperty("org.daisy.util.fileset.validating");
					PeekerPool.getInstance().release(peeker);					
				} catch (PoolException e) {
					throw new FilesetFatalException(e.getMessage(),e);
				}
			}
		}else{			
			throw new FilesetFatalException(new IOException("manifest not readable: '" + f.toString() + "'"));								 
		}  
		
		//if we get here the fileset is completely populated without fatal errors
		
		if(this.mSetReferringCollections){
			//populate the reffering property
			Iterator it = mLocalMembers.keySet().iterator();
			while(it.hasNext()) {
				FilesetFileImpl file = (FilesetFileImpl) mLocalMembers.get(it.next());				
				file.setReferringLocalMembers(mLocalMembers);				  
			}
		}
				
		
				
	}
	
	void fileInstantiatedEvent(FilesetFileImpl member) throws ParserConfigurationException, FilesetFatalException {
		//all file instantiations are reported here
		//but never by the instantiated member itself
		
		//add to this.localMembers			
		mLocalMembers.put(member.getFile().toURI(),member);
		
		//invoke the generic parse method
		try {				
			member.parse();					
		} catch (IOException ioe) {								
			mFilesetExceptionCollector.add(new FilesetFileFatalErrorException(member,ioe));			
			return;
		} catch (SAXParseException spe) {
			//malformedness, dont add/return; already added by XmlFile errhandler				
		} catch (SAXException se) {
			//other serious sax error
			mFilesetExceptionCollector.add(new FilesetFileFatalErrorException(member,se));
			return;
		} catch (BitstreamException bse) {
			mFilesetExceptionCollector.add(new FilesetFileFatalErrorException(member,bse));
			return;							
		}		
		
		//collect and handle any nonthrown exceptions
		Iterator iter = member.getErrors().iterator();
		while(iter.hasNext()) { 
			try{
				Exception e = (Exception) iter.next();				
				if(e instanceof FilesetFileException) {
					mFilesetExceptionCollector.add((FilesetFileException)e);
				}else{
					mFilesetExceptionCollector.add(new FilesetFileException(member,e));
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
				if(!mRegex.matches(mRegex.URI_REMOTE,value)) {
					//strip fragment if existing
					value = URIStringParser.stripFragment(value);					
					//resolve the uri string
					try {
						resolvedURI = referer.getFile().toURI().resolve(new URI(value));										
						if (!resolvedURI.equals(cachedURI) && !value.equals("")) {
							cachedURI = resolvedURI; 					
	     					//check if this file has already been added to main collection						
							FilesetFileImpl newmember = (FilesetFileImpl)mLocalMembers.get(resolvedURI);
							if (newmember == null) {
								//this is a member that hasnt been namedropped before													
								try {
									//determine what type to instantiate
									newmember = getType(member, resolvedURI, value, this.mFilesetType);
									if(newmember instanceof AnonymousFileImpl) {
										//exceptions.add(new FilesetFileException(newmember, new AnonymousFileException("no matching file type found for " + value + ": this file appears as AnonymousFile")));
										mFilesetExceptionCollector.add(new FilesetFileWarningException(newmember, new IOException("no matching file type found for " + value + ": this file appears as AnonymousFile")));
									}
								} catch (FileNotFoundException fnfe) {
									//exceptions.add(new FilesetFileFatalErrorException(member,fnfe));								
									mFilesetExceptionCollector.add(new FilesetFileFatalErrorException(member,fnfe));
									mMissingURIs.add(resolvedURI);
									continue;
								} catch (IOException ioe) {
									//exceptions.add(new FilesetFileFatalErrorException(member,ioe));
									mFilesetExceptionCollector.add(new FilesetFileFatalErrorException(member,ioe));
									continue;
								} 
								//report fileInstantiatedEvent
								this.fileInstantiatedEvent(newmember);						
							} //if (newmember == null)
							//put in the incoming members references list
							referer.putReferencedMember(newmember);
						} //!resolvedURI.equals(cache)
					} catch (URISyntaxException use) {
						mFilesetExceptionCollector.add(new FilesetFileFatalErrorException(member, use));
						continue;
					}
				}//if matches URI_LOCAL
				else {
					mRemoteMembers.add(value);
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
				
		if (mRegex.matches(mRegex.FILE_MP3,value)){
			return new Mp3FileImpl(uri);
		}
		
		if (mRegex.matches(mRegex.FILE_WAV,value)){
			return new WavFileImpl(uri);
		}
		
		Peeker peeker = null;
		
		try{
			
			peeker = PeekerPool.getInstance().acquire();
			PeekResult peekResult = peeker.peek(uri);
			String rootName = peekResult.getRootElementLocalName().intern();
			
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
				if (mRegex.matches(mRegex.FILE_NCC,value)) {			
					return new D202NccFileImpl(uri);			
				}else if (filesetType == FilesetType.DAISY_202) {
					return new D202TextualContentFileImpl(uri);
				}else if ((peekResult!=null)&&
						((peekResult.getPrologSystemId().indexOf("x")>=0)
								||(peekResult.getPrologPublicId().indexOf("X")>=0)
								||(peekResult.getRootElementNsUri().indexOf("x")>=0))){
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
				if((peekResult!=null)&&(peekResult.getRootElementNsUri().equals("http://www.ascc.net/xml/schematron")
						||peekResult.getRootElementNsUri().equals("http://purl.oclc.org/dsdl/schematron"))){
					return new SchematronFileImpl(uri);
				}

				if((peekResult!=null)&&(peekResult.getRootElementNsUri().equals("http://www.w3.org/2001/XMLSchema"))){
					return new XsdFileImpl(uri);
				}
								
			}
			
			if(rootName== "grammar" && (peekResult!=null && peekResult.getRootElementNsUri().equals("http://relaxng.org/ns/structure/1.0"))) {
				return new RelaxngFileImpl(uri);
			}
			
		} catch (Exception e) { //peeker.peek
			//the file wasnt an xml file or something else went wrong			
		}finally{
			try {
				PeekerPool.getInstance().release(peeker);
			} catch (PoolException e) {

			}	
		}
		
		
		//now we know its not an xml file, nor any of the common audiofilestypes mp3|wav
						
		//need to test for (non xhtml) html again		
		if (mRegex.matches(mRegex.FILE_XHTML,value)){
			return new HtmlFileImpl(uri);
		}
		
		if (mRegex.matches(mRegex.FILE_CSS,value)){
			return new CssFileImpl(uri);
		}
	
		if (mRegex.matches(mRegex.FILE_JPG,value)){
			return new JpgFileImpl(uri);
		}

		if (mRegex.matches(mRegex.FILE_GIF,value)){
			return new GifFileImpl(uri);
		}
	
		if (mRegex.matches(mRegex.FILE_PNG,value)){
			return new PngFileImpl(uri);
		}
		
		if (mRegex.matches(mRegex.FILE_BMP,value)){
			return new BmpFileImpl(uri);
		}
		
		if (mRegex.matches(mRegex.FILE_MP2,value)){
			return new Mp2FileImpl(uri);
		}		

		if (mRegex.matches(mRegex.FILE_DTD,value)){
			return new DtdFileImpl(uri);
		}
		
		if (mRegex.matches(mRegex.FILE_PDF,value)){
			return new PdfFileImpl(uri);
		}
		
		//if no factual match, still instantiate it				
		return new AnonymousFileImpl(uri);			
		
	}
		
	public ManifestFile getManifestMember() {		
		return mManifestMember;
	}
			
	public Collection getLocalMembers() {
		return mLocalMembers.values();		
	}
		
	public Collection getLocalMembersURIs() {
		return mLocalMembers.keySet();
	}
	
	public FilesetFile getLocalMember(URI absoluteURI) {
		return (FilesetFile)mLocalMembers.get(absoluteURI);		
	}
	
	public boolean hadErrors() {		
		//return (!exceptions.isEmpty());
		return mFilesetExceptionCollector.hasExceptions();
	}
	
	public Collection getErrors() {
		//return this.exceptions;
		return mFilesetExceptionCollector.getExceptions();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.Fileset#getFilesetType()
	 */
	public FilesetType getFilesetType() {
		return this.mFilesetType;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.Fileset#getRemoteResources()
	 */
	public Collection getRemoteResources() {
      return this.mRemoteMembers;
	}
	
	/**
	 * @deprecated
	 * @see org.daisy.util.fileset.interfaces.FilesetFile#getRelativeURI
	 */
	public URI getRelativeURI(FilesetFile filesetFile) {
	    ManifestFile manifest = this.getManifestMember();
	    URI parent = manifest.getFile().getParentFile().toURI();
	    URI filesetFileURI = filesetFile.getFile().toURI();
	    URI relative = parent.relativize(filesetFileURI);
	    return relative;
	}
	
	public Collection getMissingMembersURIs() {
	    return mMissingURIs;
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