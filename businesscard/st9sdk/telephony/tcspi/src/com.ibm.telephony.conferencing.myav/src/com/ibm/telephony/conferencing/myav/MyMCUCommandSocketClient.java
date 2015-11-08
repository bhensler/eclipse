/*
 * Licensed Materials - Property of IBM
 *
 * L-MCOS-96LQPJ
 *
 * (C) Copyright IBM Corp. 2006, 2013. All rights reserved.
 *
 * US Government Users Restricted Rights- Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package com.ibm.telephony.conferencing.myav;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * TCP transport handler for <code>MyMCUCommand</code> requests to 
 * <code>MyMCU</code> and incoming notifications from <code>MyMCU</code>.
 * It runs on a dedicated thread created during the service startup.
 *
 * @see MyMCUCommand
 */

public class MyMCUCommandSocketClient implements Runnable, MyAVConstants {
	private static final String CNAME = MyMCUCommandSocketClient.class.getName();
	private static final Logger logger = MyAVLogger.getLogger(MyMCUCommandSocketClient.class);
	
	private MyAVConferenceService myConferenceService;	
	
	private Thread mcuCommandClientThread = null;
	private ObjectOutputStream mcuCommandOutputStream = null;
	private Socket mcuCommandSocket = null;	
	
	public MyMCUCommandSocketClient(MyAVConferenceService myConferenceService) {
		this.myConferenceService = myConferenceService;
	}
	
	public void run() {
		final String MNAME = "run";
		
		while (true) {
			openSocket();
		
			if (mcuCommandSocket == null) {
				logger.logp(Level.SEVERE, CNAME, MNAME, "Unable to connect to MCU command server at "+
						MyAVConfig.MYMCU_HOST_NAME+":"+MyAVConfig.MYMCU_HOST_COMMAND_PORT);
				
				try {
					logger.logp(Level.FINE, CNAME, MNAME, "Retrying connection to MCU command server at "+MyAVConfig.MYMCU_HOST_NAME+":"+
							MyAVConfig.MYMCU_HOST_COMMAND_PORT+" in 1 minute");
					Thread.sleep(60000);
				} catch (InterruptedException ie) {}			
			} else {
				logger.logp(Level.INFO, CNAME, MNAME, "Connected to MCU command server at "+MyAVConfig.MYMCU_HOST_NAME+":"+MyAVConfig.MYMCU_HOST_COMMAND_PORT);
				myConferenceService.setMyMCUCommandSocketClientAvailable(true);			
			
				ObjectInputStream in = null;
				try {				
					mcuCommandOutputStream = new ObjectOutputStream(mcuCommandSocket.getOutputStream());
					in = new ObjectInputStream(mcuCommandSocket.getInputStream());
		
					while (mcuCommandOutputStream != null) {
						processCommand(in);
					}
				} catch (IOException e) {
					// This occurs if the MyMCU application is shutdown
					logger.logp(Level.SEVERE, CNAME, MNAME, e.toString(), e);					
				} catch (ClassNotFoundException e) {
					logger.logp(Level.SEVERE, CNAME, MNAME, e.toString(), e);
				} finally {
					logger.logp(Level.INFO, CNAME, MNAME, "Stopping MCU command client");					
					try {					
						if (in != null)
							in.close();
						if (mcuCommandSocket != null)
							mcuCommandSocket.close();
						if (mcuCommandOutputStream != null)
							mcuCommandOutputStream.close();
						mcuCommandOutputStream = null;
						mcuCommandSocket = null;
					} catch (IOException e) {
						logger.logp(Level.SEVERE, CNAME, MNAME, e.toString(), e);
					}
					myConferenceService.setMyMCUCommandSocketClientAvailable(false);
				}
			}
		}
	}

	private void openSocket() {
		final String MNAME = "openSocket";
		
		// The MCU command server is started by MyMCU and may not be immediately
		// available; retry for a few minutes before giving up.
		int tries = 0;
		while (mcuCommandSocket == null && tries < 30) {
			try {
				int port = 50001;
				try {
					port = Integer.parseInt(MyAVConfig.MYMCU_HOST_COMMAND_PORT);
				} catch (NumberFormatException e) {
					logger.logp(Level.WARNING, CNAME, MNAME, "Invalid port '"+MyAVConfig.MYMCU_HOST_COMMAND_PORT+"' using default of "+port);
				}
				logger.logp(Level.INFO, CNAME, MNAME, "Connecting to MCU command server at "+MyAVConfig.MYMCU_HOST_NAME+":"+port);				
				mcuCommandSocket = new Socket(MyAVConfig.MYMCU_HOST_NAME, port);
			} catch (IOException e) {
				logger.logp(Level.FINER, CNAME, MNAME, "Retrying connection to MCU command server at "+MyAVConfig.MYMCU_HOST_NAME+":"+MyAVConfig.MYMCU_HOST_COMMAND_PORT);
				tries++;
				try {
					Thread.sleep(10000);
				} catch (InterruptedException ie) {}
			}
		}
	}
	
	private void processCommand(ObjectInputStream in) throws NumberFormatException, IOException, ClassNotFoundException {
		final String MNAME = "processCommand";
		
		logger.logp(Level.FINEST, CNAME, MNAME, "Waiting for next command");
		Properties message = (Properties) in.readObject();
		int transactionId = Integer.parseInt((String) message.remove("transactionId"));
		String commandId = (String) message.remove("commandId");
		
		Map<String, String> newResults = new HashMap<String, String>();
		for (Iterator<Entry<Object, Object>> iter = message.entrySet().iterator(); iter.hasNext();) {			
			Entry<Object, Object> entry = iter.next();
			newResults.put((String) entry.getKey(), (String) entry.getValue());
		}
		logger.logp(Level.FINER, CNAME, MNAME, "transactionId="+transactionId+",response="+commandId+",results="+newResults);
		
		MyMCUCommand command = myConferenceService.removePendingCommand(transactionId);
		if (command != null) {
			command.setResults(newResults);
			if (commandId.equals("deliverFailureResponse")) {
				logger.logp(Level.FINE, CNAME, MNAME, "delivering deliverFailureResponse for "+command.getId()+",transactionId="+transactionId+",parameters="+command.getParameters());									
				command.deliverFailureResponse();
			} else if (commandId.equals("deliverSuccessResponse")) {
				logger.logp(Level.FINE, CNAME, MNAME, "delivering deliverSuccessResponse for "+command.getId()+",transactionId="+transactionId+",parameters="+command.getParameters());									
				command.deliverSuccessResponse();
			} else {
				logger.logp(Level.WARNING, CNAME, MNAME, "Unrecognized response="+command.getId()+",transactionId="+transactionId+",parameters="+command.getParameters());					
			}
		} else {
			// Special case for notifications
			if (transactionId <= 0) {
				logger.logp(Level.FINER, CNAME, MNAME, "notificationId="+transactionId+",response="+commandId+",results="+newResults);
				
				if (commandId.equals(MYAV_NOTIFICATION_USER_ACK) ||
						commandId.equals(MYAV_NOTIFICATION_USER_BYE) || 
						commandId.equals(MYAV_NOTIFICATION_USER_INVITE) ||
						commandId.equals(MYAV_NOTIFICATION_USER_SET_PROPERTY)){
					String userUri = newResults.get("from");
					if (userUri != null) {
						int i = userUri.indexOf(';');
						if (i > 0) {
							userUri = userUri.substring(0, i);
						}
					}
					String conferenceId = newResults.get("to");
					if (conferenceId != null) {
						int i = conferenceId.indexOf(':');
						int j = conferenceId.indexOf('@');
						if (i > 0 && j > 0) {
							conferenceId = conferenceId.substring(i+1, j);
						}
					}
					
					if (commandId.equals(MYAV_NOTIFICATION_USER_INVITE))
						myConferenceService.mcuNotifyUserInvite(conferenceId, userUri);
					else if (commandId.equals(MYAV_NOTIFICATION_USER_BYE)) 
						myConferenceService.mcuNotifyUserBye(conferenceId, userUri);
					else if (commandId.equals(MYAV_NOTIFICATION_USER_ACK))
						myConferenceService.mcuNotifyUserAck(conferenceId, userUri);
					else if (commandId.equals(MYAV_NOTIFICATION_USER_SET_PROPERTY))
						myConferenceService.mcuNotifyUserSetProperty(conferenceId, newResults.get("userName"), newResults.get("propertyName"), newResults.get("propertyValue"));
				} else {
					logger.logp(Level.WARNING, CNAME, MNAME, "Unrecognized notification for response="+commandId+",transactionId="+transactionId+",results="+newResults);						
				}
			} else {
				logger.logp(Level.WARNING, CNAME, MNAME, "Unrecognized command for response="+commandId+",transactionId="+transactionId+",results="+newResults);
			}
		}
	}
	
	public void start() {
		final String MNAME = "start";
		logger.entering(CNAME, MNAME);
		
		logger.logp(Level.INFO, CNAME, MNAME, "Configuration="+MyAVConfig.properties);		
		
		mcuCommandClientThread = new Thread(this);
		mcuCommandClientThread.start();
		
		// Wait until socket thread has started before returning; shouldn't take but a moment.
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		} while (mcuCommandOutputStream == null && mcuCommandClientThread != null);
		
		logger.exiting(CNAME, MNAME);		
	}
	
	public void stop() {
		try {
			if (mcuCommandSocket != null)				
				mcuCommandSocket.close();
			
			mcuCommandClientThread = null;
			mcuCommandSocket = null;
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	ObjectOutputStream getOutputStream() {
		return mcuCommandOutputStream;
	}		
}
