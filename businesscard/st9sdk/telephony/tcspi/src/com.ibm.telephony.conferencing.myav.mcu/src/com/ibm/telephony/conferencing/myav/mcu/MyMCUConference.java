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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.URI;

import com.ibm.telephony.conferencing.myav.MyAVConfig;
import com.ibm.telephony.conferencing.myav.MyAVConstants;
import com.ibm.telephony.conferencing.myav.MyAVLogger;

/**
 * Simple "fake conference" that manages conference identity and lifecycle. 
 */

public class MyMCUConference implements MyAVConstants {
	private static final String CNAME = MyMCUConference.class.getName();
	private static Logger logger = MyAVLogger.getLogger(MyMCUConference.class);	
	
	private String id;
	private SipApplicationSession sipAppSession = null;
	private SipFactory sipFactory;
	private boolean started = false;
	private Set<String> connectedUserNames = Collections.synchronizedSet(new HashSet<String>());
	
	public MyMCUConference(SipFactory sipFactory) {
		this.sipFactory = sipFactory;
		id = MYMCU_CONFERENCE_AGENT_ID_PREFIX + System.currentTimeMillis();
		
		if (sipFactory != null) {
			sipAppSession = sipFactory.createApplicationSession();
		}
		
		logger.logp(Level.INFO, CNAME, "ctor", "Created conference "+getId());			
	}
	
	public String getId() {
		return id;
	}
	
	public void setUserProperty(String userName, String propertyName, String propertyValue) {
		final String MNAME = "setUserProperty";
		logger.logp(Level.FINER, CNAME, MNAME, MNAME+"("+userName+","+propertyName+","+propertyValue);		
		
		if (propertyName.equals(USER_CONNECTION_PROPERTY)) {
			if (propertyValue.equals(USER_CONNECTED)) {
				connectedUserNames.add(userName);
				logger.logp(Level.FINE, CNAME, MNAME, "Added user "+userName+" to connected list");				
			} else if (propertyValue.equals(USER_DISCONNECTED)) {
				connectedUserNames.remove(userName);
				logger.logp(Level.FINE, CNAME, MNAME, "Removed user "+userName+" from connected list");				
			}
		}
	}
	
	public String[] getConnectedUserNames() {
		return connectedUserNames.toArray(new String[connectedUserNames.size()]);
	}
	
	public boolean renewRegistration() {
		final String MNAME = "renewRegistration";
		
		if (!MyAVConfig.USE_SIP_CONTROL) {
	    	logger.logp(Level.WARNING, CNAME, MNAME, "Unexpected SIP registration renewal request");
		} else if (!started) {
			logger.logp(Level.WARNING, CNAME, MNAME, "Conference not started, ignoring registration request for conferenceId="+getId());			
		} else {
			try {
				sendRegistration(id, MyAVConfig.MYMCU_HOST_NAME, MyAVConfig.MYMCU_HOST_SIP_PORT, MyAVConfig.MYMCU_HOST_SIP_TRANSPORT, SIP_REGISTER_EXPIRES_SECONDS);
				return true;
			} catch (Exception e) {
				logger.logp(Level.SEVERE, CNAME, MNAME, "Exception while registering conferenceId="+getId(), e);				
			}		
		}
		
		return false;
	}

	public void start() {
		logger.logp(Level.INFO, CNAME, "start", "Starting conference "+getId());
		
		started = true;
		if (MyAVConfig.USE_SIP_CONTROL) {
			renewRegistration();
		}
	}

	public void stop() {
		logger.logp(Level.INFO, CNAME, "stop", "Stopping conference "+getId());
		started = false;
		connectedUserNames.clear();
		try {
			if (sipFactory != null && sipAppSession != null) {
				sendRegistration(id, MyAVConfig.MYMCU_HOST_NAME, MyAVConfig.MYMCU_HOST_SIP_PORT, MyAVConfig.MYMCU_HOST_SIP_TRANSPORT, 0);
				sipAppSession.invalidate();
				sipFactory = null;
				sipAppSession = null;
			}
		} catch (Exception e) {
			logger.logp(Level.SEVERE, CNAME, "stop", "Exception while stopping conferenceId="+getId(), e);				
		}
	}
	
	private void sendRegistration(String userAgentId, String hostName, String hostPort, String hostTransport, int expireTime) throws ServletParseException, IOException {
		final String MNAME = "sendRegistration";
		
		if (sipFactory != null && sipAppSession != null) {
			// Check for inconsistency in protocols			
			if (!MyAVConfig.SIP_PROXY_TRANSPORT.equalsIgnoreCase(MyAVConfig.MYMCU_HOST_SIP_TRANSPORT)) {
				logger.logp(Level.WARNING, CNAME, MNAME, "SIP_PROXY_TRANSPORT("+MyAVConfig.SIP_PROXY_TRANSPORT+") and MYMCU_HOST_SIP_TRANSPORT("+MyAVConfig.MYMCU_HOST_SIP_TRANSPORT+") should be equal.");
			}
			
			// Construct agent and contact address
			String agentAddr = MyAVConfig.SIP_PROXY_PROTOCOL + userAgentId + "@" + MyAVConfig.SIP_PROXY_HOST;
			String contactAddr = MyAVConfig.SIP_PROXY_PROTOCOL + userAgentId + "@" + hostName + ":" + hostPort;
			if (hostTransport != null && !hostTransport.equalsIgnoreCase("UDP")) {
				contactAddr += ";transport=" + hostTransport;				
			}
			
			// Construct proxy/registrar address			
			String proxyAddr = MyAVConfig.SIP_PROXY_PROTOCOL + MyAVConfig.SIP_PROXY_HOST + ":" + MyAVConfig.SIP_PROXY_PORT;
			if (!MyAVConfig.SIP_PROXY_TRANSPORT.equalsIgnoreCase("UDP")) {
				proxyAddr += ";transport=" + MyAVConfig.SIP_PROXY_TRANSPORT;
			}
			logger.logp(Level.FINER, CNAME, MNAME, "agent="+agentAddr+",contact="+contactAddr+",proxy="+proxyAddr+",expires="+expireTime);
			
			// Register with proxy/registrar
			SipServletRequest sipRequest = 
				sipFactory.createRequest(sipAppSession, "REGISTER", agentAddr, agentAddr);
			URI sipProxyUri = sipFactory.createURI(proxyAddr);
			sipRequest.setRequestURI(sipProxyUri);

			sipRequest.setHeader("Contact", contactAddr);
			sipRequest.setExpires(expireTime);
			
			logger.logp(Level.FINE, CNAME, MNAME, sipRequest.toString());
			
			sipRequest.send();
		} else {
			logger.logp(Level.WARNING, CNAME, MNAME, "No SipFactory/SipAppSession available");			
		}
	}

	public boolean sessionExpired(SipApplicationSession expiredSipAppSession) {
		// If this conference is using this session, extend its expiration
		if (sipAppSession != null &&
				expiredSipAppSession.getId().equals(sipAppSession.getId())) {
			expiredSipAppSession.setExpires(SIP_APP_SESSION_EXPIRES_MINUTES);
			logger.logp(Level.FINEST, CNAME, "sessionExpired", "Conference "+getId()+" extended session "+expiredSipAppSession.getId()+" by "+SIP_APP_SESSION_EXPIRES_MINUTES+" minutes");
			return true;
		}
		return false;
	}	
}