package int_daisy_dtbMigrator;

/**
 * An enum for technically significant DTB types.
 * @author Markus Gylling
 */
public enum DtbType {
	AUDIO_NCX{		
		public String toString() {			
			return "Audio and NCC/NCX";
		}
	},
	TEXT_AUDIO_NCX {		
		public String toString() {			
			return "Text, Audio (full or partial) and NCC/NCX";
		}
	},
	TEXT_NCX {		
		public String toString() {			
			return "Text and NCC/NCX";
		}
	},	
}