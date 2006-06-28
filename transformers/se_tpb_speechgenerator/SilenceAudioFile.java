package se_tpb_speechgenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * @author Linus Ericson
 */
public class SilenceAudioFile {

    public static void writeSilentFile(File outputFile, long durationInMillis, File model) throws UnsupportedAudioFileException, IOException {
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(model);
        AudioFormat format = aff.getFormat();
        AudioInputStream ais = new SilenceAudioInputStream(format, durationInMillis);
        AudioSystem.write(ais, aff.getType(), outputFile);
    }
    
    public static void writeSilentOutputStream(OutputStream outStream, long durationInMillis, InputStream model) throws UnsupportedAudioFileException, IOException {
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(model);
        AudioFormat format = aff.getFormat();
        AudioInputStream ais = new SilenceAudioInputStream(format, durationInMillis);
        AudioSystem.write(ais, aff.getType(), outStream);
    }
    
    public static void main(String args[]) throws UnsupportedAudioFileException, IOException {
        File out = new File("d:/test.wav");
        File in = new File("U:/testbok1/fileset/speechgen00009.wav");
        SilenceAudioFile.writeSilentFile(out, 15748, in);
    }
}
