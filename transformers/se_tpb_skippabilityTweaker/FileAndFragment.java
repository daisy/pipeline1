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


/**
 * Helper class representing a filename with a fragment identifier.
 * @author Linus Ericson
 */
/*package*/ class FileAndFragment {
    
    private String file;
    private String fragment;
    
    public FileAndFragment(String file, String fragment, String referer) {
        this.file = file;
        this.fragment = fragment;
        if (file == null || "".equals(file)) {
            this.file = referer;
        }
    }
    
    public FileAndFragment(String fileAndFragment, String referer) {
        this.file = fileAndFragment.substring(0, fileAndFragment.indexOf("#"));
        this.fragment = fileAndFragment.substring(fileAndFragment.indexOf("#") + 1);
        if ("".equals(this.file)) {
            this.file = referer;
        }
    }

    public String getFile() {
        return file;
    }

    public String getFragment() {
        return fragment;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((file == null) ? 0 : file.hashCode());
        result = PRIME * result + ((fragment == null) ? 0 : fragment.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final FileAndFragment other = (FileAndFragment) obj;
        if (file == null) {
            if (other.file != null)
                return false;
        } else if (!file.equals(other.file))
            return false;
        if (fragment == null) {
            if (other.fragment != null)
                return false;
        } else if (!fragment.equals(other.fragment))
            return false;
        return true;
    }
    
    
    
}
