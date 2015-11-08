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

/**
 * Global constants.
 */

public interface MyAVConstants {
	// Constants related to SIP proxy configuration
	public static final int SIP_REGISTER_EXPIRES_SECONDS = 300;
	public static final int SIP_REGISTER_DELAY_MS = 3000;	
	public static final int SIP_REGISTER_INTERVAL_MS = 270 * 1000;
	public static final int SIP_APP_SESSION_EXPIRES_MINUTES = 5;

	// Common prefix for conferences registered by this adapter;
	// comes in handy for debug and for deployment (e.g., if using forced routing
	// rules on proxy/registrar).
	public static final String MYMCU_CONFERENCE_AGENT_ID_PREFIX = "mymcu_conference_";
	
	// MCU command constants; see MyMCUCommand and MyMCUCommandSocketServer
	public static final String MYAV_CMD_ASSOCIATE = "myavc_associate";
	public static final String MYAV_CMD_CREATE_CONFERENCE = "myavc_create_conference";	
	public static final String MYAV_CMD_DELETE_CONFERENCE = "myavc_delete_conference";	
	public static final String MYAV_CMD_DIAL = "myavc_dial";	
	public static final String MYAV_CMD_DIAL_EXTENDED = "myavc_dial_extended";	
	public static final String MYAV_CMD_DISCONNECT_USER = "myavc_disconnect_user";	
	public static final String MYAV_CMD_MEDIA_CONTROL = "myavc_media_control";	
	public static final String MYAV_CMD_REGISTER_USER = "myavc_register_user";	
	public static final String MYAV_CMD_REGISTER_USER_SIP = "myavc_register_user_sip";	
	public static final String MYAV_CMD_SET_CONFERENCE_PROPERTY = "myavc_set_conference_property";	
	public static final String MYAV_CMD_SET_USER_PROPERTY = "myavc_set_user_property";	
	public static final String MYAV_CMD_START_CONFERENCE_CONTROL = "myavc_start_conference_control";	
	public static final String MYAV_CMD_STOP_CONFERENCE_CONTROL = "myavc_stop_conference_control";	
	public static final String MYAV_CMD_TIMEOUT_ERROR = "myavc_timeout_error";	
	public static final String MYAV_CMD_UNREGISTER_USER = "myavc_unregister_user";
	
	// MCU notification constants; see MyMCU and MyAVConferenceService
	public static final String MYAV_NOTIFICATION_USER_ACK = "myavn_user_ack";	
	public static final String MYAV_NOTIFICATION_USER_BYE = "myavn_user_bye";	
	public static final String MYAV_NOTIFICATION_USER_INVITE = "myavn_user_invite";	
	public static final String MYAV_NOTIFICATION_USER_SET_PROPERTY = "myavn_user_set_property";	
	
	// Telephony error codes copied from RequestFailureCodes for ease of reference
	// for MyMCU components that do not otherwise depend on TelephonyService jar.
	public static final String GENERAL_EXCEPTION_FAILURE = "com.ibm.telephony.conferencing.error.GeneralException";
    public static final	String TIMEOUT_FAILURE = "com.ibm.telephony.conferencing.error.TimeoutFailure";
    public static final String DIAL_FAILURE = "com.ibm.telephony.conferencing.error.DialFailure";
    public static final	String INVALID_ENDPOINT = "com.ibm.telephony.conferencing.error.InvalidEndpoint";
    public static final String SERVICE_UNAVAILABLE = "com.ibm.telephony.conferencing.error.ServiceUnavailable";
    
	// Constants copied from User for ease of reference
	// for MyMCU components that do not otherwise depend on the TelephonyService jar.
    public static final String USER_CONNECTION_PROPERTY = "com.ibm.telephony.conferencing.property.Connection";
    public static final String USER_CONNECTED = "com.ibm.telephony.conferencing.property.Connection.Connected";
    public static final String USER_DISCONNECTED = "com.ibm.telephony.conferencing.property.Connection.Disconnected";
    public static final String USER_TALKING_PROPERTY = "com.ibm.telephony.conferencing.property.Talking";
    public static final String USER_DISPLAY_NAME = "com.ibm.telephony.conferencing.property.ParticipantDisplayName";
    
    // Generic user name for cases where user is null in APIs like processDial(...)
    public static final String UNKNOWN_USER_NAME = "[unknown user]";
	
	// Servlet-related constants
	public static String MYMCU_SERVLET_CONTEXT = "MyMCU_Servlet_Context";
}