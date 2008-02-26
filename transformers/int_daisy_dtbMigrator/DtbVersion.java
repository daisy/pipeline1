package int_daisy_dtbMigrator;

/**
 * An enum for DTB versions. 
 * @author Markus Gylling
 */
public enum DtbVersion {
	D202 {		
		public String toString() {			
			return "Daisy 2.02";
		}
	},
	Z2002{		
		public String toString() {			
			return "Daisy/NISO Z39.86 2002";
		}
	},
	Z2005{		
		public String toString() {			
			return "Daisy/NISO Z39.86 2005";
		}
	};
}
