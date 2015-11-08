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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.collaboration.realtime.event.EventStatus;
import com.ibm.collaboration.realtime.event.EventType;
import com.ibm.collaboration.realtime.event.UserJoinEvent;
import com.ibm.collaboration.realtime.event.UserJoinEventStatus;
import com.ibm.collaboration.realtime.plugin.PluginException;
import com.ibm.collaboration.realtime.plugin.PluginStartupCallback;
import com.ibm.collaboration.realtime.plugin.PluginUserJoinHandler;
import com.ibm.collaboration.realtime.plugin.User;

public class SampleJoinHandler implements PluginUserJoinHandler {

	private final static String PLUGIN_NAME =
		"com.ibm.rtc.compliance.sample.sampleJoinHandlerPlugin";
	
	private static Logger _logger = Logger.getLogger(SampleJoinHandler.class.getName());

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

	/**
	 * For a room whose name begins "HISTORY", an attempt to enter by a user
	 * containing the BLOCK_TARGET substring in their ID will be blocked if
	 * PAST_USER is found in the list of past users.
	 */
	public static final String BLOCK_TARGET = "tesla";
	/**
	 * @see BLOCK_TARGET
	 */
	public static final String PAST_USER = "turing";
	/**
	 * Has two different room permission checks: Rule 1 is that a room name
	 * following the pattern "FORBID.xyz" will block a user whose ID begins with
	 * "xyz". Rule 2 is a historical user check applied to rooms whose name
	 * begins "HISTORY": if the room has ever had a user with PAST_USER, then a
	 * visit from a user containing BLOCK_TARGET is blocked
	 * <p>
	 * If the room name begins FORBID then the disclaimer explains the
	 * block/allow rules, otherwise a more generic message is shown.
	 */
	public void userJoin(UserJoinEvent event) {
		User u = event.getOriginatingUser();
		String sessId = event.getSessionID();
		//String reqId = event.getRequestID();
		String currId = u.getID();
		String roomName = "UNINIT";
		try {
			roomName = event.getSessionDetailUtil().getSessionName(sessId);
		}
		catch (PluginException pe){
			_logger.log(Level.SEVERE, "Error getting room name", pe);
		}
		_logger.fine("userJoin for u.id = " + currId + " in room \"" + roomName + "\" (sess = " + sessId );
		
		// block rule 1:  if room name is "FORBID.xyz" then block any user id starting "xyz"
		if (roomName.startsWith("FORBID."))
		{
			String blockPhrase = roomName.substring(7);  //"FORBID.".length
			if (currId.startsWith(blockPhrase)){
				event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "User " + currId + " is not permitted in a room named " + roomName);
			} else {
				event.setStatus(EventStatus.EVENT_MODIFIED_TYPE, "The room name " 
						+ roomName + " will block any user whose ID starts " + blockPhrase
						+ "; user " + currId + " is permitted.");
			}
		}
		else if (roomName.startsWith("HISTORY"))
		{
			// block rule 2 for room HISTORY: if (PAST_USER) then block BLOCK_TARGET
			boolean isPastUser = false;
			try {
				//the user check is not written to be efficient but rather to demo/test getUser funcs
				//do only one user lookup loop (to be more representative of expected behavior
				Iterator iter = null;
				List currUsers = event.getSessionDetailUtil().getCurrentUsers(sessId);
				iter = currUsers.iterator();
				while (iter.hasNext()){
					User cu = (User)iter.next();
					String id = cu.getID();
					if (cu.isAnonymous())
						id = id + " [Anonymous]";
					_logger.fine("  current room user: " + id);
				}
				List roomUsers = event.getSessionDetailUtil().getHistoricalUsers(sessId);
				iter = roomUsers.iterator();
				while (iter.hasNext()){
					User ru = (User)iter.next();
					String id = ru.getID();
					if (ru.isAnonymous())
						id = id + " [Anonymous]";
					_logger.fine("  past room user: " + id);
					if (id.contains(PAST_USER))
					{
						isPastUser = true;
						_logger.fine("FOUND " + PAST_USER + " in historical users");
					}
				}
			}
			catch (Exception e){
				_logger.log(Level.SEVERE, "Error processing user join attempt", e);
			}
			boolean isTargetFound = currId.contains(BLOCK_TARGET);
			if (isTargetFound)
			{
				_logger.fine("Current User attempting Login is " + BLOCK_TARGET);
				if (isPastUser){
					_logger.fine("both " + PAST_USER + " and " + BLOCK_TARGET + " found - setting block");
					event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, BLOCK_TARGET + " may not join a room used by " + PAST_USER);
				}
				else
				{
					event.setStatus(EventStatus.EVENT_MODIFIED_TYPE, PAST_USER + " is not found among room users, so " + BLOCK_TARGET + " is permitted to join.");
				}
			}
			else {
				_logger.fine("Setting MOD + reason to show disclaimer");
				event.setStatus(EventStatus.EVENT_MODIFIED_TYPE, "NOTICE:  Room named HISTORY will not permit " + BLOCK_TARGET + " if user " + PAST_USER + " is found in room user lists" );
			}
		} 
		else  //no blocking 
		{
			event.setStatus(EventStatus.EVENT_MODIFIED_TYPE, "NOTICE:  Chat room discussions and contents are subject to corporate policies and are actively monitored for compliance" );
		}
	}

	public void customPropertiesChanged(Properties properties) {
		// TODO Auto-generated method stub
		
	}

	public void init(Properties properties, PluginStartupCallback callback) {
		_logger.fine("Enter SampleJoinHandler.init");

		if (callback != null)
		{
			callback.pluginStarted(PLUGIN_NAME);
		}
		
	}

	public void terminate() {
		// TODO Auto-generated method stub
		
	}

}
