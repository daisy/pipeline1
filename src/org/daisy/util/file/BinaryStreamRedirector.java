package org.daisy.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Redirects an input stream to an output stream in binary mode in its own thread.  
 * @author Linus Ericson
 */
public class BinaryStreamRedirector extends StreamRedirector {
    
    protected boolean closeInputStream = false;
    protected boolean closeOutputStream = false;
    
    /**
     * Constructor specifying an input stream and an output stream. By the default the
     * streams are not closed when the complete stream has been redirected.
     * @param inStream the input stream
     * @param outSteam the output stream
     */
    public BinaryStreamRedirector(InputStream inStream, OutputStream outSteam) {
        super(inStream, outSteam);
    }
    
    /**
     * Constructor specifying an input file and an output stream. The output stream is
     * by default not closed when the complete stream has been redirected.
     * @param inFile the input file
     * @param outSteam the output stream
     * @throws FileNotFoundException
     */
    public BinaryStreamRedirector(File inFile, OutputStream outSteam) throws FileNotFoundException {
        this(new FileInputStream(inFile), outSteam);
        setCloseInputStream(true);
    }
    
    /**
     * Constructor specifying an input stream and an output file. The input stream is
     * by default not closed when the complete stream has been redirected.
     * @param inStream the input stream
     * @param outFile the output file
     * @throws FileNotFoundException
     */
    public BinaryStreamRedirector(InputStream inStream, File outFile) throws FileNotFoundException {
        this(inStream, new FileOutputStream(outFile));
        setCloseOutputStream(true);
    }
    
    /**
     * Constructor specifying an input file and an output file.
     * @param inFile the input file
     * @param outFile the output file
     * @throws FileNotFoundException
     */
    public BinaryStreamRedirector(File inFile, File outFile) throws FileNotFoundException {
        this(new FileInputStream(inFile), new FileOutputStream(outFile));
        setCloseInputStream(true);
        setCloseOutputStream(true);
    }
    
    /**
     * Sets whether to close the input stream after the stream has been redirected.
     * @param close
     */
    public void setCloseInputStream(boolean close) {
        closeInputStream = close;
    }
    
    /**
     * Sets whether to close the output stream after the stream has been redirected.
     * @param close
     */
    public void setCloseOutputStream(boolean close) {
        closeOutputStream = close;
    }

    /*
     * (non-Javadoc)
     * @see org.daisy.util.file.StreamRedirector#run()
     */
    @Override
    public void run() {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        BufferedOutputStream bos = new BufferedOutputStream(outputStream);
        try {            
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = bis.read(buf)) != -1) {
                bos.write(buf, 0, i);
            }
        } catch (IOException e) {           
            e.printStackTrace();
        } finally {
            try {
                bos.flush();
            } catch (IOException e1) {               
            }
            if (closeInputStream) {
                try {
                    bis.close();
                } catch (IOException e) {
                }
            }
            if (closeOutputStream) {
                try {
                    bos.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    

}
