Telephony Sample
=====================

Overview
========
This sample demonstrates how to build a telephony application that can publish telephony attribute, 
add watch on a Sametime user, and get notifications about the user's status changes. 

The Telephony Component is suitable for any external user identification. 
 
Telephony Sample Content
=============================
The sample includes the following files:

1. TelephonySample.java	       		 This file includes a sample application that uses the TelephonyAdapter component.
2. RunSample.bat	            	 This file is a simple batch file for activating the sample program.
3. st.telephony.adapter.properties	 The TelephonyAdapter component configuration file.

Telephony Sample Structure
=============================
TelephonySample implements the following interfaces:

TelephonyAdapterListener			 Receives the Telephony Presence Adapter events

How It Works
============
The TelephonySample instantiates the TelephonyAdapter as follows:
1.	Create the telephony service:
_service = TelephonyAdapterComp.getInstance();

2.	Add itself as a listener to receive sametimeStatusChanged events:
_service.setListener(this);

3.	Initiate the Telephony Adapter service:
_service.initiate();

When the Sametime community is available and ready to get requests, the CommunityAvailable event is sent. 

The public void communityAvailable() method is activated. When this event is received, the telephony application might
use any of the Telephony Presence Adapter services, for example:
1. Subscribe for user's status changes events:
_telephonyComp.addWatch(USER_TELEPHONY_ID);
2. Publish user's telephony status:
_telephonyComp.publishTelepohonyStatus(USER_TELEPHONY_ID, USER_TELEPHONY_STATUS);

The public void sametimeStatusChanged(String telephonyUserId, int sametimeStatus) method is activated when a watched user's sametime status has been changed. 
It receives the user's telephony ID and the updated Sametime status.

Running the Sample
==================
1. Customize the source:

   Change the USER_TELEPHONY_ID to a user ID which can be resolved by the Sametime environment.
   Change the USER_TELEPHONY_STATUS to one of the supported Telephony status values as follows:
     *  TELEPHONE_STATUS_UNKNOWN = 0 
	 *  TELEPHONE_STATUS_AVAILABLE = 1 
	 *  TELEPHONE_STATUS_BUSY = 2 
	 *  TELEPHONE_STATUS_DO_NOT_DISTURB = 3 
	 *  TELEPHONE_STATUS_NOT_SUT_USER = 4 
	 *  
	Please notice, it is the client's implementation responsibility to determine the visualization of the various Telephony status values.
        
2. Compile:
   Compile the TelephonySample.java to create the file TelephonySample.class

3. Set the Telephony component properties:
   At the provided configuration st.telephony.adapter.properties unmark and set the following attributes to match your environment:
         connecting.server.dns = the qualified DNS name of the Sametime server.
   
4. Customize the RunSample.bat:
    4.1. Set the JAVA_HOME variable to your Java home directory (Java 1.5).
    4.2. Set the ADAPTER_JAR variable to the server toolkit jar (stcommsrvrtk.jar).
    4.3. Set the ADAPTER_PROPERTIES_FILE variable to the TelephonyAdapter configuration file (st.telephony.adapter.properties).

5. Run
   From the command line, activate RunSample by: RunSample.bat 
   
   Please notice that the example program does not exit, it is staying on "idle" mode, listening to the user's Sametime status changes.
   In order to exit, use ctrl-c at the command line window.



