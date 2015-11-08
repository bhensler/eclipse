
*******************************************************************************

         IBM(R) Sametime(R) 9.0 Software Development Kit
                      Sametime Connect Toolkit

*******************************************************************************

This readme provides important information about the IBM Sametime Connect
Toolkit, which is part of the IBM Sametime Software Development Kit (SDK).

The IBM Sametime Connect Toolkit provides documentation and samples to help you
build components that extend and integrate with the IBM IBM Sametime Connect
9.0 client. IBM Sametime Connect 7.5 was the first release of new
instant messaging technology built on the Eclipse(TM)-based Lotus Expeditor
platform. IBM Sametime Connect 7.5 and later releases leverage the Eclipse
plug-in framework to provide developers with extensibility features that go far
beyond those available in previous IBM Sametime Connect releases.

This release of the IBM Sametime Connect Toolkit includes new APIs, and is
intended to be used with the IBM Sametime Connect 9.0 release. Plug-ins
created using this release of the toolkit may not work with IBM Sametime Connect
8.5 or prior versions, although plug-ins created using a previous toolkit release
should work with IBM Sametime Connect 9.0.


------------------------
CHANGES FOR THIS RELEASE
------------------------

NOTE: The 9.0 SDK requires the use of the Expeditor 6.2x Toolkit - the XPD Toolkit
can be found at the following developerWorks site:

    http://www14.software.ibm.com/webapp/download/nochargesearch.jsp?q=Lotus+Expeditor+Toolkit

The file API_Changes.txt in the doc subdirectory provides a list of interfaces,
classes, and members (constructors, fields, methods, inner classes) that were
added or deprecated since the 9.0 release. In addition, classes, interfaces, and
members that were added for 9.0 are marked with "Since Sametime 9.0" in the
IBM Sametime Connect API Javadoc(TM).

Note that the version numbers for all sample plug-ins have been updated for
this toolkit release. If you've already copied older versions of these plug-ins
to your IBM Sametime Connect plugins directory, you might wish to delete the
old versions before copying any of the updated versions there.

In addition to the normal samples, the samples directory also contains a few
feature projects for some of the samples, and one update site project which
includes all of the features.  Import these projects into Eclipse to see examples
of how feature and update sites are constructed.  The update site itself in the
samples directory can be used to import all of the features into Sametime Connect.

The toolkit also provides a copy of the IBM Redbook: Extending Sametime 7.5 - 
Building Plug-ins for Sametime.  Note this document was written for Sametime 7.5,
and does not reflect any changes made for later releases such as 7.5.1, 8.0x, 8.5x, etc.

Before trying to work with the sample plug-ins, be sure to see the "Documentation
Additions and Corrections" section of this readme.

NOTE: The telephony APIs were moved from the Connect Toolkit to the Telephony Toolkit
starting in the Sametime 7.5.1 SDK.

---------------------------------------
IBM SAMETIME CONNECT TOOLKIT CONTENTS
---------------------------------------

The IBM Sametime Connect Toolkit provides documentation and samples to
help you build Eclipse plug-ins for the IBM Sametime Connect 9.0 client.

The IBM Sametime Connect Toolkit includes the following components. Some of
these components have been updated for the 9.0 release and are noted as such.
The other components are unchanged from the IBM Sametime SDK 8.5 release:

- IBM Sametime Connect 9.0 Integration Guide. This document describes
  the programming model for IBM Sametime Connect 9.0 and later releases, and
  provides the information you need to build your own Eclipse plug-ins for IBM
  Sametime Connect. The Integration Guide also describes the sample programs in
  the toolkit, and provides a detailed look at one of those samples to illustrate
  key concepts.

- IBM Sametime Connect 9.0 User Experience Guidelines. This document
  provides guidelines for designing the user interface and other user-facing
  elements when developing plug-ins for IBM Sametime Connect.

- Sample programs. The toolkit samples consist of 11 Eclipse plug-ins, packaged
  as ready-to-run JAR files, most with source code.

- Javadoc for the IBM Sametime Connect 9.0 APIs described in the Integration
  Guide.

- Javadoc for the sample programs.

- Javadoc plug-in that integrates the IBM Sametime Connect API, Client Telephony
  API, and sample Javadoc with the Eclipse IDE.


The IBM Sametime Connect Toolkit directory structure is shown below (other contents
of the IBM Sametime SDK are not shown):

st90sdk
   client
      connect
         readme.txt                          - this readme file
         doc
            API_Changes.txt                  - Documentation for API changes since 8.5
            Extending_Sametime_Redbook.pdf   - IBM Redbook: Extending Sametime 7.5 - Building Plug-ins for Sametime
            ST_Integration_Guide.pdf         - IBM Sametime Connect Integration Guide
            UX_Guidelines.pdf                - IBM Sametime Connect User Experience Guidelines
         javadoc
            connect                          - IBM Sametime Connect 9.0 API Javadoc
            samples                          - sample program Javadoc
         javadoc-plugin                      - plug-in to integrate Javadoc with Eclipse IDE
         samples                             - sample program plug-in JAR files
         stXpdToolkitProfile                 - Sametime XPD Toolkit Configuration update site
    

---------------------------------------
DOCUMENTATION ADDITIONS AND CORRECTIONS
---------------------------------------

* Branding: To change the default branding in Sametime, you must now tell the ST UI plug-in to use your
  custom branding plug-in instead of the default.  This is done by setting the stBranding preference in
  the com.ibm.collaboration.realtime.ui plugin-in to the ID of your custom branding extension.  For
  example, to use the branding sample in the SDK, the property must be set as follows:
  
      com.ibm.collaboration.realtime.ui/stbranding=com.ibm.collaboration.realtime.sample.branding.custom
  
      - This can be done during install time by editing the plugin_customization.ini file within the deploy
        directory of the install CD (or network-install directory).
  
      - It can also be done afterwards by provisioning the plug-in preference to Sametime Connect, see
        technote #1261055:
        
          http://www-1.ibm.com/support/docview.wss?rs=477&uid=swg21261055
  
      A default plugin_customization.ini file has been included in the branding sample.  After installing
      this sample into a client, follow either of the two steps above to enable it.  To test this sample
      in the development environment, add the following in the "Program arguments:" section of the Arguments tab
      in the Run / Debug configuration window (see section 4.1.7):
      
          -plugincustomization [Location of Branding sample source]/plugin_customization.ini

* Extended Status and Telephony Status: These plug-ins have been removed from the SDK in 8.5.1 and added into core
  Sametime Connect but are disabled by default.  Please see Appendix B in the IBM Sametime Connect Integration Guide
  for more information on this change and details on how to enable either extended or telephony status.

------------
REQUIREMENTS
------------

In order to work with the samples in the IBM Sametime Connect Toolkit or develop
your own plug-ins, you'll need the following:

- The Lotus Expeditor 6.2x Toolkit, which facilitates development of
  plug-ins for IBM Sametime and other Lotus Expeditor applications. For more
  information about the Lotus Expeditor Toolkit, visit the Lotus Expeditor Web
  site at http://www.ibm.com/software/sw-lotus/lotus_expeditor.

- One of the following software development platforms:

      Eclipse 3.4 and optional Web Tools Project (WTP) 3.0
      IBM(R) Rational(R) Application Developer (RAD) for WebSphere Software v7.5
      IBM(R) Rational(R) Software Architect (RSA) for WebSphere Software v7.5

- A standard Java Runtime Environment, 1.5 or higher, to run the Eclipse IDE

- A Microsoft(R) Windows(R), Linux, or Mac OS X platform supported by IBM
  Sametime Connect 9.0. See the IBM Sametime 9.0 release notes for a list
  of supported IBM Sametime Connect platforms.

NOTE: At the time of this release, the XPD 6.2.3 Toolkit was not yet available on developerWorks, but the Sametime 9.0 SDK 
will also work on that when it becomes available.

To find out more about development requirements, see the IBM Sametime Connect
Integration Guide in this toolkit.