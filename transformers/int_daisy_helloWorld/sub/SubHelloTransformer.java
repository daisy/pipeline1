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
package int_daisy_helloWorld.sub;


import int_daisy_helloWorld.TestClass;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;



/**
 * @author Linus Ericson
 */
public class SubHelloTransformer extends Transformer {

    /**
     * @param a_inputListener
     * @param a_eventListeners
     * @param a_interactive
     */
    public SubHelloTransformer(InputListener a_inputListener,
            Set a_eventListeners, Boolean a_interactive) {
        super(a_inputListener, a_eventListeners, a_interactive);        
    }

    protected boolean execute(Map a_parameters) throws TransformerRunException {
        sendMessage(Level.INFO, "SubHello!");
        TestClass tc = new TestClass();
        tc.test();        
        return true;
    }

}
