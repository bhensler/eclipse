/* ***************************************************************** */
/*                                                                   */
/* Licensed Materials - Property of IBM                              */
/*                                                                   */
/* 5724-E76, 5724-L21, 5724-i67, 5724-L64, 5655-M44                  */
/*                                                                   */
/* Copyright IBM Corp. 2014  All Rights Reserved.                    */
/*                                                                   */
/* US Government Users Restricted Rights - Use, duplication or       */
/* disclosure restricted by GSA ADP Schedule Contract with           */
/* IBM Corp.                                                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.meeting.compliance.sample;

import java.util.Properties;


import com.ibm.meeting.compliance.event.ChatSentEvent;
import com.ibm.meeting.compliance.event.ChatSentEventStatus;
import com.ibm.meeting.compliance.event.EventStatus;
import com.ibm.meeting.compliance.plugin.PluginChatSentHandler;
import com.ibm.meeting.compliance.plugin.PluginStartupCallback;

public class SampleChatSentHandler implements PluginChatSentHandler
{

	private final static String PLUGIN_NAME =
		"com.ibm.meeting.compliance.sample.sampleChatSentHandlerPlugin";
	
	
	@Override
	public void chatSent(ChatSentEvent event)
	{
		
		if(event.getChatMessage().contains("sametime"))
		{
			event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "You can't send messages that contain the word sametime!");
			System.out.println("Chat message blocked: [" + event.getReason() + "]");
		}
		else if(event.getChatMessage().contains("ibm"))
		{
			event.setChatMessage(event.getChatMessage().replace("ibm", "IBM"));
			event.setStatus(EventStatus.EVENT_MODIFIED_TYPE, "Respect!");
			System.out.println("Chat message modified: [" + event.getReason() + "]");
		}
		else
		{
			event.setStatus(EventStatus.EVENT_OK_TYPE);
		}

	}

	@Override
	public void onChatSent(ChatSentEventStatus status)
	{
		System.out.println("Chat message sent: status[" + status.getStatus() + "] reason[" + status.getReason() + "] message[" + status.getChatMessage() + "] tags[" + status.getChatTags() + "]");

	}

	@Override
	public void customPropertiesChanged(Properties properties)
	{
		// TODO Auto-generated method stub

	}

	public void init(Properties properties, PluginStartupCallback callback) {
		System.out.println("Enter SampleChatSentHandler.init");

		if (callback != null)
		{
			callback.pluginStarted(PLUGIN_NAME);
		}
		
	}

	@Override
	public void terminate()
	{
		// TODO Auto-generated method stub

	}

}
