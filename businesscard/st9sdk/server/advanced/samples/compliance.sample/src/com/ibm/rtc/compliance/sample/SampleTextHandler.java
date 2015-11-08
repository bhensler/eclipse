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
import java.util.logging.Logger;

import com.ibm.collaboration.realtime.event.ChatTextEvent;
import com.ibm.collaboration.realtime.event.ChatTextEventStatus;
import com.ibm.collaboration.realtime.event.EventStatus;
import com.ibm.collaboration.realtime.plugin.PluginChatTextHandler;
import com.ibm.collaboration.realtime.plugin.PluginManager;
import com.ibm.collaboration.realtime.plugin.PluginRegistration;
import com.ibm.collaboration.realtime.plugin.PluginStartupCallback;

public class SampleTextHandler implements PluginChatTextHandler 
{
	private final static String PLUGIN_NAME =
		"com.ibm.rtc.compliance.sample.sampleTextHandlerPlugin";
	
	private static Logger _logger = Logger.getLogger(SampleTextHandler.class.getName());
	
	public void chatText(ChatTextEvent event) {
		String user = SampleUtil.getUserName(event);
		String text = event.getText();
		_logger.fine("ChatTextEvent, user = " + user + ", text = " + text);
		if ((text.startsWith("A")) || (text.contains("rock"))){
			String mod = "Aaay, " + text;
			event.setText(mod);
			event.setStatus(EventStatus.EVENT_MODIFIED_TYPE);
			event.setReason("SysAdmin-Auto:  Text modified to comply with corporate policies");
		}
		if ((text.startsWith("B")) || (text.contains("forbid"))) {
			event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "SysAdmin-Auto: Text blocked per corporate policies");
		}
		
	}

	public void onChatText(ChatTextEventStatus status) {
		int stat = status.getStatus();
		String statStr = "UNDEFINED STATUS";
		switch (stat) {
		case EventStatus.EVENT_OK_TYPE:
			statStr = "text permitted";
			break;
		case EventStatus.EVENT_BLOCKED_TYPE:
			statStr = "text blocked, reason = " + status.getReason();
			break;
		case EventStatus.EVENT_MODIFIED_TYPE:
			statStr = "text modified, reason = " + status.getReason()
			  + ", final text = " + status.getText();
			break;
		}
		_logger.fine(statStr);
		
	}

	public void customPropertiesChanged(Properties properties) {
		// TODO Auto-generated method stub
		
	}

	public void init(Properties properties, PluginStartupCallback callback) 
	{
		_logger.fine("Enter SampleTextHandler.init");

		if (callback != null)
		{
			callback.pluginStarted(PLUGIN_NAME);
		}
		
	}

	public void terminate() {
		_logger.fine("SampleTextHandler terminate");
		
	}

}
