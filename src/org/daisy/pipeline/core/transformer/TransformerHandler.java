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
package org.daisy.pipeline.core.transformer;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.UserEvent;
import org.daisy.pipeline.core.script.Job;
import org.daisy.pipeline.core.script.Task;
import org.daisy.pipeline.exception.TdfParseException;
import org.daisy.pipeline.exception.TransformerDisabledException;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.i18n.I18n;

/**
 * Handles descriptions of and initiates the execution of Transformers. The
 * TransformerHandler class is responsible for parsing the Transformer
 * description file (TDF), finding the Java class associated with the
 * Transformer and loading it. It is also responsible for verifying that the
 * parameters sent from the execution script matches the parameters described in
 * the TDF.
 * 
 * @author Linus Ericson
 * @author Romain Deltour
 */
public class TransformerHandler implements TransformerInfo {

    private I18n i18n = new I18n();

    private TransformerLoader mTransformerLoader;

    private File mTransformerDirectory;
    private String mNiceName;
    private String mDescription;
    private String mPackageName;
    private List<Parameter> mParameters = new Vector<Parameter>();
    private URI mDocumentationURI = null;

    public TransformerHandler(TransformerLoader loader)
	    throws TransformerDisabledException {
	mTransformerLoader = loader;
	try {
	    mTransformerDirectory = loader.getTransformerDir();

	    TdfParser parser = new TdfParser();
	    parser.parseTdf(loader.getTdfUrl(), mTransformerDirectory);

	    if (!parser.isPlatformSupported()) {
		throw new TransformerDisabledException(i18n
			.format("PLATFORM_CHECK_FAILED"));
	    }

	    mNiceName = parser.getNicename();
	    mDescription = parser.getDescription();
	    mDocumentationURI = parser.getDocumentationUri();
	    mParameters = parser.getParameters();

	    loader.init(parser.getClassname(), parser.getJars(), mNiceName);
	} catch (TdfParseException e) {
	    throw new TransformerDisabledException(e.getMessage(), e);
	} catch (MalformedURLException e) {
	    throw new TransformerDisabledException(i18n
		    .format("TDF_IO_EXCEPTION"), e);
	}
    }

    /**
     * Run the Transformer associated with this handler.
     * 
     * @param runParameters
     *            parameters to the Transformer
     * @param job The Job that includes this transformer execution
     * @return <code>true</code> if the run was successful, <code>false</code>
     *         otherwise
     */
    public boolean run(Map<String, String> runParameters, boolean interactive,
	    Task task, Job job) throws TransformerRunException {
	Transformer transformer = null;
	try {
	    transformer = mTransformerLoader.createTransformer(interactive,
		    task, this);
	} catch (IllegalArgumentException e) {
	    throw new TransformerRunException(i18n
		    .format("TRANSFORMER_ILLEGAL_ARGUMENT"), e);
	} catch (InstantiationException e) {
	    throw new TransformerRunException("Instantiation problems", e);
	} catch (IllegalAccessException e) {
	    throw new TransformerRunException(i18n
		    .format("TRANSFORMER_ILLEGAL_ACCESS"), e);
	} catch (InvocationTargetException e) {
	    throw new TransformerRunException(i18n
		    .format("TRANSFORMER_INVOCATION_PROBLEM"), e);
	}

	EventBus eventBus = (job!=null)?EventBus.REGISTRY.get(job):null;
	if (eventBus!=null)
	    eventBus.subscribe(transformer, UserEvent.class);
	boolean res = false;
	try {
	    res = transformer.executeWrapper(job, runParameters,
		    mTransformerDirectory);
	} finally {
	    if (eventBus!=null)
		eventBus.unsubscribe(transformer, UserEvent.class);
	}
	return res;
    }

    public String getName() {
	return getNiceName();
    }

    public String getNiceName() {
	return mNiceName;
    }

    public String getPackageName() {
	return mPackageName;
    }

    public String getDescription() {
	return mDescription;
    }

    public File getTransformerDir() {
	return mTransformerDirectory;
    }

    public URI getDocumentation() {
	return mDocumentationURI;
    }

    /**
     * Gets a collection of the parameters of the Transformer.
     */
    public Collection<Parameter> getParameters() {
	return mParameters;
    }

    public String getParameterType(String parameterName) {
	for (Iterator<Parameter> it = mParameters.iterator(); it.hasNext();) {
	    Parameter param = it.next();
	    if (parameterName.equals(param.getName())) {
		return param.getType();
	    }
	}
	return null;
    }

}
