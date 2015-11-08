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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;

import com.ibm.telephony.conferencing.myav.MyAVConfig;
import com.ibm.telephony.conferencing.myav.MyAVConstants;
import com.ibm.telephony.conferencing.myav.MyAVLogger;

/**
 * Simple "fake MCU" that coordinates the handling of requests from 
 * and notifications to <code>MyAVConferenceService</code>.
 * This communication between the conference service and MCU 
 * passes by way of the <code>MyMCUCommandSocketServer</code>
 * running on the MCU server (siplet) and the <code>MyMCUCommandSocketClient</code> 
 * running on the Sametime Media Server.
 */

public class MyMCU implements MyAVConstants {
	private static final String CNAME = MyMCU.class.getName();
	private static final Logger logger = MyAVLogger.getLogger(MyMCU.class);
	
	private MyMCUCommandSocketServer mcuCommandSocketServer;
	private Thread mcuCommandSocketServerThread;
	private SipFactory sipFactory;
	private Timer registerTimer;
	private RegistrationTimerTask registrationTimerTask;
	private SimulateTalkingUsersTimerTask simulateTalkingUsersTimerTask;
	private static boolean lastSimulateIsTalkingState = false;
	private Timer simulateTalkingUsersTimer;
	private Map<String, MyMCUConference> conferences = new ConcurrentHashMap<String, MyMCUConference>();
	
	private class RegistrationTimerTask extends TimerTask {
		final String CNAME = RegistrationTimerTask.class.getName();
	
		public void run() {
			final String MNAME = "run";
			
			int activeConfs = getConferences().size();
			if (activeConfs > 0) {
				logger.logp(Level.FINE, CNAME, MNAME, "Re-registering "+activeConfs+" ongoing conference(s)");
				
				// Re-register ongoing conferences
				for (Iterator<MyMCUConference> iter = getConferences().iterator(); iter.hasNext();) {
					MyMCUConference conf = iter.next();
					if (!conf.renewRegistration()) {
						logger.logp(Level.WARNING, CNAME, MNAME, "Ignoring terminated conference "+conf.getId());
					}
				}
			} else {
				logger.logp(Level.FINEST, CNAME, MNAME, "No active conferences");
			}
		}
	}	
	
	private class SimulateTalkingUsersTimerTask extends TimerTask {
		final String CNAME = SimulateTalkingUsersTimerTask.class.getName();
	
		public void run() {
			final String MNAME = "run";
			int connectedUsersCount = 0;
			
			for (Iterator<MyMCUConference> iter = getConferences().iterator(); iter.hasNext();) {
				MyMCUConference conf = iter.next();
				String[] connectedUserNames = conf.getConnectedUserNames();
				
				for (String userName : connectedUserNames) {
					connectedUsersCount++;
					
					// Notify service provider to update speaking status of this user
					boolean isTalking = lastSimulateIsTalkingState;
					lastSimulateIsTalkingState = !lastSimulateIsTalkingState;
					Properties notification = new Properties();
					notification.setProperty("commandId", MYAV_NOTIFICATION_USER_SET_PROPERTY);
					notification.setProperty("to", conf.getId());
					notification.setProperty("userName", userName);
					notification.setProperty("propertyName", USER_TALKING_PROPERTY);
					notification.setProperty("propertyValue", Boolean.toString(isTalking));
							
					logger.logp(Level.FINEST, CNAME, MNAME, "Notifying service provider of user="+userName+" talking="+isTalking);		
					notifyMyConferenceService(notification);					
				}
			}
			
			if (connectedUsersCount > 0) {
				logger.logp(Level.FINER, CNAME, MNAME, "Simulated speaker change notifications = "+connectedUsersCount);

				// Toggle the last talking state so the same person isn't always talking				
				if ((connectedUsersCount % 2) == 0) {
					lastSimulateIsTalkingState = !lastSimulateIsTalkingState;
				}
			}
		}
	}			
	
	public MyMCU(SipFactory sipFactory) {
		this.sipFactory = sipFactory;
	}
	
	public Collection<MyMCUConference> getConferences() {
		return conferences.values();
	}
	
	public void start() {
		final String MNAME = "start";
		
		logger.logp(Level.INFO, CNAME, MNAME, "Configuration="+MyAVConfig.properties);
		
		logger.logp(Level.FINE, CNAME, MNAME, "Starting MCU command server");
		mcuCommandSocketServer = new MyMCUCommandSocketServer(this);
		mcuCommandSocketServerThread = new Thread(mcuCommandSocketServer);
		mcuCommandSocketServerThread.start();	
		
		if (MyAVConfig.USE_SIP_CONTROL) {
			logger.logp(Level.FINER, CNAME, MNAME, "Starting registration timer");
			registrationTimerTask = new RegistrationTimerTask();			
			registerTimer = new Timer("RegisterTimer", true);
			registerTimer.scheduleAtFixedRate(registrationTimerTask, SIP_REGISTER_DELAY_MS, SIP_REGISTER_INTERVAL_MS);
		}
		
		logger.logp(Level.FINER, CNAME, MNAME, "Starting talking user simulation timer");		
		simulateTalkingUsersTimerTask = new SimulateTalkingUsersTimerTask();
		simulateTalkingUsersTimer = new Timer("SimulateTalkingUsersTimer");
		simulateTalkingUsersTimer.scheduleAtFixedRate(simulateTalkingUsersTimerTask, 30000, 30000);
		
		logger.logp(Level.INFO, CNAME, MNAME, "MyMCU available");		
	}

	private void notifyMyConferenceService(Properties notification) {
		mcuCommandSocketServer.sendNotification(notification);
	}
	
	private void cleanupConference(MyMCUConference conference) {
		String conferenceId = conference.getId();		
		logger.logp(Level.FINER, CNAME, "cleanupConference", "cleanupConference("+conferenceId+")");		
		conference.stop();
		conferences.remove(conferenceId);
	}

	/**
	 * Equivalent MCU-specific methods for processing TCSPI service requests.
	 */
	
	public boolean mcuAssociate(String namedUser, String connectedUser, String conferenceId, Map<String, String> results) {
		logger.logp(Level.FINE, CNAME, "mcuAssociate", "mcuAssociate("+namedUser+","+connectedUser+","+conferenceId+")");
		
		// TODO Add MCU-specific code
		return true;
	}	

	public boolean mcuUnregisterUser(String user, String conferenceId, Map<String, String> results) {
		logger.logp(Level.FINE, CNAME, "mcuUnregisterUser", "mcuUnregisterUser("+user+","+conferenceId+")");
		
		// TODO Add MCU-specific code
		return true;
	}
	
	public boolean mcuTimeoutError(Map<String, String> results) {
		logger.logp(Level.FINE, CNAME, "mcuTimeoutError", "mcuTimeoutError()");
		
		// In this case, returning true doesn't indicate success, it indicates
		// the notification of a timeout was acknowledged and a error code/message provided.
		results.put("errorCode", TIMEOUT_FAILURE);
		results.put("errorMessage", getClass().getSimpleName()+": Timeout occurred");		
		
		// TODO Add MCU-specific code
		
		// Note: Returning false would indicate the timeout error could not be processed
		// for some reason and the service provider should show the user a general failure
		// error message.
		return true;
	}	

	public boolean mcuStopConferenceControl(String conferenceId, Map<String, String> results) {
		final String MNAME = "mcuStopConferenceControl";		
		logger.logp(Level.FINE, CNAME, MNAME, MNAME+"("+conferenceId+")");
		
		MyMCUConference conf = conferences.get(conferenceId);
		if (conf != null) {
			cleanupConference(conf);
		} else {
			logger.logp(Level.WARNING, CNAME, MNAME, "Ignoring unrecognized conference "+conferenceId);			
		}
		
		// TODO Add MCU-specific code
		return true;
	}

	public boolean mcuStartConferenceControl(String conferenceId, String loginId, String conferenceDocumentText, Map<String, String> results) {
		final String MNAME = "mcuStartConferenceControl";
		logger.logp(Level.FINE, CNAME, MNAME, MNAME+"("+conferenceId+","+loginId+","+MyAVLogger.toShorterString(logger, conferenceDocumentText)+")");
		
		MyMCUConference conf = conferences.get(conferenceId);
		if (conf == null) {
			logger.logp(Level.WARNING, CNAME, MNAME, "Ignoring unrecognized conference "+conferenceId);
			return false;
		}
		
		conf.start();
		
		// TODO Add MCU-specific code
		return true;
	}

	public boolean mcuProcessDefaultCommand(String commandId, Map<String, String> parameters, Map<String, String> results) {
		logger.logp(Level.WARNING, CNAME, "mcuProcessDefaultCommand", "mcuProcessDefaultCommand("+commandId+","+MyAVLogger.toShorterString(logger, parameters.toString())+")");
		
		// TODO Add MCU-specific code for untyped command
		return true;
	}

	public boolean mcuSetUserProperty(String userName, String propertyName, String propertyValue, String conferenceId, Map<String, String> results) {
		final String MNAME = "mcuSetUserProperty";
		logger.logp(Level.FINE, CNAME, MNAME, MNAME+"("+userName+","+propertyName+","+propertyValue+","+conferenceId+")");
		
		// Example synchronizing of conference-specific user data like connection status
		// (used as part of talking user simulation code).
		MyMCUConference conf = conferences.get(conferenceId);
		if (conf != null) {
			conf.setUserProperty(userName, propertyName, propertyValue);
		} else {
			logger.logp(Level.WARNING, CNAME, MNAME, "Ignoring unrecognized conference "+conferenceId);			
		}	

		// TODO Add MCU-specific code
		return true;
	}

	public boolean mcuSetConferenceProperty(String conferenceId, String propertyName, String propertyValue, Map<String, String> results) {
		logger.logp(Level.FINE, CNAME, "mcuSetConferenceProperty", "mcuSetConferenceProperty("+conferenceId+","+propertyName+","+propertyValue+")");
		
		// TODO Add MCU-specific code
		return true;
	}

	public boolean mcuRegisterUser(String userName, String userRole, String userPin, String conferenceId, Map<String, String> results) {
		logger.logp(Level.FINE, CNAME, "mcuRegisterUser", "mcuRegisterUser("+userName+","+userRole+","+userPin+","+conferenceId+")");
		
		// TODO Add MCU-specific code
		return true;
	}
	
	public boolean mcuRegisterUserSip(String userName, String userRole, String userPin, String sipUri, String conferenceId, Map<String, String> results) {
		logger.logp(Level.FINE, CNAME, "mcuRegisterUserSip", "mcuRegisterUserSip("+userName+","+userRole+","+userPin+","+sipUri+","+conferenceId+")");
		
		// TODO Add MCU-specific code
		return true;
	}	

	public boolean mcuMediaControl(String user, String conferenceId, String controlFlag, int i, Map<String, String> results) {
		logger.logp(Level.FINE, CNAME, "mcuMediaControl", "mcuMediaControl("+user+","+conferenceId+","+controlFlag+","+i+")");
		
		// TODO Add MCU-specific code
		return true;
	}

	public boolean mcuDisconnectUser(String user, String conferenceId, Map<String, String> results) {
		logger.logp(Level.FINE, CNAME, "mcuDisconnectUser", "mcuDisconnectUser("+user+","+conferenceId+")");
		
		// TODO Add MCU-specific code
		return true;
	}

	public boolean mcuDial(String user, String endPoint, String dialMode, String conferenceId, Map<String, String> results) {
		final String MNAME = "mcuDial";
		logger.logp(Level.FINE, CNAME, MNAME, MNAME+"("+user+","+endPoint+","+dialMode+","+conferenceId+")");
		
		// Demonstrate error dialing failure for numbers prefixed with 900-xxx-yyyy
		if (endPoint.startsWith("900") || endPoint.startsWith("tel:900")) {
			logger.logp(Level.INFO, CNAME, MNAME, "Returning "+DIAL_FAILURE+" for 900 number "+endPoint+" as example of a call failure");			
			results.put("errorCode", DIAL_FAILURE);
			return false;
		}
		
		// Demonstrate error dialing failure for invalid endpoints (e.g., h323:invalid1234)		
		if (endPoint.indexOf("invalid") != -1) {
			logger.logp(Level.INFO, CNAME, MNAME, "Returning "+INVALID_ENDPOINT+" for invalid endpoint "+endPoint+" as example of a call failure");			
			results.put("errorCode", INVALID_ENDPOINT);
			return false;
		}
		
		// Demonstrate dynamically changing participant's display name (e.g., how caller ID could be handled)
		Map<String, String> callerId = new HashMap<String, String>();
		callerId.put("0", "Operator");
		callerId.put("411", "Directory Assistance");		
		callerId.put("611", "Customer Service");
		callerId.put("811", "Safe Digging");
		callerId.put("911", "Emergency");		
		callerId.put("5551212", "Anonymous");
		
		String displayName = callerId.get(endPoint);
		if (displayName != null) {
			final Properties notification = new Properties();
			notification.setProperty("commandId", MYAV_NOTIFICATION_USER_SET_PROPERTY);
			notification.setProperty("to", conferenceId);
			notification.setProperty("userName", user);
			notification.setProperty("propertyName", USER_DISPLAY_NAME);
			notification.setProperty("propertyValue", displayName);
			final String logMessage = "Notifying service provider of user="+user+" displayName="+displayName;
			
			Timer delayedUpdateDisplayName = new Timer(MNAME);
			delayedUpdateDisplayName.schedule(new TimerTask() {
				public void run() {
					logger.logp(Level.FINEST, CNAME, MNAME, logMessage);		
					notifyMyConferenceService(notification);					
				}}, 5000);
		}
		
		// TODO Add MCU-specific code
		return true;
	}
	
	public boolean mcuDialExtended(String user, String endPoint, String dialMode, String conferenceId, String mediaFlag, Map<String, String> results) {
		final String MNAME = "mcuDialExtended";		
		logger.logp(Level.FINE, CNAME, MNAME, MNAME+"("+user+","+endPoint+","+dialMode+","+conferenceId+","+mediaFlag+")");
		
		// Demonstrate error dialing failure for numbers prefixed with 900-xxx-yyyy
		if (endPoint.startsWith("900") || endPoint.startsWith("tel:900")) {
			logger.logp(Level.INFO, CNAME, MNAME, "Returning "+DIAL_FAILURE+" for 900 number "+endPoint+" as example of a call failure");			
			results.put("errorCode", DIAL_FAILURE);
			return false;
		}		
		
		// Demonstrate error dialing failure for invalid endpoints (e.g., h323:invalid1234)		
		if (endPoint.indexOf("invalid") != -1) {
			logger.logp(Level.INFO, CNAME, MNAME, "Returning "+INVALID_ENDPOINT+" for invalid endpoint "+endPoint+" as example of a call failure");			
			results.put("errorCode", INVALID_ENDPOINT);
			return false;
		}
		
		// TODO Add MCU-specific code
		return true;
	}	

	public boolean mcuDeleteConference(String conferenceId, Map<String, String> results) {
		final String MNAME = "mcuDeleteConference";
		logger.logp(Level.FINE, CNAME, MNAME, MNAME+"("+conferenceId+")");
		
		// Should have already been stopped by mcuStopConferenceControl, but just in case...
		MyMCUConference conf = conferences.get(conferenceId);
		if (conf != null) {
			cleanupConference(conf);
		} else {
			logger.logp(Level.WARNING, CNAME, MNAME, "Ignoring unrecognized conference "+conferenceId);			
		}	
		
		// TODO Add MCU-specific code
		return true;
	}

	public boolean mcuCreateConference(String user, String conferenceDocumentText, Map<String, String> results) {
		final String MNAME = "mcuCreateConference";
		// TODO Create unique conference ID 
		// Note that it should be registered with Proxy/Registrar when the conference is started.
		MyMCUConference conf = new MyMCUConference(sipFactory);
		conferences.put(conf.getId(), conf);
		
		// Return conference ID as result, will register later once mcuStartConferenceControl is called
		results.put("conferenceId", conf.getId());
		
		logger.logp(Level.FINE, CNAME, MNAME, 
				MNAME+"("+user+","+MyAVLogger.toShorterString(logger, conferenceDocumentText)+
				") returning conferenceId="+conf.getId());
		
		// TODO Add MCU-specific code to record conference ID for later reference 
		return true;
	}
	
	protected void doAck(SipServletRequest request) throws ServletException, IOException {
		final String MNAME = "doAck";		
		logger.logp(Level.FINER, CNAME, MNAME, "request="+request);
		
		// Notify service provider to connect this user
		Properties notification = new Properties();
		notification.setProperty("commandId", MYAV_NOTIFICATION_USER_ACK);
		notification.setProperty("to",
			request.getTo().getURI().toString());
		notification.setProperty("from",
			request.getFrom().getURI().toString());
		SipURI sipUri = (SipURI) request.getFrom().getURI();		
		notification.setProperty("user",
				sipUri.getUser());		
				
		logger.logp(Level.FINER, CNAME, MNAME, "Notifying service provider of user ACK uri="+request.getTo().getURI());		
		notifyMyConferenceService(notification);				
	}
	
	protected void doBye(SipServletRequest request) throws ServletException, IOException {
		final String MNAME = "doBye";		
		logger.logp(Level.FINER, CNAME, MNAME, "request="+request);

		// Acknowledge receipt of the BYE
		SipServletResponse okResponse = request.createResponse(200);
		logger.logp(Level.FINER, CNAME, MNAME, "response="+okResponse);
		okResponse.send();		
		
		// Notify service provider to connect this user
		Properties notification = new Properties();
		notification.setProperty("commandId", MYAV_NOTIFICATION_USER_BYE);
		notification.setProperty("to",
			request.getTo().getURI().toString());
		notification.setProperty("from",
			request.getFrom().getURI().toString());
		SipURI sipUri = (SipURI) request.getFrom().getURI();		
		notification.setProperty("user",
				sipUri.getUser());		
				
		logger.logp(Level.FINER, CNAME, MNAME, "Notifying service provider of user BYE uri="+request.getTo().getURI());		
		notifyMyConferenceService(notification);				
	}
	
	protected void doInvite(SipServletRequest request) throws ServletException, IOException {
		final String MNAME = "doInvite";
		
		logger.logp(Level.FINE, CNAME, MNAME, request.toString());

		// For test purposes, accept any offer
		SipServletResponse okResponse = request.createResponse(200);
		
		// Simply "echo" the SDP response with updated source for tracing purposes
		String offer = new String((byte[]) request.getContent());
		logger.logp(Level.FINEST, CNAME, MNAME, "original offer="+offer);
		offer = offer.replace("s=Sametime-Softphone", "s="+getClass().getSimpleName()+"-Echo");
		
		// Parse the m=audio and m=video media lines and zero out the port 
		// number if it's not the first SRTP (for TLS) or RTP (for TCP).
		// We assume that if TLS is set, then SRTP is set too as recommended
		// in the Sametime System Console.
		String[] lines = offer.split("\\r?\\n");
		boolean rtpAudio = MyAVConfig.SIP_PROXY_TRANSPORT.equals("TCP");
		boolean rtpVideo = rtpAudio;
		boolean srtpAudio = MyAVConfig.SIP_PROXY_TRANSPORT.equals("TLS");
		boolean srtpVideo = srtpAudio;
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		for (String line : lines) {
			if (line.startsWith("m=video")) {
				if (line.contains("RTP/SAVP")) {
					if (srtpVideo)
						srtpVideo = false;
					else
						line = line.replaceFirst("m=video [0-9]*", "m=video 0");
				} else {
					if (rtpVideo)
						rtpVideo = false;
					else
						line = line.replaceFirst("m=video [0-9]*", "m=video 0");
				}
			} else if (line.startsWith("m=audio")) {
				if (line.contains("RTP/SAVP")) {
					if (srtpAudio)
						srtpAudio = false;
					else
						line = line.replaceFirst("m=audio [0-9]*", "m=audio 0");
				} else {
					if (rtpAudio)
						rtpAudio = false;
					else
						line = line.replaceFirst("m=audio [0-9]*", "m=audio 0");
				}
			}
			pw.println(line);
		}
		offer = sw.toString();
		
		// Respond OK to the offer
		okResponse.setContent(offer, "application/sdp");
		okResponse.send();
		logger.logp(Level.FINER, CNAME, MNAME, "response="+okResponse);		
		
		// Notify service provider to connect this user
		Properties notification = new Properties();
		notification.setProperty("commandId", MYAV_NOTIFICATION_USER_INVITE);
		notification.setProperty("to",
			request.getTo().getURI().toString());
		notification.setProperty("from",
			request.getFrom().getURI().toString());
		SipURI sipUri = (SipURI) request.getFrom().getURI();		
		notification.setProperty("user",
				sipUri.getUser());		
				
		logger.logp(Level.FINER, CNAME, MNAME, "Notifying service provider of user INVITE uri="+request.getTo().getURI());		
		notifyMyConferenceService(notification);
	}
	
	public void sessionExpired(SipApplicationSession sipAppSession) {
		final String MNAME = "sessionExpired";
		int activeConfs = 0;
		
		// Let the conferences know that a session has expired
		// in case they want to extend it.
		for (Iterator<MyMCUConference> iter = getConferences().iterator(); iter.hasNext();) {
			MyMCUConference conference = iter.next();
			boolean sessionExtended = conference.sessionExpired(sipAppSession);
			if (sessionExtended) {
				logger.logp(Level.FINER, CNAME, MNAME, "Conference "+conference.getId()+" extended session "+sipAppSession.getId());
				return;
			}
			activeConfs++;
		}
		
		// This should not occur since each conference has a dedicated application session;
		// flag it as a potential bug by logging a warning.
		logger.logp(Level.WARNING, CNAME, MNAME, "None of the "+activeConfs+" active conferences extended session "+sipAppSession.getId());	
	}

}
