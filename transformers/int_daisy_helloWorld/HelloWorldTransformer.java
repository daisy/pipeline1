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
package int_daisy_helloWorld;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.event.UserReplyEvent;
import org.daisy.dmfc.core.transformer.Transformer;

/*
 * Created on 2005-mar-09
 */

/**
 * @author Linus Ericson
 */
public class HelloWorldTransformer extends Transformer {
	
	public HelloWorldTransformer(InputListener a_inputListener, Set a_eventListeners, Boolean a_interactive) {
		super(a_inputListener, a_eventListeners, a_interactive);		
		sendMessage(Level.INFO, i18n("CONSTRUCTING", "test class"));		
	}

	protected boolean execute(Map a_parameters) {
	    progress(0);
		sendMessage(Level.INFO, i18n("RUNNING", "world!"));
		progress(0.1);
		TestClass t = new TestClass();
		t.test();
		progress(0.3);
		//String _ret = getUserInput(Level.INFO, i18n("WHAT_IS_YOUR_NAME"), "default value");
		UserReplyEvent reply = getUserInput(i18n("WHAT_IS_YOUR_NAME"),"default value");
		progress(0.5);
		sendMessage(Level.INFO, "User says: " + reply.getReply());
		progress(0.7);
		
		for (Iterator it = a_parameters.entrySet().iterator(); it.hasNext(); ) {
		    Map.Entry entry = (Map.Entry)it.next();
		    sendMessage(Level.INFO, i18n("FOUND_PARAM", entry.getKey(), entry.getValue()));
		}		
		
		progress(1);
		/*
		com.example.linus.Hepp h = new com.example.linus.Hepp();
		System.err.println(h.hepp());
		*/		
		return true;
	}

}
