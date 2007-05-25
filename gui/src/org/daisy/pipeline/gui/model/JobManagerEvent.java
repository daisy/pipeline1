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
package org.daisy.pipeline.gui.model;

import java.util.EventObject;

public class JobManagerEvent extends EventObject {

    private static final long serialVersionUID = 19477162373305880L;

    public enum Type {
        ADD, REMOVE, UPDATE;
    }

    private JobInfo[] jobs;
    private Type type;
    private int index;

    public JobManagerEvent(Object source, JobInfo[] jobs, Type type) {
        super(source);
        this.jobs = jobs;
        this.type = type;
        this.index = -1;
    }

    public JobManagerEvent(Object source, JobInfo[] jobs, int index, Type type) {
        super(source);
        this.jobs = jobs;
        this.type = type;
        this.index = index;
    }

    /**
     * @return the jobInfos that changed
     */
    public JobInfo[] getJobs() {
        return jobs;
    }

    /**
     * @return the index at which the changes started
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return the type of the change
     */
    public Type getType() {
        return type;
    }

}
