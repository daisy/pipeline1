package int_daisy_dtbMigrator;

import java.util.Iterator;

import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.audio.AudioFile;
import org.daisy.util.fileset.interfaces.xml.TextualContentFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202NccFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986NcxFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986OpfFile;

/**
 * Describe the nature of a DTB.
 * @author Markus Gylling
 */
public class DtbDescriptor {	
	private final DtbVersion mVersion;
	private final DtbType mType;

	public DtbDescriptor(DtbVersion version, DtbType type) {
		if(version==null||type==null) throw new IllegalArgumentException();
		mVersion = version;
		mType = type;		
	}

	public DtbVersion getVersion() {
		return mVersion;
	}

	public DtbType getType() {
		return mType;
	}
	
	@Override
	public String toString() {
		return mVersion.toString() + " " + mType.toString();		
	}
	
	/**
	 * Helper method to retrieve the version of a DTB.
	 * @return the DtbVersion, or null if it could not be detected.
	 */
	public static DtbVersion getVersion(Fileset fileset) {
		if(fileset.getManifestMember() instanceof D202NccFile) {
			return DtbVersion.D202;
		} else if(fileset.getManifestMember() instanceof Z3986OpfFile) {
			Z3986OpfFile opf = (Z3986OpfFile) fileset.getManifestMember();
			if(opf.getMetaDcFormat()!=null){
				if(opf.getMetaDcFormat().contains("2005")) {
					return DtbVersion.Z2005;
				}else if(opf.getMetaDcFormat().contains("2002")) {
					return DtbVersion.Z2002;
				}
			}
		}		
		return null;
	}
	
	/**
	 * Helper method to retrieve the type of a DTB.
	 * @return the DtbType, or null if it could not be detected.
	 */
	public static DtbType getType(Fileset fileset) {
		Iterator i = fileset.getLocalMembers().iterator();
		boolean hasAudio = false;
		boolean hasText = false;		
		boolean hasNavigation = false;
		while(i.hasNext()) {
			FilesetFile file = (FilesetFile)i.next();
			if(file instanceof AudioFile) {
				hasAudio = true;
			}else if(file instanceof TextualContentFile) {
				hasText = true;
			}else if(file instanceof D202NccFile||file instanceof Z3986NcxFile) {
				hasNavigation = true;
			}
			if(hasAudio && hasText && hasNavigation) break;
		}
		
		if(hasNavigation) {
			if(hasAudio && !hasText) {
				return DtbType.AUDIO_NCX;
			}else if(!hasAudio && hasText) {
				return DtbType.TEXT_NCX;
			}else if(hasAudio && hasText) {
				return DtbType.TEXT_AUDIO_NCX;
			}
		}
		return null;
		
	}
	
}
