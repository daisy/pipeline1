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

/**
 * @author linus
 */
public abstract class Wav2Mp3 {

    private int bitrate = 128;
    private int sampleFrequency = 44100;
    private boolean stereo = true;
    
    public Wav2Mp3(int rate, int freq, boolean isStereo) {
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
