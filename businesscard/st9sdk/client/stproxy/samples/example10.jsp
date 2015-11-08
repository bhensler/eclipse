<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Copyright IBM Corp. 2009, 2014  All Rights Reserved.              --%>

<!--
 * L-MCOS-96LQPJ
-->
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   <title>IBM Sametime 9 Example 10</title>
   <link type="text/css" rel="stylesheet" href="sametime.css" />

   <script type="text/javascript">
      // Settings for the proxy
      var stproxyConfig = {
         server: "http://<YourProxyServer>:9080",      // Replace with the appropriate server & port
         tunnelURI: "http://<ThisServer>/tunnel.html", // The url of the openAjax Tunnel which must be on the same server as this file
         isConnectClient: false
      };

      djConfig = {
		parseOnLoad: true,
 		locale: "<%=request.getLocale().toString().replaceAll("_","-")%>"
      };
   </script>

   <script type="text/javascript" src="http://<YourProxyServer>:9080/stwebclient/latest/dojo.blue/dojo/latest/dojo.js"></script>
   <script type="text/javascript" src="http://<YourProxyServer>:9080/stwebclient/latest/include.js?widget=widgets&ato=false"></script>

   <script type="text/javascript">
      // User ID and password
      var userID = "<YourUser>";
      var password = "<YourPassword>";

      // Error callback
      function somethingFailed(reason, error) {
         // Something has gone wrong: display some information
         alert("Error: " + reason + ": " + error);
      }

      // After logging in, do whatever else you need to do
      function loggedInOK(x) {
         // Whatever is needed
      }
      
      // On load, let's log in
		stproxy.addOnLoad(
			function() {
				stproxy.login.loginByPassword(userID, password, stproxy.awareness.AVAILABLE, "I'm available", loggedInOK, somethingFailed);
			}
		)
   </script>

</head>

<body class="stbody tundra">
  <div class="stlogo">
    <img src="logo.png" />
  </div>
  <div class="stmain">
     <div class="stcoll">
       <h1>Example 10</h1>
       <p class="sttext">This example shows how to embed the buddy list into a web page</p>
     </div>
     <div class="stcolr stproxy_widget">
        <div class="stblist" id="myClient" dojoType="sametime.BuddyList">
        </div>
     </div>
  </div>
</body>
</html>

