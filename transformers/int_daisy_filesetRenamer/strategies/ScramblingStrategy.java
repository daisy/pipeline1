package int_daisy_filesetRenamer.strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.daisy.util.fileset.interfaces.FilesetFile;

/**
 * A naming strategy implementation that scrambles 
 * names given instance properties @author Markus Gylling
 */
public class ScramblingStrategy extends AbstractStrategy {
	private Random random = new Random();
	private List usedNames = new ArrayList();
	private static StringBuilder sb = new StringBuilder();
	private String newName = null;
	
	public ScramblingStrategy(){
		
	}
	
	/**
	 * Impl of the abstract method in superclass.
	 */
	public String createNewName(FilesetFile f) {
		sb.delete(0,sb.length());
		sb.append(this.namePrefix); //may be ""
		sb.append(scramble(f.getNameMinusExtension()));
		sb.append('.');
		sb.append(f.getExtension());		
		return sb.toString();		
	}

	private String scramble(String nameMinusExtension) {		
		//if input fileset has 100000 members or more
		//this will loop forever... kewl innit.
    	do {
    		newName = Integer.toString(random.nextInt(100000));
    		if (!usedNames.contains(newName)){
    			usedNames.add(newName);
    			return newName;    			
    		}
    	} while (true); 
	}
	
}
