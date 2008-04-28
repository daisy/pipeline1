/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package int_daisy_filesetRenamer.segment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.daisy.util.fileset.Fileset;

/**
 * Represents a unique segment of a filename. The random segment is guaranteed to be unique within the fileset.
 * @author Markus Gylling
 */
public class RandomUniqueSegment extends Segment {	
	private static Map<Fileset,List<String>> generatedRandomSegments = new HashMap<Fileset,List<String>>(); //fileset, ArrayList
	
	private RandomUniqueSegment(String content) {
		super(content);			
	}
	
	/**
	 * Create a new segment, regarding the inparam string as readymade content
	 */
	public static RandomUniqueSegment create(String content) {
		return new RandomUniqueSegment(content);
	}
	
	/**
	 * Create a new segment, unique within the inparam fileset
	 * @param groups the number of four digit groups to include in the segment
	 */
	public static RandomUniqueSegment create(Fileset fileset, int groups) {		
		String scr;
		List<String> list = generatedRandomSegments.get(fileset);
				
		//make sure length is larger than fileset size, else eternal loop
		if(groups==0) groups++;
		if(groups==1 && fileset.getLocalMembers().size()<10000) groups++;
		if(groups==2 && fileset.getLocalMembers().size()<100000000) groups++;
				
		if(list==null) {
			//this is the first random to generate for this fileset
			list = new ArrayList<String>();
			scr = scramble(groups);			
			generatedRandomSegments.put(fileset, list);
		}else{
			//this fileset already has a list of names, 
			//make sure the new one is unique
			do{
			   scr = scramble(groups); 
			} while (list.contains(scr));
		}
		list.add(scr);
		return new RandomUniqueSegment(scr);
	}
	
	private static String scramble(int loops) {	
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		String cur = null;
				
		for (int i = 0; i < loops-1; i++) {
			do {
				cur = Integer.toString(random.nextInt(9999));
				if(cur.length()<4){
					do {
						cur += Integer.toString(random.nextInt(9));
					}while (cur.length()<4);
				}
				break;					
			} while (true);			
			sb.append(cur);
			sb.append('-');
		}		
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
}
