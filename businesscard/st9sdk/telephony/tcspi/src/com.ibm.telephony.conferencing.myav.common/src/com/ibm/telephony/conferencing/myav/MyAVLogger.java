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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * For ease of debugging, MyAV uses a separate logging file in addition to 
 * the standard WebSphere Application Server's <code>trace.log</code>.
 * See <code>myav.properties</code> for more details.
  */

public class MyAVLogger {
	private final static long creationTimestamp = System.currentTimeMillis();	
	private static Logger commonLogger = null;
	private static Set<String> commonLoggerIds = new HashSet<String>();

	@SuppressWarnings("unchecked")
	public static synchronized Logger getLogger(Class clazz) {
		Logger logger = null;

		try {
			if (MyAVConfig.DEBUG_LOG_LOCATION == null) {
				// Use standard logger convention
				logger = Logger.getLogger(clazz.getName());				
			} else if (MyAVConfig.DEBUG_LOG_LOCATION.endsWith(".log")) {
				// Use one logger file, e.g., "C:\MyAV.log"
				if (commonLogger == null) {
					commonLogger = Logger.getLogger(MyAVLogger.class.getName()+creationTimestamp);
					Handler h = new FileHandler(MyAVConfig.DEBUG_LOG_LOCATION, true);
					h.setFormatter(new SimpleFormatter());
					commonLogger.addHandler(h);
				}
				logger = commonLogger;
			} else {
				// Otherwise, use location + class name + log extension
				String id = clazz.getName() + creationTimestamp;				
				logger = Logger.getLogger(id);				
				if (!commonLoggerIds.contains(id)) {
					Handler h = new FileHandler(
							MyAVConfig.DEBUG_LOG_LOCATION
							+ clazz.getSimpleName() + ".log", true);
					h.setFormatter(new SimpleFormatter());
					logger.addHandler(h);
					commonLoggerIds.add(id);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return logger;
	}
	
	static public String toShorterString(Logger logger, String s) {
		if (s == null || s.length() < 80 || logger.isLoggable(Level.FINEST)) {
			return s;
		} else {
			return s.substring(0, 80) + "...";
		}
	}	
}
