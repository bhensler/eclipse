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


import java.util.Properties;

import com.ibm.meeting.compliance.event.EventStatus;
import com.ibm.meeting.compliance.event.PollSentEvent;
import com.ibm.meeting.compliance.event.PollSentEventStatus;
import com.ibm.meeting.compliance.plugin.PluginPollSentHandler;
import com.ibm.meeting.compliance.plugin.PluginStartupCallback;
import com.ibm.meeting.compliance.plugin.User;

public class SamplePollSentHandler implements PluginPollSentHandler 
{

	private final static String PLUGIN_NAME =
		"com.ibm.meeting.compliance.sample.samplePollSentHandlerPlugin";
	

	public void onPollSent(PollSentEventStatus status) 
	{
		String sessId = status.getSessionId();
		int stat = status.getStatus();
		String uId = status.getOriginatingUser().getId();
		
		System.out.println("OnPollSent \n user = " + uId + "\n for session = " + sessId + "\n Question = " + status.getQuestion() + "Answers = " + status.getAnswers() + "Open Choice Allowed = " + status.isOpenChoiceAllowed() +
				"\n Result = " + stat + ", reason = " + status.getReason());
		
	}

	public void pollSent(PollSentEvent event) 
	{
		
		User u = event.getOriginatingUser();
		String sessId = event.getSessionId();
		String currId = u.getId();
		
		System.out.println("pollSent for u.id = " + currId + " in sess = " + sessId );
		
		if(event.getQuestion().contains("sametime"))
		{
			event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "You can't send polls that contain the word sametime!");
		}
		else if(event.getQuestion().contains("ibm"))
		{
			event.setStatus(EventStatus.EVENT_MODIFIED_TYPE);
			event.setQuestion(event.getQuestion().replaceAll("ibm", "IBM"));
		}
		else if(event.isOpenChoiceAllowed())
		{
			if(event.getAnswers().size() >= 4)
			{
				event.setStatus(EventStatus.EVENT_MODIFIED_TYPE, "No open responses allowed if four choices are available!");
				event.disableOpenChoice();
			}
		}
		
	}

	public void customPropertiesChanged(Properties properties) {
		// TODO Auto-generated method stub
		
	}

	public void init(Properties properties, PluginStartupCallback callback) {
		System.out.println("Enter SamplePollSentHandler.init");

		if (callback != null)
		{
			callback.pluginStarted(PLUGIN_NAME);
		}
		
	}

	public void terminate() {
		// TODO Auto-generated method stub
		
	}

}
