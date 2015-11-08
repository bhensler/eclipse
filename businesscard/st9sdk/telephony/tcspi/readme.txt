Licensed Materials - Property of IBM

L-MCOS-96LQPJ

(C) Copyright IBM Corp. 2006, 2013 All Rights Reserved

US Government Users Restricted Rights - Use, duplication or 
disclosure restricted by GSA ADP Schedule Contract with IBM Corp.  

******************************************************************************

			IBM Sametime Audio Video Integration
	(also known as Telephony Conferencing Service Provider Interface (TCSPI))

******************************************************************************

The Telephony Conferencing Service Provider Interface (TCSPI) contains the SPI and
corresponding javadoc, along with source code and binary files for a sample service
provider. It also includes supporting files to allow you to test the sample or your
own service provider with a Sametime 9.0 server.

The IBM Sametime Connect 9.0 client includes new telephony APIs and SPIs
that enable you to develop client-side telephony plug-ins. IBM Sametime
Connect 9.0 includes client plug-ins that provide click-to-call features
via the TCSPI and voice chat (VoIP) features.  For more information on the
client telephony APIs, see the client directory of the IBM Sametime Telephony
Toolkit.  For more information on building plug-ins for IBM Sametime Connect, see
the IBM Sametime Connect Toolkit in the IBM Sametime Software Development
Kit.


Directory Structure
-------------------

\readme.txt										- this file
\stav-integration-guide.pdf						- Sametime Voice and Video Integration Guide
\javadoc										- javadoc for the TCSPI 
\lib											- contains the following files:
\lib\TelephonyService.jar			  			- SPI binaries
\lib\com.ibm.telephony.conferencing.myav.mcu.ear	
								  				- MyAV Service Provider enterprise application
\sametime_tcspi									- MyAV Service Provider sample ready for deployment
\sametime_tcspi\properties						- Sametime 9.0 translated end user messages
\sametime_tcspi\ConferenceManager.properties	- Sametime 9.0 Service Provider configuration
\sametime_tcspi\com.ibm.telephony.conferencing.myav.jar
												- MyAV Service Provider executable
\sametime_tcspi\myav.properties					- MyAV Service Provider configuration
\src\com.ibm.telephony.conferencing.myav		- source code for MyAV Service Provider (JAR)
\src\com.ibm.telephony.conferencing.myav.common	- source code for MyAV Service Provider (common)
\src\com.ibm.telephony.conferencing.myav.mcu	- source code for MyAV Service Provider (EAR)
\src\com.ibm.telephony.conferencing.myav.mcuEAR	- source code for MyAV Service Provider (EAR)

FUNCTIONAL UPDATES TO MyAVConferenceService EXAMPLE:

* Auto-end conference by setting Conference.CALL_ENDED_PROPERTY if all participants leave.
* Invoke setAvailable(...) only after MyMCU indicates availability via setMyMCUCommandSocketClientAvailable.
* Mute participants on entry if Conference.AUTO_MUTE property is true ("optimize for large meetings" support).
* Correct handling of co-dependent properties User.MUTED_VIDEO_PROPERTY and User.MUTED_PROPERTY.

To install instructions for the MyAV Service provider, or to import into your
development environment, see the Sametime Audio/Video Integration Guide.