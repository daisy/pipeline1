/*
 * Daisy Pipeline
 * Copyright (C) 2008  Daisy Consortium
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
package se_tpb_skippabilityTweaker;

import java.util.HashSet;
import java.util.Set;

/**
 * A set of IDs belonging to skippable items in the content documents.
 * @author Linus Ericson
 */
/*package*/ class SkippableContentIds {

    private Set<String> pagenumIds;
    private Set<String> sidebarIds;
    private Set<String> prodnoteIds;
    
    public SkippableContentIds(Set<String> pagenumIds, Set<String> sidebarIds, Set<String> prodnoteIds) {
        this.pagenumIds = pagenumIds;
        this.sidebarIds = sidebarIds;
        this.prodnoteIds = prodnoteIds;
    }
    
    public SkippableContentIds() {
        this.pagenumIds = new HashSet<String>();
        this.sidebarIds = new HashSet<String>();
        this.prodnoteIds = new HashSet<String>();
    }
    
    public void addAll(SkippableContentIds other) {
        pagenumIds.addAll(other.getPagenumIds());
        sidebarIds.addAll(other.getSidebarIds());
        prodnoteIds.addAll(other.getProdnoteIds());
    }

    public Set<String> getPagenumIds() {
        return pagenumIds;
    }

    public Set<String> getProdnoteIds() {
        return prodnoteIds;
    }

    public Set<String> getSidebarIds() {
        return sidebarIds;
    }
    
    
}
