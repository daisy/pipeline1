/*
 * DMFC - The DAISY Multi Format Converter
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

package se_tpb_dtbSplitterMerger;

import org.daisy.util.exception.BaseException;

/**
 * 
 * @author Piotr Kiernicki
 */
public class TransformationAbortedByUserException extends BaseException {

    private static final long serialVersionUID = 5572494658223937410L;
   
    public TransformationAbortedByUserException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public TransformationAbortedByUserException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
}