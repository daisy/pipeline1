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
/*
 * 
 */

import java.util.Date;

/**
 * Times a processing span. 
 * 
 * @author Piotr Kiernicki
 */
public class  DtbProcessingSpan {

    private Date startTime = null;
    private Date endTime = null;
    
    
    /**
     * Sets the start time.
     */
    public void setStartTime(){
        this.startTime = new Date();
    }
    
    /**
     * Sets the end time.
     */
    public void setEndTime(){
        this.endTime = new Date();
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }
	
	
	/**
	 * A convenience method that converts the input parameter <code>long</code> value <code>timeInSeconds</code> into a <code>String</code> value in hh:mm:ss format.
	 * 
	 * @param timeInSeconds
	 * @return returns a String value showing time in hh:mm:ss format.
	 */
	public static String timeInHhMmSs(long timeInSeconds) {
		long hours = 0;
		long minutes = 0;
		long seconds= 0;
		
		hours = timeInSeconds / 3600;
		timeInSeconds = timeInSeconds - (hours * 3600);
		
		minutes = timeInSeconds / 60;
		timeInSeconds = timeInSeconds - (minutes * 60);
		
		seconds = timeInSeconds;
		
		String hPrefix = "0";
		String mPrefix = "0";
		String sPrefix = "0";
		
		if(hours>9)
			hPrefix = "";
		if(minutes>9)
			mPrefix = "";
		if(seconds>9)
			sPrefix = "";
		  	
		return(hPrefix+hours + ":" + mPrefix+minutes + ":" + sPrefix+seconds);
	}

}
