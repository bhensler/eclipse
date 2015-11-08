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

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.ibm.collaboration.realtime.event.AddFAQEvent;
import com.ibm.collaboration.realtime.event.AddFAQEventStatus;
import com.ibm.collaboration.realtime.event.EventStatus;
import com.ibm.collaboration.realtime.plugin.PluginAddFAQHandler;
import com.ibm.collaboration.realtime.plugin.PluginException;
import com.ibm.collaboration.realtime.plugin.PluginStartupCallback;
import com.ibm.collaboration.realtime.plugin.User;

public class SampleFaqHandler implements PluginAddFAQHandler 
{
	private final static String PLUGIN_NAME =
		"com.ibm.rtc.compliance.sample.sampleFaqHandlerPlugin";
	
	private static Logger _logger = Logger.getLogger(SampleFaqHandler.class.getName());

	public void addFAQ(AddFAQEvent event) {
		String user = SampleUtil.getUserName(event);
		String question = event.getQuestion();
		_logger.fine("addFAQ: user = " + user + "\n session = " + event.getSessionID() + 
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

	public void editFAQ(AddFAQEvent event) {
		String user = SampleUtil.getUserName(event);
		String answer = event.getAnswer();
		_logger.fine("editFAQ: user = " + user + "\n session = " + event.getSessionID() + 
				"\n faqId = " + event.getFaqId() +
				"\n question = " + event.getQuestion() + "\n answer = " + answer);
		if (answer.startsWith("A")){
			String mod = "Aaay, " + answer;
			event.setAnswer(mod);
			event.setStatus(EventStatus.EVENT_MODIFIED_TYPE, "Edit FAQ text modified to comply with corporate policies");
		}
		if (answer.startsWith("B")){
			event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "Edit FAQ blocked per corporate policies");
		}
	}

	public void onAddFAQ(AddFAQEventStatus status) {
		String user = SampleUtil.getUserName(status);
		_logger.fine("onAddFAQ: user = " + user + "\n session = " + status.getSessionID() + 
				"\n faqId = " + status.getFaqId() +
				"\n question = " + status.getQuestion() + "\n answer = " + status.getAnswer()
				+ "\n status = " + SampleUtil.statusString(status.getStatus()) + "\n reason = " + status.getReason());
	}

	public void onEditFAQ(AddFAQEventStatus status) {
		String user = SampleUtil.getUserName(status);
		_logger.fine("onEditFAQ: user = " + user + "\n session = " + status.getSessionID() + 
				"\n faqId = " + status.getFaqId() +
				"\n question = " + status.getQuestion() + "\n answer = " + status.getAnswer()
				+ "\n status = " + SampleUtil.statusString(status.getStatus()) + "\n reason = " + status.getReason());
	}

	public void deleteFAQ(AddFAQEvent event)
	{
		String sessId = event.getSessionID();
		boolean permitAction = false;
		List userList = null;
		try {
			String roomName = event.getSessionDetailUtil().getSessionName(sessId);
			if (roomName.contains("Delete")){
				_logger.fine("Restricted Delete room (name contains \"Delete\")");
				userList = event.getSessionDetailUtil().getCurrentUsers(sessId);
			}
		}
		catch (PluginException pe) {
			_logger.warning("PluginException trying to getRoomName/Users on sessionId = " + sessId);
		}
		if (userList == null){
			event.setStatus(EventStatus.EVENT_MODIFIED_TYPE, "Note to test blocked deletion, use a room with \"Delete\" in its name");
		} else {
			boolean permitDelete = false;
			Iterator it = userList.iterator();
			while (it.hasNext()){
				User u = (User)it.next();
				String userId = u.getID();
				if (userId.contains("wasadmin") || userId.contains("7")){
					permitDelete = true;
					break;
				}
			}
			if (permitDelete){
				event.setStatus(EventStatus.EVENT_OK_TYPE, "Event.OK message");
			} else {
				event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "FAQ Delete BLOCKED - a user whose ID includes \"wasadmin\" or \"7\" must be present because the room name contains \"Delete\".");
			}
		}
	}
	
	
	public void onFAQDelete(AddFAQEventStatus status) {
		String user = SampleUtil.getUserName(status);
		_logger.fine("onFAQDelete: user = " + user + "\n session = " + status.getSessionID() + 
				"\n faqId = " + status.getFaqId() +
				"\n question = " + status.getQuestion() + "\n answer = " + status.getAnswer());
	}

	public void customPropertiesChanged(Properties properties) {
		// TODO Auto-generated method stub
		
	}

	public void init(Properties properties, PluginStartupCallback callback) {
		_logger.fine("Enter SampleFaqHandler.init");

		if (callback != null)
		{
			callback.pluginStarted(PLUGIN_NAME);
		}
	}

	public void terminate() {
		// TODO Auto-generated method stub
		
	}

}
