package int_daisy_dtbMigrator;

/**
 * An enum for technically significant DTB types.
 * @author Markus Gylling
 */
public enum DtbType {
	AUDIO {		
		public String toString() {			
			return "Audio and NCC/NCX";
		}
	},
	TEXT_AUDIO {		
		public String toString() {			
			return "Text, Audio and NCC/NCX";
		}
	},
	TEXT {		
		public String toString() {			
			return "Text and NCC/NCX";
		}
	},	
}