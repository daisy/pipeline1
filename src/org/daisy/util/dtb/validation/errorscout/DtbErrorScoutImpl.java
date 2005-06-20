package org.daisy.util.dtb.validation.errorscout;

import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.validation.ValidationException;
import org.daisy.util.fileset.AudioFile;
import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.D202SmilFile;
import org.daisy.util.fileset.D202TextualContentFile;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetException;
import org.daisy.util.fileset.FilesetImpl;
import org.daisy.util.fileset.Mp3File;
import org.daisy.util.fileset.Regex;
import org.daisy.util.fileset.SmilFile;
import org.daisy.util.fileset.TextualContentFile;
import org.daisy.util.fileset.XmlFile;
import org.daisy.util.xml.validation.RelaxngSchematronValidator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.daisy.util.fileset.FilesetType;

/**
 * @author Markus Gylling
 */
public class DtbErrorScoutImpl implements DtbErrorScout, ErrorHandler {
	private RelaxngSchematronValidator d202NccRngSchValidator;
	private RelaxngSchematronValidator d202SmilRngSchValidator;		
	
	private LinkedHashSet errors = new LinkedHashSet(); //<Exception>
	
	private FilesetType filesetType;
	
	private FilesetFile member;		
	private Fileset fileset = null;
	
	//for use in interdoc link checking
	private URI cache = null;
	private XmlFile referencedMember = null;
	
	//vars used to determine scouting contents	
	private DtbErrorScoutingLevel scoutingLevel;
	
	private Regex regex = Regex.getInstance();
	
	private boolean doDtdScouting = true;
	private boolean doInterDocLinkScouting = true;
	private boolean doRelaxNgScouting = false;
	private boolean doSchematronScouting = false;
	private boolean doSmilDurationScouting = false;	
	private boolean doAudioFileScouting = false;
	
	/**
	 * Default class constructor
	 * @param filesetType the {@link org.daisy.util.fileset.FilesetType} this errorscout should support
	 * @throws DtbErrorScoutException
	 * @see #DtbErrorScoutImpl(FilesetType, ScoutingLevel)
	 */
	public DtbErrorScoutImpl(FilesetType filesetType) throws DtbErrorScoutException {
		scoutingLevel = DtbErrorScoutingLevel.SLIM;
		this.filesetType = filesetType;
		initialize(filesetType);	  						
	}
	
	
	/**
	 * Extended class constructor
	 * @param filesetType the {@link org.daisy.util.fileset.FilesetType} this errorscout should support
	 * @param level the {@link org.daisy.util.dtb.validation.errorscout.DtbErrorScoutingLevel} to adopt during scouting
	 * @throws DtbErrorScoutException
	 * @see #DtbErrorScoutImpl(FilesetType)
	 */
	public DtbErrorScoutImpl(FilesetType filesetType, DtbErrorScoutingLevel level) throws DtbErrorScoutException {
		this.scoutingLevel = level;
		this.filesetType = filesetType;
		initialize(filesetType);	  						
	}
	
	private void initialize(FilesetType filesetType) throws DtbErrorScoutException {
		//set the parameters based on scoutinglevel
		//these booleans default to the behavior of DtbErrorScoutingLevel.SLIM
		if (scoutingLevel!=DtbErrorScoutingLevel.SLIM) {	
			if (scoutingLevel==DtbErrorScoutingLevel.MEDIUM) {
				doRelaxNgScouting = true;			
				doSmilDurationScouting = true;		
				doAudioFileScouting = true;									
			}else if (scoutingLevel==DtbErrorScoutingLevel.MAXED) {
				doRelaxNgScouting = true;
				doSmilDurationScouting = true;		
				doAudioFileScouting = true;									
				doSchematronScouting = true;
			}
		}		
		
		//prepare the rng+sch validators
		if(doRelaxNgScouting||doSchematronScouting){
			if (filesetType==FilesetType.DAISY_202) {				
				try{					
					File d202NccSchema = new File(
							CatalogEntityResolver.getInstance().resolveEntityToURL("-//DAISY//RNG ncc v2.02//EN",null).toURI());
					File d202SmilSchema = new File(
							CatalogEntityResolver.getInstance().resolveEntityToURL("-//DAISY//RNG smil v2.02//EN",null).toURI());
					
					d202NccRngSchValidator = new RelaxngSchematronValidator
					(d202NccSchema,this,doRelaxNgScouting,doSchematronScouting);		  
					d202SmilRngSchValidator = new RelaxngSchematronValidator
					(d202SmilSchema,this,doRelaxNgScouting,doSchematronScouting);					
				}catch(Exception e){						
					throw new DtbErrorScoutException(e);
				}
			}else{
				throw new DtbErrorScoutException("FilesetType not supported");
			}
		}
	}
	
	/**
	 * @param manifest absolute URI of the manifest file (ncc, opf)
	 * @return true if the DTB had errors, false otherwise
	 * @throws DtbErrorScoutException
	 */
	
	public boolean scout(URI manifest) throws DtbErrorScoutException {
		//clear (this may not be the first scout() call on this instance)
		errors.clear();
		boolean hasErrors = false;		
		member = null;
		fileset = null;
		
		
		//build the fileset
		try { 
			fileset = (Fileset)new FilesetImpl(manifest);
			if (this.filesetType!=fileset.getFilesetType()) {
				throw new DtbErrorScoutException("This ErrorScout instance does not support this fileset type");				
			}
			if (fileset.hadErrors()) {			
				hasErrors = true;
				//copy to the local errors <Exception> HashSet from the <Exception> HashSet in Fileset
				errors.addAll(fileset.getErrors());  
			}
		}catch(FilesetException fse) {
			throw new DtbErrorScoutException(fse);
		}
		
		//iterate through Fileset members and apply appropriate scouting
		Iterator iter = fileset.getLocalMembersURIIterator();
		while(iter.hasNext()) {			
			member = fileset.getLocalMember((URI)iter.next());	
			try {
				
				if(member instanceof XmlFile) {
					if (doInterDocLinkScouting) {
						if(!isInterDocLinkValid((XmlFile)member))hasErrors = true;
					}  
				}
				
				if(member instanceof D202NccFile) {
					if(doRelaxNgScouting) {
						if(!d202NccRngSchValidator.isValid((File)member)) hasErrors = true;
					}															
				}else if (member instanceof SmilFile){
					if(doRelaxNgScouting) {
						if (member instanceof D202SmilFile){
							if(!d202SmilRngSchValidator.isValid((File)member)) hasErrors = true;
						}  
					}  										
					if (doSmilDurationScouting) {
						//SmilFile smil = (SmilFile) member;
						//System.err.print("calculated: "+smil.getMyCalculatedTimeInThisSmil().secondsValue());
						//System.err.println(" given: "+smil.getMyGivenTimeInThisSmil().secondsValue());
					}
				}else if (member instanceof TextualContentFile){
					if (member instanceof D202TextualContentFile){
						//TODO something?
					}													
				}else if (member instanceof AudioFile){
					if(doAudioFileScouting) {
						if (member instanceof Mp3File){
							Mp3File mp3file = (Mp3File) member;																
							//							System.err.println("bitrate " + mp3file.getBitrate());
							//							System.err.println("layer " + mp3file.getLayer());
							//							System.err.println("fs " + mp3file.getSampleFrequency());
							//							System.err.println("ismpeg2 " + mp3file.isMpeg2Lsf());
							//							System.err.println("id3 " + mp3file.hasID3v2());
							//							System.err.println("isMono " + mp3file.isMono());
							//							System.err.println("isVbr " + mp3file.isVbr());								
						}
					}
				}					
			} catch (ValidationException ve) {
				throw new DtbErrorScoutException(ve);
			}
		}		
		return hasErrors;
	}
	
	public Iterator getErrorsIterator() {
		return errors.iterator();
	}
	
	public Fileset getFileset() {
		return fileset;
	}
	
	public void warning(SAXParseException spe) throws SAXException {
		
	}
	
	public void error(SAXParseException spe) throws SAXException {
		errors.add(spe);
	}
	
	public void fatalError(SAXParseException spe) throws SAXException {
		errors.add(spe);
	}
	
	private boolean isInterDocLinkValid(XmlFile member) throws DtbErrorScoutException {		
		//check that all intermembership URIs with fragments in this doc resolves
		//note: nonresolving intermembership URIs without fragments are already reported by Fileset.errors
		boolean result = true;
		
		
		
		try {
			Iterator uriator = member.getUriIterator();				
			while (uriator.hasNext()) {
				String value = (String) uriator.next();
				if(!regex.matches(regex.URI_REMOTE,value)) {
					if(regex.matches(regex.URI_WITH_FRAGMENT,value)) {
						//break the bare string up
						String uriFragment = getFragment(value); 
						String uriPath = stripFragment(value);
						//get the full URI of the member to resolve
						URI uri = member.toURI().resolve(uriPath);
						if(!uri.equals(cache)) {
							//System.err.println("no cache hit");
							//the referenced member is other than last time
							cache=uri;
							//get the file instance from Fileset via the URI key
							referencedMember=(XmlFile)member.getReferencedLocalMember(uri);
							if (referencedMember==null){
								errors.add(new DtbErrorScoutExceptionRecoverable ("reference to nonexisting member "+referencedMember.getName()+"in URI " + value+ " in file " + member.getName()));
								return false;
							}								
						}else{
							//System.err.println("cache hit");
						}
						
						//check whether this colleague has the id value
						if(!referencedMember.hasIDValue(uriFragment)) {
							errors.add(new DtbErrorScoutExceptionRecoverable ("reference to nonexisting fragment in URI " + value + " in file " + member.getName()));
							result = false;
						}
					}
				}
			} //while (uriator.hasNext())
		} catch (Exception e) {
			throw new DtbErrorScoutException("interdoc link validity checking: ",e);
		}
		return result;
	}
	
	private String stripFragment(String value) {				
		StringBuffer sb = new StringBuffer();
		char hash = '#';
		for (int i = 0; i < value.length(); i++) {
			if (value.charAt(i)==hash) {
				return sb.toString();
			}
			sb.append(value.charAt(i));			
		}
		return sb.toString();								
	}
	
	private String getFragment(String value) {				
		StringBuffer sb = new StringBuffer();
		char hash = '#';		
		int hashPos = -1;
		
		for (int i = 0; i < value.length(); i++) {
			if (value.charAt(i)==hash) {
				hashPos = i;
			}else{
				if (hashPos > -1) sb.append(value.charAt(i));
			}  
		}
		return sb.toString();								
	}
	
	//	private boolean matches(Pattern compiledPattern, String match) {
	//		Matcher m = compiledPattern.matcher(match);
	//		return m.matches();	
	//	}
}
