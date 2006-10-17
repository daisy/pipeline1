package int_daisy_dtbookMigrator;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A registry for handling Dtbook versions. Tells which dtbook versions are
 * currently supported by the migration suite, and provides assoiated XSLTs for moving a certain version
 * up one notch on the upwards migration ladder.
 * @author Markus Gylling
 */
public class DtbookMigratorXSLTManager {
	
	private Map registry = new HashMap(); //String(versionValue), String(XSLTfilename)
	//private File mXSLTDir = null;
	
	DtbookMigratorXSLTManager() {		
		registry.put("1.1.0", "DTBook110_to_DTBook2005_1.xsl");
		registry.put("2005-1", "DTBook2005_1_to_DTBook2005_2.xsl");
		//mXSLTDir = xsltDir;
	}

	boolean supportsInputVersion(String inputVersion) {
		return registry.containsKey(inputVersion);		
	}

	boolean supportsOutputVersion(String outputVersion) {
		return outputVersion.matches("2005-1|2005-2");		
	}
	
	URL getStylesheet(String inputVersion) {		
		return this.getClass().getClassLoader().getResource((String)registry.get(inputVersion));		
	}
	
	/**
	 * @return the version string representing the latest available output version
	 */
	String getTopmostOutputVersion() {
		return "2005-2";
	}
	
	
	String getOutputVersion(String XSLTFileName) {
		if(XSLTFileName.equals("DTBook110_to_DTBook2005_1.xsl")){
			return "2005-1";
		}else if (XSLTFileName.equals("DTBook2005_1_to_DTBook2005_2.xsl")){
			return "2005-2";
		}
		throw new IllegalArgumentException(XSLTFileName);
	}
	
	String getInputVersion(String XSLTFileName) {
		if(XSLTFileName.equals("DTBook110_to_DTBook2005_1.xsl")){
			return "1.1.0";
		}else if (XSLTFileName.equals("DTBook2005_1_to_DTBook2005_2.xsl")){
			return "2005-1";
		}
		throw new IllegalArgumentException(XSLTFileName);
	}
}
