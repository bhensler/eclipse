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

import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.collaboration.realtime.event.AddFAQEvent;
import com.ibm.collaboration.realtime.event.AddFAQEventStatus;
import com.ibm.collaboration.realtime.event.ChatNameChangeEvent;
import com.ibm.collaboration.realtime.event.ChatNameChangeEventStatus;
import com.ibm.collaboration.realtime.event.ChatRoomDeleteEvent;
import com.ibm.collaboration.realtime.event.ChatRoomDeleteEventStatus;
import com.ibm.collaboration.realtime.event.ChatTextDeleteEvent;
import com.ibm.collaboration.realtime.event.ChatTextDeleteEventStatus;
import com.ibm.collaboration.realtime.event.EventStatus;
import com.ibm.collaboration.realtime.plugin.PluginAddFAQHandler;
import com.ibm.collaboration.realtime.plugin.PluginChatRoomHandler;
import com.ibm.collaboration.realtime.plugin.PluginException;
import com.ibm.collaboration.realtime.plugin.PluginFilePostHandler;
import com.ibm.collaboration.realtime.plugin.PluginStartupCallback;

public class SampleRoomHandler implements PluginChatRoomHandler 
{
	private final static String PLUGIN_NAME =
		"com.ibm.rtc.compliance.sample.sampleRoomHandlerPlugin";
	
	private static Logger _logger = Logger.getLogger(SampleRoomHandler.class.getName());

	public void onChatRoomDelete(ChatRoomDeleteEventStatus event) {
		String userNm = SampleUtil.getUserName(event);
		_logger.fine("OnChatRoomDelete, roomID = " + event.getSessionID() + 
				"\n deleted by user = " + userNm);
	}
	
	public void chatTextDelete(ChatTextDeleteEvent event)
	{
		String sessId = event.getSessionID();
		String roomName = "";
		try {
			roomName = event.getSessionDetailUtil().getSessionName(sessId);
		}
		catch (PluginException pe) {
			_logger.log(Level.WARNING, "Error getting roomName:", pe);
		}
		if (roomName.contains("Delete")){
			String userId = event.getOriginatingUser().getID();
			if (userId.contains("wasadmin") || userId.contains("7")){
				event.setStatus(EventStatus.EVENT_OK_TYPE);
			} else {
				event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "In room whose name contains \"Delete\" a transcript deletion can only be done by a user whose id contains \"wasadmin\" or \"7\".");
			}
		} else {
			event.setStatus(EventStatus.EVENT_OK_TYPE);
		}
	}
	
	public void onChatTextDelete(ChatTextDeleteEventStatus event) 
	{
		String userNm = SampleUtil.getUserName(event);
		Date start = event.getStartTime();
		Date end = event.getEndTime();
		String range;
		if ((start == null) && (end == null)) {
			range = "Full Transcript"; 
		}
		else
		{
			range = "from " + start + " to " + end;
		}
		_logger.fine("OnChatTextDelete: roomId = " + event.getSessionID() + 
				"\n range = " + range + 
				"\n by user: " + userNm );

	}

	public void onChatNameChange(ChatNameChangeEventStatus event) 
	{

		String userNm = SampleUtil.getUserName(event);
		_logger.fine("RoomNameChange by user " + userNm + ", new name = " + event.getNewName());
	}

	public void customPropertiesChanged(Properties properties) {
		// TODO Auto-generated method stub

	}

	public void init(Properties properties, PluginStartupCallback callback) 
	{
		_logger.fine("Enter SampleRoomHandler.init");

		if (callback != null)
		{
			callback.pluginStarted(PLUGIN_NAME);
		}
	}

	public void terminate() {
		// TODO Auto-generated method stub

	}

	public void chatRoomDelete(ChatRoomDeleteEvent event) {
		String userId = event.getOriginatingUser().getID();
		String sessId = event.getSessionID();
		String roomName = "NULL";
		try {
			roomName = event.getSessionDetailUtil().getSessionName(sessId);
		}
		catch (PluginException pe) {
			_logger.log(Level.WARNING, "Error getting roomName:", pe);
		}
		_logger.fine("Room Delete Request by user = " + userId + " on room = " + roomName);
		if (roomName.contains("Delete")){
			if (userId.contains("wasadmin") || userId.contains("7")){
				event.setStatus(EventStatus.EVENT_OK_TYPE, "Event_OK");
			}
			else {
				event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "A Room with \"Delete\" in its name can only be deleted by a user whose ID includes \"wasadmin\" or \"7\".");
			}
		} else {
			event.setStatus(EventStatus.EVENT_MODIFIED_TYPE, "Delete request approved.  Use a room with \"Delete\" in its name to test delete prevention.");
		}
	}
	
	public void chatNameChange(ChatNameChangeEvent event){
		String sessId = event.getSessionID();
		String oldName = null;
		try {
			oldName = event.getSessionDetailUtil().getSessionName(sessId);
		}
		catch (PluginException pe){
			_logger.log(Level.WARNING, "Error getting roomName:", pe);
			oldName="#$#$";
		}
		String newName = event.getNewName();
		if (newName.startsWith(oldName)){
			event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "Name change blocked, new name may not start with old name");
		}
		else if (newName.contains(oldName)){
			event.setNewName("-Remove OldName-"+newName);
			event.setStatus(EventStatus.EVENT_MODIFIED_TYPE, "Prefer no use of oldName in newName");
		} else {
			event.setStatus(EventStatus.EVENT_OK_TYPE, "OK Reason");
		}
	}

}
