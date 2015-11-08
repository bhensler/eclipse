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
package com.ibm.meeting.compliance.sample;


import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.ibm.meeting.compliance.event.EventStatus;
import com.ibm.meeting.compliance.event.EventType;
import com.ibm.meeting.compliance.event.UserEvent;
import com.ibm.meeting.compliance.event.UserEventStatus;
import com.ibm.meeting.compliance.plugin.PluginException;
import com.ibm.meeting.compliance.plugin.PluginStartupCallback;
import com.ibm.meeting.compliance.plugin.PluginUserHandler;
import com.ibm.meeting.compliance.plugin.User;

public class SampleUserHandler implements PluginUserHandler 
{

	private final static String PLUGIN_NAME =
		"com.ibm.meeting.compliance.sample.sampleJoinHandlerPlugin";

	public void onUserJoin(UserEventStatus status) 
	{
		String sessionId = status.getSessionId();
		int stat = status.getStatus();
		String uId = status.getOriginatingUser().getId();
		String ip = status.getOriginatingUser().getIp();
		String meetingRoomName = null;
		String meetingRoomOwner = null;
		String callInfo = null;
		boolean isManager = false;
		
		try
		{
			meetingRoomName = status.getMeetingRoomUtil().getMeetingRoomName(sessionId);
			meetingRoomOwner = status.getMeetingRoomUtil().getMeetingRoomOwner(sessionId);
			callInfo = status.getMeetingRoomUtil().getMeetingCallInfo(sessionId);
			isManager = status.getMeetingRoomUtil().isManager(sessionId, uId);
		}
		catch(PluginException e)
		{
			e.printStackTrace();
		}
		
		if(status.getEventType() == EventType.USER_JOIN_EVENT_TYPE)
		{
			System.out.println("OnUserJoin user[" + uId + "] ip[" + ip + "]" + "session[" + sessionId +"]" + "room name[" + meetingRoomName + "]" + "Result[" + stat + "] reason[" + status.getReason() + "]");
			System.out.println("room owner[" + meetingRoomOwner + "] call Info[" + callInfo + "] is manager[" + isManager + "]");
		}
		else
		{
			System.out.println("OnUserLeave user[" + uId + "] ip[" + ip + "]" + "session[" + sessionId +"] Result[" + stat + "] reason[" + status.getReason() + "]");
		}
	}

	public void userJoin(UserEvent event) 
	{
		User u = event.getOriginatingUser();
		String sessionId = event.getSessionId();
		String currId = u.getId();
		
		System.out.println("userJoin for u.id = " + currId + " in sess = " + sessionId );
		
		List<String> currentUsers = null;
		
		try
		{
			currentUsers = event.getMeetingRoomUtil().getCurrentUsers(sessionId);
		} 
		catch (PluginException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Iterator<String> iterator = currentUsers.iterator();
		
		System.out.println("Current Users in Session:");
		
		while(iterator.hasNext())
		{
			System.out.println("User: " + iterator.next());
		}
		
		
		if(u.getId().contains("adams100"))
		{
			event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "No Adams100!");
		}
		else
		{
			event.setStatus(EventStatus.EVENT_OK_TYPE);
		}
		
	}

	public void customPropertiesChanged(Properties properties) {
		// TODO Auto-generated method stub
		
	}

	public void init(Properties properties, PluginStartupCallback callback) {
		System.out.println("Enter SampleUserHandler.init");

		if (callback != null)
		{
			callback.pluginStarted(PLUGIN_NAME);
		}
		
	}

	public void terminate() {
		// TODO Auto-generated method stub
		
	}

}
