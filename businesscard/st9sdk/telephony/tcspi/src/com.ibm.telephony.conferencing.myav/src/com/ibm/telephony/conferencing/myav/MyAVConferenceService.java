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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.telephony.conferencing.Conference;
import com.ibm.telephony.conferencing.ConferenceException;
import com.ibm.telephony.conferencing.ConferenceFeature;
import com.ibm.telephony.conferencing.Login;
import com.ibm.telephony.conferencing.Property;
import com.ibm.telephony.conferencing.RequestFailureCodes;
import com.ibm.telephony.conferencing.User;
import com.ibm.telephony.conferencing.event.ConferenceResponseAdapter;
import com.ibm.telephony.conferencing.event.ConferenceResponseHandler;
import com.ibm.telephony.conferencing.spi.AbstractConferenceService;
import com.ibm.telephony.conferencing.spi.AbstractSipConferenceService;
import com.ibm.telephony.conferencing.spi.ConferenceService;
import com.ibm.telephony.conferencing.spi.ConferenceServiceContext;
import com.ibm.telephony.conferencing.spi.DefaultConference;
import com.ibm.telephony.conferencing.spi.DefaultUser;
import com.ibm.telephony.conferencing.util.CommandDispatcher;
import com.ibm.telephony.conferencing.util.ConferenceDocument;
import com.ibm.telephony.conferencing.util.DefaultCommandDispatcher;

/**
 * Service Provider skeletal example for Sametime Media Server.
 */

@SuppressWarnings("unchecked")
public class MyAVConferenceService extends AbstractSipConferenceService implements MyAVConstants {
	private static final String CNAME = MyAVConferenceService.class.getName();
	private static final Logger logger = MyAVLogger.getLogger(MyAVConferenceService.class);
	
	private Map<Integer, MyMCUCommand> pendingMCUCommands = new ConcurrentHashMap<Integer, MyMCUCommand>();	
	private CommandDispatcher localCommandDispatcher = new DefaultCommandDispatcher(CNAME);
	private MyMCUCommandSocketClient mcuCommandSocketClient;
	private static int mcuCommandTransactionId = 0;
	private static int dialBridgeUsersSimulationCount = 1;
	
	/**
	 * Override default constructor for logging purposes only.
	 * 
	 * @see #init(ConferenceServiceContext, Properties)
	 */
	public MyAVConferenceService() {
		logger.logp(Level.INFO, CNAME, "ctor", "USE_SIP_CONTROL="+Boolean.toString(MyAVConfig.USE_SIP_CONTROL));
	}
	
	//
	// Implementations of abstract methods from superclass
	//
	
	/**
	 * This method returns an identifier for this implementation used for differentiating
	 * service providers; it appears in the server logs and is used as the key
	 * in client's A/V conference-related preferences. By default, it returns the fully qualified class name.
	 * 
	 * @return a unique string for this service provider.
	 */	
	@Override
	public String getId() {
		return MyAVConfig.SERVICE_PROVIDER_ID;
	}
	
	/**
	 * This method indicates that the specified conference object is no longer needed and any internal resources related
	 * to real-time control of the conference should be disposed. This method does not imply changes to the persisted 
	 * conference state or schedule.
	 * 
	 * @param conference the conference whose resources can be cleaned up
	 */	
	@Override
	protected void disposeConference(DefaultConference conference) {
		logger.logp(Level.FINE, CNAME, "disposeConference", toString(conference));
	}

	/**
	 * This method returns additional properties to be merged with statically defined properties
	 * in ConferenceManager.properties file.
	 * 
	 * @see #getConfiguration(ConferenceResponseHandler)
	 */
	@Override
	public Properties getConfiguration() {
		Properties config = new Properties();
		
		// TODO add runtime configuration properties as needed for client.
		// This is appended to contents of ConferenceManager.properties file.
		config.setProperty("ServiceLocations", "MyAV Service One;MyAV Service Two");
		
		logger.logp(Level.FINE, CNAME, "getConfiguration", "configuration="+config.toString());		
		
		return config;
	}

	/**
	 * This method should be overridden by service providers who want to provide their own conference properties at the
	 * time a new Conference object is created.
	 * 
	 * @param conference the conference that has been just created and is awaiting default properties
	 */	
	@Override
	protected void populateDefaultConferenceProperties(DefaultConference conference) {
		logger.logp(Level.FINER, CNAME, "populateDefaultConferenceProperties", toString(conference));
	}

	/**
	 * This method should be overridden by service providers who want to provide their own user properties at the
	 * time a new User object is created.
	 *
	 * @param user the user that has been just created and is awaiting default properties
	 */	
	@Override
	protected void populateDefaultUserProperties(DefaultUser user) {
		logger.logp(Level.FINER, CNAME, "populateDefaultUserProperties", toString(user));
	}
	
	@Override
	public void init(ConferenceServiceContext conferenceServiceContext, Properties properties) {
		super.init(conferenceServiceContext, properties);
		logger.logp(Level.FINE, CNAME, "init", "properties="+properties);
	}

	//
	// Lifecycle methods
	//
	
	/**
	 * This method will be called once after creation.
	 *
	 */
	public void start() {
		final String MNAME = "start";
		super.start();
		
		logger.logp(Level.INFO, CNAME, MNAME, "Configuration="+MyAVConfig.properties);		
		
		logger.logp(Level.FINE, CNAME, MNAME, "Starting command dispatcher");
		startLocalCommandDispatcher();
		
		// TODO establish connection with Bridge (MCU) via proprietary means (TCP socket).
		logger.logp(Level.FINE, CNAME, MNAME, "Starting MCU command client");
		mcuCommandSocketClient = new MyMCUCommandSocketClient(this);
		mcuCommandSocketClient.start();

		// MyMCUCommandSocketClient will call setMyMCUCommandSocketAvailable when it's ready
		logger.logp(Level.FINER, CNAME, MNAME, "Waiting for MCU command client to be ready");
	}
	
	/**
	 * This method will be called before disposing of the service.
	 * It is expected this will only happen when the container is
	 * being shutdown.
	 */	
	public void stop() {
		final String MNAME = "stop";		
		super.stop();
		
		logger.logp(Level.INFO, CNAME, MNAME, "Service not available");		
		setAvailable(false);		
		
		logger.logp(Level.FINE, CNAME, MNAME, "Stopping command dispatcher");
		stopLocalCommandDispatcher();
		
		// TODO Stop connection with Bridge (MCU)		
		logger.logp(Level.FINE, CNAME, MNAME, "Stopping MCU command client");		
		mcuCommandSocketClient.stop();
	}
	
	//
	// process...deliver methods that perform requested services. See TODO flags for required code changes.
	//
	
    /**
  	 * This method indicates the named user is in fact the same as an already connected user.
  	 * The conference should take steps necessary to synchronize the two (e.g., by renaming
  	 * "Guest" user to the named user).  
  	 * 
  	 * A failure during processing of this method should result in a call to requestFailed() with the appropriate error.
  	 * 
  	 * @param namedUser		user with given name
  	 * @param connectedUser	user instance already connected (e.g., as "Guest")
  	 * @param handler		the listener that receives the response
  	 * 
   	 * @throws ConferenceException when any exceptional situation occurs that prevents the request from being attempted
  	 */
	public void processAssociate(final DefaultUser namedUser, final DefaultUser connectedUser,
			final ConferenceResponseHandler handler) throws ConferenceException {
		final String MNAME = "processAssociate";
		Conference conference = connectedUser.getConference();
		logger.logp(Level.FINE, CNAME, MNAME, "conference="+toString(conference)+"namedUser="+toString(namedUser) + ",connectedUser=" + toString(connectedUser));
		
		MyMCUCommand cmd = new MyMCUCommand(MYAV_CMD_ASSOCIATE, this, mcuCommandTransactionId++, handler) {
			public void process() {
				// TODO associate named user with connected user and call deliverAssociateResponse when complete.
				addParameter("namedUser", namedUser.getName());
				addParameter("connectedUser", connectedUser.getName());
				addParameter("conferenceId", namedUser.getConference().getId());				
				send(mcuCommandSocketClient);
			}

			public void deliverSuccessResponse() {
				associateUsers(namedUser, connectedUser);
				
				logger.logp(Level.FINER, CNAME, MNAME, "deliver response to " + MNAME);				
				deliverAssociateResponse(namedUser, connectedUser, handler);				
			}		
			
			public void deliverFailureResponse() {
				logger.logp(Level.WARNING, CNAME, MNAME, "deliver failure response to " + MNAME);				
				super.deliverFailureResponse();
			}
		};
		getCommandDispatcher().postCommand(cmd);
	}

	/**
	 * This method will be called when a new conference is needed and is the only mechanism for a service provider to 
	 * return a unique id for a conference. It does not indicate that real-time control of a conference will be started, 
	 * but rather that the service provider should setup, allocate, and reserve any conferencing resources needed to 
	 * support the conference specified in the documentText parameter. For example, the service provider should use 
	 * this method to determine call-in phone numbers, access codes, and supported features for the conference, and return 
	 * this information in deliverCreateConferenceResponse(). On success, service providers must also return a unique id for 
	 * the conference which might be used to encode affinity to a particular audio bridge or some other embedded information.
	 *  
	 * The login information can be used to authorize the creation of the conference. 
	 *
	 * A failure during processing of this method should result in a call to deliverFailureResponse() with the appropriate error.
	 * 
     * @param login         the login information to validate the context of the request
     * @param conferenceDocumentText  the document that describes the conference to schedule
	 * @param handler		the listener that receives the response. 
	 * 
	 * @throws ConferenceException when any exceptional situation occurs that prevents the request from being attempted
	 * 
	 * @see AbstractConferenceService#deliverCreateConferenceResponse
	 */	
	public void processCreateConference(final Login login, String conferenceDocumentText,
			final ConferenceResponseHandler handler) throws ConferenceException {
		final String MNAME = "processCreateConference";

    	logger.logp(Level.FINE, CNAME, MNAME, "login="+login.getName()+", id="+login.getId());

    	// Populate the conference document using the provided template then add/remove properties/features.
	    final ConferenceDocument conferenceDocument = new ConferenceDocument(conferenceDocumentText);
	    populateDefaultConferenceFeatures(conferenceDocument);	    
		populateSupportedConferenceFeatures(conferenceDocument);
	    populateDefaultConferenceProperties(conferenceDocument);		
		
		MyMCUCommand cmd = new MyMCUCommand(MYAV_CMD_CREATE_CONFERENCE, this, mcuCommandTransactionId++, handler) {
			public void process() {
				// TODO create conference and call deliverCreateConferenceResponse when complete.
				addParameter("user", login.getName());
				addParameter("conferenceDocumentText", conferenceDocument.toXmlString());
				send(mcuCommandSocketClient);
			}

			public void deliverSuccessResponse() {
				String conferenceId = getResult("conferenceId");
				logger.logp(Level.FINER, CNAME, MNAME, "deliver response to " + MNAME + " for conferenceId " + conferenceId);				
				deliverCreateConferenceResponse(conferenceId, 
						conferenceDocument.toXmlString(), handler);
			}
			
			public void deliverFailureResponse() {
				logger.logp(Level.WARNING, CNAME, MNAME, "deliver failure response to " + MNAME);				
				super.deliverFailureResponse();
			}
		};
		getCommandDispatcher().postCommand(cmd);		
	}

	private void populateSupportedConferenceFeatures(ConferenceDocument conferenceDocument) {
    	logger.logp(Level.FINER, CNAME, "populateSupportedConferenceFeatures", toString(conferenceDocument));
    	
		// TODO By default, accept all features as valid
		// (an alternative implementation could enable/disable features depending on service states
		// determined at runtime).
		Iterator iter = conferenceDocument.getFeatures().iterator();
		while (iter.hasNext()) {
			conferenceDocument.setSupportedFeature((String) iter.next());
		}
	}
	
	private void populateDefaultConferenceFeatures(ConferenceDocument conferenceDocument) {
		final String MNAME = "populateDefaultConferenceFeatures";		
		// TODO add default features as required by calling ConferenceDocument.setConferenceFeature; 
		// the conferenceDocument is later used to populate the conference instance with the desired properties/features.
		
		conferenceDocument.setFeature(ConferenceFeature.SIP_DIAL_FEATURE, Boolean.valueOf(MyAVConfig.USE_SIP_CONTROL), Boolean.valueOf(MyAVConfig.USE_SIP_CONTROL));
        logger.logp(Level.FINE, CNAME, MNAME, ConferenceFeature.SIP_DIAL_FEATURE+"="+Boolean.toString(MyAVConfig.USE_SIP_CONTROL));
	}	

	private void populateDefaultConferenceProperties(ConferenceDocument conferenceDocument) {
		final String MNAME = "populateDefaultConferenceProperties";
		
		// TODO add default properties as required by calling ConferenceDocument.setConferenceProperty; 
		// the conferenceDocument is later used to populate the conference instance with the desired properties/features.
		
		// Modify the code below as required to handle non-SIP controlled calls
		// (i.e., those using another endpoint instead of the Sametime Connect client's softphone).
		if (MyAVConfig.USE_SIP_CONTROL) {
	        Property controlProperty =
	        	conferenceDocument.getProperty(Conference.SIP_CONTROL_STATIC_PROPERTY);
	        if (controlProperty != null) {
	        	controlProperty.setValue("true");
	        } else {
	        	controlProperty =
	    			new Property(Property.SYSTEM_PROPERTY_TYPE, Conference.SIP_CONTROL_STATIC_PROPERTY, "true");
	        }
	        conferenceDocument.setConferenceProperty(controlProperty);
	        logger.logp(Level.FINE, CNAME, MNAME, controlProperty.getName()+"="+controlProperty.getValue());
		}
		
		// TODO populate with call-in information as example
		String value = getProperty("DynamicConferencesEnabled", "false");
		if (value.equals("true")) {
			String s = Long.toString(System.currentTimeMillis());
			String fakePhoneExt = s.substring(s.length()-4);
	        conferenceDocument.setConferenceProperty(new Property(Property.APPLICATION_PUBLIC_PROPERTY_TYPE, 
	                Conference.TOLLFREE_DIAL_STATIC_PROPERTY, "1 800 555 "+fakePhoneExt, true));
	        conferenceDocument.setConferenceProperty(new Property(Property.APPLICATION_PUBLIC_PROPERTY_TYPE,
	                Conference.TOLL_DIAL_STATIC_PROPERTY, "1 900 555 "+fakePhoneExt, true));
	        conferenceDocument.setConferenceProperty(new Property(Property.APPLICATION_PUBLIC_PROPERTY_TYPE,
	                Conference.AUX_DIAL_STATIC_PROPERTY, "1 866 555 "+fakePhoneExt, true));
	        logger.logp(Level.FINER, CNAME, MNAME, 
	        		"Setting properties for demonstration (dynamic extension "+fakePhoneExt+"):"+
	        		Conference.TOLLFREE_DIAL_STATIC_PROPERTY+","+
	        		Conference.TOLL_DIAL_STATIC_PROPERTY+","+
	        		Conference.AUX_DIAL_STATIC_PROPERTY);
		}
	}

 	/**
 	 * This method will be called when a conference should be removed from the service provider's list of available
 	 * resources. If successful, this method should call deliverCancelConferenceResponse() with the conferenceId.
 	 * 
 	 * The login information should be used to authorize the removal of this conference.
 	 * 
 	 * A failure during processing of this method should result in a call to requestFailed() with the appropriate error.
 	 * 
     * @param login         the login information to validate the context of the request
 	 * @param conferenceId	the id of the conference to cancel.
 	 * @param handler		the listener that receives the response
 	 * 
 	 * @throws ConferenceException when any exceptional situation occurs that prevents the request from being attempted
 	 */	
	public void processDeleteConference(Login login, final String conferenceId,
			final ConferenceResponseHandler handler) throws ConferenceException {
		final String MNAME = "processDeleteConference";
    	logger.logp(Level.FINE, CNAME, MNAME, "delete conferenceId=" + conferenceId+", login="+login.getName()+", id="+login.getId());		
		
		MyMCUCommand cmd = new MyMCUCommand(MYAV_CMD_DELETE_CONFERENCE, this, mcuCommandTransactionId++, handler) {
			public void process() {
				// TODO conference is no longer needed, release associated resoruces and deliver result response when complete
				addParameter("conferenceId", conferenceId);
				send(mcuCommandSocketClient);
			}

			public void deliverSuccessResponse() {
				logger.logp(Level.FINER, CNAME, MNAME, "deliver response to " + MNAME);				
				deliverDeleteConferenceResponse(conferenceId, handler);
			}		
			
			public void deliverFailureResponse() {
				logger.logp(Level.WARNING, CNAME, MNAME, "deliver failure response to " + MNAME);				
				super.deliverFailureResponse();
			}
		};
		getCommandDispatcher().postCommand(cmd);			
	}

 	/**
 	 * This method indicates a user that should be dialed at the specified end-point. Since the endpoint parameter is a 
 	 * string, it could be a phone number, SIP address, or any other identifier for the location of a user. The service 
 	 * provider should dial the user and then call deliverDialResponse() to indicate success.
 	 * 
 	 * A failure during processing of this method should result in a call to requestFailed() with the appropriate error.
 	 * 
 	 * @param user			the resolvable user that this call is for. This value may be null.
 	 * @param endPoint		the "callable" end-point such as a phone number or SIP URI
 	 * @param dialMode		a string from the DialModes class that indicates how the user should be dialed
 	 * @param handler		the listener that receives the response
 	 * 
  	 * @throws ConferenceException when any exceptional situation occurs that prevents the request from being attempted
 	 */	
	public void processDial(final DefaultUser user, final String endPoint, final String dialMode,
			final ConferenceResponseHandler handler) throws ConferenceException {
		final String MNAME = "processDial";
    	logger.logp(Level.FINE, CNAME, MNAME, "user=" + toString(user)+",endPoint="+endPoint+",dialMode="+dialMode);
    	
		if (MyAVConfig.USE_SIP_CONTROL && !endPoint.startsWith("sip")) {
	    	logger.logp(Level.WARNING, CNAME, MNAME, "Unexpected non SIP-controlled dial request; user=" + toString(user)+",endPoint="+endPoint,"+dialMode="+dialMode);	    	
		}
		
		if (user == null) {
			logger.logp(Level.WARNING, CNAME, MNAME, "Ignoring dial request for null user");
			deliverFailureResponse(RequestFailureCodes.DIAL_FAILURE, UNKNOWN_USER_NAME, handler);
			return;
		}
		
		setConnectedProperties(user, ConferenceService.MEDIA_AUDIO, User.CONNECTING);		
    	
		MyMCUCommand cmd = new MyMCUCommand(MYAV_CMD_DIAL, this, mcuCommandTransactionId++, handler) {
			public void process() {
				addParameter("user", user.getName());
				addParameter("endPoint", endPoint);
				addParameter("dialMode", dialMode);
				addParameter("conferenceId", user.getConference().getId());				
				send(mcuCommandSocketClient);
			}

			public void deliverSuccessResponse() {
				logger.logp(Level.FINER, CNAME, MNAME, "deliver response to " + MNAME);				
				deliverDialResponse(user, endPoint, dialMode, handler);				
				
				setConnectedProperties(user, ConferenceService.MEDIA_AUDIO, User.CONNECTED);				
			}		
			
			public void deliverFailureResponse() {
				setConnectedProperties(user, ConferenceService.MEDIA_AUDIO, User.DISCONNECTED);				
				
				logger.logp(Level.WARNING, CNAME, MNAME, "deliver failure response to " + MNAME);				
				super.deliverFailureResponse();
			}
		};
		getCommandDispatcher().postCommand(cmd);
		
		// Demonstration of participants dialing directly into the bridge;
		// This is for test purposes only.
		if (endPoint.equals("9999") || endPoint.equals("9998")) {
			logger.logp(Level.FINE, CNAME, MNAME, "Simulating participants dialing directly into bridge");
			
			final DefaultConference newUserConference = (DefaultConference) user.getConference();
			getCommandDispatcher().postCommand(new Runnable() {
				public void run() {
					// Add participants using easily identifiable name (must be unique).
					for (int i=0; i < 4; i++, dialBridgeUsersSimulationCount++) {
						String userRole;
						String newUserName;
						
						// Test case where user joining the bridge is a moderator or participant
						if (endPoint.equals("9998") && (i == 0)) {
							userRole = User.MODERATOR_USER_ROLE;
							newUserName = endPoint+"-Moderator"+dialBridgeUsersSimulationCount;							
						} else {
							userRole = User.PARTICIPANT_USER_ROLE;
							newUserName = endPoint+"-Caller"+dialBridgeUsersSimulationCount;							
						}
						
						newUserConference.registerUser(newUserName, userRole, null, new ConferenceResponseAdapter());
						DefaultUser newUser = newUserConference.getUserWithName(newUserName);
						setConnectedProperties(newUser, ConferenceService.MEDIA_AUDIO, User.CONNECTED);
						
						logger.logp(Level.FINER, CNAME, MNAME, "Registered "+newUserName+" as "+userRole);
					}
					
					// Add one last participant using the "unique" function.
					String newUserName = generateUniqueUserName(newUserConference);
					newUserConference.registerUser(newUserName, User.PARTICIPANT_USER_ROLE, null, new ConferenceResponseAdapter());
					DefaultUser newUser = newUserConference.getUserWithName(newUserName);
					setConnectedProperties(newUser, ConferenceService.MEDIA_AUDIO, User.CONNECTED);
					
					logger.logp(Level.FINER, CNAME, MNAME, "Registered "+newUserName+" as "+User.PARTICIPANT_USER_ROLE);					
				}
			});			
		}
	}

    /**
  	 * This method indicates a user that should be dialed at the specified end-point. Since the endpoint parameter is a 
  	 * string, it could be a phone number, SIP address, or any other identifier for the location of a user. The service 
  	 * provider should dial the user and then call deliverDialResponse() to indicate success.
  	 * 
  	 * A failure during processing of this method should result in a call to requestFailed() with the appropriate error.
  	 * 
  	 * @param user			the resolvable user that this call is for. This value may be null.
  	 * @param endPoint		the "callable" end-point such as a phone number or SIP URI
  	 * @param dialMode		a string from the DialModes class that indicates how the user should be dialed
  	 * @param mediaFlag     a flag indicating which media (audio, video, or both) to start
  	 * @param handler		the listener that receives the response
  	 * 
   	 * @throws ConferenceException when any exceptional situation occurs that prevents the request from being attempted
   	 * 
   	 * @since Sametime 8.5
  	 */
	public void processDialExtended(final DefaultUser user, final String endPoint, final String dialMode, 
			final int mediaFlag, final ConferenceResponseHandler handler) throws ConferenceException {
		final String MNAME = "processDialExtended";
		
		String mf = "unknown";
		if ((mediaFlag & ConferenceService.MEDIA_FLAG_VALUE_AUDIO_VIDEO) == ConferenceService.MEDIA_FLAG_VALUE_AUDIO_VIDEO)
			mf = "both";
		else if ((mediaFlag & ConferenceService.MEDIA_AUDIO) != 0)
			mf = "audio";
		else if ((mediaFlag & ConferenceService.MEDIA_VIDEO) != 0)
			mf = "video";
		
    	logger.logp(Level.FINE, CNAME, MNAME, "user=" + toString(user)+",endPoint="+endPoint+",dialMode="+dialMode+",mediaFlag="+mf);		
		
		if (MyAVConfig.USE_SIP_CONTROL && !endPoint.startsWith("sip")) {
	    	logger.logp(Level.WARNING, CNAME, MNAME, "Unexpected non SIP-controlled extended dial request; user=" + toString(user)+",endPoint="+endPoint,"+dialMode="+dialMode+",mediaFlag="+mf);	    	
		}
		
		if (user == null) {
			logger.logp(Level.WARNING, CNAME, MNAME, "Ignoring dial request for null user");
			deliverFailureResponse(RequestFailureCodes.DIAL_FAILURE, UNKNOWN_USER_NAME, handler);
			return;
		}
		
		setConnectedProperties(user, ConferenceService.MEDIA_AUDIO, User.CONNECTING);		
		
		MyMCUCommand cmd = new MyMCUCommand(MYAV_CMD_DIAL_EXTENDED, this, mcuCommandTransactionId++, handler) {
			public void process() {
				addParameter("user", user.getName());
				addParameter("endPoint", endPoint);
				addParameter("dialMode", dialMode);
				addParameter("mediaFlag", Integer.toString(mediaFlag));
				addParameter("conferenceId", user.getConference().getId());				
				send(mcuCommandSocketClient);
			}

			public void deliverSuccessResponse() {
				logger.logp(Level.FINER, CNAME, MNAME, "deliver response to " + MNAME);				
				deliverDialExtendedResponse(user, endPoint, dialMode, mediaFlag, handler);
				
				setConnectedProperties(user, mediaFlag, User.CONNECTED);				
			}		
			
			public void deliverFailureResponse() {
				setConnectedProperties(user, ConferenceService.MEDIA_AUDIO, User.DISCONNECTED);
				
				logger.logp(Level.WARNING, CNAME, MNAME, "deliver failure response to " + MNAME);				
				super.deliverFailureResponse();
			}
		};
		getCommandDispatcher().postCommand(cmd);			
	}

	/**
	 * This method indicates that a user should be disconnected from the conference. It does not imply that the user should
	 * be unregistered. If successful, the User object should be returned in a call to deliverDisconnectUserResponse(). 
	 * 
	 * A failure during processing of this method should result in a call to requestFailed() with the appropriate error.
	 *
	 * @param user		the user to disconnect
	 * @param handler	the listener that receives the response. 
	 * 
 	 * @throws ConferenceException when any exceptional situation occurs that prevents the request from being attempted
	 */
	public void processDisconnectUser(final DefaultUser user,
			final ConferenceResponseHandler handler) throws ConferenceException {
		final String MNAME = "processDisconnectUser";

		logger.logp(Level.FINE, CNAME, MNAME, "user=" + user.getName());
		
		MyMCUCommand cmd = new MyMCUCommand(MYAV_CMD_DISCONNECT_USER, this, mcuCommandTransactionId++, handler) {
			public void process() {
				// TODO disconnect user and deliver result response when complete
				addParameter("user", user.getName());
				addParameter("conferenceId", user.getConference().getId());
				send(mcuCommandSocketClient);
			}

			public void deliverSuccessResponse() {
				setDisconnectedProperties(user, ConferenceService.MEDIA_FLAG_VALUE_AUDIO_VIDEO);
				
				logger.logp(Level.FINER, CNAME, MNAME, "deliver response to " + MNAME);				
				deliverDisconnectUserResponse(user, handler);
				
				// TODO optionally handle conference clean up -- for example, if the last
				// participant was disconnected in a meeting. An alternative implementation could 
				// wait a minute or two before ending the call for a meeting in case the moderator
				// rejoins.
				DefaultConference conference = (DefaultConference) user.getConference();
				Property callEndedProperty = conference.getProperty(Conference.CALL_ENDED_PROPERTY);
				boolean isCallEnded = (callEndedProperty != null && callEndedProperty.getBooleanValue());
				
				if (!isCallEnded && getConnectedUsers(conference).size() == 0) {
					logger.logp(Level.FINE, CNAME, MNAME, "Ended call because no connected participants");					
					conference.setProperty(new Property(Property.SYSTEM_PROPERTY_TYPE,
						Conference.CALL_ENDED_PROPERTY, Property.TRUE, true));
				}
			}
			
			public void deliverFailureResponse() {
				setDisconnectedProperties(user, ConferenceService.MEDIA_FLAG_VALUE_AUDIO_VIDEO);
				
				logger.logp(Level.WARNING, CNAME, MNAME, "deliver failure response to " + MNAME);				
				super.deliverFailureResponse();
			}
		};
		getCommandDispatcher().postCommand(cmd);			
	}

	/**
	 * This method will be called to update the conference data for an already created conference. The new conference
	 * data specified as a serialized XML document in documentText should replace the already existing conference data.
	 * If successful, this method should call deliverEditConferenceResponse() with the conferenceId of the edited conference.
	 * If the XML is not valid, for example if the requested schedule times are not available, the previous conference data
	 * should be retained and the requestFailed() method should be called.
	 * 
	 * The login information should be used to authorize the editing of this conference.
	 * 
	 * A failure during processing of this method should result in a call to requestFailed() with the appropriate error.
	 * 
     * @param login         the login information to validate the context of the request
	 * @param conferenceId	the id of the conference to cancel.
	 * @param conferenceDocumentText	the new conference data as a serialized XML string
	 * @param handler		the listener that receives the response
	 * 
	 * @throws ConferenceException when any exceptional situation occurs that prevents the request from being attempted
	 */
	public void processEditConference(Login login, String conferenceId, 
			String conferenceDocumentText, ConferenceResponseHandler handler)
    		throws ConferenceException {
		final String MNAME = "processEditConference";
		logger.logp(Level.FINE, CNAME, MNAME, MNAME, new Object[] {login, conferenceId, conferenceDocumentText});
		
		//-----------------------------------------------------------
		// All of the initial conference data, including the schedule 
		// data should be replaced with the new conference data with 
		// the exception of the following:
		// - Features
		// - Access lists
		// - Conference properties
		//-----------------------------------------------------------
		// TODO if applicable, implement the above - usually only applies to scheduled conferences;
		// Note: Conferences created by Sametime Media Server are adhoc "on demand" conferences, not scheduled.
		
		deliverEditConferenceResponse(conferenceId, conferenceDocumentText, handler);
	}	

    /**
  	 * This method indicates a user has requested a media start/stop. Since the endpoint parameter is a 
  	 * string, it could be a phone number, SIP address, or any other identifier for the location of a user. The service 
  	 * provider should dial the user and then call deliverDialResponse() to indicate success.
  	 * 
  	 * A failure during processing of this method should result in a call to requestFailed() with the appropriate error.
  	 * 
  	 * @param user			the resolvable user that this call is for. This value may be null.
  	 * @param controlFlag	flag indicating operation (e.g. start or stop)
  	 * @param mediaFlag		flag indicating which media the operation applies to
  	 * @param handler		the listener that receives the response
  	 * 
   	 * @throws ConferenceException when any exceptional situation occurs that prevents the request from being attempted
   	 * 
   	 * @since Sametime 8.5
  	 */
	public void processMediaControl(final DefaultUser user, final int controlFlag, final int mediaFlag, 
			final ConferenceResponseHandler handler) throws ConferenceException {
		final String MNAME = "processMediaControl";
		
		if (user == null) {
			logger.logp(Level.WARNING, CNAME, MNAME, "Ignoring media control request for null user");
			deliverFailureResponse(RequestFailureCodes.GENERAL_EXCEPTION_FAILURE, UNKNOWN_USER_NAME, handler);
			return;
		}		
		
		final String conferenceId = user.getConference().getId();
		String cf = "unknown";		
		String mf = "unknown";
		
		if (controlFlag == ConferenceService.MEDIA_CONTROL_START)
			cf = "start";
		else if (controlFlag == ConferenceService.MEDIA_CONTROL_STOP)
			cf = "stop";
		
		if ((mediaFlag & ConferenceService.MEDIA_FLAG_VALUE_AUDIO_VIDEO) == ConferenceService.MEDIA_FLAG_VALUE_AUDIO_VIDEO)
			mf = "both";
		else if ((mediaFlag & ConferenceService.MEDIA_AUDIO) != 0)
			mf = "audio";
		else if ((mediaFlag & ConferenceService.MEDIA_VIDEO) != 0)
			mf = "video";
			
		logger.logp(Level.FINE, CNAME, MNAME, 
				"conferenceId="+conferenceId+
				",controlFlag="+cf+
				",mediaFlag="+mf);

		MyMCUCommand cmd = new MyMCUCommand(MYAV_CMD_MEDIA_CONTROL, this, mcuCommandTransactionId++, handler) {
			public void process() {
				addParameter("user", user.getName());
				addParameter("conferenceId", conferenceId);
				addParameter("controlFlag", Integer.toString(controlFlag));
				addParameter("mediaFlag", Integer.toString(mediaFlag));
				send(mcuCommandSocketClient);
			}

			public void deliverSuccessResponse() {
				logger.logp(Level.FINER, CNAME, MNAME, "deliver response to " + MNAME);				
				deliverMediaControlResponse(user, controlFlag, mediaFlag, handler);
				
				// If using SIP, nothing to do since responses to media control are handled by 
				// softphone (via mcuNotifyUserAck/mcuNotifyUserBye/mcuNotifyUserInvite notifications
				// to MyAVConferenceService from MyMCU).
				//
				// Otherwise, update the user's media connected states as requested.
				
				if (MyAVConfig.USE_SIP_CONTROL == false) {
					if (controlFlag == ConferenceService.MEDIA_CONTROL_START)
						setConnectedProperties(user, mediaFlag, User.CONNECTED);
					else
						setDisconnectedProperties(user, mediaFlag);
				}
			}
			
			public void deliverFailureResponse() {
				setDisconnectedProperties(user, mediaFlag);
				
				logger.logp(Level.WARNING, CNAME, MNAME, "deliver failure response to " + MNAME);				
				super.deliverFailureResponse();
			}
		};
		getCommandDispatcher().postCommand(cmd);			
	}

    /**
     * This method is used to either notify service providers of a user that will be participating in a conference, or 
     * change the userRole for an existing user. Conferences can either be open and allow any user to join, or closed and 
     * allow only registered users to join. With an open conference, a service provider must still explicitly register the 
     * users as they join the conference from audio bridge hardware. If this method is called for a conference that is active,
     * service providers should call the protected method, registerUser(), which will create a new User object if necessary, 
     * set some default user properties and add the User object to a running conference. Service providers can set their own 
     * user properties during user creation by overriding the populateDefaultUserProperties() method. The user PIN passed into
     * this method is guaranteed to be unique for a given conference if the service provider is relying on the calling 
     * application for PIN support. If a service provider is supplying its own PINs for users then those PINs should be unique 
     * per conference and returned in the call to deliverRegisterUserResponse(). If registration is successful, service
     * providers should call the deliverRegisterUserResponse() method.
     * 
     * When this method is called for a running conference, the userRole property of the user will be set to the 
     * userRole argument.
     * 
	 * The login information should be used to authorize the registering of the user.
     * 
	 * A failure during processing of this method should result in a call to requestFailed() with the appropriate error.
	 *
     * @param login         the login information to validate the context of the request
     * @param conferenceId  the conference this registration is scoped.
     * @param userName      uniquely identifies the user.
     * @param userRole      the initial role of the user
     * @param userPin      	the PIN of the user
     * @param handler      the listener that receives the response. 
     * 
	 * @throws ConferenceException when any exceptional situation occurs that prevents the request from being attempted
     */
	public void processRegisterUser(Login login, final String conferenceId, final String userName, 
			final String userRole, final String userPin, final ConferenceResponseHandler handler) throws ConferenceException {
		final String MNAME = "processRegisterUser";
		
		DefaultConference conference = getConferenceWithId(conferenceId);
		DefaultUser	user = null;
		
		logger.logp(Level.FINE, CNAME, MNAME, 
				"conferenceId="+conferenceId+",userName="+userName+",id="+login.getId()+",userRole="+userRole);
	
		if (conference != null)	{      
			user = registerUser(userName, userRole, userPin, null, conference);			
		}

		if (user == null) {
			deliverFailureResponse(RequestFailureCodes.REGISTER_USER_ERROR, "No existing conference", handler);
		} else {
			MyMCUCommand cmd = new MyMCUCommand(MYAV_CMD_REGISTER_USER, this, mcuCommandTransactionId++, handler) {
				public void process() {
					// TODO synchronize newly created (in-memory) user with third-party conference server and
					// report the result when finished.
					
					addParameter("userName", userName);
					addParameter("userRole", userRole);
					addParameter("userPin", userPin);
					addParameter("conferenceId", conferenceId);
					send(mcuCommandSocketClient);
				}

				public void deliverSuccessResponse() {
					logger.logp(Level.FINER, CNAME, MNAME, "deliver response to " + MNAME);
					deliverRegisterUserResponse(conferenceId, userName, userRole, userPin, handler);	
				}
				
				public void deliverFailureResponse() {
					logger.logp(Level.WARNING, CNAME, MNAME, "deliver failure response to " + MNAME);				
					super.deliverFailureResponse();
				}
			};
			getCommandDispatcher().postCommand(cmd);			
		}
	}

    /**
     * This method is the same as <code>processRegisterUser</code> with the addition of 
     * the SIP URI of the user for a SIP enabled conference.
     * 
     * @param sipUri The user's SIP URI
     * 
	 * @throws ConferenceException when any exceptional situation occurs that prevents the request from being attempted
	 * @see #processRegisterUser(Login login, String conferenceId, String userName, String userRole, String userPin, ConferenceResponseHandler handler)
	 * 
	 * @since Sametime 8.5
     */
	public void processRegisterUserSip(Login login, final String conferenceId, 
			final String userName, final String userRole, final String userPin, final String sipUri, 
			final ConferenceResponseHandler handler) throws ConferenceException {
		final String MNAME = "processRegisterUserSip";
		
		DefaultConference conference = getConferenceWithId(conferenceId);
		DefaultUser	user = null;
		
		logger.logp(Level.FINE, CNAME, MNAME, 
				"conferenceId="+conferenceId+",userName="+userName+",id="+login.getId()+",userRole="+userRole+",sipUri="+sipUri);
	
		if (conference != null)	{      
			user = registerUser(userName, userRole, userPin, sipUri, null, conference);
		}

		if (user == null) {
			deliverFailureResponse(RequestFailureCodes.REGISTER_USER_ERROR, "No existing conference", handler);
		} else {
			MyMCUCommand cmd = new MyMCUCommand(MYAV_CMD_REGISTER_USER_SIP, this, mcuCommandTransactionId++, handler) {
				public void process() {
					// TODO synchronize newly created (in-memory) user with third-party conference server and
					// report the result when finished.
					
					addParameter("userName", userName);
					addParameter("userRole", userRole);
					addParameter("userPin", userPin);
					addParameter("conferenceId", conferenceId);
					addParameter("sipUri", sipUri);					
					send(mcuCommandSocketClient);
				}

				public void deliverSuccessResponse() {
					logger.logp(Level.FINER, CNAME, MNAME, "deliver response to " + MNAME);
					deliverRegisterUserResponse(conferenceId, userName, userRole, userPin, handler);	
				}
				
				public void deliverFailureResponse() {
					logger.logp(Level.WARNING, CNAME, MNAME, "deliver failure response to " + MNAME);				
					super.deliverFailureResponse();
				}
			};
			getCommandDispatcher().postCommand(cmd);			
		}
	}

 	/**
 	 * This method indicates that a conference property should be set on the specified conference object. If successful, 
 	 * the conference and property should be returned in a call to deliverSetPropertyResponse().
 	 * 
 	 * A failure during processing of this method should result in a call to requestFailed() with the appropriate error.
 	 * 
 	 * @param conference	the effected conference
 	 * @param property		the property to set
 	 * @param handler		the listener that receives the response
 	 * 
  	 * @throws ConferenceException when any exceptional situation occurs that prevents the request from being attempted
 	 */
	public void processSetConferenceProperty(final DefaultConference conference, final Property property,
			final ConferenceResponseHandler handler)	throws ConferenceException {
		final String MNAME = "processSetConferenceProperty";
		
		logger.logp(Level.FINE, CNAME, MNAME, 
				"conference="+toString(conference)+",name="+property.getName()+",value="+property.getValue());

		MyMCUCommand cmd = new MyMCUCommand(MYAV_CMD_SET_CONFERENCE_PROPERTY, this, mcuCommandTransactionId++, handler) {
			public void process() {
				// TODO synchronize in-memory conference setting with third-party conference server and
				// report the result when finished.
				
				addParameter("conferenceId", conference.getId());
				addParameter("propertyName", property.getName());
				addParameter("propertyValue", property.getValue());
				send(mcuCommandSocketClient);
			}

			public void deliverSuccessResponse() {
				conference.setProperty(property);
				
				// TODO synchronize changes conference setting with other resources; if the property
				// applies to all users, set the property on the conference AND the users themselves
				// (for example, MUTE/UNMUTE as shown below).
				if (property.getName().equals(Conference.MUTED_PROPERTY)) {
					if (property.getValue().equals(Conference.UNMUTED))	{
						setPropertyForAllUsers(conference, new Property(Property.APPLICATION_PUBLIC_PROPERTY_TYPE, 
								User.SILENCED_PROPERTY, User.UNSILENCED), null);
					} else {
						setPropertyForAllUsers(conference, new Property(Property.APPLICATION_PUBLIC_PROPERTY_TYPE, 
								User.SILENCED_PROPERTY, User.SILENCED), User.MODERATOR_USER_ROLE);
					}
				} else if (property.getName().equals(Conference.MUTED_VIDEO_PROPERTY)) {
					if (property.getValue().equals(Conference.UNMUTED)) {
						setPropertyForAllUsers(conference, new Property(Property.APPLICATION_PUBLIC_PROPERTY_TYPE, 
								User.SILENCED_VIDEO_PROPERTY, User.UNSILENCED), null);
					} else {
						setPropertyForAllUsers(conference, new Property(Property.APPLICATION_PUBLIC_PROPERTY_TYPE, 
								User.SILENCED_VIDEO_PROPERTY, User.SILENCED), User.MODERATOR_USER_ROLE);
					}
				}
				
				logger.logp(Level.FINER, CNAME, MNAME, "deliver response to " + MNAME);				
				deliverSetPropertyResponse(conference, property, handler);	
			}
			
			public void deliverFailureResponse() {
				logger.logp(Level.WARNING, CNAME, MNAME, "deliver failure response to " + MNAME);				
				super.deliverFailureResponse();
			}
		};
		getCommandDispatcher().postCommand(cmd);				
	}	

 	/**
 	 * This method indicates a user specific property that should be set by the service provider. If successful, the User
 	 * and Property should be returned in a call to deliverSetPropertyResponse(). 
 	 * 
 	 * A failure during processing of this method should result in a call to requestFailed() with the appropriate error.
 	 * 
 	 * @param user		the effected user
 	 * @param property 	the property to set
 	 * @param handler	the listener that receives the response
 	 * 
  	 * @throws ConferenceException when any exceptional situation occurs that prevents the request from being attempted
 	 */	
	public void processSetUserProperty(final DefaultUser user, final Property property, 
			final ConferenceResponseHandler handler) throws ConferenceException {
		final String MNAME = "processSetUserProperty";
		
		logger.logp(Level.FINE, CNAME, MNAME, 
				"user="+user.getName()+",name="+property.getName()+",value="+property.getValue());

		Property currentProperty = user.getProperty(property.getName());
		if ((currentProperty != null) && (currentProperty.isReadOnly())) {
			deliverFailureResponse(RequestFailureCodes.READ_ONLY_PROPERTY, "Cannot set read-only property", handler);
		} else {	
			MyMCUCommand cmd = new MyMCUCommand(MYAV_CMD_SET_USER_PROPERTY, this, mcuCommandTransactionId++, handler) {
				public void process() {
					// TODO synchronize third-party specific user properties as required
					addParameter("userName", user.getName());
					addParameter("propertyName", property.getName());
					addParameter("propertyValue", property.getValue());
					addParameter("conferenceId", user.getConference().getId());					
					send(mcuCommandSocketClient);					
				}

				public void deliverSuccessResponse() {
					user.setProperty(property);
									
					logger.logp(Level.FINER, CNAME, MNAME, "deliver response to " + MNAME);					
					deliverSetPropertyResponse(user, property, handler);
					
					updateDependentUserProperties(user, property);					
				}
				
				public void deliverFailureResponse() {
					logger.logp(Level.WARNING, CNAME, MNAME, "deliver failure response to " + MNAME);				
					super.deliverFailureResponse();
				}
			};
			getCommandDispatcher().postCommand(cmd);				
		}
	}
	
 	/**
 	 * This method will be called to prepare the service provider for real-time control of a conference. The service provider
 	 * should prepare any conferencing resources necessary to send and receive conference or user events within that conference. 
 	 * If this method is successful, a runtime conference object should be returned in a call to 
 	 * deliverStartConferenceControlResponse(). 
 	 * 
 	 * The login information should be used to authorize control of the conference.
      * 
 	 * A failure during processing of this method should result in a call to requestFailed() with the appropriate error.
 	 *
      * @param login         the login information to validate the context of the request
 	 * @param conferenceId The conference to enable control
 	 * @param initialProperties	The set of initial properties to set on the conference object
 	 * @param handler     The listener that receives the response
 	 * 
  	 * @throws ConferenceException when any exceptional situation occurs that prevents the request from being attempted
 	 */
	public void processStartConferenceControl(final Login login, final String conferenceId,
			List initialProperties, final ConferenceResponseHandler handler)
			throws ConferenceException {
		final String MNAME = "processStartConferenceControl";

		final DefaultConference conference;
		if (getConferenceWithId(conferenceId) == null) {
			conference = createConference(login, conferenceId, initialProperties, null);
		} else {
			conference = getConferenceWithId(conferenceId);	
		}
		
		if (MyAVConfig.USE_SIP_CONTROL) {
			String uri = MyAVConfig.SIP_PROXY_PROTOCOL + conferenceId + "@" + MyAVConfig.SIP_PROXY_HOST + ":" + MyAVConfig.SIP_PROXY_PORT;
			if (!MyAVConfig.SIP_PROXY_TRANSPORT.equalsIgnoreCase("UDP")) {
				uri += ";transport=" + MyAVConfig.SIP_PROXY_TRANSPORT;
			}
			logger.logp(Level.FINE, CNAME, MNAME, "Setting SIP MCU URI to "+uri);			
			conference.setSipMcuUri(uri);			
		}		
		
		final ConferenceDocument conferenceDocument = getConferenceDocument(initialProperties);
		logger.logp(Level.FINE, CNAME, MNAME, 
				"conferenceId="+conferenceId+",login="+login.getName()+",id="+login.getId()+",conferenceDocument="+toString(conferenceDocument));
		
		MyMCUCommand cmd = new MyMCUCommand(MYAV_CMD_START_CONFERENCE_CONTROL, this, mcuCommandTransactionId++, handler) {
			public void process() {
				// TODO set system properties depending on what conference supports (e.g., client hosted).
				// Set Property.SYSTEM_PROPERTY_XXX's as required.
				conference.setProperty(new Property(Property.SYSTEM_PROPERTY_TYPE,
		                Conference.CONFERENCE_IS_CLIENT_HOSTED, Property.FALSE, true));
				
				addParameter("conferenceId", conferenceId);
				addParameter("loginId", login.getId());
				addParameter("conferenceDocumentText", conferenceDocument.toXmlString());
				send(mcuCommandSocketClient);
			}

			public void deliverSuccessResponse() {
				// TODO start conference and signal when complete.
				// If error, call deliverFailureResponse(RequestFailureCodes.XXX_ERROR, msg, handler);
				logger.logp(Level.FINER, CNAME, MNAME, "deliver response to " + MNAME);				
				deliverStartConferenceControlResponse(conference, handler);	
			}		
			
			public void deliverFailureResponse() {
				logger.logp(Level.WARNING, CNAME, MNAME, "deliver failure response to " + MNAME);				
				super.deliverFailureResponse();
			}
		};
		getCommandDispatcher().postCommand(cmd);
	}

 	/**
 	 * This method is an indication that the service provider can stop managing the conference in a real-time control 
 	 * manner. It does not mean that the audio conference should be terminated, only that the resources allocated 
 	 * to support real-time conference control can be disposed. If successful, the conference that was stopped should be 
 	 * returned in a call to deliverStopConferenceControl(). 
 	 * 
 	 * A failure during processing of this method should result in a call to requestFailed() with the appropriate error.
 	 *
 	 * @param conference The conference to disable control
 	 * @param handler	The listener that receives the response
 	 * 
  	 * @throws ConferenceException when any exceptional situation occurs that prevents the request from being attempted
 	 */
	public void processStopConferenceControl(final DefaultConference conference,
			final ConferenceResponseHandler handler) throws ConferenceException {
		final String MNAME = "processStopConferenceControl";
    	final String conferenceId = conference.getId();		
    	logger.logp(Level.FINE, CNAME, MNAME, "conferenceId=" + conferenceId);

		MyMCUCommand cmd = new MyMCUCommand(MYAV_CMD_STOP_CONFERENCE_CONTROL, this, mcuCommandTransactionId++, handler) {
			public void process() {
				// TODO do whatever cleanup is necessary and report when finished.
		    	// Note: conferenceId may be null.
				
				addParameter("conferenceId", conferenceId);
				send(mcuCommandSocketClient);
			}

			public void deliverSuccessResponse() {
				logger.logp(Level.FINER, CNAME, MNAME, "deliver response to " + MNAME);
				deliverStopConferenceControlResponse(conference, handler);				
			}
			
			public void deliverFailureResponse() {
				logger.logp(Level.WARNING, CNAME, MNAME, "deliver failure response to " + MNAME);				
				super.deliverFailureResponse();
			}
		};
		getCommandDispatcher().postCommand(cmd);		
	}

 	/** 
 	 * This method will be called when an asynchronous request has not been answered in an appropriate amount of time
 	 * defined in the ConferenceManager.properties file. Service providers should cancel processing of the timed out
 	 * request and then call deliverFailureResponse() for the listener.
 	 * 
 	 * @param handler the listener that receives the response
  	 * @throws ConferenceException when any exceptional situation occurs that prevents the request from being attempted
 	 */
	public void processTimeoutError(final ConferenceResponseHandler handler)
			throws ConferenceException {
		final String MNAME = "processTimeoutError";
		logger.logp(Level.FINE, CNAME, MNAME, MNAME);		

		MyMCUCommand cmd = new MyMCUCommand(MYAV_CMD_TIMEOUT_ERROR, this, mcuCommandTransactionId++, handler) {
			public void process() {
				send(mcuCommandSocketClient);
			}

			public void deliverSuccessResponse() {
				// "Success" is a relative term in this case; it's actually 
				// the MCU acknowledging that it's received the timeout error notification
				// and provided an error code/error message.
				logger.logp(Level.WARNING, CNAME, MNAME, "deliver response to " + MNAME);
				
				// TODO Process requests cannot take forever; if a timeout occurs, handle
				// whatever cleanup is necessary and deliver the failure result. 
				//
				// Note: 'handler' parameter is the same object presented on the processXXX request
				// and can be used as a key for tracking pending application-specific asynchronous requests.
				super.deliverFailureResponse();
			}
			
			public void deliverFailureResponse() {
				logger.logp(Level.WARNING, CNAME, MNAME, "deliver failure response to " + MNAME);

				// Failure in this case means the MCU was not notified or encountered an error
				// processing the notification, so return the superclass' general failure message.				
				super.deliverFailureResponse();
			}
		};
		getCommandDispatcher().postCommand(cmd);			
	}

    /**
     * This method provides a way to remove users from a conference, which is different from hanging up a user. If the 
     * conference is active, the unregistered user should be ejected from the conference. If the conference is not active yet, 
     * then the unregistered user will be removed from the access list for the conference. If this method is called to 
     * unregister the owner of the conference, it should not result in the conference being terminated. 
     * 
     * If successful, a call should be made to deliverUnregisterUserResponse() after the user has been either disconnected
     * from the conference or removed from the access list. 
     *
	 * The login information should be used to authorize the registering of the user.
     * 
	 * A failure during processing of this method should result in a call to requestFailed() with the appropriate error.
	 *
     * @param login         the login information to validate the context of the request
     * @param conferenceId  the conference this registration is scoped.
     * @param userName      uniquely identifies the user.
     * @param handler      the listener that receives the response. 
     * 
	 * @throws ConferenceException when any exceptional situation occurs that prevents the request from being attempted
     */
	public void processUnregisterUser(Login login, final String conferenceId, final String userName,
			final ConferenceResponseHandler handler) throws ConferenceException {
		final String MNAME = "processUnregisterUser";
		logger.logp(Level.FINE, CNAME, MNAME, 
				"conferenceId="+conferenceId+",userName="+userName+",id="+login.getId());

		final DefaultConference conference = getConferenceWithId(conferenceId);		
		MyMCUCommand cmd = new MyMCUCommand(MYAV_CMD_UNREGISTER_USER, this, mcuCommandTransactionId++, handler) {
			public void process() {
				if (conference != null)	{	
					// TODO Add application-specific code for releasing user-specific resources.
					
				    DefaultUser user = conference.getUserWithName(userName);		    		
					unregisterUser(user);
					
					addParameter("userName", userName);
					addParameter("conferenceId", conferenceId);
					send(mcuCommandSocketClient);					
				}
			}

			public void deliverSuccessResponse() {
				logger.logp(Level.FINER, CNAME, MNAME, "deliver response to " + MNAME);				
				deliverUnregisterUserResponse(conferenceId, userName, handler);
			}
			
			public void deliverFailureResponse() {
				logger.logp(Level.WARNING, CNAME, MNAME, "deliver failure response to " + MNAME);				
				super.deliverFailureResponse();
			}
		};
		getCommandDispatcher().postCommand(cmd);			
	}

    /** 
     * This method will be called to validate a conference document in the context of a login before the conference
     * is actually created. It is meant to give service providers a way to notify the calling application that 
     * a particular schedule or feature set is not available for the specified conference, either for all users or the 
     * particular user specified by the login parameter. If this method is successful, service providers should call 
     * deliverValidateConferenceResponse() with a conference document containing supported features. If this method
     * fails, service providers should call deliverFailureResponse() with an appropriate error code. Note that this
     * method may or may not be called before createConference() is called, and even though this method succeeds, 
     * a subsequent createConference() call with the same parameters can still fail.
     * 
     * @param login         the login information to validate the context of the request
     * @param conferenceDocumentText  the document that describes the conference to validate
	 * @param handler		the listener that receives the response.
	 *  
     * @throws ConferenceException
     */
	public void processValidateConference(Login login, String conferenceDocumentText,
			ConferenceResponseHandler handler) throws ConferenceException {
		final String MNAME = "processValidateConference";
		logger.logp(Level.FINE, CNAME, MNAME, MNAME, new Object[] {login, conferenceDocumentText});
		
		// This method is typically called before the conference is
		// actually started. It is used by callers (typically
		// clients) to determine if the conference is actually 
		// going to happen. It may not because of schedule issues
		// or one or more unsupported features.  Note that even if
		// a positive response it is delivered, it is still possible
		// that the the actual starting of the conference could fail.
		// 
		// TODO Add application-specific code for confirming that the conference is valid,
		// otherwise deliver the failure response. In this implementation, all conferences
		// are considered valid.
		
		deliverValidateConferenceResponse(conferenceDocumentText, handler);
	}

	//
	// CommandDispatcher methods for offloading processXXX method handling to another thread.
	//
	// @see CommandDispatcher
	//
	
	protected synchronized CommandDispatcher getCommandDispatcher()
	{
		return localCommandDispatcher;
	}

	protected synchronized void startLocalCommandDispatcher()
	{
		localCommandDispatcher.start();
	}	
	
	protected synchronized void stopLocalCommandDispatcher()
	{
		localCommandDispatcher.stop();
	}
	
	// 
	// Notification methods from MCU to service provider
	//

	public void mcuNotifyUserAck(String conferenceId, String userUri) {
		final String MNAME = "mcuNotifyUserAck";		
		logger.logp(Level.FINER, CNAME, MNAME, "conferenceId="+conferenceId+",userUri="+userUri);
		
		DefaultConference conference = getConferenceWithId(conferenceId);
		if (conference == null) {
			logger.logp(Level.WARNING, CNAME, MNAME, "Ignoring unrecognized conference: "+conferenceId);
			return;
		}
		DefaultUser user = conference.getUserWithNormalizedUri(userUri);
		if (user == null) {
			List<DefaultUser> users = conference.getUserList();
			logger.logp(Level.WARNING, CNAME, MNAME, "Ignoring unrecognized user: "+
					userUri+" (conference users="+toString(users)+")");
			return;
		}			
		
		setConnectedProperties(user, ConferenceService.MEDIA_AUDIO, User.CONNECTED);
	}	
	
	public void mcuNotifyUserInvite(String conferenceId, String userUri) {
		final String MNAME = "mcuNotifyUserInvite";
		logger.logp(Level.FINER, CNAME, MNAME, "conferenceId="+conferenceId+",userUri="+userUri);
		
		DefaultConference conference = getConferenceWithId(conferenceId);
		if (conference == null) {
			logger.logp(Level.WARNING, CNAME, MNAME, "Ignoring unrecognized conference: "+conferenceId);
			return;
		}
		DefaultUser user = conference.getUserWithNormalizedUri(userUri);
		if (user == null) {
			List<DefaultUser> users = conference.getUserList();
			logger.logp(Level.WARNING, CNAME, MNAME, "Ignoring unrecognized user: "+
					userUri+" (conference users="+toString(users)+")");
			return;
		}	
		
		setConnectedProperties(user, ConferenceService.MEDIA_AUDIO, User.CONNECTING);
	}
	

	public void mcuNotifyUserSetProperty(String conferenceId, String userName,
			String propertyName, String propertyValue) {
		final String MNAME = "mcuNotifyUserSetProperty";
		logger.logp(Level.FINER, CNAME, MNAME, "conferenceId="+conferenceId+",userName="+userName+",name="+propertyName+",value="+propertyValue);

		DefaultConference conference = getConferenceWithId(conferenceId);
		if (conference == null) {
			logger.logp(Level.WARNING, CNAME, MNAME, "Ignoring unrecognized conference: "+conferenceId);
			return;
		}
		DefaultUser user = conference.getUserWithName(userName);
		if (user == null) {
			List<DefaultUser> users = conference.getUserList();
			logger.logp(Level.WARNING, CNAME, MNAME, "Ignoring unrecognized user: "+
					userName+" (conference users="+toString(users)+")");
			return;
		}
		
		user.setProperty(new Property(propertyName, propertyValue));
		logger.logp(Level.FINE, CNAME, MNAME, "Set user "+user.getName()+" property "+propertyName+" to "+propertyValue);
	}	
	
	public void mcuNotifyUserBye(String conferenceId, String userUri) {
		final String MNAME = "mcuNotifyUserBye";		
		logger.logp(Level.FINER, CNAME, MNAME, "conferenceId="+conferenceId+",user="+userUri);
		
		DefaultConference conference = getConferenceWithId(conferenceId);
		if (conference == null) {
			logger.logp(Level.WARNING, CNAME, MNAME, "Ignoring unrecognized conference: "+conferenceId);
			return;
		}
		DefaultUser user = conference.getUserWithNormalizedUri(userUri);
		if (user == null) {
			List<DefaultUser> users = conference.getUserList();
			logger.logp(Level.WARNING, CNAME, MNAME, "Ignoring unrecognized user: "+
					userUri+" (conference users="+toString(users)+")");
			return;
		}		

		setDisconnectedProperties(user, ConferenceService.MEDIA_FLAG_VALUE_AUDIO_VIDEO);		
	}
	
	//
	// Convenience methods.
	//
	
	private ConferenceDocument getConferenceDocument(List initialProperties) {
		ConferenceDocument result = null;
		
		for(int index=0; (result == null) && (index < initialProperties.size()); ++index) {
			Object propObject = initialProperties.get(index);
			if (propObject instanceof Property) {
				Property property = (Property) propObject;
				if (property.getName().equals(Conference.CONFERENCE_DOCUMENT)) {
					result = new ConferenceDocument(property.getValue());
				}
			}
		}
		
		return result;
	}	
	
	private void setDisconnectedProperties(DefaultUser user, int mediaFlag) {
		final String MNAME = "setDisconnectedProperties";
		
		if (user == null) {
			logger.logp(Level.FINE, CNAME, MNAME, "Ignoring "+UNKNOWN_USER_NAME+" mediaFlag="+mediaFlag);
			return;
		}
		
		// TODO Set other third-party defined properties affected by disconnected as needed.
		if ((mediaFlag & ConferenceService.MEDIA_AUDIO) != 0) {
			// By providing the ConferenceResponseAdapter (handler), user.setProperty
			// below will invoke the service adapter method MyAVConferenceService.processSetUserProperty
			// which in turn notifies MyMCU of the property change. Without this parameter, User.setProperty
			// is treated as a local event and the adapter is not notified.
			//
			// MyMCU uses this connect/disconnect notification as part of its "speaking simulation" that 
			// toggles users' USER_TALKING_PROPERTY value on/off at regular intervals for test purposes. 

			user.setProperty(new Property(User.CONNECTION_PROPERTY, User.DISCONNECTED), new ConferenceResponseAdapter(CNAME+":"+MNAME, null));
			logger.logp(Level.FINER, CNAME, MNAME, "Disconnected audio for user: "+user.getName());			
		}

		if ((mediaFlag & ConferenceService.MEDIA_VIDEO) != 0) {
			logger.logp(Level.FINER, CNAME, MNAME, "Disconnected video for user: "+user.getName());	
			user.setProperty(new Property(User.CONNECTION_VIDEO_PROPERTY, User.DISCONNECTED));
		}
	}
	
	private void setConnectedProperties(DefaultUser user, int mediaFlag, String state) {
		final String MNAME = "setConnectedProperties";
		
		if (user == null) {
			logger.logp(Level.FINE, CNAME, MNAME, "Ignoring "+UNKNOWN_USER_NAME+" mediaFlag="+mediaFlag+" state="+state);
			return;
		}
		
		// TODO Set other third-party defined properties affected by connected as needed.
		if ((mediaFlag & ConferenceService.MEDIA_AUDIO) != 0) {
			// Handle auto-mute and muted conference for late joiners
			DefaultConference conference = (DefaultConference) user.getConference();
			Property conferenceMutedProperty = conference.getProperty(Conference.MUTED_PROPERTY);
			Property silencedProperty = user.getProperty(User.SILENCED_PROPERTY);
			Property connectionProperty = user.getProperty(User.CONNECTION_PROPERTY);

			boolean isConferenceMuted = (conferenceMutedProperty != null) && conferenceMutedProperty.getValue().equals(Conference.MUTED);
			boolean isSilenced = (silencedProperty != null) && silencedProperty.getValue().equals(User.SILENCED);
			boolean isModerator = user.getRole().equals(User.MODERATOR_USER_ROLE);
			boolean isConnectingFirstTime = state.equals(User.CONNECTING) &&
				(connectionProperty == null ||
				(!connectionProperty.getValue().equals(User.CONNECTING)) &&
				!connectionProperty.getValue().equals(User.CONNECTED));
			
			// Auto-mute is only applied the first time the user enters the conference; detect
			// this by checking if connection state transitions from CONNECTING to CONNECTED.
			if (isConferenceMuted && !isSilenced && !isModerator && isConnectingFirstTime) {
				logger.logp(Level.FINE, CNAME, MNAME, "Silencing audio for user "+user.getName()+ " because conference is muted");					
				user.setProperty(new Property(Property.APPLICATION_PUBLIC_PROPERTY_TYPE, User.SILENCED_PROPERTY, User.SILENCED));			
			}
			
			// By providing the ConferenceResponseAdapter (handler), user.setProperty
			// below will invoke the service adapter method MyAVConferenceService.processSetUserProperty
			// which in turn notifies MyMCU of the property change. Without this parameter, User.setProperty
			// is treated as a local event and the adapter is not notified.
			//
			// MyMCU uses this connect/disconnect notification as part of its "speaking simulation" that 
			// toggles users' USER_TALKING_PROPERTY value on/off at regular intervals for test purposes. 
			
			user.setProperty(new Property(User.CONNECTION_PROPERTY, state), new ConferenceResponseAdapter(CNAME+":"+MNAME, null));
			logger.logp(Level.FINER, CNAME, MNAME, "Connection audio state for user "+user.getName()+" is "+state);			
		}
		
		if ((mediaFlag & ConferenceService.MEDIA_VIDEO) != 0) {
			logger.logp(Level.FINER, CNAME, MNAME, "Connection video state for user "+user.getName()+" is "+state);			
			user.setProperty(new Property(User.CONNECTION_VIDEO_PROPERTY, state));		
		}
	}		
	
	private void setPropertyForAllUsers(DefaultConference conference, Property property, String exclusionRole) {
		final String MNAME = "setPropertyForAllUsers";
		logger.logp(Level.FINER, CNAME, MNAME, "property="+property+",conferenceId="+conference.getId()+",exclusionRole="+exclusionRole);		
		
		Iterator iter = conference.getUserList().iterator();
		while (iter.hasNext()) {
			DefaultUser	user = (DefaultUser) iter.next();
			
			if (user.getRole().equals(exclusionRole) == false) {
				user.setProperty(property);
				updateDependentUserProperties(user, property);
			}
		}
	}
	
	private void updateDependentUserProperties(DefaultUser user, Property property) {
		final String MNAME = "updateDependentUserProperties";
		
		// Handle related audio and video properties
		if (property.getName().equals(User.MUTED_PROPERTY)) {
			if (property.getValue().equals(User.UNMUTED)) {
				Property silencedProperty = user.getProperty(User.SILENCED_PROPERTY);
				boolean isSilenced = (silencedProperty != null) && silencedProperty.getValue().equals(User.SILENCED);
				if(isSilenced) {
					// User is unmuted - automatically unsilence audio
					user.setProperty(new Property(Property.APPLICATION_PUBLIC_PROPERTY_TYPE, 
							User.SILENCED_PROPERTY, User.UNSILENCED));
					logger.logp(Level.FINER, CNAME, MNAME, "Automatically silenced "+toString(user));					
				}
			}
		} else if (property.getName().equals(User.MUTED_VIDEO_PROPERTY)) {
			if (property.getValue().equals(User.UNMUTED)) {
				Property silencedVideoProperty = user.getProperty(User.SILENCED_VIDEO_PROPERTY);
				boolean isSilencedVideo = (silencedVideoProperty != null) && silencedVideoProperty.getValue().equals(User.SILENCED);
				if(isSilencedVideo) {
					// User is unmuted - automatically unsilence video
					user.setProperty(new Property(Property.APPLICATION_PUBLIC_PROPERTY_TYPE, 
							User.SILENCED_VIDEO_PROPERTY, User.UNSILENCED));
					logger.logp(Level.FINER, CNAME, MNAME, "Automatically unsilenced "+toString(user));					
				}
			}
		} 
	}
	
	private List<DefaultUser> getConnectedUsers(DefaultConference conference) {
		final String MNAME = "getConnectedUsers";
		
		List<DefaultUser> users = new ArrayList<DefaultUser>();
		Iterator iter = conference.getUserList().iterator();
		while (iter.hasNext()) {
			DefaultUser	user = (DefaultUser) iter.next();
			if (user.isConnected() || user.isConnecting()) {
				users.add(user);
			}
		}
		
		logger.logp(Level.FINER, CNAME, MNAME, "Number of connected users = "+users.size());
		if (users.size() > 0 && logger.isLoggable(Level.FINEST)) {
			logger.logp(Level.FINEST, CNAME, MNAME, "Connected users ["+toString(users)+"]");
		}
		
		return users;
	}
	
	//
	// Notification when MCU command socket is ready / no longer available
	// 
	void setMyMCUCommandSocketClientAvailable(boolean available) {
		final String MNAME = "setMyMCUCommandSocketClientAvailable";
		
		// Signal ready to go (or not)!
		setAvailable(available);
		logger.logp(Level.INFO, CNAME, MNAME, "Service available="+available);		
	}
	
	//
	// MCU commands that have been dispatched but have not yet been
	// processed are retained for deliverXxxReponse notification. 
	//
	
	void addPendingCommand(int transactionId, MyMCUCommand myMCUCommand) {
		pendingMCUCommands.put(transactionId, myMCUCommand);
	}	
	
	MyMCUCommand removePendingCommand(int transactionId) {
		return pendingMCUCommands.remove(transactionId);
	}	
	
	//
	// Logging methods for pretty printing objects of interest.
	//
	
	private String toString(Conference conference) {
		StringBuffer sb = new StringBuffer();
		sb.append("id="+conference.getId());
		return sb.toString();
	}
	
	private String toString(DefaultUser user) {
		if (user == null) {
			return UNKNOWN_USER_NAME;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("name="+user.getName());
		
		if (user.getUri() != null) {
			sb.append(",uri="+DefaultConference.normalizeUri(user.getUri()));
		}
		sb.append(",properties="+toStringListProperties(user.getProperties()));
		return sb.toString();		
	}
	
	private String toStringListProperties(List list) {
		Properties props = new Properties();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Property prop = (Property) iter.next();
			props.put(prop.getName(), prop.getValue());
		}
		return props.toString();
	}
	
	private String toString(List<DefaultUser> users) {
		StringBuffer sb = new StringBuffer();
		Iterator iter = users.listIterator();
		while (iter.hasNext()) {
			sb.append(toString((DefaultUser) iter.next()));
			if (iter.hasNext()) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
	
	private String toString(ConferenceDocument conferenceDocument) {
		return MyAVLogger.toShorterString(logger, conferenceDocument.toXmlString());
	}
}
