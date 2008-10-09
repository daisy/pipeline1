/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
package org.daisy.util.file;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.daisy.util.text.URIUtils;

/**
 * Read filenames or file URIs and output <code>File</code> or
 * <code>URI</code> objects. 
 * @author Linus Ericson
 */
public class FilenameOrFileURI {
    
    private static Pattern schemePattern = Pattern.compile("[a-z]{2,}:.*");
    
    /**
     * Convert a filename or a file URI to a <code>File</code>
     * object.
     * @param filenameOrFileURI a filename or a file URI
     * @return a <code>File</code> object
     */
    public static File toFile(String filenameOrFileURI) {
        try {
            if (hasScheme(filenameOrFileURI)) {
                try {                    
                    File f = new File(URIUtils.createURI(filenameOrFileURI));
                    return f;
                } catch (URISyntaxException e) {
                    e.printStackTrace();                   
                }
                return null;
            } 
            return new File(filenameOrFileURI);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Convert a filename or a file URI to a <code>URI</code>
     * object.
     * @param filenameOrFileURI a filename or a file URI
     * @return a <code>URI</code> object
     */
    public static URI toURI(String filenameOrFileURI) {
        File file = toFile(filenameOrFileURI);
        return file==null?null:file.toURI();
    }
    
    /**
     * Checks if a path starts with  scheme identifier. If it
     * does, it is assumed to be a URI.
     * @param test the string to test.
     * @return <code>true</code> if the specified string starts with a scheme
     * identitier, <code>false</code> otherwise. 
     */
    private static boolean hasScheme(String test) {
        return schemePattern.matcher(test).matches();        
    }    
}
