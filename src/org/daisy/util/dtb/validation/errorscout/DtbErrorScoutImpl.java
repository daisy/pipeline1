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

package org.daisy.util.dtb.validation.errorscout;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.audio.AudioFile;
import org.daisy.util.fileset.interfaces.audio.Mp3File;
import org.daisy.util.fileset.interfaces.xml.OpfFile;
import org.daisy.util.fileset.interfaces.xml.SmilFile;
import org.daisy.util.fileset.interfaces.xml.TextualContentFile;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202NccFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202SmilFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202TextualContentFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986DtbookFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986NcxFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986OpfFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986ResourceFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986SmilFile;
import org.daisy.util.fileset.util.DefaultFilesetErrorHandlerImpl;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.validation.RelaxngSchematronValidator;
import org.daisy.util.xml.validation.ValidationException;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Markus Gylling
 * @deprecated use org.daisy.util.fileset.validation.ValidatorFactory instead
 */
public class DtbErrorScoutImpl implements DtbErrorScout, ErrorHandler {
	private RelaxngSchematronValidator d202NccRngSchValidator;
	private RelaxngSchematronValidator d202SmilRngSchValidator;		
		
	private RelaxngSchematronValidator z39OpfRngSchValidator; 
	private RelaxngSchematronValidator z39SmilRngSchValidator; 
	private RelaxngSchematronValidator z39NcxRngSchValidator; 
	private RelaxngSchematronValidator z39ResRngSchValidator;
	private RelaxngSchematronValidator z39DtbookRngSchValidator20051;
	private RelaxngSchematronValidator z39DtbookRngSchValidator20052;

	private LinkedHashSet errors = new LinkedHashSet(); //<Exception>	
	private FilesetType filesetType;	
	private FilesetFile member;		
	private Fileset fileset = null;	
	private FilesetRegex regex = FilesetRegex.getInstance();
	//for use in interdoc link checking
	private URI cache = null;
	private XmlFile referencedMember = null;	
	//vars used to determine scouting contents	
	private DtbErrorScoutingLevel scoutingLevel;			
	private boolean doDtdScouting = true;
	private boolean doInterDocLinkScouting = true;
	private boolean doRelaxNgScouting = false;
	private boolean doLimitedSchematronScouting = false;
	private boolean doFullSchematronScouting = false;
	private boolean doSmilDurationScouting = false;	
	private boolean doAudioFileScouting = false;
	
	/**
	 * Default class constructor
	 * @param filesetType the {@link org.daisy.util.fileset.FilesetType} this errorscout should support
	 * @throws DtbErrorScoutException
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
				doLimitedSchematronScouting = true;
			}else if (scoutingLevel==DtbErrorScoutingLevel.MAXED) {
				doRelaxNgScouting = true;
				doSmilDurationScouting = true;		
				doAudioFileScouting = true;			
				doLimitedSchematronScouting = true;
				doFullSchematronScouting = true;
			}
		}		
				
		//prepare the rng+sch validators
		if(doRelaxNgScouting||doLimitedSchematronScouting||doFullSchematronScouting){
			if (filesetType==FilesetType.DAISY_202) {				
				try{					
					d202NccRngSchValidator = new RelaxngSchematronValidator("-//DAISY//RNG ncc v2.02//EN",null,this,doRelaxNgScouting,doLimitedSchematronScouting);		  
					d202SmilRngSchValidator = new RelaxngSchematronValidator("-//DAISY//RNG smil v2.02//EN",null,this,doRelaxNgScouting,doFullSchematronScouting);					
				}catch(Exception e){						
					throw new DtbErrorScoutException(e);
				}
			}else if (filesetType==FilesetType.Z3986) {
				try{					
				    z39OpfRngSchValidator = new RelaxngSchematronValidator("+//ISBN 0-9673008-1-9//RNG OEB 1.2 Package//EN",null,this,doRelaxNgScouting,false);
					z39SmilRngSchValidator = new RelaxngSchematronValidator("-//NISO//RNG dtbsmil 2005-1//EN",null,this,doRelaxNgScouting,false);
					z39NcxRngSchValidator = new RelaxngSchematronValidator("-//NISO//RNG ncx 2005-1//EN",null,this,doRelaxNgScouting,false);
					z39ResRngSchValidator = new RelaxngSchematronValidator("-//NISO//RNG resource 2005-1//EN",null,this,doRelaxNgScouting,true);
					z39DtbookRngSchValidator20051 = new RelaxngSchematronValidator("-//NISO//RNG dtbook 2005-1//EN",null,this,doRelaxNgScouting,true);
					z39DtbookRngSchValidator20052 = new RelaxngSchematronValidator("-//NISO//RNG dtbook 2005-2//EN",null,this,doRelaxNgScouting,true);
					
				}catch(Exception e){						
					throw new DtbErrorScoutException(e.getMessage());
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
			fileset = new FilesetImpl(manifest, new DefaultFilesetErrorHandlerImpl(), true,false);
			if (this.filesetType!=fileset.getFilesetType()) {
				throw new DtbErrorScoutException("This ErrorScout instance does not support this fileset type");				
			}
			if (fileset.hadErrors()) {			
				hasErrors = true;
				//copy to the local errors <Exception> HashSet from the <Exception> HashSet in Fileset
				errors.addAll(fileset.getErrors());  
			}
		}catch(FilesetFatalException fse) {
			throw new DtbErrorScoutException(fse);
		}
		
		//System.err.println("fileset built");
		
		//iterate through Fileset members and apply appropriate scouting
		long calculatedTotalTimeMillis = 0;
		SmilClock statedTotalTime = null;
		
		Iterator iter = fileset.getLocalMembers().iterator();				
		while(iter.hasNext()) {			
			member = (FilesetFile)iter.next();	
			try {				
				//do interdoc link checks for all xml files
				if(member instanceof XmlFile) {
					if (doInterDocLinkScouting) {
						if(!isInterDocLinkValid((XmlFile)member))hasErrors = true;
					}  
				}				
				//do specific tests for each type
				if(member instanceof D202NccFile) {
					D202NccFile ncc = (D202NccFile) member;
					//get the totaltime for check after while loop
					statedTotalTime = ncc.getStatedDuration();
					//check the heading hirearchy
					if (!ncc.hasHierarchicalHeadingSequence()){
						errors.add(new FilesetFatalException("incorrect heading hierarchy in "+ncc.getFile().getName()));
						hasErrors = true;
					}
					if(doRelaxNgScouting) {
						if(!d202NccRngSchValidator.isValid((File)member)) hasErrors = true;
					}
				} else if (member instanceof Z3986NcxFile){
					if(doRelaxNgScouting) {
						if(!z39NcxRngSchValidator.isValid((File)member)) hasErrors = true;
					}					
				} else if (member instanceof Z3986ResourceFile){
					if(doRelaxNgScouting) {
						if(!z39ResRngSchValidator.isValid((File)member)) hasErrors = true;
					}				
				} else if (member instanceof OpfFile){
					Z3986OpfFile opf = (Z3986OpfFile) member;
					//get the totaltime for check after while loop
					statedTotalTime = opf.getStatedDuration();
					if(doRelaxNgScouting) {
						if(!z39OpfRngSchValidator.isValid((File)member)) hasErrors = true;
					}	
				}else if (member instanceof SmilFile){
					
					if(doRelaxNgScouting) {
						if (member instanceof D202SmilFile){
							if(!d202SmilRngSchValidator.isValid((File)member)) hasErrors = true;
						}  
						if (member instanceof Z3986SmilFile){
							if(!z39SmilRngSchValidator.isValid((File)member)) hasErrors = true;
						} 
					}  														
					if ((doSmilDurationScouting)&&
							((member instanceof D202SmilFile)||(member instanceof Z3986SmilFile))) {
						try{
							SmilFile smil = (SmilFile) member;
							//add to total count 
							calculatedTotalTimeMillis = calculatedTotalTimeMillis + smil.getCalculatedDuration().millisecondsValue();
							//test timeInThisSmil
							if (smil.getStatedDuration()!=null) {							
								if(smil.getCalculatedDuration().secondsValueRounded() != smil.getStatedDuration().secondsValue()) {
									errors.add(new FilesetFatalException("expected duration "+smil.getCalculatedDuration().secondsValueRounded()+" but found "+smil.getStatedDuration().secondsValue()+ " in "+smil.getFile().getName()));
									hasErrors=true;
								}
							}
						}catch (Exception e){							
							errors.add(e);
						}
					}
				}else if (member instanceof TextualContentFile){
					if (member instanceof D202TextualContentFile){						
						D202TextualContentFile doc = (D202TextualContentFile)member;
						if (!doc.hasHierarchicalHeadingSequence()){
							errors.add(new FilesetFatalException("incorrect heading hierarchy in "+doc.getFile().getName()));
							hasErrors = true;
						}
					} else if (member instanceof Z3986DtbookFile) {
					    if (doRelaxNgScouting) {
					    	try {
						    	PeekerPool pool = PeekerPool.getInstance();
						    	Peeker peeker = pool.acquire();
						    	PeekResult result = peeker.peek(member.getFile());
						    	Attributes attrs = result.getRootElementAttributes();
						    	String version = attrs.getValue("version"); 
						    	if (version.equals("2005-2")) {
						    		if (!z39DtbookRngSchValidator20052.isValid(member.getFile())) {
						    			hasErrors = true;
						    		}
						    	} else {
						    		if (!z39DtbookRngSchValidator20051.isValid(member.getFile())) {
						    			hasErrors = true;
						    		}
						        }
					    	} catch (PoolException e) {
					    		errors.add(new FilesetFatalException("Cannot create a Peeker pool", e));
					    		hasErrors = true;
					    	} catch (SAXException e) {
					    		errors.add(new FilesetFatalException("Error while peeking DTBook document", e));
					    		hasErrors = true;
							} catch (IOException e) {
								errors.add(new FilesetFatalException("Error while peeking DTBook document", e));
					    		hasErrors = true;
							}
					    }
					}
				}else if (member instanceof AudioFile){
					if(doAudioFileScouting) {
						if (member instanceof Mp3File){
							Mp3File mp3file = (Mp3File) member;
							try {
								mp3file.parse();
								if (mp3file.hasID3v2()) {
									errors.add(new FilesetFatalException("warning: mp3 file "+mp3file.getFile().getName()+" has ID3 tags"));
									hasErrors=true;
								}
								if (!mp3file.isMono()) {
									errors.add(new FilesetFatalException("warning: mp3 file "+mp3file.getFile().getName()+" is not mono"));
									hasErrors=true;
								}
								if (mp3file.isVbr()) {
									errors.add(new FilesetFatalException("mp3 file "+mp3file.getFile().getName()+" is VBR"));
									hasErrors=true;
								}
							} catch (Exception e) {
								errors.add(e);
								hasErrors=true;
							}
						}
					}
				}					
			} catch (ValidationException ve) {
				throw new DtbErrorScoutException(ve);
			}
		}//iter.hasNext
		
		if (doSmilDurationScouting){
			//test totaltime
			if (statedTotalTime!=null) {
				SmilClock calculatedTotalTime = new SmilClock(calculatedTotalTimeMillis);
				if (statedTotalTime.secondsValueRounded()!= calculatedTotalTime.secondsValueRounded()) {
					errors.add(new FilesetFatalException("found stated totaltime "+statedTotalTime.secondsValueRounded()+" but expected "+calculatedTotalTime.secondsValueRounded()));
					hasErrors=true;
				}
			}else{
				errors.add(new FilesetFatalException("no stated totaltime for this DTB"));
				hasErrors=true;
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
			Iterator uriator = member.getReferencedLocalMembers().iterator();				
			while (uriator.hasNext()) {
				String value = ((FilesetFile)uriator.next()).getFile().toURI().toString();
				if(!regex.matches(regex.URI_REMOTE,value)) {
					if(regex.matches(regex.URI_WITH_FRAGMENT,value)) {					
						//break the bare string up
						String uriFragment = getFragment(value); 
						String uriPath = stripFragment(value);
						//get the full URI of the member to resolve
						URI uri = member.getFile().toURI().resolve(uriPath);
						if(!uri.equals(cache)) {
							//the referenced member is other than last time
							cache=uri;
							//get the file instance from Fileset via the URI key
							referencedMember=(XmlFile)member.getReferencedLocalMember(uri);
							if (referencedMember==null){
								errors.add(new DtbErrorScoutException ("reference to nonexisting member in URI " + value+ " in file " + member.getFile().getName()));
								//return false;
								continue;
							}								
						}
						//check whether this colleague has the id value
						if(!referencedMember.hasIDValue(uriFragment)) {
							errors.add(new DtbErrorScoutException ("reference to nonexisting fragment in URI " + value + " in file " + member.getFile().getName()));
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
		//StringBuffer sb = new StringBuffer();
		StringBuilder sb = new StringBuilder();
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
		//StringBuffer sb = new StringBuffer();
		StringBuilder sb = new StringBuilder();
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
