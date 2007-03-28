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
package org.daisy.dmfc.core.transformer;

import java.io.File;
import java.net.URL;
import java.util.EventObject;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.stream.Location;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.event.BusListener;
import org.daisy.dmfc.core.event.EventBus;
import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.dmfc.core.event.RequestEvent;
import org.daisy.dmfc.core.event.StateChangeEvent;
import org.daisy.dmfc.core.event.TransformerMessageEvent;
import org.daisy.dmfc.core.event.TransformerProgressChangeEvent;
import org.daisy.dmfc.core.event.TransformerStateChangeEvent;
import org.daisy.dmfc.core.event.UserAbortEvent;
import org.daisy.dmfc.core.event.UserEvent;
import org.daisy.dmfc.core.event.UserReplyEvent;
import org.daisy.dmfc.exception.TransformerAbortException;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.i18n.I18n;

/**
 * Base class for all Transformers. Every Transformer extending this base class
 * must provide a constructor taking the same parameters as the constructor of
 * this base class does.
 * 
 * Extending this class shall not be considered as derivative works.
 * 
 * @author Linus Ericson
 * @author Markus Gylling
 */

public abstract class Transformer implements BusListener { 
	
	private boolean mIsInteractive;	  
    private long startTime = 0;	
	private File transformerDirectory = null;	
	private TransformerInfo transformerInfo = null;
	private boolean mIsAborted = false;
	private InputListener mInputListener = null;
	private I18n mInternationalization;
	
	
	/**
	 * Creates a new Transformer.
	 * @param eventListeners a set of  event listeners
	 * @param isInteractive
	 * @deprecated use the (Boolean isinteractive) constructor instead
	 */
	public Transformer(InputListener inListener, Set eventListeners, Boolean isInteractive) {		
		this(inListener,isInteractive);		
	}
	
	/**
	 * Constructor. Creates a new Transformer.
	 * @param inListener
	 * @param isInteractive
	 */
	public Transformer(InputListener inListener,Boolean isInteractive) {
		mInternationalization = new I18n();
		mIsInteractive = isInteractive.booleanValue();
		mInputListener = inListener;
		//subscribe to user events
		EventBus.getInstance().subscribe(this, UserEvent.class);
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.ENGLISH, this.getClass().getClassLoader());
			addI18nBundle(bundle);
		} catch (MissingResourceException e) {				
			sendMessage("No resource bundle found for " + this.getClass().getName());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.dmfc.core.event.BusListener#recieved(java.util.EventObject)
	 */
	public void recieved(EventObject event) {
		if(event instanceof UserAbortEvent) {
			sendMessage(getName() + " aborting...");
			mIsAborted  = true;
		}
	}
	
	/**
	 * Convenience method to check if a UserAbortEvent has been rasied.
	 * @throws TransformerAbortException if a UserAbortEvent has been rasied.
	 * @see #isAborted()
	 */
	protected void checkAbort() throws TransformerAbortException {
		if(mIsAborted) {
			throw new TransformerAbortException(getName() + " aborted.");
		}
	}
	
	/**
	 * Convenience method to check if a UserAbortEvent has been rasied.
	 * @return true if a UserAbortEvent has been raised, false otherwise.
	 * @see #checkAbort()
	 */
	protected boolean isAborted() {
		if(mIsAborted) return true;
		return false;
	}
	
	private String getName() {
		return this.getTransformerInfo().getName();
	}
	
		
	/**
	 * Executes the Transformer with the specified parameters.
	 * @param parameters a collection of parameters
	 * @return <code>true</code> if the Transformer was successful, <code>false</code> otherwise.
	 */
	protected abstract boolean execute(Map parameters) throws TransformerRunException;
	
	final public boolean executeWrapper(Map parameters, File dir) throws TransformerRunException {
	    checkAbort();
	    transformerDirectory = dir;
	    sendMessage(true);
	    sendMessage(0);
        startTime = System.currentTimeMillis();
        boolean ret = execute(parameters);
	    checkAbort();
	    sendMessage(1);
	    sendMessage(false);
	    return ret;
	}
	    
	/**
	 * Performs any Transformer specific checks. In the default implementation,
	 * this function always returns <code>true</code>.
	 * @return <code>true</code> if al dependency ckecks are OK, <code>false</code> otherwise
	 */
	public static boolean isSupported() {
		return true;
	}
	

	final protected UserReplyEvent getUserInput(String message, String defaultValue) {
		if (!mIsInteractive) {
			return new UserReplyEvent(this,defaultValue);			
		}		
		return mInputListener.getUserReply(new RequestEvent(this,message));		
	}
	
	
	/**
	 * Final function for returning the transformer directory. This method cannot be overridden.
	 * <p>The method {@link #getTransformerDirectoryResource(String)} is preferred
	 * since the getTransformerDirectory() method does not support jarness.</p>
	 * @return the directory of the transformer
	 */
	final protected File getTransformerDirectory() {
		//TODO find out if in jar, throw exception
		
	    return transformerDirectory;
	}
	
	/**
	 * Retrieve a URL of a resource associated with this transformer.
	 * <p>This method is preferred to {@link #getTransformerDirectory()} since
	 * it supports jarness.</p>
	 */
	final protected URL getTransformerDirectoryResource(String subPath) throws IllegalArgumentException {
		//TODO check the viability of this method
		URL url;
	    url = this.getClass().getResource(subPath);
	    if(null==url) {
	    	String qualifiedPath = this.getClass().getPackage().getName().replace('.','/') + "/";	    	
	    	url = this.getClass().getClassLoader().getResource(qualifiedPath+subPath);
	    }
	    if(url==null) throw new IllegalArgumentException(subPath + "in " + this.getName());
	    return url;
	}
	
	/**
	 * Sets the transformer information.
	 * @param tInfo the transformer information
	 */
	/*package*/ void setTransformerInfo(TransformerInfo tInfo) {
		this.transformerInfo = tInfo;
	}
	
	/**
	 * Gets some information about the transformer, such as name and description.
	 * @return the TransformerInfo
	 */
	public TransformerInfo getTransformerInfo() {
		return this.transformerInfo;
	}

	/*
	 * Convenience methods for EventBus 
	 */
	
	/**
	 * Convenience method to emit a message. The Type
	 * will default to MessageEvent.Type.INFO, the Cause will default
	 * to MessageEvent.Cause.SYSTEM
	 */
	protected void sendMessage(String message) {
		this.sendMessage(message, MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM, null);
	}
	
	/**
	 * Convenience method to emit a message. The Cause will default
	 * to MessageEvent.Cause.SYSTEM
	 */
	protected void sendMessage(String message, MessageEvent.Type type) {
		this.sendMessage(message, type, MessageEvent.Cause.SYSTEM, null);
	}

	/**
	 * Convenience method to emit a message.
	 */
	protected void sendMessage(String message, MessageEvent.Type type, MessageEvent.Cause cause) {
		this.sendMessage(message, type, cause, null);
	}

	/**
	 * Convenience method to emit a message.
	 */
	protected void sendMessage(String message, MessageEvent.Type type, MessageEvent.Cause cause, Location location) {		
		EventBus.getInstance().publish(new TransformerMessageEvent(this,message,type,cause,location));
	}
	
	/**
	 * Convenience method to send a progress event.
	 * @param progress A double between 0.0 and 1.0 inclusive
	 */
	protected void sendMessage(double progress) {
		EventBus.getInstance().publish(new TransformerProgressChangeEvent(this,progress));   
	}
	
	/**
	 * Convenience method to send a progress event.
	 * @param progress A double between 0.0 and 1.0 inclusive
	 * @see #sendMessage(double)
	 */	protected void progress(double progress) {
		sendMessage(progress);   
	}
	
    protected void sendMessage(boolean started) {
    	StateChangeEvent.Status state = !started 
    		? StateChangeEvent.Status.STOPPED 
    		: StateChangeEvent.Status.STARTED;   	
    	
    	EventBus.getInstance().publish(new TransformerStateChangeEvent(this,state));
	}
		
	/**
	 * Bridge for the 'old' messaging API, deprecated as of 200703.
	 * @deprecated
	 */
	protected void sendMessage(Level level, String msg) {
		
		String message = msg;

		MessageEvent.Type type;
		if(level== Level.SEVERE) {
			type = MessageEvent.Type.ERROR;
		}else if(level== Level.WARNING) {
			type = MessageEvent.Type.WARNING;
		}else {
			type = MessageEvent.Type.INFO;
		}
		
		MessageEvent.Cause cause = MessageEvent.Cause.SYSTEM;
								
		sendMessage(message,type,cause);
	}
	
	/*
	 * i18n convenience methods
	 */
	
	protected String i18n(String msgId) {
		return mInternationalization.format(msgId);
	}

	protected String i18n(String msgId, Object[] params) {
		return mInternationalization.format(msgId, params);
	}

	protected void addI18nBundle(ResourceBundle bundle) {
		mInternationalization.addBundle(bundle);
	}

	protected I18n getI18n() {
		return mInternationalization;
	}

	protected String i18n(String msgId, Object param) {
		return i18n(msgId, new Object[]{param});
	}

	protected String i18n(String msgId, Object param1, Object param2) {
		return i18n(msgId, new Object[]{param1, param2});
	}

}
