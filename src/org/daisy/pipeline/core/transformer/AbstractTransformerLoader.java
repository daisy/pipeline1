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
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.CoreMessageEvent;
import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.script.Task;
import org.daisy.pipeline.exception.TransformerDisabledException;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.i18n.I18n;

/**
 * @author Romain Deltour
 */
public abstract class AbstractTransformerLoader implements TransformerLoader {

    private InputListener inputListener;
    private Class<?> transformerClass;
    private Constructor<?> transformerConstructor;
    private I18n i18n = new I18n();

    public AbstractTransformerLoader(InputListener inputListener) {
	this.inputListener = inputListener;
    }

    /**
     * @param classname
     * @param jars
     * @param niceName
     * @throws ClassNotFoundException
     */
    public void init(String classname, Collection<String> jars, String nicename)
	    throws TransformerDisabledException {
	try {
	    loadClass(classname, getClassLoader(jars), nicename);
	    // Check the transformer class has a known constructor
	    checkConstructor();
	    // Do dependency checks in the class associated with the Transformer
	    if (!checkSupported()) {
		throw new TransformerDisabledException(i18n
			.format("TRANSFORMER_NOT_SUPPORTED"));
	    }
	} catch (ClassNotFoundException e) {
	    throw new TransformerDisabledException(i18n
		    .format("CANNOT_CREATE_TRANSFORMER_CLASS"), e);
	} catch (NoSuchMethodException e) {
	    throw new TransformerDisabledException(i18n
		    .format("NOSUCHMETHOD_IN_TRANSFORMER"), e);
	} catch (IOException e) {
	    throw new TransformerDisabledException(i18n
		    .format("TDF_IO_EXCEPTION"), e);
	}
    }

    /**
     * Creates an instance object of the Transformer class.
     * 
     * @param interactive
     * @return a <code>Transformer</code> object
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Transformer createTransformer(boolean interactive, Task task,
	    TransformerInfo info) throws IllegalArgumentException,
	    InstantiationException, IllegalAccessException,
	    InvocationTargetException {

	/*
	 * mg20070327; we check which constructor (old with listener set, or new
	 * without) this transformer supports.
	 */

	List<Object> params = new LinkedList<Object>();
	if (transformerConstructor.getParameterTypes().length == 2) {
	    params.add(inputListener);
	    params.add(Boolean.valueOf(interactive));
	} else {
	    params.add(inputListener);
	    params.add(new HashSet<Object>()); // this is the dummy no longer
	    // used but
	    // kept for Transformer backwards
	    // compatibility
	    params.add(Boolean.valueOf(interactive));
	}

	Transformer trans = (Transformer) transformerConstructor
		.newInstance(params.toArray());
	trans.setTransformerInfo(info);
	trans.setLoadedFromJar(isLoadedFromJar());
	trans.setTask(task);
	return trans;
    }

    /**
     * @return
     * @throws MalformedURLException
     */
    public abstract URL getTdfUrl() throws MalformedURLException;

    /**
     * @return
     */
    public abstract File getTransformerDir();

    /**
     * @param classname
     * @param classloader
     * @return
     * @throws ClassNotFoundException
     */
    private void loadClass(String classname, ClassLoader classloader,
	    String nicename) throws ClassNotFoundException {

	EventBus.getInstance().publish(
		new CoreMessageEvent(this, i18n.format("LOADING_TRANSFORMER",
			nicename, classname), MessageEvent.Type.DEBUG));
	Class<?> clazz = Class.forName(classname, true, classloader);

	EventBus.getInstance().publish(
		new CoreMessageEvent(this, i18n.format("TRANSFORMER_LOADED",
			clazz.getProtectionDomain().getCodeSource()
				.getLocation()), MessageEvent.Type.DEBUG));
	transformerClass = clazz;
    }

    /**
     * Makes sure the constructor we wish to use exists.
     * 
     * @throws NoSuchMethodException
     */
    private void checkConstructor() throws NoSuchMethodException {
	/*
	 * mg20070327: first we check for the 'new' constructor that doesnt take
	 * the deprecated set of EventListener
	 */
	Class<?>[] params = { InputListener.class, Boolean.class };
	try {
	    transformerConstructor = transformerClass.getConstructor(params);
	} catch (NoSuchMethodException nsme) {
	    Class<?>[] params2 = { InputListener.class, Set.class,
		    Boolean.class };
	    transformerConstructor = transformerClass.getConstructor(params2);
	}

    }

    /**
     * Calls the static method <code>isSupported</code> in the Transformer class
     * and returns the result.
     * 
     * @return
     * @throws NoSuchMethodException
     * @throws TransformerRunException
     */
    private boolean checkSupported() throws NoSuchMethodException,
	    TransformerDisabledException {
	Method isSupportedMethod = transformerClass.getMethod("isSupported",
		(Class[]) null);
	Boolean result;
	try {
	    result = (Boolean) isSupportedMethod.invoke(null, (Object[]) null);
	} catch (IllegalArgumentException e) {

	    throw new TransformerDisabledException(
		    "Cannot run static isSupported method of Transformer: "
			    + i18n.format("TRANSFORMER_ILLEGAL_ARGUMENT"), e);
	} catch (IllegalAccessException e) {
	    throw new TransformerDisabledException(
		    "Cannot run static isSupported method of Transformer: "
			    + i18n.format("TRANSFORMER_ILLEGAL_ACCESS"), e);
	} catch (InvocationTargetException e) {
	    throw new TransformerDisabledException(
		    "Cannot run static isSupported method of Transformer: "
			    + i18n.format("TRANSFORMER_INVOCATION_PROBLEM"), e);
	}
	return result.booleanValue();
    }

    protected abstract ClassLoader getClassLoader(Collection<String> jars)
	    throws IOException;

    protected abstract boolean isLoadedFromJar();
}
