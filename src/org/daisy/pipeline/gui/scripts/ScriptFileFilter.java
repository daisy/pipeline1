package org.daisy.pipeline.gui.scripts;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.PoolException;
import org.xml.sax.SAXException;

/**
 * @author Romain Deltour
 * 
 */
public class ScriptFileFilter implements FileFilter {

    public boolean accept(File pathname) {
        if (pathname.isDirectory()) {
            return !".svn".equals(pathname.getName());
        } else {

            Peeker peeker = null;
            try {
                peeker = PeekerPool.getInstance().acquire();
                PeekResult result = peeker.peek(pathname);
                return "taskScript".equals(result.getRootElementLocalName());
            } catch (PoolException e) {
                // TODO Auto-generated catch block
                System.err.println("pool exception");
                e.printStackTrace();
            } catch (SAXException e) {
                // TODO Auto-generated catch block
                System.err.println("sax exception");
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.err.println("io exception");
                e.printStackTrace();
            }
        }
        return false;
    }
}
