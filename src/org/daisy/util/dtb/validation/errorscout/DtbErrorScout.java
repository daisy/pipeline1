package org.daisy.util.dtb.validation.errorscout;

import java.net.URI;
import java.util.Iterator;

import org.daisy.util.fileset.interfaces.Fileset;
/**
 * <p>Go scouting in a DTB for errors</p>
 * <p>Checks feasible validity of a DTB, does not claim to do full conformance checking</p>
 * <p>Only supports Single Volume DTBs (SVDTB)</p>
 * Usage example:
 *<pre><code>
 *  		DtbErrorScout errorScout = (DtbErrorScout) new DtbErrorScoutImpl(FilesetType.DAISY_202, DtbErrorScoutingLevel.MEDIUM);			
 *			if (errorScout.scout(new URI("file:/E:/mydtb/ncc.html"))) {		
 *				Iterator it = errorScout.getErrorsIterator();				
 *				while (it.hasNext()) {
 *					 Exception e = (Exception)it.next();		
 *					System.err.print(e.toString());
 *					if(e instanceof SAXParseException) {
 *						SAXParseException se = (SAXParseException) e;
 *						System.err.print(" at line: " +se.getLineNumber());
 *						System.err.print(" in entity: "+se.getSystemId());
 *					}  
 *					System.err.println("");
 *				}								
 *			}else{
 *				System.err.println("Scout found no errors");
 *			}	
 *</code></pre>
 * @author Markus Gylling
 */
public interface DtbErrorScout {
	
	/**
	 * Execute the scouting procedure
	 * @param manifest absolute URI of the manifest file (ncc, opf, x) being input port for the DTB fileset
	 * @return true if errors were encountered, false otherwise
	 * @throws DtbErrorScoutException
	 */
	public boolean scout(URI manifest) throws DtbErrorScoutException;
		
	/**
	 * @return an iterator over the &lt;Exception&gt; errors HashSet populated during the last execution of {@link #scout(URI)}
	 */
	public Iterator getErrorsIterator();
					
	/**
	 * @return the Fileset object built during the last execution of {@link #scout(URI)}
	 */
	public Fileset getFileset();
	
}
