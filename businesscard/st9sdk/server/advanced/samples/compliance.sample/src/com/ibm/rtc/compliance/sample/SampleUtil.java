/* ***************************************************************** */
/*                                                                   */
/* Licensed Materials - Property of IBM                              */
/*                                                                   */
/* 5724-E76, 5724-L21, 5724-i67, 5724-L64, 5655-M44                  */
/*                                                                   */
/* Copyright IBM Corp. 2006, 2014  All Rights Reserved.              */
/*                                                                   */
/* US Government Users Restricted Rights - Use, duplication or       */
/* disclosure restricted by GSA ADP Schedule Contract with           */
/* IBM Corp.                                                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.rtc.compliance.sample;

import com.ibm.collaboration.realtime.event.EventStatus;
import com.ibm.collaboration.realtime.plugin.User;

public class SampleUtil 
{
	static String statusString (int statusInt)
	{
		String retVal = null;
		switch(statusInt){
		case EventStatus.EVENT_OK_TYPE:
			retVal = "EVENT_OK";
			break;
		case EventStatus.EVENT_BLOCKED_TYPE:
			retVal = "EVENT_BLOCKED";
			break;
		case EventStatus.EVENT_MODIFIED_TYPE:
			retVal = "EVENT_MODIFIED";
			break;
		default:
			retVal = "UNDEFINED_EVENT_TYPE";
			break;
		}
		return retVal;
	}
	
	static String getUserName (EventStatus event)
	{
		User u = event.getOriginatingUser();
		String retVal = u.getID();
		if (u.isAnonymous()){
			retVal += " [Anonymous]";
		}
		return retVal;
	}

}
