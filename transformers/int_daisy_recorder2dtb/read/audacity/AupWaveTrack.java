package int_daisy_recorder2dtb.read.audacity;

import java.util.LinkedList;

/**
 * An AupWaveTrack consists of a list of AupAudioFile
 * @author Markus Gylling
 */
public class AupWaveTrack extends LinkedList<AupBlockFile> {
				
	/**
	 * Get the duration of this wave track, expressed in seconds.
	 */
	public double getDurationSeconds() {
		double s = 0;
		for(AupBlockFile af : this) {
			s += af.getDurationSeconds();
		}		
		return s; 		
	}
	
	/**
	 * Get the audiofile in which the inparam time (double seconds) occurs,
	 * relative to the timeline of the current wavetrack
	 */
	public AupBlockFile getAudioFile(double seconds) {
		double s = 0;
		for(AupBlockFile af:this) {
			s+= af.getDurationSeconds();
			if(seconds<=s) return af;
		}
		System.err.println("Warning: did not find a AupBlockFile that includes " 
				+ Double.toString(seconds) + "; total wavetrack time is " + Double.toString(this.getDurationSeconds()) +". Returning last AupBlockFile.");
		return this.getLast();		
	}
}
