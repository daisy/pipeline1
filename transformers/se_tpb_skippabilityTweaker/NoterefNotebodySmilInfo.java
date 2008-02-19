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

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping between a content doc noteref ID and the SMIL references in the
 * corresponding note body
 * @author Linus Ericson
 */
/*package*/ class NoterefNotebodySmilInfo {

    private List<String> noterefIds;
    private String notebodyId;
    private List<FileAndFragment> smilRefs = new ArrayList<FileAndFragment>();
    
    public NoterefNotebodySmilInfo(List<String> noterefId, String notebodyId) {
        this.noterefIds = noterefId;
        this.notebodyId = notebodyId;
    }

    public String getNotebodyId() {
        return notebodyId;
    }

    public List<String> getNoterefIds() {
        return noterefIds;
    }

    public List<FileAndFragment> getSmilRefs() {
        return smilRefs;
    }
    
    public void addSmilRef(FileAndFragment smilRef) {
        smilRefs.add(smilRef);
    }
    
}
