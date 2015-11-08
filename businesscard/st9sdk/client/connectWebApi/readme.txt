*******************************************************************************

         IBM(R) Sametime(R) 9.0 Software Development Kit
                   IBM Sametime Connect Web API Toolkit

*******************************************************************************

This readme provides important information about the IBM Sametime Connect
Client Web API Toolkit, which is part of the IBM Sametime Software
Development Kit (SDK).

The IBM Sametime Connect Web API Toolkit is a web application programming
interface (API) that provides an external interface to basic functionality of the
IBM Sametime Client. It allows Web developers to Sametime-enable their Web pages
and applications with "livenames." Web-based applications that integrate the Connect
Web API are essentially able to proxy the functionality of the locally running
Sametime Client (managing contacts, starting chats, Presence status).  The Connect
Web API Toolkit particularly differs from another Sametime Web-based toolkit,
Sametime Links, in this respect.  Whereas the Sametime Links Toolkit is self-contained
by the use of JavaScript and applets, the Connect Web API Toolkit requires the client
to be installed.  It allows for a more seamless integration with the Sametime Client.

Additional information about Sametime is available at http://www.lotus.com/sametime

-----------------------------------------------
IBM SAMETIME CONNECT WEB API TOOLKIT CONTENTS
-----------------------------------------------

The IBM Sametime Connect Web API Toolkit provides documentation and samples to
help you build web pages that interface with the IBM Sametime Connect 9.0 client.

The IBM Sametime Connect Web API Toolkit includes the following components.

- IBM Sametime Connect Web API Toolkit 9.0 Developer's Guide. This document
  describes the web programming model for interfacing with IBM Sametime Connect 9.0
  and later releases, and provides the information you need to build your own Sametime-
  enabled web pages. The Developer's Guide also describes the sample web pages available
  in the toolkit, and provides detailed looks at each of those samples to illustrate key
  concepts.

- Sample web pages. The toolkit samples consist of 5 HTML pages that build upon each
  other to demonstrate different levels of the functionality available in the toolkit.
  The final sample includes a web page that interfaces with the Recent Buddies sample
  in the Connect Toolkit.  The Recent Buddies sample from the Connect Toolkit includes
  a custom Web API servlet-based action which you can invoke to retrieve the list of
  recent buddies.

The IBM Sametime Connect Web API Toolkit directory structure is shown below
(other contents of the IBM Sametime SDK are not shown):

st90sdk
   client
      connectWebApi
         readme.txt                          - this readme file
         doc
            ConnectWebApiDevguide.pdf  		 - IBM Sametime Connect Web API Developer's Guide
         samples                             - Sample HTML pages

---------------------------------------
DOCUMENTATION ADDITIONS AND CORRECTIONS
---------------------------------------
