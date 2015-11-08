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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized configuration for TCSPI sample implementation, MyAV.
 * See <code>myav.properties</code> for more details.
 */

public class MyAVConfig implements MyAVConstants {
	private static final String CNAME = MyAVConfig.class.getName();
	private static final Logger logger = MyAVLogger.getLogger(MyAVConfig.class);
	
	private static final String PROPERTIES_FILE_NAME = "myav.properties";
	
	// To override defaults, modify myav.properties in sametime_tscpi directory 
	private static final String DEFAULT_MYMCU_HOST_NAME = "your.mymcu.server";
	private static final String DEFAULT_MYMCU_HOST_COMMAND_PORT = "50001";	
	private static final String DEFAULT_MYMCU_HOST_SIP_PORT = "5080";
	private static final String DEFAULT_MYMCU_HOST_SIP_PORT_SECURE = "5081";
	private static final String DEFAULT_MYMCU_HOST_SIP_TRANSPORT = "TCP";
	private static final String DEFAULT_SIP_PROXY_HOST = "your.proxyreg.server";
	private static final String DEFAULT_SIP_PROXY_PORT = "5060";
	private static final String DEFAULT_SIP_PROXY_PORT_SECURE = "5061";
	private static final String DEFAULT_SIP_PROXY_TRANSPORT = "TCP";
	private static final String DEFAULT_DEBUG_LOG_LOCATION = null;
	private static final String DEFAULT_USE_SIP_CONTROL = "true";
	private static final String DEFAULT_SERVICE_PROVIDER_ID = "com.sametime.example.MyAVConferenceService";
	
	public static String MYMCU_HOST_NAME;
	public static String MYMCU_HOST_COMMAND_PORT;	
	public static String MYMCU_HOST_SIP_PORT;
	public static String MYMCU_HOST_SIP_TRANSPORT;
	public static String SIP_PROXY_HOST;
	public static String SIP_PROXY_PORT;
	public static String SIP_PROXY_TRANSPORT;
	public static String SIP_PROXY_PROTOCOL;
	public static String DEBUG_LOG_LOCATION;
	public static String SERVICE_PROVIDER_ID;	
	public static boolean USE_SIP_CONTROL;
	
	// Note: MYMCU_HOST_SIP_PORT is set to MYMCU_HOST_SIP_PORT_SECURE if TLS is specified as transport
	// Note: SIP_PROXY_PORT is set to SIP_PROXY_PORT_SECURE if TLS is specified as transport	
	private static String MYMCU_HOST_SIP_PORT_SECURE;
	private static String SIP_PROXY_PORT_SECURE;
	
	public static Properties properties = new Properties();
	
	static {
		final String MNAME = "ctor";
		String fn = PROPERTIES_FILE_NAME;
		
		try {
			InputStream fs = MyAVConfig.class.getClassLoader().getResourceAsStream(fn);
			
			if (fs == null) {
				// If not found as a resource, look in the sametime_tcspi directory 
				// for compatibility with previous versions.
				fn = System.getProperty("server.root") + 
					File.separator + "sametime_tcspi" + 
					File.separator + PROPERTIES_FILE_NAME;
				fs = new FileInputStream(fn);
			}

			if (fs == null) {
				logger.logp(Level.SEVERE, CNAME, MNAME, "Unable to load " + fn);
			} else {
				properties.load(fs);				
				
				MYMCU_HOST_NAME = properties.getProperty("MYMCU_HOST_NAME", DEFAULT_MYMCU_HOST_NAME);
				MYMCU_HOST_COMMAND_PORT = properties.getProperty("MYMCU_HOST_COMMAND_PORT", DEFAULT_MYMCU_HOST_COMMAND_PORT);
				MYMCU_HOST_SIP_PORT = properties.getProperty("MYMCU_HOST_SIP_PORT", DEFAULT_MYMCU_HOST_SIP_PORT);
				MYMCU_HOST_SIP_PORT_SECURE = properties.getProperty("MYMCU_HOST_SIP_PORT_SECURE", DEFAULT_MYMCU_HOST_SIP_PORT_SECURE);
				MYMCU_HOST_SIP_TRANSPORT = properties.getProperty("MYMCU_HOST_SIP_TRANSPORT", DEFAULT_MYMCU_HOST_SIP_TRANSPORT);
				SIP_PROXY_HOST = properties.getProperty("SIP_PROXY_HOST", DEFAULT_SIP_PROXY_HOST);
				SIP_PROXY_PORT = properties.getProperty("SIP_PROXY_PORT", DEFAULT_SIP_PROXY_PORT);
				SIP_PROXY_PORT_SECURE = properties.getProperty("SIP_PROXY_PORT_SECURE", DEFAULT_SIP_PROXY_PORT_SECURE);
				SIP_PROXY_TRANSPORT = properties.getProperty("SIP_PROXY_TRANSPORT", DEFAULT_SIP_PROXY_TRANSPORT);
				DEBUG_LOG_LOCATION = properties.getProperty("DEBUG_LOG_LOCATION", DEFAULT_DEBUG_LOG_LOCATION);
				USE_SIP_CONTROL = Boolean.parseBoolean(properties.getProperty("USE_SIP_CONTROL", DEFAULT_USE_SIP_CONTROL));
				SERVICE_PROVIDER_ID = properties.getProperty("SERVICE_PROVIDER_ID", DEFAULT_SERVICE_PROVIDER_ID);
				
				logger.logp(Level.INFO, CNAME, MNAME, PROPERTIES_FILE_NAME +"="+properties);
				fs.close();
			}
		} catch (IOException e) {
			logger.logp(Level.SEVERE, CNAME, MNAME, "Unable to load " + fn, e);
		}
		
		// Check for inconsistency in protocols
		if (!SIP_PROXY_TRANSPORT.equalsIgnoreCase(MYMCU_HOST_SIP_TRANSPORT)) {
			logger.logp(Level.WARNING, CNAME, MNAME, "SIP_PROXY_TRANSPORT("+SIP_PROXY_TRANSPORT+") and MYMCU_HOST_SIP_TRANSPORT("+MYMCU_HOST_SIP_TRANSPORT+") should be equal.");
		}		
		
		// If TLS is specified, use secure ports
		if (MYMCU_HOST_SIP_TRANSPORT.equalsIgnoreCase("TLS")) {
			MYMCU_HOST_SIP_PORT = MYMCU_HOST_SIP_PORT_SECURE;
		}
		if (SIP_PROXY_TRANSPORT.equalsIgnoreCase("TLS")) {
			SIP_PROXY_PORT = SIP_PROXY_PORT_SECURE;
			SIP_PROXY_PROTOCOL = "sips:";
		} else {
			SIP_PROXY_PROTOCOL = "sip:";
		}
	}
}
