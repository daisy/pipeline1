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
package org.daisy.dmfc.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;

import org.daisy.dmfc.core.listener.MessageListener;
import org.daisy.dmfc.core.listener.ScriptProgressListener;
import org.daisy.dmfc.core.listener.TransformerProgressListener;
import org.daisy.dmfc.core.message.CoreMessage;
import org.daisy.dmfc.core.message.Message;
import org.daisy.dmfc.core.message.TransformerMessage;
import org.daisy.dmfc.core.message.property.Cause;
import org.daisy.dmfc.core.message.property.Type;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.util.i18n.I18n;

/**
 * Base class for several classes in DMFC that need event handling functionality.
 * @author Linus Ericson
 * @author Markus Gylling
 */
public abstract class EventSender {

	/*
	 * This class contains a series of deprecated methods that can
	 * an will be deleted once all transformers have been ported to the new
	 * (as per 2007-03) listener implementation.
	 */
	
	private Set eventListeners = null; 												//this set may contain old and new listener impls	
	private MessageListener mMessageListener = null; 							//is one of the members of eventListeners 
	private TransformerProgressListener mTransformerProgressListener = null; 	//is one of the members of eventListeners 
	private ScriptProgressListener mScriptProgressListener = null; 				//is one of the members of eventListeners
	
	private I18n internationalization = new I18n();
	protected String messageOriginator = "DMFC";
		
	
	/**
	 * Default constructor. 
	 * <p>Create a new EventSender specifying one each of the listener impls defined in org.daisy.dmfc.core.listener.</p>
	 * <p>To add additional listeners, use {@link #addEventListener(java.util.EventListener)}</p>
	 * @param listeners
	 */
	protected EventSender(MessageListener mListener, TransformerProgressListener tpListener, ScriptProgressListener spListener) {
		Set set = new HashSet();
		set.add(mListener);
		set.add(tpListener);
		set.add(spListener);
		eventListeners = set;
		
		mMessageListener = mListener;
		mTransformerProgressListener = mTransformerProgressListener;
		mScriptProgressListener = mScriptProgressListener;
		
	}

	
	/**
	 * Constructor. Create a new EventSender specifying a set of listeners of the object.
	 * @param listeners
	 */
	protected EventSender(Set listeners) {
		//this Set may contain both the old and new listener objects
		eventListeners = listeners;
	}
	
	/**
	 * Creates a new EventSender specifying a single listener of the object.
	 * @param eventListener a event listener
	 * @deprecated 
	 */
	protected EventSender(org.daisy.dmfc.core.EventListener eventListener) {
		eventListeners = new HashSet();
		addEventListener(eventListener);
	}
	
//	/**
//	 * Creates a new EventSender specifying a single listener of the object.
//	 * @param eventListener a event listener 
//	 */
//	protected EventSender(java.util.EventListener eventListener) {
//		eventListeners = new HashSet();
//		addEventListener(eventListener);
//	}
	
	
	/**
	 * Adds a EventListener.
	 * @param eventListener the EventListener to add.
	 * @deprecated
	 */
	protected void addEventListener(org.daisy.dmfc.core.EventListener eventListener) {
		eventListeners.add(eventListener);
	}

	
	/**
	 * Adds a EventListener.
	 * @param eventListener the EventListener to add.
	 */
	protected void addEventListener(java.util.EventListener eventListener) {
		eventListeners.add(eventListener);
	}
	
	/**
	 * @return the set of EventListeners registered with this EventSender 
	 */
	protected Set getEventListeners() {
		return eventListeners;
	}
	
	/**
	 * Sends a message to all listeners.
	 * @param level the level of the message
	 * @param message the message
	 * @deprecated use sendMessage(org.daisy.dmfc.core.message.Message instead)
	 */
	public void sendMessage(Level level, String message) {
		//old listener impl, left alive temporarily
	    Prompt prompt = new Prompt(level, message, messageOriginator);
		send(prompt);

		//temporary bridge for new listener impl:		
		Type type = null;
		if(level == Level.SEVERE) {
			type = Type.ERROR;
		}else if (level == Level.WARNING) {
			type = Type.WARNING;
		}else{
			type = Type.INFO;
		}
		
		//we cant tell the cause given level and message, so use SYSTEM
		if(this instanceof Transformer) {
			TransformerMessage msg = new TransformerMessage((Transformer)this, message, type, Cause.SYSTEM);
			sendMessage(msg);
		}else{
			CoreMessage msg = new CoreMessage(this, message, type, Cause.SYSTEM);
			sendMessage(msg);
		}
		//end temporary bridge for new listener impl.
	}
	
	/**
	 * Sends a message to all implementations of org.daisy.dmfc.core.listener.MessageListener
	 * @param message the Message
	 */
	public void sendMessage(Message message) {	    
		   Iterator it = eventListeners.iterator();		
			while (it.hasNext()) {
				Object listener = it.next();
				if(listener instanceof org.daisy.dmfc.core.listener.MessageListener) {
					((org.daisy.dmfc.core.listener.MessageListener)listener).message(message);
				}
			}
	}
	
	
	/**
	 * @deprecated
	 */
	protected void status(boolean started) {
		//old
	    Prompt prompt = new Prompt(started, messageOriginator);
	    send(prompt);
	    
	    //new
	    if(this instanceof Transformer) {
	    	if(started) {
	    		mTransformerProgressListener.transformerStart((Transformer)this);	
	    	}else{
	    		mTransformerProgressListener.transformerEnd((Transformer)this);
	    	}	
	    		
	    }	    		    
	}
	
	/**
	 * @deprecated
	 */
	protected void send(Prompt prompt) {
	    Iterator it = eventListeners.iterator();		
		while (it.hasNext()) {
			//old code:
			//EventListener eventListener = (EventListener)it.next();			
			//eventListener.message(prompt);

			//new code:
			//temporary while old listener impl deprecation period is active 
			Object listener = it.next();
			if(listener instanceof org.daisy.dmfc.core.EventListener) {
				((org.daisy.dmfc.core.EventListener)listener).message(prompt);
			}
			
		}
	}
	
	
	
	protected String i18n(String msgId) {
		return internationalization.format(msgId);
	}
	
	protected String i18n(String msgId, Object[] params) {
		return internationalization.format(msgId, params);
	}
	
	protected String i18n(String msgId, Object param) {
		return i18n(msgId, new Object[]{param});
	}
	
	protected String i18n(String msgId, Object param1, Object param2) {
		return i18n(msgId, new Object[]{param1, param2});
	}
	
	protected void addI18nBundle(ResourceBundle bundle) {
	    internationalization.addBundle(bundle);
	}
	
	protected I18n getI18n() {
		return internationalization;
	}
}
