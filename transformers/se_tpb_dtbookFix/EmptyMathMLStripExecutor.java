package se_tpb_dtbookFix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.xml.NamespaceReporter;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.DoctypeParser;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.daisy.util.xml.stax.StaxFilter;

/**
 * This executor removes the internal subset of the doctype declaration and
 * strips away any MathML namespace declarations if the document doesn't contain
 * any MathML elements. If there are MathML elements in the document, the source
 * is just copied to the result.
 * 
 * @author Linus Ericson
 */
public class EmptyMathMLStripExecutor extends Executor {
    
    private TransformerDelegateListener mTransformer = null;
    
    public EmptyMathMLStripExecutor(Map<String, String> parameters, String niceName, TransformerDelegateListener tdl) {
        super(parameters, niceName);
        mTransformer = tdl;
    }

    @Override
    boolean supportsVersion(String version) {
        return true;
    }
    
    @Override
    void execute(Source source, Result result) throws TransformerRunException {
        try {            
            // Is there any math in here?
            NamespaceReporter namespaceReporter = new NamespaceReporter(FilenameOrFileURI.toURI(source.getSystemId()).toURL());
            Set<String> namespaces = namespaceReporter.getNamespaceURIs();
            boolean containsMath =  namespaces.contains(Namespaces.MATHML_NS_URI);
            
            File inputFile = FilenameOrFileURI.toFile(source.getSystemId());
            File outputFile = FilenameOrFileURI.toFile(result.getSystemId());
            
            if (containsMath) {
                // Book contains math. Just copy input to output                                    
                FileUtils.copyFile(inputFile, outputFile);
            } else {
                Map<String, Object> xifProp = null;
                XMLInputFactory factory = null;
                FileInputStream fis = null;
                try {
                    xifProp = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
                    factory = StAXInputFactoryPool.getInstance().acquire(xifProp);
                    factory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
                    fis = new FileInputStream(inputFile);
                    XMLEventReader reader = factory.createXMLEventReader(fis);
                    OutputStream outstream = new FileOutputStream(outputFile);
                    StaxFilter filter = new MathStripFilter(reader, outstream);
                    filter.filter();
                    filter.close();
                } catch (Exception e) {
                    mTransformer.delegateMessage(this, mTransformer.delegateLocalize(
                            "MATH_STRIP_FAIL", new String[] { e.getMessage() }),
                            MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM, null);
                    try {
                        FileUtils.copy(inputFile, outputFile);
                    } catch (IOException ioe) {
                        throw new TransformerRunException(ioe.getMessage(), ioe);
                    }
                }finally{
                    if(fis!=null) try {fis.close();} catch (IOException e) {}
                    StAXInputFactoryPool.getInstance().release(factory, xifProp);
                }
            }
        } catch (MalformedURLException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (IOException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (XMLStreamException e) {
            throw new TransformerRunException(e.getMessage(), e);
        }
    }
    
    /**
     * Inner class that strips out the internal subset of the doctype declaration
     * and removes any MathML namespace declarations.
     */
    private class MathStripFilter extends StaxFilter {

        public MathStripFilter(XMLEventReader xer, OutputStream outStream) throws XMLStreamException {
            super(xer, outStream);
        }

        @Override
        protected DTD dtd(DTD event) {
            // Create the new doctype declaration. There should be a helper class that does this...
            DoctypeParser doctypeParser = new DoctypeParser(event.getDocumentTypeDeclaration());
            StringBuilder builder = new StringBuilder("<!DOCTYPE ");
            builder.append(doctypeParser.getRootElem());
            boolean hasPublicID = false;
            String publicID = doctypeParser.getPublicId();
            if ((publicID != null) && (publicID.length() > 0)) {
                builder.append(" PUBLIC \"");
                builder.append(publicID);
                builder.append("\"");
                hasPublicID = true;
            }
            String systemID = doctypeParser.getSystemId();
            if ((systemID != null) && (systemID.length() > 0)) {
                if (!hasPublicID) {
                    builder.append(" SYSTEM");
                }
                builder.append(" \"");
                builder.append(systemID);
                builder.append("\"");
            }
            // No internal subset added here...            
            builder.append(">");            
            DTD dtd = getEventFactory().createDTD(builder.toString());
            return dtd;
        }

        @Override
        protected StartElement startElement(StartElement event) {
            // Iterate through all namespace declarations. If a MathML namespace
            // declaration is found, make sure it isn't copied.
            Iterator iter = event.getNamespaces();
            List<Namespace> namespaces = new ArrayList<Namespace>();
            boolean mathNsFound = false;
            while (iter.hasNext()) {
                Namespace ns = (Namespace)iter.next();
                if (Namespaces.MATHML_NS_URI.equals(ns.getValue())) {
                    mathNsFound = true;
                } else {
                    namespaces.add(ns);
                }
            }
            if (mathNsFound) {
                return getEventFactory().createStartElement(event.getName(), event.getAttributes(), namespaces.iterator());
            }
            return event;
        }
        
    }

}
