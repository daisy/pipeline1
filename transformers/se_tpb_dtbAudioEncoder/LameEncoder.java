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
     * @throws EncodingException
     */
    public LameEncoder(int rate, int freq, boolean isStereo) throws EncodingException {
        super(rate, freq, isStereo);
    }

    public void encode(File wavFile, File mp3File) throws EncodingException {
        int k = this.getSampleFrequency() / 1000;
        int h = this.getSampleFrequency() - (k*1000);
        
        ArrayList arr = new ArrayList();
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
        	System.err.println("Encoding: " + lameCommand + " " + arr.toString());
            Command.execute((String[])(arr.toArray(new String[arr.size()])));            
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
