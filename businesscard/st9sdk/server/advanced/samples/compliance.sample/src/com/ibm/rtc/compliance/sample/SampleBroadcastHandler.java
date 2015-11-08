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

import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.ibm.collaboration.realtime.event.AddFAQEvent;
import com.ibm.collaboration.realtime.event.AddFAQEventStatus;
import com.ibm.collaboration.realtime.event.AnnouncementEvent;
import com.ibm.collaboration.realtime.event.AnnouncementEventStatus;
import com.ibm.collaboration.realtime.event.BroadcastChatEvent;
import com.ibm.collaboration.realtime.event.BroadcastChatEventStatus;
import com.ibm.collaboration.realtime.event.EventStatus;
import com.ibm.collaboration.realtime.event.EventType;
import com.ibm.collaboration.realtime.event.InstantPollEvent;
import com.ibm.collaboration.realtime.event.InstantPollEventStatus;
import com.ibm.collaboration.realtime.event.InstantPollcastEventStatus;
import com.ibm.collaboration.realtime.event.SkilltapRequestEvent;
import com.ibm.collaboration.realtime.event.SkilltapRequestEventStatus;
import com.ibm.collaboration.realtime.event.UserJoinEvent;
import com.ibm.collaboration.realtime.event.UserJoinEventStatus;
import com.ibm.collaboration.realtime.plugin.PluginBroadcastHandler;
import com.ibm.collaboration.realtime.plugin.PluginStartupCallback;

public class SampleBroadcastHandler implements PluginBroadcastHandler {

	private final static String PLUGIN_NAME =
		"com.ibm.rtc.compliance.sample.sampleBroadcastHandlerPlugin";
	
	private static Logger _logger = Logger.getLogger(SampleBroadcastHandler.class.getName());
	
	public void announcement(AnnouncementEvent event) {

		_logger.fine("announcement, session = " + event.getSessionID()
				+ "\n user = " + event.getOriginatingUser().getID()
				+ "\n orig text = " + event.getText());
		String text = event.getText();
		if ((text.startsWith("A")) || (text.contains("rock"))){
			String mod = "Aaay, " + text;
			event.setText(mod);
			event.setStatus(EventStatus.EVENT_MODIFIED_TYPE);
			event.setReason("SysAdmin-Auto:  Text modified to comply with corporate policies");
		}
		if ((text.startsWith("B")) || (text.contains("gesundheit"))){
			event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "SysAdmin-Auto: Text blocked per corporate policies");
		}
	}

	public void onAnnouncement(AnnouncementEventStatus status) {
		_logger.fine("onAnnouncement, session = " + status.getSessionID()
				+ "\n user = " + status.getOriginatingUser().getID()
				+ "\n FINAL text = " + status.getText()
				+ "\n status = " + SampleUtil.statusString(status.getStatus())
				+ "\n reason = " + status.getReason());
	}

	public void customPropertiesChanged(Properties properties) {
		// TODO Auto-generated method stub

	}

	public void init(Properties properties, PluginStartupCallback callback) {
		_logger.fine("Enter SampleBroadcastHandler.init");

		if (callback != null)
		{
			callback.pluginStarted(PLUGIN_NAME);
		}
	}

	public void terminate() {
		// TODO Auto-generated method stub

	}

	public void broadcastChat(BroadcastChatEvent event) {
		_logger.fine("broadcastChat, session = " + event.getSessionID()
				+ "\n user = " + event.getOriginatingUser().getID()
				+ "\n orig text = " + event.getText());
		String text = event.getText();
		if ((text.startsWith("A")) || (text.contains("rock"))){
			String mod = "Aaay, " + text;
			event.setText(mod);
			event.setStatus(EventStatus.EVENT_MODIFIED_TYPE);
			event.setReason("SysAdmin-Auto:  Text modified to comply with corporate policies");
		}
		if ((text.startsWith("B")) || (text.contains("gesundheit"))){
			event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "SysAdmin-Auto: Text blocked per corporate policies");
		}
	}

	public void onBroadcastChat(BroadcastChatEventStatus status) {
		_logger.fine("onBroadcastChat, session = " + status.getSessionID()
				+ "\n user = " + status.getOriginatingUser().getID()
				+ "\n FINAL text = " + status.getText()
				+ "\n status = " + SampleUtil.statusString(status.getStatus())
				+ "\n reason = " + status.getReason());
	}

	@Override
	public void instantPoll(InstantPollEvent event) {
		String ques = event.getQuestion();
		String[] ans = event.getAnswers();
		_logger.fine("instantPoll, session = " + event.getSessionID()
				+ "\n user = " + event.getOriginatingUser().getID()
				+ "\n orig question = " + ques
				+ "\n first answer = " + ans[0]);
		if ((ques.startsWith("A")) || (ques.contains("rock"))){
			String mod = "Aaay, " + ques;
			event.setQuestion(mod);
			event.setStatus(EventStatus.EVENT_MODIFIED_TYPE);
			event.setReason("SysAdmin-Auto:  Text modified to comply with corporate policies");
		}
		if ((ques.startsWith("B")) || (ques.contains("gesundheit"))){
			event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "SysAdmin-Auto: Text blocked per corporate policies");
		} else {
			boolean ansMod = false;
			for (int i = 0; i < ans.length; i++){
				String a = ans[i];
				if ((a.startsWith("A")) || (a.contains("rock"))){
					String mod = "Aaay, " + a;
					ans[i] = mod;
					ansMod = true;
				}
			}
			if (ansMod){
				event.setAnswers(ans);
				event.setStatus(EventStatus.EVENT_MODIFIED_TYPE);
				event.setReason("SysAdmin-Auto:  Answers modified to comply with corporate policies");
			}
		}
		
		
	}

	public void onInstantPoll(InstantPollEventStatus status)
	{
		String ques = status.getQuestion();
		String[] ans = status.getAnswers();
		
		if (status instanceof InstantPollcastEventStatus)
		{
			String pollcast = ((InstantPollcastEventStatus)status).getPollcastSession();
			
			switch(status.getEventType())
			{
				case EventType.INSTANTPOLL_EVENT_TYPE:
					_logger.fine("onInstantPoll - INSTANTPOLL_EVENT_TYPE"
							+ "\n session = " + status.getSessionID()
							+ "\n pollcast " + pollcast
							+ "\n user = " + status.getOriginatingUser().getID()
							+ "\n orig question = " + ques
							+ "\n first answer = " + ans[0]
			 				+ "\n status = " + SampleUtil.statusString(status.getStatus())
							+ "\n reason = " + status.getReason());
					break;
					
				case EventType.INSTANTPOLL_RESPONSE_EVENT_TYPE:
					String resp = "";
					for (int i=0; i<ans.length; i++) {
						resp += "[" + ans[i] + ( i+1==ans.length ? "]" : "] , " );
					}
					_logger.fine("onInstantPoll - INSTANTPOLL_RESPONSE_EVENT_TYPE"
							+ "\n session = " + status.getSessionID()
							+ "\n pollcast = " + pollcast
							+ "\n user = " + status.getOriginatingUser().getID()
							+ "\n responses = " + resp);
					break;
					
				default:
					_logger.fine("onInstantPoll, invalid event type: " + status.getEventType()
						+ "\n session = " + status.getSessionID()
						+ "\n user = " + status.getOriginatingUser().getID()
						+ "\n orig pollcast = " + pollcast);
					break;
			}
		}
		else
		{
			_logger.fine("onInstantPoll- using 8.5.2 API");
			_logger.fine("onInstantPoll, session = " + status.getSessionID()
					+ "\n user = " + status.getOriginatingUser().getID()
					+ "\n orig question = " + ques
					+ "\n first answer = " + ans[0]
	 				+ "\n status = " + SampleUtil.statusString(status.getStatus())
					+ "\n reason = " + status.getReason());
		}
	}

	public void onSkilltapRequest(SkilltapRequestEventStatus status) {
		_logger.fine("onSkilltapRequest, session = " + status.getSessionID()
				+ "\n user = " + status.getOriginatingUser().getID()
				+ "\n FINAL text = " + status.getText()
				+ "\n status = " + SampleUtil.statusString(status.getStatus())
				+ "\n reason = " + status.getReason());
	}

	public void skilltapRequest(SkilltapRequestEvent event) {
		_logger.fine("skilltapRequest, session = " + event.getSessionID()
				+ "\n user = " + event.getOriginatingUser().getID()
				+ "\n orig text = " + event.getText());
		String text = event.getText();
		if ((text.startsWith("A")) || (text.contains("rock"))){
			String mod = "Aaay, " + text;
			event.setText(mod);
			event.setStatus(EventStatus.EVENT_MODIFIED_TYPE);
			event.setReason("SysAdmin-Auto:  Text modified to comply with corporate policies");
		}
		if ((text.startsWith("B")) || (text.contains("gesundheit"))){
			event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "SysAdmin-Auto: Text blocked per corporate policies");
		}
	}

	public void addSkilltapFAQ(AddFAQEvent event) {
		String user = SampleUtil.getUserName(event);
		String question = event.getQuestion();
		_logger.fine("addSkilltapFAQ: user = " + user + "\n session = " + event.getSessionID() + 
				"\n faqId = " + event.getFaqId() +
				"\n question = " + question + "\n answer = " + event.getAnswer());
		if (question.startsWith("A")){
			String mod = "Aaay, " + question;
			event.setQuestion(mod);
			event.setStatus(EventStatus.EVENT_MODIFIED_TYPE, "Create FAQ text modified to comply with corporate policies");
		}
		if (question.startsWith("B")){
			event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "Create FAQ blocked per corporate policies");
		}
	}

	public void onAddSkilltapFAQ(AddFAQEventStatus status) {
		String user = SampleUtil.getUserName(status);
		_logger.fine("onAddSkilltapFAQ: user = " + user + "\n session = " + status.getSessionID() + 
				"\n faqId = " + status.getFaqId() +
				"\n question = " + status.getQuestion() + "\n answer = " + status.getAnswer()
				+ "\n status = " + SampleUtil.statusString(status.getStatus()) + "\n reason = " + status.getReason());
	}

	public void editSkilltapFAQ(AddFAQEvent event) {
		String user = SampleUtil.getUserName(event);
		String answer = event.getAnswer();
		_logger.fine("editSkilltapFAQ: user = " + user + "\n session = " + event.getSessionID() + 
				"\n faqId = " + event.getFaqId() +
				"\n question = " + event.getQuestion() + "\n answer = " + answer);
		if (answer.startsWith("A")){
			String mod = "Aaay, " + answer;
			event.setAnswer(mod);
			event.setStatus(EventStatus.EVENT_MODIFIED_TYPE, "Create FAQ text modified to comply with corporate policies");
		}
		if (answer.startsWith("B")){
			event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "Create FAQ blocked per corporate policies");
		}
	}

	public void onEditSkilltapFAQ(AddFAQEventStatus status) {
		String user = SampleUtil.getUserName(status);
		_logger.fine("onEditSkilltapFAQ: user = " + user + "\n session = " + status.getSessionID() + 
				"\n faqId = " + status.getFaqId() +
				"\n question = " + status.getQuestion() + "\n answer = " + status.getAnswer()
				+ "\n status = " + SampleUtil.statusString(status.getStatus()) + "\n reason = " + status.getReason());
	}

	public void deleteSkilltapFAQ(AddFAQEvent event) {
		_logger.fine("deleteSkilltapFAQ: user = " + event.getOriginatingUser().getID() 
				+ "\n session = " + event.getSessionID() + 
				"\n faqId = " + event.getFaqId() +
				"\n question = " + event.getQuestion() + "\n answer = " + event.getAnswer());
		String ques = event.getQuestion();
		if (ques.startsWith("Delete")){
			String ans = event.getAnswer();
			if (!ans.startsWith("OK")){
				event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "FAQ Ques Starts \"Delete\", Answer must begin with \"OK\" to delete");
			}
		}
	}

	public void onSkilltapFAQDelete(AddFAQEventStatus status) {
		_logger.fine("onSkilltapFAQDelete: user = " + status.getOriginatingUser().getID()
				+ "\n session = " + status.getSessionID() + 
				"\n faqId = " + status.getFaqId() +
				"\n question = " + status.getQuestion() + "\n answer = " + status.getAnswer()
				+ "\n status = " + SampleUtil.statusString(status.getStatus()) + "\n reason = " + status.getReason());
	}

	public void onUserJoin(UserJoinEventStatus status) {
		String sessId = status.getSessionID();
		int stat = status.getStatus();
		String statStr = SampleUtil.statusString(stat);
		String uID = SampleUtil.getUserName(status);
		String type = "UNKOWN_TYPE";
		if (status.getEventType() == EventType.USER_JOIN_EVENT_TYPE)
		{
			type = "JOIN";
		}
		else if (status.getEventType() == EventType.USER_LEAVE_EVENT_TYPE)
		{
			type = "LEAVE";
		}
		_logger.fine("OnUserJoin-" + type + " \n user = " + uID + "\n for session = " + sessId +
				"\n Result = " + statStr + ", reason = " + status.getReason());
		
	}
	
	public void userJoin(UserJoinEvent event){
		_logger.fine("userJoin, session = " + event.getSessionID()
				+ "\n user = " + event.getOriginatingUser().getID());

		String sessId = event.getSessionID();
		if (sessId.startsWith("FORBID.")){
			StringTokenizer st = new StringTokenizer(sessId);
			String firstWord = st.nextToken();
			String forbidWord = firstWord.substring(7);
			String user = event.getOriginatingUser().getID();
			if (user.startsWith(forbidWord)){
				event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "Community named " + firstWord 
						+ " inidcates to block userId starting " + forbidWord + ", so blocking user = " + user);
			}
			else {
				event.setStatus(EventStatus.EVENT_MODIFIED_TYPE, "Community named " + firstWord 
						+ " inidcates to block userId starting " + forbidWord + ", current user permitted = " + user);
			}
		}
	}
}
