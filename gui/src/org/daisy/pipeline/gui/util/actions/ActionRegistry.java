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
