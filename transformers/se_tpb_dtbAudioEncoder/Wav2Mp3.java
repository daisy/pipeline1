package se_tpb_dtbAudioEncoder;

import java.io.File;

/**
 * @author linus
 */
public abstract class Wav2Mp3 {

    private int bitrate = 128;
    private int sampleFrequency = 44100;
    private boolean stereo = true;
    
    public Wav2Mp3(int rate, int freq, boolean isStereo) throws EncodingException {
        // FIXME add error checking on these values
        bitrate = rate;
        sampleFrequency = freq;
        stereo = isStereo;
    }
    
    public abstract void encode(File wavFile, File mp3File) throws EncodingException;
    
    /**
     * @return Returns the bitrate.
     */
    public int getBitrate() {
        return bitrate;
    }
    /**
     * @return Returns the sampleFrequency.
     */
    public int getSampleFrequency() {
        return sampleFrequency;
    }
    /**
     * @return Returns the stereo.
     */
    public boolean getStereo() {
        return stereo;
    }

}
