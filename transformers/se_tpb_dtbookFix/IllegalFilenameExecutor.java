package se_tpb_dtbookFix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.daisy.util.xml.stax.StaxFilter;

/**
 * This executor renames files with illegal (according to the z3986 spec) characters.
 * This limitation only applies to DTBs, so the executor only needs to be run when
 * creating a DTB.
 * The only allowed characters are A-Z, a-z, 0-9, '.', '_' and '-'.
 * 
 * http://www.daisy.org/z3986/2005/Z3986-2005.html#Allowed-Char
 * @author Linus Ericson
 */
public class IllegalFilenameExecutor extends Executor {
    
    private TransformerDelegateListener mTransformer = null;
    
    private Map<URI,URI> renameMapping = new HashMap<URI,URI>();
    
    private File inputDir;
    private File outputDir;

    public IllegalFilenameExecutor(Map<String, String> parameters, String niceName, TransformerDelegateListener tdl) {
        super(parameters, niceName);
        inputDir = FilenameOrFileURI.toFile(parameters.get("input")).getParentFile();
        outputDir = FilenameOrFileURI.toFile(parameters.get("output")).getParentFile();
        mTransformer = tdl;
    }

    @Override
    boolean supportsVersion(String version) {        
        return true;
    }
    
    @Override
    void execute(Source source, Result result) throws TransformerRunException {
        File inputFile = new EFile(FilenameOrFileURI.toFile(source.getSystemId()));
        File outputFile = FilenameOrFileURI.toFile(result.getSystemId());
                
        Map<String, Object> xifProp = null;
        XMLInputFactory factory = null;
        FileInputStream fis = null;
        try {
            // Setup StAX filter
            xifProp = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(
                    false);
            factory = StAXInputFactoryPool.getInstance().acquire(xifProp);
            factory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver
                    .getInstance()));
            fis = new FileInputStream(inputFile);
            XMLEventReader reader = factory.createXMLEventReader(fis);
            OutputStream outstream = new FileOutputStream(outputFile);
            StaxFilter filter = new IllegalFilenameFilter(reader, outstream);
            filter.filter();
            filter.close();
            
            // Copy the files in the renameMapping
            for (URI fromUri : renameMapping.keySet()) {
                URI toUri = renameMapping.get(fromUri);
                fromUri = inputDir.toURI().resolve(fromUri);
                toUri = outputDir.toURI().resolve(toUri);
                File fromFile = new File(fromUri);
                File toFile = new File(toUri);
                FileUtils.createDirectory(toFile.getParentFile());
                FileUtils.copyFile(fromFile, toFile);
                mTransformer.delegateMessage(this, mTransformer.delegateLocalize(
                        "ILLEGAL_FILENAME_RENAME", new String[] { fromFile.getName(), toFile.getName() }),
                        MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mTransformer.delegateMessage(this, mTransformer.delegateLocalize(
                    "ILLEGAL_FILENAME_FAIL", new String[] { e.getMessage() }),
                    MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM, null);
            try {
                FileUtils.copy(inputFile, outputFile);
            } catch (IOException ioe) {
                throw new TransformerRunException(ioe.getMessage(), ioe);
            }
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                }
            StAXInputFactoryPool.getInstance().release(factory, xifProp);
        }
        
    }
    
    /**
     * This is a StAX filter that makes sure all filenames contain legal characters.
     */
    private class IllegalFilenameFilter extends StaxFilter {
        
        private FilesetRegex regex = FilesetRegex.getInstance();
        private Pattern allowedCharsPattern = Pattern.compile("[A-Za-z0-9._-]+");
        
        private int renameCounter = 0;
        
        public IllegalFilenameFilter(XMLEventReader xer, OutputStream outStream)
                throws XMLStreamException {
            super(xer, outStream);
        }

        @Override
        protected StartElement startElement(StartElement event) {
            Iterator iter = event.getAttributes();
            List<Attribute> attrs = new ArrayList<Attribute>();
            while (iter.hasNext()) {
                Attribute attr = (Attribute) iter.next();
                StringBuilder prefixAndLocal = new StringBuilder();
                String prefix = attr.getName().getPrefix();
                if (prefix != null && !"".equals(prefix)) {
                    prefixAndLocal.append(prefix).append(":");
                }
                prefixAndLocal.append(attr.getName().getLocalPart());
                if (regex.matches(regex.DTBOOK_ATTRIBUTES_WITH_URIS,
                        prefixAndLocal.toString())
                        || regex.matches(
                                regex.DTBOOK_COMPOUND_ATTRIBUTES_WITH_URIS,
                                prefixAndLocal.toString())) {
                    String uriString = attr.getValue();
                    uriString = this.handleAttribute(uriString);
                    attrs.add(getEventFactory().createAttribute(attr.getName(),
                            uriString));
                } else {
                    attrs.add(attr);
                }
            }

            return getEventFactory().createStartElement(event.getName(),
                    attrs.iterator(), event.getNamespaces());
        }
        
        private String handleAttribute(String value) {
            try {
                URI uri = new URI(value).normalize();
                if (uri.isAbsolute()) {
                    return value;
                }
                String path = uri.getRawPath();
                int pos = path.lastIndexOf("/");
                String lastPart = path.substring(pos!=-1? pos + 1 : 0);
                Matcher matcher = allowedCharsPattern.matcher(lastPart);
                if (!matcher.matches()) {
                    path = uri.getPath();
                    pos = path.lastIndexOf("/");
                    lastPart = path.substring(pos!=-1? pos + 1 : 0);
                    if (renameMapping.containsKey(uri)) {
                        return renameMapping.get(uri).toString();
                    } else {
                        String newName = getNewName(lastPart);
                        String newPath = path.substring(0, pos!=-1? pos + 1 : 0) + newName;
                        URI newUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), newPath, uri.getQuery(), uri.getFragment());
                        renameMapping.put(uri, newUri);                        
                        return newUri.toString();
                    }                    
                } else {
                    return value;
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }
        
        private String getNewName(String oldName) {
            StringBuilder sb = new StringBuilder("renamed");
            String counter = String.valueOf(++renameCounter);
            sb.append("00000".substring(counter.length()));
            sb.append(counter);
            int index = oldName.lastIndexOf(".");
            if (index != -1) {
                sb.append(oldName.substring(index));
            }
            return sb.toString();            
            //String temp = oldName.replaceAll("%[A-Fa-f0-9]{2}", "_");
            //return temp.replaceAll("[^A-Za-z0-9._-]", "_");
        }
        
        
        
    }

}
