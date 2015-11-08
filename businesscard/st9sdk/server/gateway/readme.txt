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


US Government Users Restricted Rights- Use, duplication or
disclosure restricted by GSA ADP Schedule Contract with IBM Corp.  

****************************************************************************

            IBM Lotus Sametime Gateway 9.0 Toolkit

****************************************************************************



Contents:

1. Toolkit contents
2. Requirements


--------------------
1. Toolkit CONTENTS
--------------------


The IBM Lotus Sametime Gateway 9.0 Toolkit provides documentation and samples to help you build
message handler plug-in applications and Common Event Infrastructure event consumers 
for the IBM Lotus Sametime Gateway 9.0.

The Toolkit includes the following components:

- IBM Lotus Sametime Gateway 9.0 Integration Guide. This document describes the programming model
  for developing message handler plug-ins and event consumers for the IBM Lotus Sametime Gateway,
  and provides the information you need to build your own plug-ins and event consumers for the
  Sametime Gateway 9.0. The Integration Guide also describes the sample programs in the Toolkit and
  provides a detailed look at the samples to illustrate key concepts.


- Sample programs. The Toolkit samples consist of 3 plug-ins and 2 event consumers.  Source code,
  project and classpath files are packaged in the EAR file, rtcgw_samplesEAR.ear. The EAR file
  can be installed into the Sametime Gateway 9.0 or imported into an application development
  environment to review or modify sources.  One of the event consumers samples is a Message-Driven
  Bean (MDB) application that writes output from the Event logger.


- Javadoc(TM) for the Sametime Gateway APIs described in the Integration Guide.


The Sametime Gateway Toolkit directory structure is shown below:

gateway
   readme.txt				- This readme file
   relnotes.txt				- Release notes
   doc
      STGW_Integration_guide.pdf	- Integration Guide
   javadoc				- Sametime Gateway 9.0 Toolkit API Javadoc
   license				- Sametime Gateway 9.0 Toolkit license files
   samples
        rtcgw_samplesEAR.ear		- EAR file which contains 5 samples.  The EAR file can be installed
						or imported into an application development environment.
	

------------
REQUIREMENTS
------------

In order to work with the samples in the IBM Lotus Sametime Toolkit, or develop
your own plug-ins, you'll need the following:

- rtc.gatewayAPI.jar and com.ibm.events.client.jar from the RTC Gateway Server install

- A J2EE 1.4.2 Java development environment that uses the Java Development Kit 1.5,
  such as IBM Rational Application Developer (RAD) 6.0.0, or 6.0.1

- A Microsoft(R) Windows(R) XP, Windows 2003 or Windows 2008

To find out more about development requirements, see the IBM Lotus Sametime Gateway
9.0 Integration Guide included in this Toolkit.
