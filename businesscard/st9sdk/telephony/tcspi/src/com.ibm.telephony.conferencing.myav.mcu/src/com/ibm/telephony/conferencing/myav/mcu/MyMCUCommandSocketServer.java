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

package com.ibm.telephony.conferencing.myav.mcu;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.telephony.conferencing.myav.MyAVConfig;
import com.ibm.telephony.conferencing.myav.MyAVConstants;
import com.ibm.telephony.conferencing.myav.MyAVLogger;

/**
 * TCP transport handler for incoming requests from 
 * <code>MyAVConferenceService</code> and outgoing notifications.
 */

public class MyMCUCommandSocketServer implements Runnable, MyAVConstants {
	static final String CNAME = MyMCUCommandSocketServer.class.getName();	
	private static final Logger logger = MyAVLogger.getLogger(MyMCUCommandSocketServer.class);
	
	private MyMCU myMcu;
	private CommandTransactionHandler transactionHandler;
	private NotificationHandler notificationHandler;
	
	private class MyMCUCommandHandler {
		String transactionId;
		String commandId;
		Map<String, String> parameters = new HashMap<String, String>();
		
		public MyMCUCommandHandler() {
		}
		
		private void read(ObjectInputStream in) throws IOException, ClassNotFoundException {
			final String MNAME = "read";
			 
			Properties message = (Properties) in.readObject();
			logger.logp(Level.FINEST, CNAME, MNAME, "message=" + message);
			
			transactionId = (String) message.remove("transactionId");
			commandId = (String) message.remove("commandId");
			
			for (Iterator<Object> iter = message.keySet().iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				parameters.put(key, message.getProperty(key));
			}
			
			logger.logp(Level.FINER, CNAME, MNAME, "transactionId=" + transactionId + ",commandId="+commandId+",parameters="+parameters);			
		}

		public void process() {
			final String MNAME = "process";
			logger.logp(Level.FINER, CNAME, MNAME, "transactionId=" + transactionId + ",commandId="+commandId+",parameters="+toShorterString(parameters.toString()));

			boolean success = true;
			Map<String, String> results = new HashMap<String, String>();
			
			try {
				if (commandId.equals(MYAV_CMD_ASSOCIATE)) {
					success = myMcu.mcuAssociate(parameters.get("namedUser"), parameters.get("connectedUser"), parameters.get("conferenceId"), results);
				} else if (commandId.equals(MYAV_CMD_CREATE_CONFERENCE)) {
					success = myMcu.mcuCreateConference(parameters.get("user"), parameters.get("conferenceDocumentText"), results);
				} else if (commandId.equals(MYAV_CMD_DELETE_CONFERENCE)) {
					success = myMcu.mcuDeleteConference(parameters.get("conferenceId"), results);				
				} else if (commandId.equals(MYAV_CMD_DIAL)) {
					success = myMcu.mcuDial(parameters.get("user"), parameters.get("endPoint"), parameters.get("dialMode"), parameters.get("conferenceId"), results);			
				} else if (commandId.equals(MYAV_CMD_DIAL_EXTENDED)) {
					success = myMcu.mcuDialExtended(parameters.get("user"), parameters.get("endPoint"), parameters.get("dialMode"), parameters.get("conferenceId"), parameters.get("mediaFlag"), results);
				} else if (commandId.equals(MYAV_CMD_DISCONNECT_USER)) {
					success = myMcu.mcuDisconnectUser(parameters.get("user"), parameters.get("conferenceId"), results);
				} else if (commandId.equals(MYAV_CMD_MEDIA_CONTROL)) {
					success = myMcu.mcuMediaControl(parameters.get("user"), parameters.get("conferenceId"), parameters.get("controlFlag"), Integer.parseInt(parameters.get("mediaFlag")), results);
				} else if (commandId.equals(MYAV_CMD_REGISTER_USER)) {
					success = myMcu.mcuRegisterUser(parameters.get("userName"), parameters.get("userRole"), parameters.get("userPin"), parameters.get("conferenceId"), results);
				} else if (commandId.equals(MYAV_CMD_REGISTER_USER_SIP)) {
					success = myMcu.mcuRegisterUserSip(parameters.get("userName"), parameters.get("userRole"), parameters.get("userPin"), parameters.get("sipUri"), parameters.get("conferenceId"), results);
				} else if (commandId.equals(MYAV_CMD_SET_CONFERENCE_PROPERTY)) {
					success = myMcu.mcuSetConferenceProperty(parameters.get("conferenceId"), parameters.get("propertyName"), parameters.get("propertyValue"), results);
				} else if (commandId.equals(MYAV_CMD_SET_USER_PROPERTY)) {
					success = myMcu.mcuSetUserProperty(parameters.get("userName"), parameters.get("propertyName"), parameters.get("propertyValue"), parameters.get("conferenceId"), results);
				} else if (commandId.equals(MYAV_CMD_START_CONFERENCE_CONTROL)) {
					success = myMcu.mcuStartConferenceControl(parameters.get("conferenceId"), parameters.get("loginId"), parameters.get("conferenceDocumentText"), results);
				} else if (commandId.equals(MYAV_CMD_STOP_CONFERENCE_CONTROL)) {
					success = myMcu.mcuStopConferenceControl(parameters.get("conferenceId"), results);
				} else if (commandId.equals(MYAV_CMD_UNREGISTER_USER)) {
					success = myMcu.mcuUnregisterUser(parameters.get("userName"), parameters.get("conferenceId"), results);
				} else if (commandId.equals(MYAV_CMD_TIMEOUT_ERROR)) {
					success = myMcu.mcuTimeoutError(results);
				} else {
					success = myMcu.mcuProcessDefaultCommand(commandId, parameters, results);
				}
			} catch (Exception e) {
				logger.logp(Level.SEVERE, CNAME, MNAME, "exception="+e+",transactionId="+transactionId+",commandId="+commandId+",parameters="+toShorterString(parameters.toString()), e);
				success = false;
			}
		
			if (success)
				sendSuccess(results);
			else 
				sendFailure(GENERAL_EXCEPTION_FAILURE, "Failed to execute "+commandId, results);
		}
		
		public void sendSuccess(Map<String, String> results) {
			final String MNAME = "sendSuccess";
			Properties notification = new Properties();
			
			notification.put("transactionId", transactionId);
			notification.put("origination", commandId); // include original command ID for trace			
			notification.put("commandId", "deliverSuccessResponse");
			
			for (Iterator<Entry<String, String>> iterator = results.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, String> entry = iterator.next();
				notification.put(entry.getKey(), entry.getValue());
			}
			
			sendNotification(notification);
			logger.logp(Level.FINE, CNAME, MNAME, "deliverSuccessResponse for notification="+notification);
		}
		
		public void sendFailure(String defaultErrorCode, String errorMessage, Map<String, String> results) {
			final String MNAME = "sendFailure";
			Properties notification = new Properties();
			
			notification.put("transactionId", transactionId);
			notification.put("origination", commandId); // include original command ID for trace
			notification.put("commandId", "deliverFailureResponse");
			if (results.containsKey("errorCode"))
				notification.put("errorCode", results.get("errorCode"));				
			else
				notification.put("errorCode", defaultErrorCode);
			notification.put("errorMessage", errorMessage);
			
			for (Iterator<Entry<String, String>> iterator = results.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, String> entry = iterator.next();
				notification.put(entry.getKey(), entry.getValue());
			}
			
			sendNotification(notification);			
			logger.logp(Level.FINE, CNAME, MNAME, "deliverFailureResponse for notification="+notification);
		}		
	}
	
	private class CommandTransactionHandler {
		final private String CNAME = CommandTransactionHandler.class.getName();
		private ObjectInputStream in;
		private ObjectOutputStream out;
		
		public CommandTransactionHandler(ObjectInputStream in, ObjectOutputStream out) {
			this.in = in;
			this.out = out;
		}
		
		public void run() {
			final String MNAME = "run";
			
			notificationHandler = new NotificationHandler(out);
			notificationHandler.start();
			
			boolean error = false;
			while (!error) {
				MyMCUCommandHandler command = new MyMCUCommandHandler();
				try {
					command.read(in);
					command.process();					
				} catch (Exception e) {
					error = true;
					logger.logp(Level.SEVERE, CNAME, MNAME, "Exception processing transactionId="+command.transactionId+",commandId="+command.commandId, e);
				}
			}
		}
	}
	
	private class NotificationHandler {
		final private String CNAME = NotificationHandler.class.getName();
		private ObjectOutputStream out;
		private boolean running = false;
		private List<Properties> notifications = new ArrayList<Properties>();
		private int notificationId = 0;
		
		public NotificationHandler(ObjectOutputStream out) {
			this.out = out;
		}
		
		private void start() {
			logger.logp(Level.INFO, CNAME, "start", "Start processing notifications");			
			running = true;
			
			Thread thread = new Thread(new Runnable() {
				public void run() {
					processNotifications();					
				}
			});
			thread.start();
		}
		
		private void stop() {
			logger.logp(Level.INFO, CNAME, "stop", "Stop processing notifications");
			
			running = false;
			synchronized (notifications) {
				notifications.clear();
			}
		}
		
		private void processNotifications()
		{
			final String MNAME = "processNotifications";			
			logger.logp(Level.FINER, CNAME, MNAME, "running="+Boolean.toString(running));			
				
			try {
				while (running) {
					Properties notification = null;					
					synchronized (notifications) {
						if (notifications.size() == 0) {
							notifications.wait();	
						} else {
							notification = notifications.remove(0);
						}
					}
						
					if (notification != null) {
						processNotification(notification);
					}
				}
			} catch (InterruptedException e) {
				logger.logp(Level.SEVERE, CNAME, MNAME, e.toString(), e);
				stop();
			} catch (IOException e) {
				logger.logp(Level.SEVERE, CNAME, MNAME, e.toString(), e);
				stop();				
			}
		}
		
		private void processNotification(Properties notification) throws IOException {
			final String MNAME = "processNotification";
			logger.logp(Level.FINER, CNAME, MNAME, "notification="+notification);
			
			out.writeObject(notification);
			out.flush();
		}

		private void sendNotification(Properties notification) {
			final String MNAME = "sendNotification";
		
			if (!notification.containsKey("transactionId")) {
					notificationId--;			
					notification.setProperty("transactionId", Integer.toString(notificationId));
			}
			
			if (!notification.containsKey("commandId")) {
					notification.setProperty("commandId", "deliverNotification");
			}
			logger.logp(Level.FINER, CNAME, MNAME, "Notifying with notification="+notification);			
			
			synchronized (notifications) {
				notifications.add(notification);
				notifications.notifyAll();
				logger.logp(Level.FINEST, CNAME, MNAME, "Notified with notification="+notification);				
			}
		}
	}
	
	public MyMCUCommandSocketServer(MyMCU myMcu) {
		this.myMcu = myMcu;
	}
	
	/**
	 * Notification from MCU to TCSPI Service adapter.
	 */
	
	public void sendNotification(Properties notification) {
		logger.logp(Level.FINE, CNAME, "sendNotification", "notification="+notification);
		
		notificationHandler.sendNotification(notification);
	}	

	private void processSocketRequest() {
		final String MNAME = "processSocketRequest";
		ServerSocket serverSocket = null;
		Socket connection = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		
		try {
			int port = 50001;
			try {
				port = Integer.parseInt(MyAVConfig.MYMCU_HOST_COMMAND_PORT);
			} catch (NumberFormatException e) {
				logger.logp(Level.WARNING, CNAME, MNAME, "Invalid port '"+MyAVConfig.MYMCU_HOST_COMMAND_PORT+"' using default of "+port);
			}
			logger.logp(Level.INFO, CNAME, MNAME, "MCU command server accepting connections at "+MyAVConfig.MYMCU_HOST_NAME+":"+port);
			
			serverSocket = new ServerSocket(port);
			connection = serverSocket.accept();
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			
			logger.logp(Level.FINER, CNAME, MNAME, "Starting " + CommandTransactionHandler.class.getSimpleName());			
			transactionHandler = new CommandTransactionHandler(in, out);
			transactionHandler.run();
		} catch (SocketException e) {
			logger.logp(Level.SEVERE, CNAME, MNAME, "Caught exception and continuing: " + e.toString(), e);
		} catch (IOException e) {
			logger.logp(Level.SEVERE, CNAME, MNAME, "Caught exception and continuing: " + e.toString(), e);
		} finally {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
				if (connection != null)
					connection.close();
				if (serverSocket != null)
					serverSocket.close();
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
	}

	public void run() {
		final String MNAME = "run";
		
		logger.logp(Level.INFO, CNAME, MNAME, "Configuration="+MyAVConfig.properties);		
		try {
			while (true) {
				processSocketRequest();
				logger.logp(Level.WARNING, CNAME, MNAME, "processSocketRequest returned, retrying in 10 seconds.");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {}				
			}
		} catch (Exception e) {
			logger.logp(Level.SEVERE, CNAME, MNAME, "processSocketRequest exception, retrying in 10 seconds", e);
		}		
	}
	
	//
	// Logging methods for pretty printing objects of interest.
	//	
	
	private String toShorterString(String s) {
		if (s == null || s.length() < 80 || logger.isLoggable(Level.FINEST)) {
			return s;
		} else {
			return s.substring(0, 80) + "...";
		}
	}	
}
