package org.daisy.dmfc.qmanager;


/**
 * Status for Job object
 * @author Laurie Sherve
 * @author Linus Ericson
 */
public class Status {

		public final static int WAITING =1;
		public final static int IN_PROGRESS=2;
		public final static int COMPLETED=3;
		public final static int FAILED =4;
		
		public static String getStatusString(int status){
			String str= "";
			switch (status){
				case 1:
					str= "Waiting";
					break;
				case 2:
					str = "In Progress";
					break;
				case 3:
					str = "Completed";
					break;
				case 4:
					str = "Failed. See Log";
					break;
				default:
					str = "Error. See log";
					break;
			}
			return str;
		}
		
}
