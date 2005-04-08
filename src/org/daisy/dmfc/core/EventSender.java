/*
 * Created on 2005-mar-07
 */
package org.daisy.dmfc.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;

import org.daisy.util.i18n.I18n;

/**
 * Base class for several classes in DMFC that need event handling functionality.
 * @author Linus Ericson
 */
public abstract class EventSender {

	private Set eventListeners;
	private I18n internationalization = null;
		
	protected EventSender(EventListener a_eventListener) {
		eventListeners = new HashSet();
		addEventListener(a_eventListener);
	}
	
	protected EventSender(Set a_eventListeners) {
		eventListeners = a_eventListeners;
	}
	
	protected void addEventListener(EventListener a_eventListener) {
		eventListeners.add(a_eventListener);
	}
	
	protected Set getEventListeners() {
		return eventListeners;
	}
	
	protected void sendMessage(Level a_level, String a_message) {
	    Prompt _prompt = new Prompt(a_level, a_message);
		Iterator _iter = eventListeners.iterator();		
		while (_iter.hasNext()) {
			EventListener _eventListener = (EventListener)_iter.next();			
			_eventListener.message(_prompt);
		}
	}
	
	protected void progress(double a_progress) {
	    Prompt _prompt = new Prompt(a_progress);
	    Iterator _iter = eventListeners.iterator();	    
		while (_iter.hasNext()) {
			EventListener _eventListener = (EventListener)_iter.next();			
			_eventListener.message(_prompt);
		}
	}
	
	protected String i18n(String a_msgId) {
		if (internationalization == null) {
			return "<i18n not initialized> " + a_msgId;
		}
		return internationalization.format(a_msgId);
	}
	
	protected String i18n(String a_msgId, Object[] a_params) {
		if (internationalization == null) {
			return "<i18n not initialized> " + a_msgId;
		}
		return internationalization.format(a_msgId, a_params);
	}
	
	protected String i18n(String a_msgId, Object a_param) {
		return i18n(a_msgId, new Object[]{a_param});
	}
	
	protected String i18n(String a_msgId, Object a_param1, Object a_param2) {
		return i18n(a_msgId, new Object[]{a_param1, a_param2});
	}
	
	protected void setI18nBundle(ResourceBundle a_bundle) {
		internationalization = new I18n(a_bundle);
	}
	
	protected void setI18n(I18n a_i18n) {
		internationalization = a_i18n;
	}
	
	protected I18n getI18n() {
		return internationalization;
	}
}
