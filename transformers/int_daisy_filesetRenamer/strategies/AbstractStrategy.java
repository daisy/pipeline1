package int_daisy_filesetRenamer.strategies;

import int_daisy_filesetRenamer.FilesetRenamingException;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.i18n.CharUtils;

/**
 * Base non-reentrant abstract class for creating a renaming table 
 * ("naming strategy") for a fileset.
 * @author Markus Gylling
 */
public abstract class AbstractStrategy implements RenamingStrategy {
	protected Fileset inputFileset = null;
	private boolean isValidated = false;
	protected HashMap namingStrategy = new HashMap(); // <URI>,<URI>
	protected List typeRestrictions = new ArrayList();
	protected String namePrefix = "";
	
	public void createStrategy() {
		//populate the URI(old), URI(new) map.
		this.namingStrategy.clear();
		for (Iterator iter = this.inputFileset.getLocalMembersURIs().iterator(); iter.hasNext();) {
			URI uri = (URI)iter.next();
			FilesetFile f = this.inputFileset.getLocalMember(uri);
			if(this.isTypeEnabled(f)) {
				//create a new name
				File ret = new File(f.getFile().getParentFile(),createNewName(f));				
				this.namingStrategy.put(uri,ret.toURI());
			}else{
				//let name be the same
				this.namingStrategy.put(uri,uri);
			}			
		}
	}
	
	/**
	 * For subclasses to override.
	 * Contract for overriders is to populate the namingStrategy HashMap &lt;URI&gt;,&lt;URI&gt;,
	 * including names that do not change, including types that are filtered.
	 */
	public abstract String createNewName(FilesetFile f);
	
	
	public boolean validate() throws FilesetRenamingException {		
		isValidated = true;		
		if (!namingStrategy.isEmpty()) {
			URI curValue;
			URI curKey;
			int i = -1;
			for (Iterator iter = namingStrategy.keySet().iterator(); iter.hasNext();) {
				i++;
				URI value = (URI) namingStrategy.get(iter.next());
				int k = -1;
				for (Iterator iter2 = namingStrategy.keySet().iterator(); iter2.hasNext();) {					
					k++;
					curKey = (URI)iter2.next();
					curValue = (URI) namingStrategy.get(curKey);
					if(i!=k) {
						if(value.equals(curValue)) {
							throw new FilesetRenamingException("duplicate output name: " + value.toString());														
						}						
						if(value.equals(curKey)) {
							throw new FilesetRenamingException("output name collides with input name of other member: " + value.toString());													
						}
					}
				}								
			}			
		} else { 
			throw new FilesetRenamingException("strategy not set");
		}//if (!strategy.isEmpty())
		
		return true;
	}

	public void setTypeRestriction(Class filesetFileInterface) {
		typeRestrictions.add(filesetFileInterface);				
	}

	public void setTypeRestriction(List filesetFileInterfaces) {
		typeRestrictions.addAll(filesetFileInterfaces);				
	}
	
	public void setDefaultPrefix(String prefix) {
		this.namePrefix = CharUtils.toPrintableAscii(CharUtils.toNonWhitespace(prefix));					
	}
	
	protected boolean isTypeEnabled(FilesetFile file) {
		//if no restriction set, always true
		if(this.typeRestrictions.isEmpty()) return true;			
		//cast to see if inparam file is related to a member
		//of the restriction list
		for (int i = 0; i < typeRestrictions.size(); i++) {			
			try{
				Class test = (Class)typeRestrictions.get(i);
				Object cast = test.cast(file);
				return true;  //we didnt get an exception...				
			}catch (Exception e) {
				//just continue the loop	
			}					
		}				
		return false;
	}
	
	public String getNewLocalName(FilesetFile file) {		
		return this.getNewLocalName(file.getFile().toURI());
	}

	public String getNewLocalName(URI filesetFileURI) {		
		URI newURI = (URI)namingStrategy.get(filesetFileURI);
		if(null != newURI) {
			return (new File(newURI)).getName();
		}
		return null;
	}

	public void setInputFileset(Fileset fileset) {
		this.inputFileset = fileset;		
	}
	
	public Fileset getInputFileset() {
		return this.inputFileset;		
	}
	
	public Iterator getIterator(){
		return this.namingStrategy.keySet().iterator();
	}
	
}
