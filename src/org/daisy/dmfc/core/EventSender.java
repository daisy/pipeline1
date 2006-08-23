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
 */package org.daisy.dmfc.core;

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
	private I18n internationalization = new I18n();
	protected String messageOriginator = "DMFC";
		
	/**
	 * Creates a new EventSender specifying a single listener of the object.
	 * @param eventListener a event listener
	 */
	protected EventSender(EventListener eventListener) {
		eventListeners = new HashSet();
		addEventListener(eventListener);
	}
	
	/**
	 * Creates a new EventSender specifying a set of listeners of the object.
	 * @param listeners
	 */
	protected EventSender(Set listeners) {
		eventListeners = listeners;
	}
	
	/**
	 * Adds a EventListener.
	 * @param eventListener the EventListener to add.
	 */
	protected void addEventListener(EventListener eventListener) {
		eventListeners.add(eventListener);
	}
	
	/**
	 * @return a set of EventListeneters
	 */
	protected Set getEventListeners() {
		return eventListeners;
	}
	
	/**
	 * Sends a message to all listeners.
	 * @param level the level of the message
	 * @param message the message
	 */
	public void sendMessage(Level level, String message) {
	    Prompt prompt = new Prompt(level, message, messageOriginator);
		send(prompt);
	}
	
	protected void status(boolean started) {
	    Prompt prompt = new Prompt(started, messageOriginator);
	    send(prompt);
	}
	
	protected void send(Prompt prompt) {
	    Iterator it = eventListeners.iterator();		
		while (it.hasNext()) {
			EventListener eventListener = (EventListener)it.next();			
			eventListener.message(prompt);
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
