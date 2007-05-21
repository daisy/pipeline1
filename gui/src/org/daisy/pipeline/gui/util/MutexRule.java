package org.daisy.pipeline.gui.util;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * @author Romain Deltour
 *
 */
public class MutexRule implements ISchedulingRule {
    public boolean contains(ISchedulingRule rule) {
        return rule == this;
    }

    public boolean isConflicting(ISchedulingRule rule) {
        return rule == this;
    }
}