/*
 * Daisy Pipeline (C) 2005-2009 Daisy Consortium
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
package se_tpb_daisy202splitter;

import java.io.File;

/**
 * A set of prompts (audio and smil)
 * @author Linus Ericson
 */
public class PromptSet {

    private PromptVolume[] prompts = null;
    
    /**
     * Creates a new prompt set
     */
    public PromptSet() {
        prompts = new PromptVolume[10];
    }
    
    /**
     * Sets the specified prompt volume.
     * @param pos the volume number (starting at index 1)
     * @param promptVolume the prompt volume
     */
    public void setPromptVolume(int pos, PromptVolume promptVolume) {
        prompts[pos - 1] = promptVolume;
    }
    
    /**
     * Gets the specified prompt volume
     * @param pos the volume number (starting at index 1)
     * @return a prompt volume
     */
    public PromptVolume getPromptVolume(int pos) {
        return prompts[pos - 1];
    }
    
    /**
     * A prompt volume (combination of a smil file and an audio file)  
     */
    public static class PromptVolume {
        public File smilFile;
        public File audioFile;
    }
}
