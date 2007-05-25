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
package org.daisy.pipeline.gui.util.viewers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.daisy.pipeline.gui.util.Category;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Romain Deltour
 * 
 */
public abstract class CategorizedContentProvider implements
        ITreeContentProvider {

    private List<Category> categories;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        // Nothing
    }

    public List<Category> getCategories() {
        return categories;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof Category) {
            Category category = (Category) parentElement;
            List<Object> children = new LinkedList<Object>();
            for (Object obj : getAllElements()) {
                if (category.contains(obj)) {
                    children.add(obj);
                }
            }
            return children.toArray();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
        if (categories != null && !(element instanceof Category)) {
            return findCategory(element);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element) {
        if (element instanceof Category) {
            Category category = (Category) element;
            for (Object obj : getAllElements()) {
                if (category.contains(obj)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        if (categories != null) {
            List<Category> cats = new ArrayList<Category>(categories.size());
            for (Category category : categories) {
             if(hasChildren(category)) {
                 cats.add(category);
                 category.setVisible(true);
             } else {
                 category.setVisible(false);
             }
            }
            return cats.toArray();
        } else {
            return getAllElements();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // Nothing
    }

    public boolean isCategorized() {
        return categories != null;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    protected Category findCategory(Object object) {
        for (Category category : categories) {
            if (category.contains(object)) {
                return category;
            }
        }
        return null;
    }

    protected abstract Object[] getAllElements();
}
