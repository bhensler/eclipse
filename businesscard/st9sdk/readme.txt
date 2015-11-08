Licensed Materials - Property of IBM
(C) Copyright IBM Corp. 2006, 2013 All Rights Reserved

US Government Users Restricted Rights - Use, duplication or 
disclosure restricted by GSA ADP Schedule Contract with IBM Corp.  

*******************************************************************************

     IBM(R) Sametime(R) 9.0 Software Development Kit

*******************************************************************************

This readme provides important information about the IBM Sametime 9.0 
Software Development Kit (SDK).

IBM Sametime is a market-leading product and platform for real-time
collaboration. IBM Sametime provides instant messaging, presence awareness, and
online meeting (Web conferencing) services that enable people to collaborate in
real time, regardless of their physical location.

The IBM Sametime SDK is a collection of toolkits for the IBM Sametime
platform. These toolkits enable you to build applications that access IBM
Sametime services, extend IBM Sametime by providing new capabilities, or
integrate with IBM Sametime client and server components.

The IBM Sametime 9.0 SDK is an update to the IBM Sametime 9.0 SDK and can be
downloaded from the IBM developerWorks Lotus Toolkit Downloads Web site:

   http://www.ibm.com/developerworks/lotus/downloads/toolkits.html

Previous releases of the IBM Sametime SDK and IBM Sametime toolkits are also
available for download on that site.


---------------------------------
INSTALLING THE IBM SAMETIME SDK
---------------------------------

The IBM Sametime 9.0 SDK is packaged in a .zip archive file. To install
the SDK, simply extract the contents of this file to a local drive, which will
create an st9sdk directory containing the SDK contents.

To uninstall the SDK, simply delete the st852ifr1sdk directory.

NOTE: If you previously installed an older version of the IBM Sametime SDK such
as 7.5x or 8.0x, install the 9.0 version in a separate directory, or uninstall
the previous SDK before installing 9.0 .


---------------------------
IBM SAMETIME SDK CONTENTS
---------------------------

The IBM Sametime 9.0 SDK includes the following toolkits:

- IBM Sametime Connect Toolkit:
     Used to build Eclipse plug-ins to integrate with or extend the IBM
     Sametime Connect client, releases 9.0 or later.

- IBM Sametime Connect Web API Toolkit:
     Used to enable web pages to interact with the local Sametime Client. For
     example, a web page could include awareness links that open chat windows in
     the actual Sametime Client UI.

- IBM Sametime Java Toolkit:
     Used to add IBM Sametime features to Java applications.

- IBM Sametime Browser Client Toolkit:
     The Browser Client toolkit is a collection of APIs that can be used to build web enabled
     communication enabled business (CEBP) processes by embedding presence, instant messaging
     and other real-time capabilities inside a web application.

- IBM Sametime Links Toolkit:
     Used to add IBM Sametime features to Web pages using JavaScript and HTML.
     Note: This is different from the Connect Web API Toolkit in that it does not
     require the Sametime Client to be installed - it is fully self contained and
     uses Java applets to show awareness, to chat, etc.
     NOTE: The Sametime Links Toolkit is being deprecated starting in Sametime 8.5 in favor
     of the Sametime Browser IM Toolkit.

- IBM Sametime Helper Toolkit:
     Provides an external interface to basic functionality of the IBM Sametime Client.
     For example, an application can use the Helper Toolkit to open a chat window in
     the actual Sametime Client UI.

- Sametime Advanced Toolkit:
     Used to build applications that extend the capabilities of IBM Sametime Advanced 9.0.
     Example of Sametime Advanced features include persistent chat rooms, broadcast and 
     collaboration channels, and location services.

- Community Server Toolkit:
     Used to build Java components that add or extend services on the IBM
     Sametime server.

- Directory and Database Access Toolkit:
     Used to build C++ or Java components for the IBM Sametime server that
     provide directory integration, chat logging, or virus scanning services.

- Gateway Toolkit:
     Used to build plug-ins and event consumers to extend policy compliance and
     logging requirements between a local Sametime community and one or many
     external communities. 

- Meeting Toolkit:
     This toolkit contains the Online Meeting Toolkit, the Meeting Room
     Client (MRC) Extensibility API, and Meeting Compliance API.

     The Online Meeting Toolkit is used to schedule and manage IBM Sametime
     Web conferences via HTTP.

     The MRC Extensibility API is a JavaScript API that allows you to customize
     certain aspects of Web conferences via JavaScript extensions.
     
     The Meeting Compliance API is used to provide enhanced control over certain meeting 
     features, such as to log meeting entry/exit, polls, start/stop of sharing, and to 
     block meeting entry or sidebar tool submitted content.

- Monitoring and Statistics Toolkit:
     Used to access IBM Sametime server statistics in XML format via HTTP.

- Telephony Toolkit:
     This toolkit combines the Telephony Conferencing Service Provider Interface
     (TCSPI) Toolkit with the Client Telephony API Toolkit.

     The Client Telephony API Toolkit contains Javadoc for the telephony packages.

     The TCSPI toolkit is used to provide click-to-call telephony services for
     IBM Sametime Connect, IBM Sametime Web conferencing, and IBM Notes.

See the document Sametime_SDK_Overview.pdf in the st9sdk directory for more
information about each of these toolkits and when to use them.

The IBM Sametime SDK directory structure is shown below:

st9sdk
   readme.txt                   - This readme file
   Sametime_SDK_Overview.pdf    - IBM Sametime SDK Overview document
   client                       - IBM Sametime client toolkits
      connect                      - IBM Sametime Connect Toolkit
      connectWebApi                - IBM Sametime Connect Web API Toolkit
      sthelper                     - IBM Sametime Helper Toolkit
      stjava                       - IBM Sametime Java Toolkit
      stlinks                      - IBM Sametime Links Toolkit
      stproxy                      - IBM Sametime Browser IM Toolkit
   license                      - IBM Sametime SDK license files
   server                       - IBM Sametime server toolkits
      advanced                     - Sametime Advanced Toolkit
      commserver                   - Community Server Toolkit


      dda                          - Directory & Database Access Toolkit


      gateway                      - Gateway Toolkit
      meeting                      - Online Meeting Toolkit
      stats                        - Monitoring & Statistics Toolkit
   telephony                     - IBM Sametime telephony toolkits
      client                       - IBM Sametime Client Telephony APIs 
      tcspi                        - Telephony Conferencing SPI Toolkit

Most of the toolkit directories include a readme file that provides more
information about that toolkit.
    

------------
REQUIREMENTS
------------

See the documentation for each toolkit for specific toolkit requirements.

