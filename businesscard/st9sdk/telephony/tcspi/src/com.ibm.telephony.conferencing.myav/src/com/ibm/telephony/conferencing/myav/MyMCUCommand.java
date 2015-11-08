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
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.telephony.conferencing.event.ConferenceResponseHandler;
import com.ibm.telephony.conferencing.util.AbstractCommand;
import com.ibm.telephony.conferencing.util.Command;
import com.ibm.telephony.conferencing.util.CommandDispatcher;

/**
 * Simple command for requested "processXXX" service methods. Subclasses override
 * the <code>process</code> method to sequentially handle process service requests on another thread 
 * managed by the <code>CommandDispatcher</code>.
 * 
 * @see CommandDispatcher
 */

public abstract class MyMCUCommand extends AbstractCommand implements Runnable, Command, MyAVConstants {
	private static final String CNAME = MyMCUCommand.class.getName();	
	private static final Logger logger = MyAVLogger.getLogger(MyMCUCommand.class);
	
	private MyAVConferenceService myConferenceService;	
	private Map<String, String> parameters = new HashMap<String, String>();
	private Map<String, String> results = new HashMap<String, String>();
	private ConferenceResponseHandler handler;
	private int transactionId;
		
	public MyMCUCommand(String id, MyAVConferenceService myConferenceService, int transactionId, ConferenceResponseHandler handler) {
		super(MyMCUCommand.class.getSimpleName(), id);
		this.handler = handler;
		this.myConferenceService = myConferenceService;
		this.transactionId = transactionId;
	}
	
	public void addParameter(String key, String value) {
		parameters.put(key, (value==null ? "null" : value));
	}
	
	public Map<String, String> getParameters() {
		return parameters;
	}
	
	public Map<String, String> getResults() {
		return results;
	}
	
	public String getResult(String key) {
		return results.get(key);
	}			
	
	public void setResults(Map<String, String> newResults) {
		results = newResults;
	}
	
	public void send(MyMCUCommandSocketClient client) {
		final String MNAME = "send";
		
		logger.logp(Level.FINE, CNAME, MNAME, "transactionId="+transactionId+",commandId="+getId()+",parameters="+parameters);			
		myConferenceService.addPendingCommand(transactionId, this);
		
		ObjectOutputStream outputStream = client.getOutputStream();
		if (outputStream == null || !myConferenceService.isAvailable()) {
			logger.logp(Level.WARNING, CNAME, MNAME, "No MCU command stream for transactionId="+transactionId+",commandId="+getId());
			deliverServiceUnavailableResponse();			
			return;
		}
		
		try {
			Properties message = new Properties();
			message.setProperty("transactionId", Integer.toString(transactionId));
			message.setProperty("commandId", getId());
			Set<String> keys = parameters.keySet();
			for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
				String key = iter.next();
				message.setProperty(key, parameters.get(key));
			}
			outputStream.writeObject(message);
			outputStream.flush();
		} catch (IOException e) {
			logger.logp(Level.SEVERE, CNAME, MNAME, "Exception while processing " + toString(), e);			
		}
	}
	
	public void run() {
		final String MNAME = "run";
		
		logger.logp(Level.FINEST, CNAME, MNAME, "Start processing of " + toString());
		try {
			process();
			logger.logp(Level.FINEST, CNAME, MNAME, "End processing of " + toString());				
		} catch (Exception e) {
			logger.logp(Level.SEVERE, CNAME, MNAME,  "Exception while processing " + toString(), e);
		}
	}

	public void deliverFailureResponse() {
		String errorCode = results.get("errorCode");
		String errorMessage = results.get("errorMessage");
		logger.logp(Level.WARNING, CNAME, "deliverFailureResponse", "commandId=" + getId() + ",errorCode=" + errorCode + ",message=" + errorMessage);
		myConferenceService.removePendingCommand(transactionId);
		myConferenceService.deliverFailureResponse(errorCode, errorMessage, handler);
	}
	
	private void deliverServiceUnavailableResponse() {
		logger.logp(Level.FINER, CNAME, "deliverServiceUnavailableResponse", "commandId=" + getId());		
		results.put("errorCode", SERVICE_UNAVAILABLE);
		results.put("errorMessage", myConferenceService.getConfiguration().getProperty(SERVICE_UNAVAILABLE));
		deliverFailureResponse();
	}

	abstract public void deliverSuccessResponse();
}

