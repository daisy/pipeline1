/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package se_tpb_dtbAudioEncoder;

import java.io.File;
import java.util.ArrayList;

import org.daisy.util.execution.Command;
import org.daisy.util.execution.ExecutionException;

/**
 * @author linus
 */
public class LameEncoder extends Wav2Mp3 {

    private static String lameCommand = "lame";
    
    /**
     * @param rate
     * @param freq
     * @param isStereo
     */
    public LameEncoder(int rate, int freq, boolean isStereo) {
        super(rate, freq, isStereo);
    }

    public void encode(File wavFile, File mp3File) throws EncodingException {
        int k = this.getSampleFrequency() / 1000;
        int h = this.getSampleFrequency() - (k*1000);
        
        ArrayList<String> arr = new ArrayList<String>();
        arr.add(lameCommand);
        arr.add("--quiet");
        arr.add("-h");
        if (!this.getStereo()) {
            arr.add("-m");
            arr.add("m");
            arr.add("-a");
        }
        arr.add("--cbr");
        arr.add("-b");
        arr.add(String.valueOf(this.getBitrate()));
        arr.add("--resample");
        arr.add(String.valueOf(k) + "." + "000".substring(String.valueOf(h).length()) + String.valueOf(h));
        arr.add(wavFile.getAbsolutePath());
        arr.add(mp3File.getAbsolutePath());
        
        try {
        	//System.err.println("Encoding: " + lameCommand + " " + arr.toString());
            Command.execute((arr.toArray(new String[arr.size()])));            
        } catch (ExecutionException e) {
            throw new EncodingException(e.getMessage());
        }
    }
    
    public static boolean setCommand(String command) {
        lameCommand = command;
        try {
            Command.execute(lameCommand + " --help", true);
            //System.err.println("Result: " + result);
        } catch (ExecutionException e) {
            return false;
        }
        return true;
    }

}
