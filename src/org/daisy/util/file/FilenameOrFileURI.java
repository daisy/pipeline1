/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.util.file;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

/**
 * Read filenames or file URIs and output <code>File</code> or
 * <code>URI</code> objects. 
 * @author Linus Ericson
 */
public class FilenameOrFileURI {
    
    private static Pattern schemePattern = Pattern.compile("[a-z]{2,}:.*");
    
    public static File toFile(String filenameOrFileURI) {
        try {
            if (hasScheme(filenameOrFileURI)) {
                try {                    
                    File f = new File(new URI(filenameOrFileURI));
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
    
    public static URI toURI(String filenameOrFileURI) {
        File file = toFile(filenameOrFileURI);
        return file==null?null:file.toURI();
    }
    
    private static boolean hasScheme(String test) {
        return schemePattern.matcher(test).matches();        
    }
    
    /* Test methods */
    
    private static void test(String tst) {
        File res = FilenameOrFileURI.toFile(tst);
        URI res2 = FilenameOrFileURI.toURI(tst);
        System.err.println("\n" + tst + "\n\t-> " + (res==null?"null":res.toString()));
        System.err.println("\t-> " + (res2==null?"null":res2.toString()));
    }
    
    public static void main(String args[]) {
        test("C:\\Program Files\\jEdit 4.2\\jedit.jar");
        test("C:/Program Files/jEdit 4.2/jedit.jar");
        test("file:///C:/Program%20Files/eclipse-3.0.2/workspace/dmfc/doc/developer-primer.html");        
        test("/tpb/spool/arkiv/C13500_C13999/C13930/ncc.html");
        test("file:///tpb/spool/arkiv/C13500_C13999/C13930/ncc.html");
        test("file:/c:/tpb/spool/arkiv/C13500_C13999/C13930/ncc.html");
        test("http://www.google.com");
    }
    
}
