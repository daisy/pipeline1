/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
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
package org.daisy.pipeline.gui.util.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IAction;

/**
 * @author Romain Deltour
 *
 */
public final class ActionRegistry {

    private static ActionRegistry _default = new ActionRegistry();
    private Map<String,IAction> actionMap;
    
    
    public ActionRegistry() {
        super();
        actionMap = new HashMap<String, IAction>();
    }

    public static ActionRegistry getDefault() {
        return _default;
    }

    public IAction getAction(String id) {
        return actionMap.get(id);
    }
    
    public void register(IAction action) {
        actionMap.put(action.getId(), action);
    }

}
