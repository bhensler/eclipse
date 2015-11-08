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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipApplicationSessionListener;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import com.ibm.telephony.conferencing.myav.MyAVConfig;
import com.ibm.telephony.conferencing.myav.MyAVConstants;
import com.ibm.telephony.conferencing.myav.MyAVLogger;

/**
 * SIPlet responsible for routing appropriate SIP messages to 
 * <code>MyMCU</code> or <code>MyMCUConference</code> instance.
 */

public class MyMCUServlet extends SipServlet implements Servlet, SipApplicationSessionListener, MyAVConstants {
	private static final long serialVersionUID = 4173489458561739869L;
	private static final String CNAME = MyMCUServlet.class.getName();
	private static final Logger logger = MyAVLogger.getLogger(MyMCUServlet.class);

	private MyMCU getMyMCU() {
		MyMCU myMcu = (MyMCU) getServletContext().getAttribute(MYMCU_SERVLET_CONTEXT);
		if (myMcu == null) {
			throw new RuntimeException("Missing MyMCU in servlet context "+MYMCU_SERVLET_CONTEXT);
		}
		return myMcu;
	}

	public void init() throws ServletException {
		final String MNAME = "init";
		logger.entering(CNAME, MNAME);		
		
		super.init();
		
		logger.logp(Level.INFO, CNAME, MNAME, "Configuration="+MyAVConfig.properties);		
		
		SipFactory sipFactory = (SipFactory) getServletContext().getAttribute(SIP_FACTORY);
        if (sipFactory == null) {
            throw new ServletException("No SipFactory in context");
        }
	
		MyMCU myMcu = (MyMCU) getServletContext().getAttribute(MYMCU_SERVLET_CONTEXT);
		if (myMcu == null) {
			logger.logp(Level.INFO, CNAME, MNAME, "Creating MyMCU instance");		
			myMcu = new MyMCU(sipFactory);
			getServletContext().setAttribute(MYMCU_SERVLET_CONTEXT, myMcu);			
			myMcu.start();
		} else {
			logger.logp(Level.INFO, CNAME, MNAME, "Using existing MyMCU instance");			
		}
		
		logger.exiting(CNAME, MNAME);		
	}
		
	public MyMCUServlet() {
		logger.logp(Level.INFO, CNAME, "ctor", "USE_SIP_CONTROL="+Boolean.toString(MyAVConfig.USE_SIP_CONTROL));		
	} 
	
	protected void doAck(SipServletRequest request) throws ServletException, IOException {
		logger.logp(Level.FINE, CNAME, "doAck", "request="+request);		
		getMyMCU().doAck(request);
	}

	protected void doBye(SipServletRequest request) throws ServletException, IOException {
		logger.logp(Level.FINE, CNAME, "doBye", "request="+request);		
		getMyMCU().doBye(request);
	}	
	
	protected void doResponse(SipServletResponse response) throws ServletException,
			IOException {
		logger.logp(Level.FINE, CNAME, "doResponse", "response="+response);
		super.doResponse(response);
	}

	protected void doInvite(SipServletRequest request) throws ServletException, IOException {
		logger.logp(Level.FINE, CNAME, "doInvite", "request="+request);		
		getMyMCU().doInvite(request);
	}
	
	/**
	 * SipApplicationSessionListener methods
	 */

	public void sessionCreated(SipApplicationSessionEvent event) {
		logger.logp(Level.FINE, CNAME, "sessionCreated", "event="+event);
	}

	public void sessionDestroyed(SipApplicationSessionEvent event) {
		logger.logp(Level.FINE, CNAME, "sessionDestroyed", "event="+event);
	}

	public void sessionExpired(SipApplicationSessionEvent event) {
		final String MNAME = "sessionExpired";
		logger.logp(Level.FINE, CNAME, MNAME, "SipApplicationSession id="+event.getApplicationSession().getId());
		
		// Let the MCU know that one of its sessions has expired
		// in case it wants to extend it.
		getMyMCU().sessionExpired(event.getApplicationSession());
	}
}