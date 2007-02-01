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

/**
 * @author Piotr Kiernicki
 */
public class DtbSplitterMergerConstants {
	
	public final static String SPLIT_MODE = "split";
	public final static String MERGE_MODE = "merge";
	/**
	 * Number of bytes in one measure unit that is used to specify 
	 * the size of the created volumes (default value: 1024*1024).
	 */
	public static int volumeSizeMeasureUnit = 1024*1024;//1MB
	
	/**
	 * (default value: "MB")
	 */
	public static String volumeSizeMeasureUnitName = "MB";	
	
	
}