package se_tpb_dtbAudioEncoder;

import java.io.File;

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
        StringBuffer params = new StringBuffer();
        params.append("--quiet ");
        //params.append("-h ");        
        params.append("-f ");
        if (!this.getStereo()) {
            params.append("-m m ");
            params.append("-a ");
        }
        
        params.append("--cbr ");
        params.append("-b " + this.getBitrate() + " ");
        
        int k = this.getSampleFrequency() / 1000;
        int h = this.getSampleFrequency() - (k*1000);

        params.append("--resample " + k + "." + "000".substring(String.valueOf(h).length()) + h);
        System.err.println("Encoding: " + lameCommand + " " + params.toString() + " \"" + wavFile.getAbsolutePath() + "\" \"" + mp3File.getAbsolutePath() + "\"");
        try {
            Command.execute(lameCommand + " " + params.toString() + " \"" + wavFile.getAbsolutePath() + "\" \"" + mp3File.getAbsolutePath() + "\"");
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
